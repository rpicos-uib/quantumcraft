package com.rpicos.quantumcraft.item;

import com.rpicos.quantumcraft.blockentity.QubitLineBlockEntity;
import com.rpicos.quantumcraft.network.QuantumResultPayload;
import com.rpicos.quantumcraft.network.QuantumScanner;
import com.rpicos.quantumcraft.sim.QuantumRegister;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Right-click a qubit line to run the whole circuit it's part of and read out the exact
 * probability of every basis state - the "ideal" readout, no measurement actually performed, the
 * same sense in which CircuitSimCraft's oscilloscope shows an exact voltage. Shift-right-click
 * instead performs a genuine Born-rule measurement: the circuit is run once, then the register
 * is actually collapsed onto a single random outcome, exactly the way a real quantum computer's
 * one-shot measurement would be, and only that outcome is reported.
 */
public class QuantumProbeItem extends Item {
	public QuantumProbeItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		BlockPos pos = context.getClickedPos();
		if (!(level.getBlockEntity(pos) instanceof QubitLineBlockEntity)) {
			return InteractionResult.PASS;
		}
		if (!(context.getPlayer() instanceof ServerPlayer player) || !(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.PASS;
		}

		QuantumScanner.ScanResult scan = QuantumScanner.scan(serverLevel, pos);
		QuantumRegister register = scan.circuit().run();

		List<Float> probabilities = new ArrayList<>();
		boolean measuring = player.isShiftKeyDown();
		int measuredOutcome = -1;
		if (measuring) {
			measuredOutcome = register.measure(new Random());
		}
		for (double p : register.probabilities()) {
			probabilities.add((float) p);
		}

		QuantumResultPayload payload = new QuantumResultPayload(
				scan.numQubits(), probabilities, measuring, measuredOutcome, scan.warnings());
		ServerPlayNetworking.send(player, payload);

		String message = measuring
				? "Measured: |" + toBitString(measuredOutcome, scan.numQubits()) + ">"
				: String.format("Ran %d-qubit circuit", scan.numQubits());
		player.sendOverlayMessage(Component.literal(message));
		return InteractionResult.SUCCESS_SERVER;
	}

	private static String toBitString(int value, int numQubits) {
		StringBuilder sb = new StringBuilder();
		for (int q = numQubits - 1; q >= 0; q--) {
			sb.append((value >> q) & 1);
		}
		return sb.toString();
	}
}
