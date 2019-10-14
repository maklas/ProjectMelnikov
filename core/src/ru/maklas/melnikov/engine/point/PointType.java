package ru.maklas.melnikov.engine.point;

import com.badlogic.gdx.graphics.Color;

public enum  PointType {

	BLUE, RED, GREEN, UNKNOWN;

	public Color getColor() {
		switch (this) {
			case RED: return Color.RED;
			case BLUE: return Color.BLUE;
			case GREEN: return Color.GREEN;
			default: return Color.WHITE;
		}
	}

	public int getClassification() {
		switch (this){
			case BLUE: return 0;
			case RED: return 1;
			case GREEN: return 2;
			default: throw new RuntimeException("");
		}
	}
}