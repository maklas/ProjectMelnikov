package ru.maklas.melnikov.utils;

import com.badlogic.gdx.math.MathUtils;

public class LogisticUtils {


	public static double sigmoid(double value){
		return 1 / (1 + Math.exp(-value));
	}

	public static double sigmoidNoInfinity(double value){
		return MathUtils.clamp(1 / (1 + Math.exp(-value)), 1e-3, 1e3);
	}

	public static double logisticCost(double value, int target) {
		if (target == 1){
			return -Math.log(value);
		} else if (target == 0){
			return -Math.log(1 - value);
		} else {
			throw new RuntimeException("Bad input: " + target);
		}
	}

	public static int classify(double val) {
		return val >= 0.5 ? 1 : 0;
	}

	public static double hypothesis(double x1, double x2, double th0, double th1, double th2) {
		return sigmoidNoInfinity(th0 + (x1 * th1) + (x2 * th2));
	}

	/**
	 * @param features [x][2]
	 * @param lables   [x]
	 * @param weights  [3]
	 * @param learningRate хз
	 * @return gradient descent to be distracted from weights [3]
	 */
	public static double[] gradientDescent(double[][] features, int[] lables, double[] weights, double learningRate) {

		int size = features.length;

		double[] predictions = new double[size];
		for (int i = 0; i < size; i++) {
			double[] feature = features[i];
			predictions[i] = hypothesis(feature[0], feature[1], weights[0], weights[1], weights[2]);
		}
		double[] predictionsMinusLabels = new double[size];
		for (int i = 0; i < size; i++) {
			predictionsMinusLabels[i] = predictions[i] - lables[i];
		}
		double[] gradient = new double[3];
		gradient[0] = 0;
		for (int i = 0; i < size; i++) {
			double p = predictionsMinusLabels[i];
			gradient[0] += p;
		}
		for (int i = 0; i < size; i++) {
			double f1 = features[i][0];
			double p = predictionsMinusLabels[i];
			gradient[1] += f1 * p;
		}
		for (int i = 0; i < size; i++) {
			double f2 = features[i][1];
			double p = predictionsMinusLabels[i];
			gradient[2] += f2 * p;
		}

		for (int i = 0; i < gradient.length; i++) {
			gradient[i] = gradient[i]* (learningRate / size);
		}

		return gradient;
	}

}
