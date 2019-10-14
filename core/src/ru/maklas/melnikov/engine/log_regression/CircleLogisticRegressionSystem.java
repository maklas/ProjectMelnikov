package ru.maklas.melnikov.engine.log_regression;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.functions.bi_functions.CircleBiFunction;
import ru.maklas.melnikov.functions.bi_functions.GraphBiFunction;
import ru.maklas.melnikov.utils.LogisticUtils;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

public class CircleLogisticRegressionSystem extends BaseLogisticRegressionSystem {

	private BiFunctionComponent bfc;

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
		bfc = new BiFunctionComponent(new CircleBiFunction(1, 1, 1)).setColor(Color.BLACK);
		engine.add(new Entity().add(bfc));
	}

	@Override
	protected void train() {
		if (points.size() < 2) return;
		CircleBiFunction model = (CircleBiFunction) bfc.fun;

		Matrix features = new Matrix();
		DoubleArray labels = new DoubleArray(points.size());
		DoubleArray weights = DoubleArray.with(model.th0, model.th1, model.th2);

		for (int i = 0; i < points.size(); i++) {
			Entity p = points.get(i);
			features.addRow(DoubleArray.with(1, p.x, p.y));
			labels.add(p.get(M.point).type.getClassification());
		}

		DoubleArray weightAdjustments = LogisticUtils.gradientDescent(features, labels, weights, 0.1);
		model.th0 -= weightAdjustments.get(0);
		model.th1 -= weightAdjustments.get(1);
		model.th2 -= weightAdjustments.get(2);
		iteration++;
	}

	@Override
	protected double getAccuracy() {
		return LogisticUtils.accuracy(getFeatures(), getLabels(), getWeights());
	}

	@Override
	protected void drawSides() {
		GraphBiFunction f = bfc.fun;
		sr.begin(ShapeRenderer.ShapeType.Point);

		double rightX = Utils.camRightX(cam);
		double leftX = Utils.camLeftX(cam);
		double botY = Utils.camBotY(cam);
		double topY = Utils.camTopY(cam);
		double step = 3 * cam.zoom;

		for (double x = leftX; x < rightX; x += step) {
			for (double y = botY; y < topY; y += step) {
				double val = f.f(x, y);
				sr.setColor(val > 0 ? Color.RED : Color.BLUE);
				sr.point((float) x, (float) y, 0);
			}
		}
		sr.end();
	}

	@Override
	protected double getCost() {
		return LogisticUtils.logisticCost(getFeatures(), getLabels(), getWeights());
	}

	@Override
	protected double getCost(Entity point) {
		double value = LogisticUtils.sigmoidNoInfinity(bfc.fun.f(point.x, point.y));
		int target = point.get(M.point).type.getClassification();
		return LogisticUtils.logisticCost(value, target);
	}

	private DoubleArray getLabels(){
		DoubleArray labels = new DoubleArray();
		for (int i = 0; i < points.size(); i++) {
			Entity p = points.get(i);
			labels.add(p.get(M.point).type.getClassification());
		}
		return labels;
	}

	private DoubleArray getWeights(){
		CircleBiFunction model = (CircleBiFunction) bfc.fun;
		return DoubleArray.with(model.th0, model.th1, model.th2);
	}
}
