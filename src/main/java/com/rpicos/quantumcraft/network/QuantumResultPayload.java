package com.rpicos.quantumcraft.network;

import com.rpicos.quantumcraft.QuantumCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

/** Server -> client: the full result of probing a quantum circuit, either the exact Born-rule
 *  probability of every basis state (the "ideal" readout - real quantum mechanics never lets you
 *  see this without disturbing the state, but as a teaching aid it is the whole point, the same
 *  spirit as an oscilloscope showing an exact voltage no scripted approximation produced), or a
 *  single collapsed classical outcome from a genuine probabilistic measurement. */
public record QuantumResultPayload(int numQubits, List<Float> probabilities, boolean isMeasurement,
		int measuredOutcome, List<String> warnings) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<QuantumResultPayload> TYPE =
			new CustomPacketPayload.Type<>(QuantumCraft.id("quantum_result"));

	public static final StreamCodec<RegistryFriendlyByteBuf, QuantumResultPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, QuantumResultPayload::numQubits,
			ByteBufCodecs.FLOAT.apply(ByteBufCodecs.list()), QuantumResultPayload::probabilities,
			ByteBufCodecs.BOOL, QuantumResultPayload::isMeasurement,
			ByteBufCodecs.VAR_INT, QuantumResultPayload::measuredOutcome,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), QuantumResultPayload::warnings,
			QuantumResultPayload::new
	);

	@Override
	public CustomPacketPayload.Type<QuantumResultPayload> type() {
		return TYPE;
	}
}
