package ru.maklas.melnikov.functions.bi_functions;


import ru.maklas.melnikov.utils.LogisticUtils;

public class LogisticBiFunction extends GraphBiFunction{

	public double th0;
	public double th1;
	public double th2;

	public LogisticBiFunction() {

	}

	public LogisticBiFunction(double th0, double th1, double th2) {
		this.th0 = th0;
		this.th1 = th1;
		this.th2 = th2;
	}

	@Override
	public double f(double x1, double x2) {
		return LogisticUtils.hypothesis(x1, x2, th0, th1, th2);
	}

	@Override
	public double g(double x1, double x2) {
		return f(x1, x2);
	}

}
