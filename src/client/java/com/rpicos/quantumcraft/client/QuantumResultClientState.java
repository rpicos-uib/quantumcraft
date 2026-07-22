package com.rpicos.quantumcraft.client;

import com.rpicos.quantumcraft.network.QuantumResultPayload;

/** Unlike CircuitCraft's oscilloscope channels, a quantum probe reading is a one-shot event, not
 *  a continuously re-sent stream - so instead of pruning stale entries every tick, the latest
 *  result is simply kept on screen for a fixed display window after each probe use, then cleared. */
final class QuantumResultClientState {
	private static final long DISPLAY_DURATION_MILLIS = 8000;

	private static volatile QuantumResultPayload latest;
	private static volatile long receivedAtMillis;

	private QuantumResultClientState() {
	}

	static void update(QuantumResultPayload payload) {
		latest = payload;
		receivedAtMillis = System.currentTimeMillis();
	}

	static QuantumResultPayload current() {
		QuantumResultPayload payload = latest;
		if (payload == null) {
			return null;
		}
		if (System.currentTimeMillis() - receivedAtMillis > DISPLAY_DURATION_MILLIS) {
			return null;
		}
		return payload;
	}
}
