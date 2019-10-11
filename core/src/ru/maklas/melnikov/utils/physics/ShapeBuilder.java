package ru.maklas.melnikov.utils.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

/** Created by maklas on 17.06.2017. **/

public class ShapeBuilder {

    float scale;
    private Vector2[] squareVerts;


    public ShapeBuilder(float scale) {
        this.scale = scale;
        squareVerts = new Vector2[4];

        for (int i = 0; i < 4; i++) {
            squareVerts[i] = new Vector2();
        }
    }

    /** Center is left-bottom **/
    public PolygonShape buildRectangle(float x, float y, float width, float height){
        x /= scale;
        y /= scale;
        width /= scale;
        height /= scale;

        squareVerts[0].set(x, y);
        squareVerts[1].set(x + width, y);
        squareVerts[2].set(x + width, y + height);
        squareVerts[3].set(x, y + height);

        PolygonShape poly = new PolygonShape();
        poly.set(squareVerts);

        return poly;
    }

    /** Center is in the middle of a brick **/
    public PolygonShape buildRectangleCentered(float x, float y, float width, float height){
        x /= scale;
        y /= scale;
        float w2 = (width / 2) / scale;
        float h2 = (height / 2) / scale;

        squareVerts[0].set(x - w2, y - h2);
        squareVerts[1].set(x + w2, y - h2);
        squareVerts[2].set(x + w2, y + h2);
        squareVerts[3].set(x - w2, y + h2);

        PolygonShape poly = new PolygonShape();
        poly.set(squareVerts);

        return poly;
    }

    private final Vector2 tempVec = new Vector2();

    public CircleShape buildCircle(float x, float y, float radius){
        CircleShape circle = new CircleShape();
        circle.setRadius(radius/scale);
        circle.setPosition(tempVec.set(x/scale, y/scale));
        return circle;
    }

    public Shape buildRectanglePivot(float width, float height, float pivotX, float pivotY) {
        width /= scale;
        height /= scale;
        Vector2[] squareVerts = this.squareVerts;

        squareVerts[0].set(-width * pivotX, -height * pivotY);
        squareVerts[1].set(width * (1f - pivotX), -height * pivotY);
        squareVerts[2].set(width * (1f - pivotX), height * (1f - pivotY));
        squareVerts[3].set(-width * pivotX, height * (1f - pivotY));

        PolygonShape shape = new PolygonShape();
        shape.set(squareVerts);
        return shape;
    }

    public Shape buildRectanglePivotAndOffset(float width, float height, float pivotX, float pivotY, float offsetX, float offsetY) {
        width /= scale;
        height /= scale;
        offsetX /= scale;
        offsetY /= scale;
        Vector2[] squareVerts = this.squareVerts;

        squareVerts[0].set(-width * pivotX + offsetX, -height * pivotY + offsetY);
        squareVerts[1].set(width * (1f - pivotX) + offsetX, -height * pivotY + offsetY);
        squareVerts[2].set(width * (1f - pivotX) + offsetX, height * (1f - pivotY) + offsetY);
        squareVerts[3].set(-width * pivotX + offsetX, height * (1f - pivotY) + offsetY);

        PolygonShape shape = new PolygonShape();
        shape.set(squareVerts);
        return shape;
    }
}
