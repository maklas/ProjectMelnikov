package ru.maklas.melnikov.utils;

public class LogisticUtils {


	public static double sigmoid(double value){
		return 1 / (1 + Math.exp(-value));
	}

	public static double logisticCost(double value, int target) {
		if (target == 1){
			return -Math.log10(value);
		} else if (target == 0){
			return -Math.log10(1 - value);
		} else {
			throw new RuntimeException("Bad input: " + target);
		}
	}

	public static int classify(double val) {
		return val >= 0.5 ? 1 : 0;
	}

	public static double hypothesis(double x1, double x2, double th0, double th1, double th2) {
		return sigmoid(th0 + (x1 * th1) + (x2 * th2));
	}

	public static double gradientDescent(double parameter, double cost) {
		return parameter - (0);//TODO
	}

}
