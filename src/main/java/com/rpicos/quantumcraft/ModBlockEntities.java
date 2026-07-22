package com.rpicos.quantumcraft;

import com.rpicos.quantumcraft.blockentity.ControlNodeBlockEntity;
import com.rpicos.quantumcraft.blockentity.GateBlockEntity;
import com.rpicos.quantumcraft.blockentity.QubitLineBlockEntity;
import com.rpicos.quantumcraft.blockentity.TargetXNodeBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public final class ModBlockEntities {
	public static final BlockEntityType<QubitLineBlockEntity> QUBIT_LINE =
			register("qubit_line", QubitLineBlockEntity::new, ModBlocks.QUBIT_LINE);
	public static final BlockEntityType<GateBlockEntity> GATE =
			register("gate", GateBlockEntity::new, ModBlocks.GATE);
	public static final BlockEntityType<ControlNodeBlockEntity> CONTROL_NODE =
			register("control_node", ControlNodeBlockEntity::new, ModBlocks.CONTROL_NODE);
	public static final BlockEntityType<TargetXNodeBlockEntity> TARGET_X_NODE =
			register("target_x_node", TargetXNodeBlockEntity::new, ModBlocks.TARGET_X_NODE);

	private static <T extends BlockEntity> BlockEntityType<T> register(
			String path, BlockEntityType.BlockEntitySupplier<T> factory, Block block) {
		ResourceKey<BlockEntityType<?>> key = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, QuantumCraft.id(path));
		BlockEntityType<T> type = new BlockEntityType<>(factory, Set.of(block));
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, type);
	}

	public static void init() {
	}
}
