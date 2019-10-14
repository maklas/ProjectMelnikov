package ru.maklas.melnikov.engine.log_regression;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
import ru.maklas.melnikov.utils.Log;
import ru.maklas.melnikov.utils.LogisticUtils;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class MultiClassLogisticRegressionSystem extends BaseLogisticRegressionSystem {

	private Array<BiFunctionComponent> functions;
	private Array<PointType> typeOrder;
	private int classCount;

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
		functions = new Array<>();
		typeOrder = new Array<>();

		Vector2 vec = new Vector2(0, 1).rotate(15);
		classCount = MathUtils.clamp(parameters.getClassCount(), 3, 5);
		float rotation = 360.0f / classCount;

		for (int i = 0; i < classCount; i++) {
			PointType type = PointType.values()[i];
			BiFunctionComponent bfc = new BiFunctionComponent(new LogisticBiFunction(1, vec.x, vec.y)).setColor(type.getColor());
			engine.add(new Entity().add(bfc));
			functions.add(bfc);
			typeOrder.add(type);
			vec.rotate(rotation);
		}
	}

	@Override
	protected void train() {
		for (int i = 0; i < functions.size; i++) {
			LogisticBiFunction model = (LogisticBiFunction) functions.get(i).fun;
			PointType targetType = typeOrder.get(i);

			Matrix features = new Matrix();
			DoubleArray labels = new DoubleArray(points.size());
			DoubleArray weights = DoubleArray.with(model.th0, model.th1, model.th2);

			for (int j = 0; j < points.size(); j++) {
				Entity p = points.get(j);
				features.addRow(DoubleArray.with(1, p.x, p.y));
				labels.add(classify(p, targetType));
			}

			DoubleArray weightAdjustments = LogisticUtils.gradientDescent(features, labels, weights, parameters.getLearningRate());
			model.th0 -= weightAdjustments.get(0);
			model.th1 -= weightAdjustments.get(1);
			model.th2 -= weightAdjustments.get(2);
		}
		iteration++;
	}
	@Override
	protected double getAccuracy() {
		Matrix features = getFeatures();
		double accuracy = 0;
		for (PointType pointType : typeOrder) {
			accuracy += LogisticUtils.accuracy(features, getLabels(pointType), getWeights(pointType));
		}
		return accuracy / typeOrder.size;
	}


	@Override
	protected void drawSides() {
		sr.begin(ShapeRenderer.ShapeType.Point);

		double rightX = Utils.camRightX(cam);
		double leftX = Utils.camLeftX(cam);
		double botY = Utils.camBotY(cam);
		double topY = Utils.camTopY(cam);
		double step = 2 * cam.zoom;
		//double[] values = new double[functions.size];

		Color color = new Color();
		for (double x = leftX; x < rightX; x += step) {
			for (double y = botY; y < topY; y += step) {
				color.set(0, 0, 0, 1);
				boolean meaningfull = false;
				for (BiFunctionComponent function : functions) {
					double val = function.fun.f(x, y);
					if (val < 0) {
						color.add(function.color);
						meaningfull = true;
					}
				}
				if (meaningfull) {
					sr.setColor(color);
					sr.point((float) x, (float) y, 0);
				}
			}
		}
		sr.end();
	}

	@Override
	protected double getCost() {
		Matrix features = getFeatures();
		double cost = 0;
		for (PointType pointType : typeOrder) {
			cost += LogisticUtils.logisticCost(features, getLabels(pointType), getWeights(pointType));
		}
		return cost / typeOrder.size;
	}

	@Override
	protected double getCost(Entity point) {
		PointType type = point.get(M.point).type;
		double value = getFunction(type).fun.f(point.x, point.y);
		return LogisticUtils.logisticCost(value, classify(point, type));
	}

	private DoubleArray getLabels(PointType type){
		DoubleArray labels = new DoubleArray();
		for (int i = 0; i < points.size(); i++) {
			Entity p = points.get(i);
			labels.add(classify(p, type));
		}
		return labels;
	}

	private DoubleArray getWeights(PointType pointType) {
		LogisticBiFunction model = (LogisticBiFunction) getFunction(pointType).fun;
		return DoubleArray.with(model.th0, model.th1, model.th2);
	}

	private BiFunctionComponent getFunction(PointType type){
		for (int i = 0; i < typeOrder.size; i++) {
			if (type == typeOrder.get(i)) {
				return functions.get(i);
			}
		}
		throw new RuntimeException();
	}

	private int classify(Entity point, PointType type){
		return point.get(M.point).type == type ? 0 : 1;
	}

	@Override
	protected void addPoint(PointType type, double x, double y) {
		if (type.ordinal() >= classCount) return;
		super.addPoint(type, x, y);
	}

	@Override
	protected Array<KeyValuePair<PointType, Double>> getPredictions(double x, double y) {
		double sum = 0;
		Array<KeyValuePair<PointType, Double>> array = new Array<>();
		for (PointType pointType : typeOrder) {
			double prediction = 1 - LogisticUtils.prediction(DoubleArray.with(1, x, y), getWeights(pointType));
			array.add(new KeyValuePair<>(pointType, prediction));
			sum += prediction;
		}
		double multiplication = 1.0 / sum;
		array.foreach(pair -> pair.value *= multiplication);
		return array;
	}
}
