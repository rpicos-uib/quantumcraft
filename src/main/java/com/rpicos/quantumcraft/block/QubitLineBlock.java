package com.rpicos.quantumcraft.block;

import com.mojang.serialization.MapCodec;
import com.rpicos.quantumcraft.blockentity.QubitLineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

/** The anchor block a qubit's timeline row starts from - unoriented, no properties. */
public class QubitLineBlock extends Block implements EntityBlock {
	public static final MapCodec<QubitLineBlock> CODEC = simpleCodec(QubitLineBlock::new);

	public QubitLineBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<QubitLineBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new QubitLineBlockEntity(pos, state);
	}
}
