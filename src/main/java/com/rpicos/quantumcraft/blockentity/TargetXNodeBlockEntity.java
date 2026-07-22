package com.rpicos.quantumcraft.blockentity;

import com.rpicos.quantumcraft.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** The "target" half of a CNOT or Toffoli - see {@link ControlNodeBlockEntity} for how the two
 *  pair up. Always applies an X to whichever qubit's row it sits on, conditioned on every control
 *  in the same time-column being set. */
public class TargetXNodeBlockEntity extends BlockEntity {
	public TargetXNodeBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TARGET_X_NODE, pos, state);
	}
}
