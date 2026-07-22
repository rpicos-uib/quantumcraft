package com.rpicos.quantumcraft.sim;

/** A complex number, immutable. No Minecraft dependency - this whole package is a standalone
 *  quantum-state simulator, unit-testable with plain javac/java. */
public record Complex(double re, double im) {
	public static final Complex ZERO = new Complex(0, 0);
	public static final Complex ONE = new Complex(1, 0);
	public static final Complex I = new Complex(0, 1);

	public static Complex polar(double magnitude, double phaseRadians) {
		return new Complex(magnitude * Math.cos(phaseRadians), magnitude * Math.sin(phaseRadians));
	}

	public Complex add(Complex other) {
		return new Complex(re + other.re, im + other.im);
	}

	public Complex subtract(Complex other) {
		return new Complex(re - other.re, im - other.im);
	}

	public Complex multiply(Complex other) {
		return new Complex(re * other.re - im * other.im, re * other.im + im * other.re);
	}

	public Complex scale(double factor) {
		return new Complex(re * factor, im * factor);
	}

	public Complex conjugate() {
		return new Complex(re, -im);
	}

	public double magnitudeSquared() {
		return re * re + im * im;
	}

	public double magnitude() {
		return Math.sqrt(magnitudeSquared());
	}

	@Override
	public String toString() {
		if (im == 0) {
			return String.format("%.4f", re);
		}
		return String.format("%.4f%+.4fi", re, im);
	}
}
