package ru.maklas.melnikov.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import ru.maklas.melnikov.assets.A;
import ru.maklas.melnikov.engine.rendering.PolygonUnit;
import ru.maklas.melnikov.statics.Game;

import java.util.Comparator;

public class PolyUtils {

    public static Array<PolygonUnit> unit(Body body, TextureRegion region){
        Array<PolygonUnit> array = new Array<>();
        Array<Fixture> fixtureList = body.getFixtureList();
        for (Fixture fixture : fixtureList) {
            float[] vertices = getFloatVertices(((PolygonShape) fixture.getShape()), Game.scale);
            short[] triangleData = A.physics.triangulator.computeTriangles(vertices, false).toArray();
            PolygonUnit unit = new PolygonUnit(region, vertices, triangleData);
            unit.moveLocalyByPivot();
            array.add(unit);
        }
        return array;
    }

    public static Vector2[] getVertices(PolygonShape shape){
        int size = shape.getVertexCount();
        Vector2[] ret = new Vector2[size];

        for (int i = 0; i < size; i++) {
            Vector2 v = new Vector2();
            shape.getVertex(i, v);
            ret[i] = v;
        }

        return ret;
    }

    public static float[] getFloatVertices(PolygonShape shape, float scale){
        float[] floatVertices = getFloatVertices(shape);
        for (int i = 0; i < floatVertices.length; i++) {
            floatVertices[i] *= scale;
        }
        return floatVertices;
    }
    public static float[] getFloatVertices(PolygonShape shape){
        int size = shape.getVertexCount();
        float[] ret = new float[size * 2];
        Vector2 tempVec = Utils.obtain();

        for (int i = 0; i < size; i++) {
            shape.getVertex(i, tempVec);
            ret[i * 2] = tempVec.x;
            ret[i * 2 + 1] = tempVec.y;
        }

        Utils.free(tempVec);
        return ret;
    }

    public static void moveVertices(float[] vertices, float dx, float dy){
        int size = vertices.length;
        for (int i = 0; i < size; i+=2) {
            vertices[i] += dx;
            vertices[i+1] += dy;
        }
    }

    public static float[] scale(float[] vertices, float scale){
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= scale;
        }
        return vertices;
    }

    public static float[][] splitByTriangles(float[] vertices, short[] triangles){
        int triSize = (triangles.length)/3;
        float[][] ret = new float[triSize][];

        for (int i = 0; i < triangles.length; i += 3) {
            short one = triangles[i];
            short two = triangles[i + 1];
            short three = triangles[i + 2];

            float[] verts = new float[6];
            verts[0] = vertices[one * 2];
            verts[1] = vertices[one * 2 + 1];

            verts[2] = vertices[two * 2];
            verts[3] = vertices[two * 2 + 1];

            verts[4] = vertices[three * 2];
            verts[5] = vertices[three * 2 + 1];
            ret[i/3] = verts;
        }
        return ret;
    }

    public static float[] toFloats(Array<Vector2> points) {
        float[] floats = new float[points.size * 2];

        for (int i = 0; i < points.size; i++) {
            Vector2 p = points.get(i);
            floats[i * 2] = p.x;
            floats[i * 2 + 1] = p.y;
        }
        return floats;
    }

    /** Returns angle that between 1-2 and 2-3 vectors. **/
    public static float getAngle(float[] triangle){
        return getAngle(
                triangle[0],
                triangle[1],
                triangle[2],
                triangle[3],
                triangle[4],
                triangle[5]);
    }

    /** Returns angle that between 1-2 and 2-3 vectors. **/
    public static float getAngle(float x1, float y1, float x2, float y2, float x3, float y3){
        float a2 = Vector2.len2(x3 - x2, y3 - y2);
        float b2 = Vector2.len2(x1 - x2, y1 - y2);
        float c2 = Vector2.len2(x3 - x1, y3 - y1);
        double aLen = Math.sqrt(a2);
        double bLen = Math.sqrt(b2);
        //c2 == a2 + b2 - (2 * aLen * bLen * x);
        double x = (a2 + b2 - c2) / (2 * aLen * bLen);
        return (float) Math.acos(x) * MathUtils.radDeg;
    }

    public static boolean allAnglesMoreThan(float[] t, float minAngle){
        if (getAngle(t[0], t[1], t[2], t[3], t[4], t[5]) < minAngle) return false;
        if (getAngle(t[2], t[3], t[4], t[5], t[0], t[1]) < minAngle) return false;
        if (getAngle(t[4], t[5], t[0], t[1], t[2], t[3]) < minAngle) return false;
        return true;
    }

    /**
     * @param fixture to slice
     * @param point world point
     * @param direction direction of slice
     * @param width distance between left and right slice. World scale
     */
    public static SliceResult sliceInThree(Fixture fixture, Vector2 point, Vector2 direction, float width){
        final float stepBack = 100 * Game.scaleReversed;
        final float raycastLen = stepBack + 600 * Game.scaleReversed;
        Body body = fixture.getBody();
        World world = body.getWorld();

        Vector2 halfWidthRight = new Vector2(width/2, 0).rotate(direction.angle() - 90);
        Vector2 halfWidthLeft = new Vector2(halfWidthRight).scl(-1, -1);


        Vector2 rayCastStartRight = new Vector2(direction).setLength(stepBack).scl(-1).add(point).add(halfWidthRight);
        Vector2 rayCastFinishRight = new Vector2(direction).setLength(raycastLen).add(point).add(halfWidthRight);
        Vector2 rayCastStartLeft = new Vector2(direction).setLength(stepBack).scl(-1).add(point).add(halfWidthLeft);
        Vector2 rayCastFinishLeft = new Vector2(direction).setLength(raycastLen).add(point).add(halfWidthLeft);


        SliceRayCaster rayCaster = new SliceRayCaster(fixture);
        boolean hitRight = rayCaster.launch(world, rayCastStartRight, rayCastFinishRight);
        Vector2 rightOne = new Vector2(body.getLocalPoint(rayCaster.point1));
        Vector2 rightTwo = new Vector2(body.getLocalPoint(rayCaster.point2));

        boolean hitLeft = rayCaster.launch(world, rayCastStartLeft, rayCastFinishLeft);
        Vector2 leftOne = new Vector2(body.getLocalPoint(rayCaster.point1));
        Vector2 leftTwo = new Vector2(body.getLocalPoint(rayCaster.point2));

        //Столкнулись с очень маленьким объектом. Возвращаем как есть
        if (!hitLeft && !hitRight){
            return new SliceResult(SliceType.TOO_SMALL, null, null, null);
        }

        if (hitRight && !hitLeft){
            Array<Vector2>[] shapes = sliceByOneSide(((PolygonShape) fixture.getShape()), rightOne, rightTwo);
            Array<Vector2> middle = shapes[0];
            Array<Vector2> rights = shapes[1];
            return new SliceResult(SliceType.ONLY_RIGHT, null, middle, rights);
        }

        if (!hitRight){

            Array<Vector2>[] shapes = sliceByOneSide(((PolygonShape) fixture.getShape()), leftOne, leftTwo);
            Array<Vector2> lefts = shapes[0];
            Array<Vector2> middle = shapes[1];

            return new SliceResult(SliceType.ONLY_LEFT, lefts, middle, null);
        }

        Array<Vector2>[] shapes = sliceByOneSide(((PolygonShape) fixture.getShape()), rightOne, rightTwo);
        Array<Vector2> both = shapes[0];
        Array<Vector2> rights = shapes[1];
        Array<Array<Vector2>> bothShapes = sliceByOneSide(both, leftOne, leftTwo);
        Array<Vector2> lefts = bothShapes.get(0);
        Array<Vector2> middles = bothShapes.get(1);

        return new SliceResult(SliceType.FULL, lefts, middles, rights);
    }


    /** Slices shape in two. Returns an array of two. Part from the left side of the cut and right side. **/
    private static Array<Vector2>[] sliceByOneSide(PolygonShape shape, Vector2 p1, Vector2 p2){
        Vector2[] polyPoints = PolyUtils.getVertices(shape);
        Array<Vector2> leftVerticies = new Array<>();
        Array<Vector2> rightVerticies = new Array<>();
        leftVerticies.add(new Vector2(p1));
        leftVerticies.add(new Vector2(p2));
        rightVerticies.add(new Vector2(p1));
        rightVerticies.add(new Vector2(p2));


        for (Vector2 vertex : polyPoints) {
            if (det(p1, p2, vertex) > 0){
                leftVerticies.add(vertex);
            } else {
                rightVerticies.add(vertex);
            }
        }

        leftVerticies = arrangeClockwise(leftVerticies);
        rightVerticies = arrangeClockwise(rightVerticies);
        return (Array<Vector2>[]) new Array[]{leftVerticies, rightVerticies};
    }


    private static Array<Array<Vector2>> sliceByOneSide(Array<Vector2> shape, Vector2 p1, Vector2 p2){
        Array<Vector2> leftVerticies = new Array<>();
        Array<Vector2> rightVerticies = new Array<>();
        leftVerticies.add(new Vector2(p1));
        leftVerticies.add(new Vector2(p2));
        rightVerticies.add(new Vector2(p1));
        rightVerticies.add(new Vector2(p2));


        for (Vector2 vertex : shape) {
            if (det(p1, p2, vertex) > 0){
                leftVerticies.add(vertex);
            } else {
                rightVerticies.add(vertex);
            }
        }

        leftVerticies = arrangeClockwise(leftVerticies);
        rightVerticies = arrangeClockwise(rightVerticies);
        return Array.with(leftVerticies, rightVerticies);
    }

    public static class SliceResult{

        SliceType type;

        Array<Vector2> leftPart;
        Array<Vector2> rightPart;
        Array<Vector2> middlePoints;

        public SliceResult(SliceType type, Array<Vector2> leftPart, Array<Vector2> middlePoints, Array<Vector2> rightPart) {
            this.type = type;
            this.leftPart = leftPart;
            this.rightPart = rightPart;
            this.middlePoints = middlePoints;
        }

        public SliceType getType() {
            return type;
        }

        public Array<Vector2> getLeftPart() {
            return leftPart;
        }

        public Array<Vector2> getRightPart() {
            return rightPart;
        }

        public Array<Vector2> getMiddlePoints() {
            return middlePoints;
        }
    }

    public enum SliceType{
        TOO_SMALL,
        ONLY_LEFT,
        ONLY_RIGHT,
        FULL
    }

    private static class SliceRayCaster implements RayCastCallback{

        Vector2 point1 = new Vector2();
        Vector2 point2 = new Vector2();
        Vector2 currentPoint;
        boolean hit;
        Fixture target;

        public SliceRayCaster(Fixture target){
            this.target = target;
            this.hit = false;
        }

        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture == target){
                this.currentPoint.set(point);
                this.hit = true;
                return 0;
            }
            return 1;
        }

        public boolean launch(World world, Vector2 p1, Vector2 p2){
            hit = false;
            currentPoint = point1;
            world.rayCast(this, p1, p2);
            if (!hit){
                return false;
            }
            hit = false;
            currentPoint = point2;
            world.rayCast(this, p2, p1);
            if (!hit){
                throw new RuntimeException("How did that happen");
            }
            return true;
        }
    }

    private static float det(Vector2 a, Vector2 b, Vector2 p) {
        return det(a.x, a.y, b.x, b.y, p.x, p.y);
    }

    private static float det(float x1, float y1, float x2, float y2, float pointX, float pointY) {
        return x1*y2 + x2*pointY + pointX*y1 - y1*x2 - y2*pointX - pointY*x1;
    }

    private static Array<Vector2> arrangeClockwise(Array<Vector2> vec) {
        int n = vec.size;
        int i1 = 1;
        int i2 = n-1;
        Array<Vector2> tempVec = new Array<>(n);
        for (int i = 0; i < n; i++) {
            tempVec.add(new Vector2());
        }
        Vector2 C = new Vector2();
        Vector2 D = new Vector2();

        vec.sort(comp1);

        tempVec.get(0).set(vec.get(0));
        C.set(vec.get(0));
        D.set(vec.get(n - 1));

        for (int i = 1; i < n - 1; i++) {
            float d = det(C, D, vec.get(i));
            if (d < 0) {
                tempVec.get(i1++).set(vec.get(i));
            } else {
                tempVec.get(i2--).set(vec.get(i));
            }
        }

        tempVec.get(i1).set(vec.get(n - 1));

        return tempVec;
    }

    private static final Comparator<Vector2> comp1 = (a, b) -> {
        if (a.x > b.x) {
            return 1;
        } else if (a.x < b.x) {
            return -1;

        }
        return 0;
    };

}
