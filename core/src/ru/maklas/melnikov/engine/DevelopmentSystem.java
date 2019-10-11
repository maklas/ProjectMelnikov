package ru.maklas.melnikov.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.WindowedMean;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.input.TouchUpEvent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
import ru.maklas.melnikov.utils.LogisticUtils;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.EntitySystem;

public class DevelopmentSystem extends EntitySystem {

	private ImmutableArray<Entity> points;
	private ImmutableArray<Entity> biFunctions;

	@Override
	public void onAddedToEngine(Engine engine) {
		points = entitiesFor(PointComponent.class);
		subscribe(TouchUpEvent.class, this::onTouch);
		biFunctions = entitiesFor(BiFunctionComponent.class);
	}

	private void onTouch(TouchUpEvent e) {
		engine.add(new Entity(e.getX(), e.getY(), 0).add(new PointComponent(e.getButton() == Input.Buttons.LEFT ? PointType.BLUE : PointType.RED)));

		Array<NormalizedData> data = normalize(points);

		for (Entity biFunction : biFunctions) {
			BiFunctionComponent bf = biFunction.get(M.biFun);
			double costSum = 0;
			if (bf.fun instanceof LogisticBiFunction){
				LogisticBiFunction fun = (LogisticBiFunction) bf.fun;

				for (NormalizedData datum : data) {
					double val = fun.f(datum.x1, datum.x2);
					double cost = LogisticUtils.logisticCost(val, datum.type);
					costSum += cost;
				}
			}
			double totalCost = costSum / data.size;
			System.out.println(bf.name + ": " + totalCost);
		}

	}

	private Array<NormalizedData> normalize(ImmutableArray<Entity> points) {
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
			double newX = (point.x - xMean) / xStd;
			double newY = (point.y - yMean) / yStd;
			normalizedData.add(new NormalizedData(newX, newY, point.get(M.point).type.getClassification()));
		}

		return normalizedData;
	}

	@Override
	public void update(float dt) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
			points.foreach(engine::removeLater);
		}

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
