package com.rpicos.quantumcraft;

import com.rpicos.quantumcraft.item.QuantumProbeItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public final class ModItems {
	public static final Item QUBIT_LINE = registerBlockItem("qubit_line", ModBlocks.QUBIT_LINE);
	public static final Item GATE = registerBlockItem("gate", ModBlocks.GATE);
	public static final Item CONTROL_NODE = registerBlockItem("control_node", ModBlocks.CONTROL_NODE);
	public static final Item TARGET_X_NODE = registerBlockItem("target_x_node", ModBlocks.TARGET_X_NODE);

	public static final Item PROBE = registerItem("probe", QuantumProbeItem::new);

	private static Item registerBlockItem(String path, Block block) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, QuantumCraft.id(path));
		Item item = new BlockItem(block, new Item.Properties().setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	private static Item registerItem(String path, Function<Item.Properties, Item> factory) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, QuantumCraft.id(path));
		Item item = factory.apply(new Item.Properties().setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static void init() {
	}
}
