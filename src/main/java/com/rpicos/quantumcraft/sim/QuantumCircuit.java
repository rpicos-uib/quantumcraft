package com.rpicos.quantumcraft.sim;

import java.util.ArrayList;
import java.util.List;

/** An ordered sequence of gates, exactly the "one row per qubit, gates left to right in time"
 *  structure a standard quantum-circuit diagram already is - this class is what a world scan of
 *  a player's qubit-line rows gets turned into, and {@link #run()} is what actually executes it,
 *  entirely independent of Minecraft. Unlike {@code CircuitCraft}'s continuously-ticking MNA
 *  network, a quantum circuit here is static and is "solved" once, on demand, rather than once
 *  per game tick - there is no notion of a mid-circuit voltage to watch evolve in real time, only
 *  a well-defined final state once every gate up to that point has been applied. */
public final class QuantumCircuit {
	public sealed interface Operation permits SingleGate, ControlledGate, ToffoliGate {
	}

	public record SingleGate(int qubit, Complex[][] matrix, String gateName) implements Operation {
	}

	public record ControlledGate(int control, int target, Complex[][] matrix, String gateName) implements Operation {
	}

	public record ToffoliGate(int control1, int control2, int target) implements Operation {
	}

	private final int numQubits;
	private final List<Operation> operations = new ArrayList<>();

	public QuantumCircuit(int numQubits) {
		this.numQubits = numQubits;
	}

	public int numQubits() {
		return numQubits;
	}

	public List<Operation> operations() {
		return List.copyOf(operations);
	}

	public void addSingleGate(int qubit, Complex[][] matrix, String gateName) {
		operations.add(new SingleGate(qubit, matrix, gateName));
	}

	public void addControlledGate(int control, int target, Complex[][] matrix, String gateName) {
		operations.add(new ControlledGate(control, target, matrix, gateName));
	}

	public void addToffoli(int control1, int control2, int target) {
		operations.add(new ToffoliGate(control1, control2, target));
	}

	/** Builds a fresh |0...0> register and applies every operation on this timeline, in order.
	 *  Every call starts from scratch - there is no persisted mid-circuit state between calls,
	 *  matching how a real quantum computation is always run from a known initial state. */
	public QuantumRegister run() {
		QuantumRegister register = new QuantumRegister(numQubits);
		for (Operation op : operations) {
			switch (op) {
				case SingleGate g -> register.applySingleQubitGate(g.qubit(), g.matrix());
				case ControlledGate g -> register.applyControlledGate(g.control(), g.target(), g.matrix());
				case ToffoliGate g -> register.applyToffoli(g.control1(), g.control2(), g.target());
			}
		}
		return register;
	}
}
