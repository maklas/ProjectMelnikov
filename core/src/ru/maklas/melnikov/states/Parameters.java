package ru.maklas.melnikov.states;

public class Parameters {

	public enum Mode {POINT, CLOUD}

	private Mode mode = Mode.CLOUD;
	private double cloudRadius = 7.5;
	private int cloudSize = 100;
	private double learningRate = 0.01;

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public double getCloudRadius() {
		return cloudRadius;
	}

	public void setCloudRadius(double cloudRadius) {
		this.cloudRadius = cloudRadius;
	}

	public int getCloudSize() {
		return cloudSize;
	}

	public void setCloudSize(int cloudSize) {
		this.cloudSize = cloudSize;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
}
