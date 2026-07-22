package com.rpicos.quantumcraft.sim;

import java.util.Random;

/** A genuine complex-amplitude statevector for up to a handful of qubits - no scripted
 *  approximation of "what a superposition looks like," the actual 2^n-dimensional state vector
 *  a real simulator would carry, with gates applied as real unitary matrix operations. Qubit 0
 *  is the least-significant bit of a basis-state index, so a 3-qubit register's basis states
 *  are indexed 0 (|000>) through 7 (|111>), read q2 q1 q0 left to right, matching the common
 *  little-endian qubit-indexing convention.
 *
 * <p>Deliberately capped in practice at 3 qubits by how this mod builds circuits (one qubit per
 * world row), not by anything in this class - the state vector itself works for any qubit count
 * a caller asks for, it just grows as 2^n and stops being displayable long before it stops being
 * computable. */
public final class QuantumRegister {
	private final int numQubits;
	private final Complex[] amplitudes;

	public QuantumRegister(int numQubits) {
		if (numQubits < 1) {
			throw new IllegalArgumentException("A register needs at least one qubit");
		}
		this.numQubits = numQubits;
		this.amplitudes = new Complex[1 << numQubits];
		amplitudes[0] = Complex.ONE;
		for (int i = 1; i < amplitudes.length; i++) {
			amplitudes[i] = Complex.ZERO;
		}
	}

	public int numQubits() {
		return numQubits;
	}

	public int dimension() {
		return amplitudes.length;
	}

	public Complex amplitude(int basisState) {
		return amplitudes[basisState];
	}

	public Complex[] amplitudes() {
		return amplitudes.clone();
	}

	public double[] probabilities() {
		double[] probs = new double[amplitudes.length];
		for (int i = 0; i < amplitudes.length; i++) {
			probs[i] = amplitudes[i].magnitudeSquared();
		}
		return probs;
	}

	/** Should stay 1.0 (within floating-point error) after any sequence of unitary gates - a
	 *  cheap sanity check that a gate implementation hasn't silently broken unitarity. */
	public double totalProbability() {
		double total = 0;
		for (double p : probabilities()) {
			total += p;
		}
		return total;
	}

	/** Applies an arbitrary 2x2 unitary to a single qubit, leaving every other qubit untouched -
	 *  the general mechanism behind X, Y, Z, H, S, and T alike; only the matrix passed in
	 *  differs between them. */
	public void applySingleQubitGate(int qubit, Complex[][] matrix) {
		requireValidQubit(qubit);
		int bit = 1 << qubit;
		for (int i = 0; i < amplitudes.length; i++) {
			if ((i & bit) != 0) {
				continue; // only visit each {i, j} pair once, from the i-has-bit-0 side
			}
			int j = i | bit;
			Complex ai = amplitudes[i];
			Complex aj = amplitudes[j];
			amplitudes[i] = matrix[0][0].multiply(ai).add(matrix[0][1].multiply(aj));
			amplitudes[j] = matrix[1][0].multiply(ai).add(matrix[1][1].multiply(aj));
		}
	}

	/** A controlled-U gate: applies the given single-qubit matrix to the target qubit only for
	 *  basis states where the control qubit is 1. CNOT is a controlled-X, CZ a controlled-Z, and
	 *  so on - one mechanism covers every two-qubit gate this mod offers as a block. */
	public void applyControlledGate(int control, int target, Complex[][] matrix) {
		requireValidQubit(control);
		requireValidQubit(target);
		if (control == target) {
			throw new IllegalArgumentException("Control and target must be different qubits");
		}
		int controlBit = 1 << control;
		int targetBit = 1 << target;
		for (int i = 0; i < amplitudes.length; i++) {
			if ((i & targetBit) != 0 || (i & controlBit) == 0) {
				continue; // only the target-bit-0-and-control-bit-1 side of each pair
			}
			int j = i | targetBit;
			Complex ai = amplitudes[i];
			Complex aj = amplitudes[j];
			amplitudes[i] = matrix[0][0].multiply(ai).add(matrix[0][1].multiply(aj));
			amplitudes[j] = matrix[1][0].multiply(ai).add(matrix[1][1].multiply(aj));
		}
	}

	/** The Toffoli (CCNOT) gate: flips the target qubit exactly when both controls are 1 - the
	 *  smallest gate in this mod's set that is not reducible to single- or two-qubit gates alone,
	 *  and (with H) enough to build a classically-universal reversible logic gate. */
	public void applyToffoli(int control1, int control2, int target) {
		requireValidQubit(control1);
		requireValidQubit(control2);
		requireValidQubit(target);
		if (control1 == control2 || control1 == target || control2 == target) {
			throw new IllegalArgumentException("Toffoli needs three distinct qubits");
		}
		int c1Bit = 1 << control1;
		int c2Bit = 1 << control2;
		int targetBit = 1 << target;
		for (int i = 0; i < amplitudes.length; i++) {
			boolean controlsSet = (i & c1Bit) != 0 && (i & c2Bit) != 0;
			if (!controlsSet || (i & targetBit) != 0) {
				continue;
			}
			int j = i | targetBit;
			Complex tmp = amplitudes[i];
			amplitudes[i] = amplitudes[j];
			amplitudes[j] = tmp;
		}
	}

	/** A genuine Born-rule measurement: samples a basis outcome with probability equal to its
	 *  amplitude's squared magnitude, then collapses the whole register onto that single outcome
	 *  - real randomness, not a scripted "pick the most likely one." Returns the sampled basis
	 *  state index. */
	public int measure(Random random) {
		double[] probs = probabilities();
		double draw = random.nextDouble();
		double cumulative = 0;
		int outcome = probs.length - 1; // floating-point fallback if rounding leaves a sliver unassigned
		for (int i = 0; i < probs.length; i++) {
			cumulative += probs[i];
			if (draw < cumulative) {
				outcome = i;
				break;
			}
		}
		for (int i = 0; i < amplitudes.length; i++) {
			amplitudes[i] = i == outcome ? Complex.ONE : Complex.ZERO;
		}
		return outcome;
	}

	private void requireValidQubit(int qubit) {
		if (qubit < 0 || qubit >= numQubits) {
			throw new IllegalArgumentException("Qubit " + qubit + " out of range for a " + numQubits + "-qubit register");
		}
	}
}
