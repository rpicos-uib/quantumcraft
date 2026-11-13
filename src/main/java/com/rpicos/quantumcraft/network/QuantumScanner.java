package com.rpicos.quantumcraft.network;

import com.rpicos.quantumcraft.blockentity.ControlNodeBlockEntity;
import com.rpicos.quantumcraft.blockentity.GateBlockEntity;
import com.rpicos.quantumcraft.blockentity.QubitLineBlockEntity;
import com.rpicos.quantumcraft.blockentity.TargetXNodeBlockEntity;
import com.rpicos.quantumcraft.sim.Gates;
import com.rpicos.quantumcraft.sim.QuantumCircuit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Turns a right-clicked {@link QubitLineBlockEntity} into a {@link QuantumCircuit}, by pure
 * on-demand world inspection rather than any persisted registry - unlike CircuitSimCraft's
 * continuously-ticking, incrementally-rebuilt network, a quantum circuit here is read fresh
 * every time it's probed, since there is no per-tick state to maintain between probes at all.
 *
 * <p>Two steps: (1) flood-fill outward from the clicked qubit line through any other qubit lines
 * directly adjacent to it (six-face adjacency, no orientation) to find every qubit line in the
 * same circuit, sorted by Z (then Y, then X, as a deterministic tie-break) to assign qubit
 * indices 0..N-1; (2) scan east from each qubit line's own position, one block at a time up to
 * {@link #MAX_TIMELINE_LENGTH}, recording any gate/control/target block found at each offset -
 * an offset with nothing at it is simply a time-step with no operation, not an error or a gap
 * that stops the scan. Every entry found is grouped by its time-column (the offset from each
 * qubit line's own anchor) and processed in ascending column order into a single
 * {@link QuantumCircuit}: a column with only single-qubit gate entries applies each
 * independently (they act on different qubits and commute); a column with exactly one target and
 * one or two controls becomes a CNOT or Toffoli; any other combination in the same column is
 * skipped as an inconsistent placement rather than guessed at.
 */
public final class QuantumScanner {
	private static final int MAX_TIMELINE_LENGTH = 24;
	private static final int MAX_QUBITS = 3;

	private QuantumScanner() {
	}

	public record ScanResult(QuantumCircuit circuit, int numQubits, List<String> warnings) {
	}

	private sealed interface Entry {
	}

	private record SingleGateEntry(int qubit, GateBlockEntity gate) implements Entry {
	}

	private record ControlEntry(int qubit) implements Entry {
	}

	private record TargetEntry(int qubit) implements Entry {
	}

	public static ScanResult scan(ServerLevel level, BlockPos clickedLinePos) {
		List<BlockPos> lines = findConnectedQubitLines(level, clickedLinePos);
		lines.sort(Comparator.<BlockPos>comparingInt(BlockPos::getZ)
				.thenComparingInt(BlockPos::getY)
				.thenComparingInt(BlockPos::getX));

		int numQubits = Math.min(lines.size(), MAX_QUBITS);
		List<String> warnings = new ArrayList<>();
		if (lines.size() > MAX_QUBITS) {
			warnings.add("Only the first " + MAX_QUBITS + " qubit lines (by Z position) are used; "
					+ (lines.size() - MAX_QUBITS) + " more were found but ignored.");
		}

		Map<Integer, List<Entry>> byColumn = new TreeMap<>();
		for (int qubit = 0; qubit < numQubits; qubit++) {
			BlockPos anchor = lines.get(qubit);
			for (int offset = 1; offset <= MAX_TIMELINE_LENGTH; offset++) {
				BlockPos pos = anchor.relative(Direction.EAST, offset);
				BlockEntity be = level.getBlockEntity(pos);
				Entry entry = switch (be) {
					case GateBlockEntity gate -> new SingleGateEntry(qubit, gate);
					case ControlNodeBlockEntity ignored -> new ControlEntry(qubit);
					case TargetXNodeBlockEntity ignored -> new TargetEntry(qubit);
					case null, default -> null;
				};
				if (entry != null) {
					byColumn.computeIfAbsent(offset, k -> new ArrayList<>()).add(entry);
				}
			}
		}

		QuantumCircuit circuit = new QuantumCircuit(numQubits);
		for (var column : byColumn.entrySet()) {
			applyColumn(circuit, column.getValue(), warnings, column.getKey());
		}

		return new ScanResult(circuit, numQubits, warnings);
	}

	private static void applyColumn(QuantumCircuit circuit, List<Entry> entries, List<String> warnings, int column) {
		List<Integer> controls = new ArrayList<>();
		Integer target = null;
		for (Entry entry : entries) {
			switch (entry) {
				case SingleGateEntry g -> circuit.addSingleGate(g.qubit(), g.gate().matrix(), g.gate().gateName());
				case ControlEntry c -> controls.add(c.qubit());
				case TargetEntry t -> {
					if (target != null) {
						warnings.add("Column " + column + " has more than one target - skipped.");
						return;
					}
					target = t.qubit();
				}
			}
		}

		if (controls.isEmpty() && target == null) {
			return; // single-qubit gates only in this column, already applied above
		}
		if (target == null) {
			warnings.add("Column " + column + " has a control with no target - skipped.");
			return;
		}
		if (controls.size() == 1) {
			circuit.addControlledGate(controls.get(0), target, Gates.X, "CNOT");
		} else if (controls.size() == 2) {
			circuit.addToffoli(controls.get(0), controls.get(1), target);
		} else {
			warnings.add("Column " + column + " has " + controls.size() + " controls for one target "
					+ "(only 1 or 2 supported) - skipped.");
		}
	}

	private static List<BlockPos> findConnectedQubitLines(ServerLevel level, BlockPos start) {
		List<BlockPos> found = new ArrayList<>();
		Set<BlockPos> visited = new HashSet<>();
		Deque<BlockPos> queue = new ArrayDeque<>();
		queue.add(start.immutable());
		visited.add(start.immutable());

		while (!queue.isEmpty()) {
			BlockPos pos = queue.poll();
			found.add(pos);
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				if (visited.contains(neighbor)) {
					continue;
				}
				if (level.getBlockEntity(neighbor) instanceof QubitLineBlockEntity) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}
		return found;
	}
}
