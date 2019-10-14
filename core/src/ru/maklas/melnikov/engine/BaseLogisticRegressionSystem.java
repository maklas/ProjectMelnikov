package ru.maklas.melnikov.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.ImageAssets;
import ru.maklas.melnikov.engine.input.TouchUpEvent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.engine.rendering.YScalable;
import ru.maklas.melnikov.states.Parameters;
import ru.maklas.melnikov.utils.StringUtils;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.melnikov.utils.math.DoubleArray;
import ru.maklas.melnikov.utils.math.Matrix;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.RenderEntitySystem;

import java.util.Random;

public abstract class BaseLogisticRegressionSystem extends RenderEntitySystem implements YScalable {

	protected ImmutableArray<Entity> points;
	protected Batch batch;
	protected OrthographicCamera cam;
	protected Parameters parameters;
	protected int iteration = 0;
	protected double yScale = 1;
	protected ShapeRenderer sr;
	protected boolean trainForAccuracy = false;

	@Override
	public void onAddedToEngine(Engine engine) {
		points = entitiesFor(PointComponent.class);
		subscribe(TouchUpEvent.class, this::onTouch);
		batch = engine.getBundler().get(B.batch);
		sr = engine.getBundler().get(B.sr);
		cam = engine.getBundler().get(B.cam);
		parameters = engine.getBundler().get(B.params);
	}

	private void onTouch(TouchUpEvent e) {
		PointType type = e.getButton() == Input.Buttons.LEFT
				? PointType.BLUE
				: (e.getButton() == Input.Buttons.RIGHT
				? PointType.RED : PointType.GREEN);
		if (type == PointType.GREEN && parameters.getMode() != Parameters.Mode.MULTIPLE){
			return;
		}

		switch (parameters.getMode()) {
			case POINT:
				engine.add(new Entity(e.getX(), e.getY(), 0).add(new PointComponent(type)));
				break;
			case CLOUD:
			case MULTIPLE:
				Random random = new Random();
				double diameter = parameters.getCloudRadius() * 2 * cam.zoom;
				for (int i = 0; i < parameters.getCloudSize(); i++) {
					double x = e.getX() + (random.nextGaussian() - 0.5) * diameter;
					double y = e.getY() + (random.nextGaussian() - 0.5) * diameter;
					engine.add(new Entity(((float) x), ((float) y), 0).add(new PointComponent(type)));
				}
				break;
		}
	}

	@Override
	public EntitySystem setYScale(double yScale) {
		this.yScale = yScale;
		return this;
	}

	protected abstract void train();

	protected abstract double getAccuracy();

	/** translucent marks of separated lines **/
	protected abstract void drawSides();

	/** average cost for all points **/
	protected abstract double getCost();

	/** Cost for specific point **/
	protected abstract double getCost(Entity point);

	@Override
	public void render() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			points.cpyArray().foreach(engine::removeLater);
			iteration = 0;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			trainForAccuracy = !trainForAccuracy;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.T) || Gdx.input.isKeyPressed(Input.Keys.Y)) {
			train();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.U) || (trainForAccuracy && getAccuracy() < 0.99 )) {
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
		double cost = getCost(closestPoint);
		A.images.font.draw(batch, StringUtils.vec(pos, 2) + " | " + StringUtils.dfOpt(cost, 4), pos.x, pos.y, 10, Align.left, false);
		batch.end();
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
		y -= 20 * cam.zoom;
		A.images.font.setColor(trainForAccuracy ? Color.GREEN : Color.RED);
		A.images.font.draw(batch, "TFA: " + trainForAccuracy, x, y, 10, Align.left, false);
		batch.end();
	}

	protected Matrix getFeatures(){
		Matrix features = new Matrix();
		for (int i = 0; i < points.size(); i++) {
			Entity p = points.get(i);
			features.addRow(DoubleArray.with(1, p.x, p.y));
		}
		return features;
	}
}
