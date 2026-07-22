package com.rpicos.quantumcraft.blockentity;

import com.rpicos.quantumcraft.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The "control dot" half of a multi-qubit gate: placed on a control qubit's row at a given
 *  time-column, paired at scan time with a {@link TargetXNodeBlockEntity} at the same
 *  time-column on a different qubit's row. One control paired with one target makes a CNOT; two
 *  controls paired with one target (at the same column, on three different qubit lines) make a
 *  Toffoli - the same block serves both, since the scanner counts however many controls share a
 *  column with the target rather than needing a separate "which multi-qubit gate is this" choice
 *  on the block itself. */
public class ControlNodeBlockEntity extends BlockEntity {
	public ControlNodeBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CONTROL_NODE, pos, state);
	}
}
