package ru.maklas.melnikov.functions.bi_functions;

public class CircleBiFunction extends GraphBiFunction {

	public double th0; //x movement
	public double th1; //y movement
	public double th2; //radius

	public CircleBiFunction(double th0, double th1, double th2) {
		this.th0 = th0;
		this.th1 = th1;
		this.th2 = th2;
	}

	@Override
	public double f(double x, double y) {
		double left = x - th0;
		double right = y - th1;
		return left * left + right * right - th2 * th2;
	}

	@Override
	public double g(double x, double y) {
		double dX = 2 * (x - th0);
		double dY = 2 * (y - th1);
		return Math.sqrt(dX * dX + dY * dY);
	}
}
