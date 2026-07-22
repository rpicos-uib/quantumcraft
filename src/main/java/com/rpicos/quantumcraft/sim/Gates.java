package com.rpicos.quantumcraft.sim;

/** The standard single-qubit gate matrices this mod's gate blocks apply, each a genuine 2x2
 *  unitary matrix - represented as {@code {{a, b}, {c, d}}} acting on the basis
 *  {@code {|0>, |1>}}. */
public final class Gates {
	private static final double INV_SQRT2 = 1.0 / Math.sqrt(2.0);

	private Gates() {
	}

	public static final Complex[][] IDENTITY = {
			{Complex.ONE, Complex.ZERO},
			{Complex.ZERO, Complex.ONE}
	};

	/** Pauli-X (the quantum NOT gate): flips |0> and |1>. */
	public static final Complex[][] X = {
			{Complex.ZERO, Complex.ONE},
			{Complex.ONE, Complex.ZERO}
	};

	/** Pauli-Y. */
	public static final Complex[][] Y = {
			{Complex.ZERO, Complex.I.scale(-1)},
			{Complex.I, Complex.ZERO}
	};

	/** Pauli-Z: leaves |0> alone, flips the sign of |1>. */
	public static final Complex[][] Z = {
			{Complex.ONE, Complex.ZERO},
			{Complex.ZERO, new Complex(-1, 0)}
	};

	/** Hadamard: creates an equal superposition from a basis state - the gate this mod's
	 *  "superposition" pedagogy leans on most heavily. */
	public static final Complex[][] H = {
			{new Complex(INV_SQRT2, 0), new Complex(INV_SQRT2, 0)},
			{new Complex(INV_SQRT2, 0), new Complex(-INV_SQRT2, 0)}
	};

	/** S (phase) gate: a quarter-turn phase rotation of |1>. */
	public static final Complex[][] S = {
			{Complex.ONE, Complex.ZERO},
			{Complex.ZERO, Complex.I}
	};

	/** T gate: an eighth-turn phase rotation of |1> - S is T applied twice. */
	public static final Complex[][] T = {
			{Complex.ONE, Complex.ZERO},
			{Complex.ZERO, Complex.polar(1.0, Math.PI / 4.0)}
	};
}
