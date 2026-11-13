package com.rpicos.quantumcraft.block;

import com.mojang.serialization.MapCodec;
import com.rpicos.quantumcraft.blockentity.GateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/** A single-qubit gate block. Unoriented (a gate has no "leads" the way a circuit component
 *  does) - right-click (empty hand) cycles which of the six gates it is. */
public class GateBlock extends Block implements EntityBlock {
	public static final MapCodec<GateBlock> CODEC = simpleCodec(GateBlock::new);

	public GateBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<GateBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GateBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		// See CircuitSimCraft's ComponentBlock for why this guard is needed: without it, ANY item in
		// hand (not just a genuinely empty one) falls back to the empty-hand cycle behavior and
		// silently swallows the interaction, so a held Quantum Probe's own useOn() never runs.
		return stack.isEmpty() ? InteractionResult.TRY_WITH_EMPTY_HAND : InteractionResult.PASS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (level.getBlockEntity(pos) instanceof GateBlockEntity gate) {
			gate.cyclePreset();
			player.sendOverlayMessage(Component.literal("Gate: " + gate.gateName()));
		}
		return InteractionResult.SUCCESS_SERVER;
	}
}
