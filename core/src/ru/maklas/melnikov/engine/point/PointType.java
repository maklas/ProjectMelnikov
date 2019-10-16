package ru.maklas.melnikov.engine.point;

import com.badlogic.gdx.graphics.Color;

public enum  PointType {

	RED, YELLOW, GREEN, BLUE, PURPLE, UNKNOWN;

	public Color getColor() {
		switch (this) {
			case RED: return Color.RED;
			case BLUE: return Color.BLUE;
			case GREEN: return Color.GREEN;
			case YELLOW: return Color.YELLOW;
			case PURPLE: return Color.PURPLE;
			default: return Color.WHITE;
		}
	}

	public int getClassification() {
		switch (this){
			case BLUE: return 0;
			case RED: return 1;
			case GREEN: return 2;
			case YELLOW: return 3;
			case PURPLE: return 4;
			default: throw new RuntimeException("");
		}
	}
}