package com.rpicos.quantumcraft.block;

import com.mojang.serialization.MapCodec;
import com.rpicos.quantumcraft.blockentity.ControlNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** The control marker for a CNOT/Toffoli - see {@link ControlNodeBlockEntity}. Unoriented, no
 *  properties: which qubit line it's on and which time-column it sits at is all the scanner
 *  needs. */
public class ControlNodeBlock extends Block implements EntityBlock {
	public static final MapCodec<ControlNodeBlock> CODEC = simpleCodec(ControlNodeBlock::new);

	public ControlNodeBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<ControlNodeBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ControlNodeBlockEntity(pos, state);
	}
}
