package ru.maklas.melnikov.engine.point;

import com.badlogic.gdx.graphics.Color;

public enum  PointType {RED, BLUE, UNKNOWN;

	public Color getColor() {
		switch (this) {
			case RED: return Color.RED;
			case BLUE: return Color.BLUE;
			default: return Color.WHITE;
		}
	}
}