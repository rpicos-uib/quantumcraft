package com.rpicos.quantumcraft;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTab {
	public static final ResourceKey<CreativeModeTab> KEY =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, QuantumCraft.id("gates"));

	public static final CreativeModeTab TAB = Registry.register(
			BuiltInRegistries.CREATIVE_MODE_TAB,
			KEY,
			FabricCreativeModeTab.builder()
					.title(Component.translatable("itemGroup.quantumcraft.gates"))
					.icon(() -> new ItemStack(ModItems.GATE))
					.displayItems((parameters, output) -> {
						output.accept(ModItems.QUBIT_LINE);
						output.accept(ModItems.GATE);
						output.accept(ModItems.CONTROL_NODE);
						output.accept(ModItems.TARGET_X_NODE);
						output.accept(ModItems.PROBE);
					})
					.build()
	);

	public static void init() {
	}
}
