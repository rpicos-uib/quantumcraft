package com.rpicos.quantumcraft;

import com.rpicos.quantumcraft.network.QuantumResultPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuantumCraft implements ModInitializer {
	public static final String MOD_ID = "quantumcraft";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("QuantumCraft: initializing quantum circuitry");
		ModBlocks.init();
		ModBlockEntities.init();
		ModItems.init();
		ModCreativeTab.init();
		PayloadTypeRegistry.clientboundPlay().register(QuantumResultPayload.TYPE, QuantumResultPayload.STREAM_CODEC);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
