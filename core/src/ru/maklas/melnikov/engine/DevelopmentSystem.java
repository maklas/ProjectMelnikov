package ru.maklas.melnikov.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.ImageAssets;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.functions.FunctionComponent;
import ru.maklas.melnikov.engine.input.TouchUpEvent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.engine.rendering.YScalable;
import ru.maklas.melnikov.functions.FunctionFromFloats;
import ru.maklas.melnikov.functions.GraphFunction;
import ru.maklas.melnikov.functions.bi_functions.GraphBiFunction;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
import ru.maklas.melnikov.states.FunctionGraphState;
import ru.maklas.melnikov.states.Parameters;
import ru.maklas.melnikov.utils.LogisticUtils;
import ru.maklas.melnikov.utils.StringUtils;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.melnikov.utils.gsm_lib.GSMPush;
import ru.maklas.melnikov.utils.gsm_lib.State;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.RenderEntitySystem;

import java.util.Random;

public class DevelopmentSystem extends RenderEntitySystem implements YScalable {

	private ImmutableArray<Entity> points;
	private ImmutableArray<Entity> biFunctions;
	private Batch batch;
	private OrthographicCamera cam;
	private Parameters parameters;
	private int iteration = 0;
	private FloatArray costHistory = new FloatArray();
	private double yScale = 1;
	private ShapeRenderer sr;

	@Override
	public void onAddedToEngine(Engine engine) {
		points = entitiesFor(PointComponent.class);
		subscribe(TouchUpEvent.class, this::onTouch);
		biFunctions = entitiesFor(BiFunctionComponent.class);
		batch = engine.getBundler().get(B.batch);
		sr = engine.getBundler().get(B.sr);
		cam = engine.getBundler().get(B.cam);
		parameters = engine.getBundler().get(B.params);
	}

	private void onTouch(TouchUpEvent e) {
		switch (parameters.getMode()) {
			case POINT:
				engine.add(new Entity(e.getX(), e.getY(), 0).add(new PointComponent(e.getButton() == Input.Buttons.LEFT ? PointType.BLUE : PointType.RED)));
				break;
			case CLOUD:
				Random random = new Random();
				double diameter = parameters.getCloudRadius() * 2 * cam.zoom;
				for (int i = 0; i < parameters.getCloudSize(); i++) {
					double x = e.getX() + (random.nextGaussian() - 0.5) * diameter;
					double y = e.getY() + (random.nextGaussian() - 0.5) * diameter;
					engine.add(new Entity(((float) x), ((float) y), 0).add(new PointComponent(e.getButton() == Input.Buttons.LEFT ? PointType.BLUE : PointType.RED)));
				}
				break;
		}
	}

	private double getCost() {
		return LogisticUtils.logisticCost(getFeatures(), getLabels(), getWeights());
	}

	private double getAccuracy() {
		return LogisticUtils.accuracy(getFeatures(), getLabels(), getWeights());
	}

	@Override
	public void render() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			points.cpyArray().foreach(engine::removeLater);
			iteration = 0;
			costHistory.clear();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			State state = getEngine().getBundler().get(B.gsmState);
			State newState = new FunctionGraphState(Array.with(new Entity().add(new FunctionComponent(new FunctionFromFloats(costHistory)).color(Color.BLACK))), new Parameters());
			state.getGsm().setCommand(new GSMPush(state, newState, true, true));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.T) || Gdx.input.isKeyPressed(Input.Keys.Y)) {
			train();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.U)) {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 16) {
				train();
			}
		}

		drawSides();
		drawPoints();
		drawCostsForPoints();
		drawIterationAndCost();
	}

	private void drawSides() {
		GraphBiFunction f = getFunction();
		if (f == null) return;

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

	private void drawPoints() {
		batch.begin();

		for (Entity point : points) {
			PointComponent pp = point.get(M.point);
			Color color = pp.colorOverride != null ? pp.colorOverride : pp.type.getColor();
			batch.setColor(color);
			float scale = 0.25f * cam.zoom;
			float x = point.x;
			float y = (float) (point.y / yScale);
			ImageAssets.draw(batch, A.images.circle, x, y, 0.5f, 0.5f, scale, scale, 0);
		}

		batch.end();
	}

	private void drawCostsForPoints() {
		if (points.size() == 0) return;
		GraphBiFunction f = getFunction();
		if (f == null) return;
		Vector2 mouse = Utils.getMouse(cam);
		Entity closestPoint = points.first();
		float minDst2 = mouse.dst2(closestPoint.x, closestPoint.y);
		final float range2 = (float) Math.pow(5 * cam.zoom, 2);

		for (int i = 1; i < points.size(); i++) {
			Entity p = points.get(i);
			float dst2 = mouse.dst2(p.x, p.y);
			if (dst2 < minDst2){
				closestPoint = p;
				minDst2 = dst2;
			}
		}

		if (minDst2 > range2) return;

		A.images.font.setColor(closestPoint.get(M.point).getColor());
		batch.begin();
		Vector2 pos = Utils.vec1.set(closestPoint.x, closestPoint.y);
		double value = LogisticUtils.sigmoid(f.f(closestPoint.x, closestPoint.y));
		double cost = LogisticUtils.logisticCost(value, closestPoint.get(M.point).type.getClassification());
		A.images.font.draw(batch, StringUtils.vec(pos, 2) + " | " + StringUtils.dfOpt(cost, 4), pos.x, pos.y, 10, Align.left, false);
		batch.end();
	}

	private GraphBiFunction getFunction(){
		if (biFunctions.size() == 0) return null;
		return biFunctions.get(0).get(M.biFun).fun;
	}

	private void drawIterationAndCost() {
		A.images.font.setColor(Color.RED);
		batch.begin();
		float x = Utils.camLeftX(cam);
		float y = Utils.camTopY(cam) - 15 * cam.zoom;
		A.images.font.draw(batch, "Iteration: " + iteration, x, y, 10, Align.left, false);
		y -= 20 * cam.zoom;
		A.images.font.draw(batch, "Cost: " + StringUtils.dfOpt(getCost(), 10), x, y, 10, Align.left, false);
		y -= 20 * cam.zoom;
		A.images.font.draw(batch, "Accuracy: " + StringUtils.dfOpt(getAccuracy() * 100, 3), x, y, 10, Align.left, false);
		y -= 20 * cam.zoom;
		A.images.font.draw(batch, "LR: " + StringUtils.dfOpt(parameters.getLearningRate(), 10), x, y, 10, Align.left, false);
		batch.end();
	}

	private void train() {
		if (biFunctions.size() < 1) return;
		if (points.size() < 3) return;
		GraphBiFunction biFun = biFunctions.get(0).get(M.biFun).fun;
		if (!(biFun instanceof LogisticBiFunction)) return;
		LogisticBiFunction model = (LogisticBiFunction) biFun;

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
		costHistory.add((float) getCost());
	}

	private Matrix getFeatures(){
		Matrix features = new Matrix();
		for (int i = 0; i < points.size(); i++) {
			Entity p = points.get(i);
			features.addRow(DoubleArray.with(1, p.x, p.y));
		}
		return features;
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
		if (biFunctions.size() < 1) return null;
		GraphBiFunction biFun = biFunctions.get(0).get(M.biFun).fun;
		if (!(biFun instanceof LogisticBiFunction)) return null;
		LogisticBiFunction model = (LogisticBiFunction) biFun;
		return DoubleArray.with(model.th0, model.th1, model.th2);
	}

	@Override
	public EntitySystem setYScale(double yScale) {
		this.yScale = yScale;
		return this;
	}
}
