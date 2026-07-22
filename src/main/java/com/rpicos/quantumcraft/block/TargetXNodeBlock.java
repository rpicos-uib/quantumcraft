package com.rpicos.quantumcraft.block;

import com.mojang.serialization.MapCodec;
import com.rpicos.quantumcraft.blockentity.TargetXNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** The target marker for a CNOT/Toffoli - see {@link TargetXNodeBlockEntity}. Unoriented. */
public class TargetXNodeBlock extends Block implements EntityBlock {
	public static final MapCodec<TargetXNodeBlock> CODEC = simpleCodec(TargetXNodeBlock::new);

	public TargetXNodeBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<TargetXNodeBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TargetXNodeBlockEntity(pos, state);
	}
}
