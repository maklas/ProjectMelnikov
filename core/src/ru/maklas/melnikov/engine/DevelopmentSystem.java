package ru.maklas.melnikov.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.assets.ImageAssets;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.input.TouchUpEvent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.functions.bi_functions.GraphBiFunction;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
import ru.maklas.melnikov.utils.LogisticUtils;
import ru.maklas.melnikov.utils.StringUtils;
import ru.maklas.melnikov.utils.Utils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.RenderEntitySystem;

public class DevelopmentSystem extends RenderEntitySystem {

	private ImmutableArray<Entity> points;
	private ImmutableArray<Entity> biFunctions;
	private Batch batch;
	private Array<NormalizedData> normalizedData;
	private OrthographicCamera cam;

	@Override
	public void onAddedToEngine(Engine engine) {
		points = entitiesFor(PointComponent.class);
		subscribe(TouchUpEvent.class, this::onTouch);
		biFunctions = entitiesFor(BiFunctionComponent.class);
		batch = engine.getBundler().get(B.batch);
		cam = engine.getBundler().get(B.cam);
	}

	private void onTouch(TouchUpEvent e) {
		engine.add(new Entity(e.getX(), e.getY(), 0).add(new PointComponent(e.getButton() == Input.Buttons.LEFT ? PointType.BLUE : PointType.RED)));

	}

	private double getCost(){
		normalizedData = normalize(points);
		for (Entity biFunction : biFunctions) {
			BiFunctionComponent bf = biFunction.get(M.biFun);
			double costSum = 0;
			if (bf.fun instanceof LogisticBiFunction) {
				LogisticBiFunction fun = (LogisticBiFunction) bf.fun;

				for (NormalizedData datum : normalizedData) {
					double val = fun.f(datum.x1, datum.x2);
					double cost = LogisticUtils.logisticCost(val, datum.type);
					costSum += cost;
				}
			}
			double totalCost = costSum / normalizedData.size;
			return totalCost;
		}
		return 0;
	}

	private Array<NormalizedData> normalize(ImmutableArray<Entity> points) {
		final boolean applyNormalization = false;
		Array<NormalizedData> normalizedData = new Array<>(points.size());
		WindowedMean xwm = new WindowedMean(points.size());
		WindowedMean ywm = new WindowedMean(points.size());
		for (Entity point : points) {
			xwm.addValue(point.x);
			ywm.addValue(point.y);
		}

		float xMean = xwm.getMean();
		float xStd = xwm.standardDeviation();
		float yMean = ywm.getMean();
		float yStd = ywm.standardDeviation();
		for (int i = 0; i < points.size(); i++) {
			Entity point = points.get(i);
			double newX = applyNormalization ? (point.x - xMean) / xStd : point.x;
			double newY = applyNormalization ? (point.y - yMean) / yStd : point.y;
			normalizedData.add(new NormalizedData(newX, newY, point.get(M.point).type.getClassification()));
		}

		return normalizedData;
	}

	@Override
	public void render() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			points.cpyArray().foreach(engine::removeLater);
			normalizedData = null;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.T) || Gdx.input.isKeyPressed(Input.Keys.Y)) {
			train();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.U)){
			long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 16){
				train();
			}
		}

		//draw points and costs for each
		if (normalizedData != null && false) {
			batch.setColor(Color.GRAY);
			batch.begin();

			for (NormalizedData data : normalizedData) {
				ImageAssets.draw(batch, A.images.circle, ((float) data.x1), ((float) data.x2), 0.5f, 0.5f, 0.5f * cam.zoom, 0.5f * cam.zoom, 0);
			}

			A.images.font.setColor(Color.BLACK);
			if (biFunctions.size() > 0 ) {
				GraphBiFunction biFun = biFunctions.get(0).get(M.biFun).fun;
				for (NormalizedData data : normalizedData) {
					double val = biFun.f(data.x1, data.x2);
					String value = StringUtils.df(val, 2);
					String cost = StringUtils.df(LogisticUtils.logisticCost(val, data.type), 4);
					A.images.font.draw(batch, value + " | " + cost, ((float) data.x1), ((float) data.x2), 10, Align.left, false);
				}

			}


			batch.end();
		}

		boolean printCost = true;

		if (printCost) {
			A.images.font.setColor(Color.RED);
			batch.begin();
			float x = Utils.camLeftX(cam);
			float y = Utils.camTopY(cam) - 15 * cam.zoom;
			A.images.font.draw(batch, "Cost: " + StringUtils.df(getCost(), 4), x, y, 10, Align.left, false);
			batch.end();
		}

	}

	private void train() {
		if (biFunctions.size() < 1) return;
		if (points.size() < 3) return;
		GraphBiFunction biFun = biFunctions.get(0).get(M.biFun).fun;
		if (!(biFun instanceof LogisticBiFunction)) return;
		LogisticBiFunction model = (LogisticBiFunction) biFun;

		Array<NormalizedData> data = normalize(points);
		double[][] features = new double[points.size()][2];
		int[] labels = new int[points.size()];
		double[] weights = new double[]{model.th0, model.th1, model.th2};

		for (int i = 0; i < features.length; i++) {
			NormalizedData d = data.get(i);
			features[i] = new double[]{d.x1, d.x2};
			labels[i] = d.type;
		}

		double[] weightAdjustments = LogisticUtils.gradientDescent(features, labels, weights, 0.1);
		model.th0 -= weightAdjustments[0];
		model.th1 -= weightAdjustments[1];
		model.th2 -= weightAdjustments[2];
	}

	private class NormalizedData {
		double x1;
		double x2;
		int type;

		public NormalizedData(double x1, double x2, int type) {
			this.x1 = x1;
			this.x2 = x2;
			this.type = type;
		}

	}
}
