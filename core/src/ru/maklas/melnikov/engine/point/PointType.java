package ru.maklas.melnikov.engine.point;

import com.badlogic.gdx.graphics.Color;

public enum  PointType {

	BLUE, RED, GREEN, PURPLE, BLACK, UNKNOWN;

	public Color getColor() {
		switch (this) {
			case RED: return Color.RED;
			case BLUE: return Color.BLUE;
			case GREEN: return Color.GREEN;
			case PURPLE: return Color.PURPLE;
			case BLACK: return Color.BLACK;
			default: return Color.WHITE;
		}
	}

	public int getClassification() {
		switch (this){
			case BLUE: return 0;
			case RED: return 1;
			case GREEN: return 2;
			case PURPLE: return 3;
			case BLACK: return 4;
			default: throw new RuntimeException("");
		}
	}
}