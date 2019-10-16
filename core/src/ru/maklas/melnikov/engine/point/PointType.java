package ru.maklas.melnikov.engine.point;

import com.badlogic.gdx.graphics.Color;

public enum  PointType {

	RED, YELLOW, GREEN, CYAN, BLUE, PURPLE, UNKNOWN;

	public Color getColor() {
		switch (this) {
			case RED: return Color.RED;
			case YELLOW: return Color.YELLOW;
			case GREEN: return Color.GREEN;
			case CYAN: return Color.CYAN;
			case BLUE: return Color.BLUE;
			case PURPLE: return Color.PURPLE;
			default: return Color.WHITE;
		}
	}

	public int getClassification() {
		switch (this){
			case RED: return 0;
			case YELLOW: return 1;
			case GREEN: return 2;
			case CYAN: return 3;
			case BLUE: return 4;
			case PURPLE: return 5;
			default: throw new RuntimeException("");
		}
	}
}