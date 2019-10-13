package ru.maklas.melnikov.utils;

import com.badlogic.gdx.math.MathUtils;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;

public class LogisticUtils {


	public static double sigmoid(double value){
		return 1 / (1 + Math.exp(-value));
	}

	public static double sigmoidNoInfinity(double value){
		return MathUtils.clamp(1 / (1 + Math.exp(-value)), 1e-3, 1e3);
	}

	public static double hypothesis(double x1, double x2, double th0, double th1, double th2) {
		return sigmoidNoInfinity(th0 + (x1 * th1) + (x2 * th2));
	}

	public static DoubleArray predictions(Matrix features, DoubleArray weights){
		DoubleArray predictions = new DoubleArray(features.getHeight());
		for (int i = 0; i < features.getHeight(); i++) {
			predictions.add(sigmoidNoInfinity(features.getRow(i).sumMultiplication(weights)));
		}
		return predictions;
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

	public static double logisticCost(Matrix features, DoubleArray labels, DoubleArray weights) {
		DoubleArray predictions = predictions(features, weights);
		double costs = 0;
		for (int i = 0; i < predictions.size; i++) {
			costs += logisticCost(predictions.get(i), labels.get(i) > 0.5 ? 1 : 0);
		}
		return costs / predictions.size;
	}

	public static double accuracy(Matrix features, DoubleArray labels, DoubleArray weights) {
		DoubleArray predictions = predictions(features, weights);
		DoubleArray diff = predictions.minus(labels);
		return 1.0 - ((double) diff.count(d -> Math.abs(d) >= 0.5) / diff.size);
	}


	/**
	 * @param features [x][3]
	 * @param lables   [x]
	 * @param weights  [3]
	 * @param learningRate хз
	 * @return gradient descent to be distracted from weights [3]
	 */
	public static DoubleArray gradientDescent(Matrix features, DoubleArray lables, DoubleArray weights, double learningRate) {
		int size = features.getHeight();
		DoubleArray predictionsMinusLabels = predictions(features, weights).minus(lables);
		DoubleArray gradient = features //[x, 3]
				.transpose() //[3, x]
				.mul(predictionsMinusLabels.toColumn()) //[3, 1]
				.firstColumn();//[3]
		return gradient.mul(learningRate / size);
	}

}
