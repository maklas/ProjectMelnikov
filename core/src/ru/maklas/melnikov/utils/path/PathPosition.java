package ru.maklas.melnikov.utils.path;

import com.badlogic.gdx.math.Vector2;

public class PathPosition {

    int pointId;
    float distance;
    boolean finished = false;

    float x;
    float y;
    float angle;



    public int getPointId() {
        return pointId;
    }

    public float getDistance() {
        return distance;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void set(Vector2 pos) {
        x = pos.x;
        y = pos.y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isFinished() {
        return finished;
    }
}
