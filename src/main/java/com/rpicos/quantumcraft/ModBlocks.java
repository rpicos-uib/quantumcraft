package com.rpicos.quantumcraft;

import com.rpicos.quantumcraft.block.ControlNodeBlock;
import com.rpicos.quantumcraft.block.GateBlock;
import com.rpicos.quantumcraft.block.QubitLineBlock;
import com.rpicos.quantumcraft.block.TargetXNodeBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public final class ModBlocks {
	public static final Block QUBIT_LINE = register("qubit_line", QubitLineBlock::new, gateProperties());
	public static final Block GATE = register("gate", GateBlock::new, gateProperties());
	public static final Block CONTROL_NODE = register("control_node", ControlNodeBlock::new, gateProperties());
	public static final Block TARGET_X_NODE = register("target_x_node", TargetXNodeBlock::new, gateProperties());

	private static BlockBehaviour.Properties gateProperties() {
		return BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.GLASS);
	}

	private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, QuantumCraft.id(path));
		T block = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	public static void init() {
	}
}
