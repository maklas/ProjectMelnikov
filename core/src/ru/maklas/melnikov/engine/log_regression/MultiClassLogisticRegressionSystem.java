package ru.maklas.melnikov.engine.log_regression;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.input.KeyTypeEvent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
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
	private boolean drawFullGradient;

	@Override
	public void onAddedToEngine(Engine engine) {
		super.onAddedToEngine(engine);
		functions = new Array<>();
		typeOrder = new Array<>();

		classCount = MathUtils.clamp(parameters.getClassCount(), 3, 5);

		if (classCount == 3){
			typeOrder = Array.with(PointType.RED, PointType.GREEN, PointType.BLUE);
		} else if (classCount == 4) {
			typeOrder = Array.with(PointType.RED, PointType.GREEN, PointType.PURPLE, PointType.BLUE);
		} else {
			for (int i = 0; i < classCount; i++) {
				typeOrder.add(PointType.values()[i]);
			}
		}

		Vector2 vec = new Vector2(0, 1).rotate(15);
		float rotation = 360.0f / classCount;
		for (PointType type : typeOrder) {
			BiFunctionComponent bfc = new BiFunctionComponent(new LogisticBiFunction(1, vec.x, vec.y)).setColor(type.getColor());
			engine.add(new Entity().add(bfc));
			functions.add(bfc);
			vec.rotate(rotation);
		}
	}

	@Override
	protected void onKeyType(KeyTypeEvent e) {
		super.onKeyType(e);
		if (e.getCharacter() != '0') return;

		if (classCount == 5) {
			addCloud(typeOrder.get(0), 6.0, 13.0);
			addCloud(typeOrder.get(1), 18.0, 0.0);
			addCloud(typeOrder.get(2), 7.5, -19.0);
			addCloud(typeOrder.get(3), -15.0, -15.0);
			addCloud(typeOrder.get(4), -17.5, 6.0);
			reEvaluatePointCounts();
		} else if (classCount == 4) {
			addCloud(typeOrder.get(0),10.0, 10.0);
			addCloud(typeOrder.get(1),10.0, -10.0);
			addCloud(typeOrder.get(2),-10.0, -10.0);
			addCloud(typeOrder.get(3),-10.0, 10.0);
		} else if (classCount == 3) {
			addCloud(typeOrder.get(0), 0, 10);
			addCloud(typeOrder.get(1), -5, -3);
			addCloud(typeOrder.get(2), 5, -3);
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
	protected void trainForAccuracy() {
		Matrix features = getFeatures();

		for (int i = 0; i < functions.size; i++) {
			LogisticBiFunction model = (LogisticBiFunction) functions.get(i).fun;
			PointType targetType = typeOrder.get(i);

			DoubleArray labels = new DoubleArray(points.size());
			DoubleArray weights = DoubleArray.with(model.th0, model.th1, model.th2);

			for (int j = 0; j < points.size(); j++) {
				labels.add(classify(points.get(j), targetType));
			}

			double accuracy = LogisticUtils.accuracy(features, labels, weights);
			if (accuracy > 0.99) continue;

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
	public void render() {
		super.render();
		if (Gdx.input.isKeyPressed(Input.Keys.PLUS)) {
			for (BiFunctionComponent function : functions) {
				LogisticBiFunction f = (LogisticBiFunction) function.fun;
				f.th0 *= 1.04;
				f.th1 *= 1.04;
				f.th2 *= 1.04;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.MINUS)) {
			for (BiFunctionComponent function : functions) {
				LogisticBiFunction f = (LogisticBiFunction) function.fun;
				f.th0 /= 1.04;
				f.th1 /= 1.04;
				f.th2 /= 1.04;
			}
		}
	}

	@Override
	protected void drawSides() {
		sr.begin(ShapeRenderer.ShapeType.Point);
		double step = (Gdx.input.isKeyPressed(Input.Keys.U) || Gdx.input.isKeyPressed(Input.Keys.Y) || trainForAccuracy || cameraIsMoving() ? 4 : 2) * cam.zoom;

		if (drawFullGradient) {
			drawFullGradient(step);
		} else {
			drawDistinguishableGradient(step);
		}
		sr.end();
	}

	private void drawDistinguishableGradient(double step) {

		double rightX = Utils.camRightX(cam);
		double leftX = Utils.camLeftX(cam);
		double botY = Utils.camBotY(cam);
		double topY = Utils.camTopY(cam);
		double[] values = new double[functions.size];

		Color color = new Color();
		boolean shift = false;
		for (double x = leftX; x < rightX; x += step / 2) {
			shift = !shift;
			for (double y = botY + (shift ? step / 2 : 0); y < topY; y += step) {
				color.set(0, 0, 0, 1);
				double sum = 0;
				for (int i = 0; i < values.length; i++) {
					double v = LogisticUtils.sigmoid(-functions.get(i).fun.f(x, y));
					values[i] = v;
					sum += v;
				}
				double max = 0;
				double secondMax = 0;
				for (int i = 0; i < values.length; i++) {
					float percentage = (float) (values[i] / sum);
					values[i] = percentage;
					if (percentage >= max) {
						secondMax = max;
						max = percentage;
					} else if (percentage > secondMax) {
						secondMax = percentage;
					}
				}
				double purity = max - secondMax; //0..1
				if (purity < 0.005) continue;
				for (int i = 0; i < values.length; i++) {
					float coloMul = (float) (values[i] * (1 + Math.sqrt(purity)));
					Color c = functions.get(i).color;
					color.add(c.r * coloMul, c.g * coloMul, c.b * coloMul, 1);
				}
				sr.setColor(color);
				sr.point((float) x, (float) y, 0);
			}
		}
	}

	private void drawFullGradient(double step) {
		double rightX = Utils.camRightX(cam);
		double leftX = Utils.camLeftX(cam);
		double botY = Utils.camBotY(cam);
		double topY = Utils.camTopY(cam);
		double[] values = new double[functions.size];

		Color color = new Color();
		boolean shift = false;
		for (double x = leftX; x < rightX; x += step / 2) {
			shift = !shift;
			for (double y = botY + (shift ? step / 2 : 0); y < topY; y += step) {
				color.set(0, 0, 0, 1);
				//1.
				double sum = 0;
				for (int i = 0; i < values.length; i++) {
					double v = LogisticUtils.sigmoid(-functions.get(i).fun.f(x, y));
					values[i] = v;
					sum += v;
				}

				for (int i = 0; i < values.length; i++) {
					Color c = functions.get(i).color;
					float percentage = (float) (values[i] / sum);
					if (percentage > 0) {
						color.add(c.r * percentage, c.g * percentage, c.b * percentage, 1);
					}
				}
				sr.setColor(color);
				sr.point((float) x, (float) y, 0);
			}
		}
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
		if (!typeOrder.contains(type, false)) return;
		super.addPoint(type, x, y);
	}

	@Override
	protected Array<KeyValuePair<PointType, Double>> getPredictions(double x, double y) {
		DoubleArray features = DoubleArray.with(1, x, y);
		double sum = 0;
		Array<KeyValuePair<PointType, Double>> array = new Array<>();

		for (PointType pointType : typeOrder) {
			double prediction = LogisticUtils.prediction(features, getWeights(pointType).negate());
			array.add(new KeyValuePair<>(pointType, prediction));
			sum += prediction;
		}
		double multiplication = 1.0 / sum;
		array.foreach(pair -> pair.value *= multiplication);
		return array;
	}

	public void setDrawFullGradient(boolean enabled) {
		this.drawFullGradient = enabled;
	}
}
