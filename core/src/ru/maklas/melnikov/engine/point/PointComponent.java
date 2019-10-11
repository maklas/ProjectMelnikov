package ru.maklas.melnikov.engine.point;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.mengine.Component;

public class PointComponent implements Component {

	public PointType type = PointType.UNKNOWN;
	public Color colorOverride;

	public PointComponent() {
	}

	public PointComponent(PointType type) {
		this.type = type;
	}
}
