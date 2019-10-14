package ru.maklas.melnikov.engine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
import ru.maklas.melnikov.utils.LogisticUtils;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class ThreeClassLogisticRegressionSystem extends BaseLogisticRegressionSystem {

	private Array<BiFunctionComponent> functions;
	private Array<PointType> typeOrder;

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
		functions = new Array<>();
		typeOrder = new Array<>();
		for (int i = 0; i < 3; i++) {
			PointType type = PointType.values()[i];
			BiFunctionComponent bfc = new BiFunctionComponent(new LogisticBiFunction(1, (i - 0.67) * 3, 1)).setColor(type.getColor());
			engine.add(new Entity().add(bfc));
			functions.add(bfc);
			typeOrder.add(type);
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

}
