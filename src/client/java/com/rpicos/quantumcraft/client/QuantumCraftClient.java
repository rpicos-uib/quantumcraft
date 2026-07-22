package com.rpicos.quantumcraft.client;

import com.rpicos.quantumcraft.QuantumCraft;
import com.rpicos.quantumcraft.network.QuantumResultPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public class QuantumCraftClient implements ClientModInitializer {
	private static final Identifier QUANTUM_RESULT_HUD_ID = QuantumCraft.id("quantum_result");

	@Override
	public void onInitializeClient() {
		HudElementRegistry.addLast(QUANTUM_RESULT_HUD_ID, new QuantumResultHud());

		ClientPlayNetworking.registerGlobalReceiver(QuantumResultPayload.TYPE,
				(payload, context) -> QuantumResultClientState.update(payload));
	}
}
