package ru.maklas.melnikov.utils.path;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class LinePath {

    private Array<Vector2> points = new Array<>();
    private Vector2 tempVec = new Vector2();

    public void add(float x, float y){
        points.add(new Vector2(x, y));
    }

    public void add(Vector2 point){
        points.add(point.cpy());
    }

    public Array<Vector2> getPoints() {
        return points;
    }

    public PathPosition move(PathPosition pos, float distance){
        if (pos.pointId >= points.size - 1){
            setToLastAndFinish(pos);
            return pos;
        }

        Vector2 from = points.get(pos.pointId);
        Vector2 to   = points.get(pos.pointId + 1);
        float currentProgress = pos.distance + distance;
        int point = pos.pointId;

        float dst = from.dst(to);
        while (currentProgress > dst){
            point++;
            if (point == points.size - 1){
                setToLastAndFinish(pos);
                return pos;
            }
            currentProgress -= dst;
            from = points.get(point);
            to = points.get(point + 1);
            dst = from.dst(to);
        }

        pos.pointId = point;
        pos.distance = currentProgress;
        pos.finished = false;
        recalculateCoordinates(from, to, pos);
        return pos;
    }

    public void set(PathPosition pos, int point, float distance){
        if (point >= points.size - 1){
            setToLastAndFinish(pos);
        } else {
            pos.distance = distance;
            recalculateCoordinates(points.get(point), points.get(point + 1), pos);
        }
    }

    private void setToLastAndFinish(PathPosition pp){
        pp.pointId = points.size - 1;
        Vector2 pos = points.get(pp.pointId);
        pp.set(pos);
        Vector2 prevPoint = points.get(pp.pointId - 1);
        pp.angle = Vector2.angle(pos.x - prevPoint.x, pos.y - prevPoint.y);
        prevPoint.angle(pos);
        pp.finished = true;
        pp.distance = 0;
    }

    private void recalculateCoordinates(Vector2 from, Vector2 to, PathPosition pos){
        Vector2 dir = tempVec.set(to).sub(from).setLength(pos.distance);
        pos.angle = dir.angle();
        pos.set(dir.add(from));
    }

    public void draw(ShapeRenderer sr, Color lineColor, Color pointColor){
        Color oldColor = sr.getColor();
        sr.setColor(lineColor);
        for (int i = 0; i < points.size - 1; i++) {
            sr.line(points.get(i), points.get(i + 1));
        }
        sr.setColor(pointColor);
        for (Vector2 point : points) {
            sr.circle(point.x, point.y, 3);
        }
        sr.setColor(oldColor);
    }
}
