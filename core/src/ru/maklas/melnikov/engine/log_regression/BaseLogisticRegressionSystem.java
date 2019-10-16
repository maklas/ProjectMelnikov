package ru.maklas.melnikov.engine.log_regression;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.ImageAssets;
import ru.maklas.melnikov.engine.B;
import ru.maklas.melnikov.engine.M;
import ru.maklas.melnikov.engine.input.KeyTypeEvent;
import ru.maklas.melnikov.engine.input.TouchUpEvent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.engine.rendering.CameraComponent;
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

import java.util.EnumMap;
import java.util.Map;
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
	protected EnumMap<PointType, Integer> pointCounts;
	protected boolean drawGradient = true;

	@Override
	public void onAddedToEngine(Engine engine) {
		pointCounts = new EnumMap<>(PointType.class);
		points = entitiesFor(PointComponent.class);
		subscribe(TouchUpEvent.class, this::onTouch);
		subscribe(KeyTypeEvent.class, this::onKeyType);
		batch = engine.getBundler().get(B.batch);
		sr = engine.getBundler().get(B.sr);
		cam = engine.getBundler().get(B.cam);
		parameters = engine.getBundler().get(B.params);
	}

	protected void onKeyType(KeyTypeEvent e) {
		char character = e.getCharacter();
		if (!Character.isDigit(character)){
			return;
		}
		int typeId = Character.digit(character, 10);
		if (typeId < 1 || typeId >= PointType.values().length) return;

		Vector2 mouse = Utils.getMouse(cam);
		PointType type = PointType.values()[typeId - 1];

		switch (parameters.getMode()) {
			case POINT:
			case CIRCLE:
				addPoint(type, mouse.x, mouse.y);
				break;
			case CLOUD:
			case MULTIPLE:
				addCloud(type, mouse.x, mouse.y);
				break;
		}
		reEvaluatePointCounts();
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
			case CIRCLE:
				addPoint(type, e.getX(), e.getY());
				break;
			case CLOUD:
			case MULTIPLE:
				addCloud(type, e.getX(), e.getY());
				break;
		}
		reEvaluatePointCounts();
	}

	@Override
	public EntitySystem setYScale(double yScale) {
		this.yScale = yScale;
		return this;
	}

	protected void trainForAccuracy() {
		train();
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
		A.images.font.getData().setScale(cam.zoom);
		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			points.cpyArray().foreach(engine::removeLater);
			iteration = 0;
			reEvaluatePointCounts();
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
			trainForAccuracy = !trainForAccuracy;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.T) || Gdx.input.isKeyPressed(Input.Keys.Y)) {
			train();
		}

		double accuracy = getAccuracy();
		if (trainForAccuracy && accuracy >= 0.99){
			trainForAccuracy = false;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.U)) {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 16) {
				train();
			}
		} else if (trainForAccuracy && getAccuracy() < 0.99) {
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 16) {
				trainForAccuracy();
			}
		}

		if (drawGradient) {
			drawSides();
		}
		drawPoints();
		drawCostsForPoints();
		drawInfo();
		if (UIUtils.shift()){
			drawPrediction();
		}
	}

	protected final boolean cameraIsMoving(){
		ImmutableArray<Entity> cameras = entitiesFor(CameraComponent.class);
		if (cameras.size() == 0) return false;
		CameraComponent cc = cameras.get(0).get(M.camera);
		return cc.vX != 0f || cc.vY != 0f;
	}

	protected void drawPrediction(){
		Vector2 mouse = Utils.getMouse(cam);
		float x = mouse.x;
		float y = mouse.y;
		Array<KeyValuePair<PointType, Double>> predictions = getPredictions(x, y);
		if (predictions == null || predictions.size == 0) return;
		predictions.filter(p -> p.value > 0.001).sort(Utils.reverseComparator(Utils.comparingDouble(KeyValuePair::getValue)));

		batch.begin();
		A.images.font.setColor(Color.BLACK);
		A.images.font.draw(batch, "(" + StringUtils.ff(x, 2) + ", " + StringUtils.ff(y, 2) + ")", x, y + 10 * cam.zoom, 10, Align.left, false);
		y -= 20 * cam.zoom;
		for (KeyValuePair<PointType, Double> prediction : predictions) {
			A.images.font.setColor(prediction.key.getColor());
			A.images.font.draw(batch, StringUtils.df(prediction.value * 100, 2) + "%", x, y, 10, Align.left, false);
			y -= 20 * cam.zoom;
		}
		batch.end();
	}

	/** No need to sort. 0..1 **/
	protected Array<KeyValuePair<PointType, Double>> getPredictions(double x, double y) {
		return null;
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

	private final Color infoBackground = Color.LIGHT_GRAY.cpy().sub(0, 0, 0, 0.3f);
	private void drawInfo() {
		float x = Utils.camLeftX(cam);
		float y = Utils.camTopY(cam);
		sr.setColor(infoBackground);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.rect(x, y, 160 * cam.zoom, - (110 + pointCounts.size() * 20) * cam.zoom);
		sr.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);


		A.images.font.setColor(Color.RED);
		batch.begin();
		y -= 15 * cam.zoom;
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
		for (Map.Entry<PointType, Integer> e : pointCounts.entrySet()) {
			y -= 20 * cam.zoom;
			A.images.font.setColor(e.getKey().getColor());
			A.images.font.draw(batch, String.valueOf(e.getValue()), x, y, 10, Align.left, false);
		}

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

	protected void addPoint(PointType type, double x, double y){
		engine.add(new Entity((float) x, (float) y, 0).add(new PointComponent(type)));
	}

	protected void addCloud(PointType type, double x, double y){
		Random random = new Random();
		double diameter = parameters.getCloudRadius() * 2 * cam.zoom;
		for (int i = 0; i < parameters.getCloudSize(); i++) {
			double posX = x + (random.nextGaussian() - 0.5) * diameter;
			double posY = y + (random.nextGaussian() - 0.5) * diameter;
			addPoint(type, posX, posY);
		}
	}

	protected void reEvaluatePointCounts() {
		pointCounts.clear();
		for (Entity point : points) {
			PointType type = point.get(M.point).type;
			Integer count = pointCounts.get(type);
			if (count == null){
				count = 1;
			} else {
				count++;
			}
			pointCounts.put(type, count);
		}
	}

	public void setDrawGradient(boolean bool) {
		this.drawGradient = bool;
	}

	protected class KeyValuePair<K, V> {

		K key;
		V value;

		public KeyValuePair() {

		}

		public KeyValuePair(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public void setKey(K key) {
			this.key = key;
		}

		public V getValue() {
			return value;
		}

		public void setValue(V value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "(" + key + ", " + value + ")";
		}
	}

	//0.01 -> 4110
	//0.001 -> 14110
}
