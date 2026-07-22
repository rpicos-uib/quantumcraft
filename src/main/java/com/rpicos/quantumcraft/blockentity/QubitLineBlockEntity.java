package com.rpicos.quantumcraft.blockentity;

import com.rpicos.quantumcraft.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Anchor for one qubit's timeline row - placed once at the start (the west end) of a row of
 *  gate blocks extending east, one per qubit. Carries no state of its own: which qubit index a
 *  line gets is derived at scan time from its position among the other qubit lines directly
 *  adjacent to it (see {@code network.QuantumScanner}), not stored here, so there is nothing for
 *  a player to configure on this block beyond placing it. */
public class QubitLineBlockEntity extends BlockEntity {
	public QubitLineBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.QUBIT_LINE, pos, state);
	}
}
