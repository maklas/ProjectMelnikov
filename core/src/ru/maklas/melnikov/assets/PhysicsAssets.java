package ru.maklas.melnikov.assets;

import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import ru.maklas.melnikov.statics.Game;
import ru.maklas.melnikov.utils.physics.Builders;
import ru.maklas.mengine.Entity;

public class PhysicsAssets extends Asset {

    public World world;
    public DelaunayTriangulator triangulator;
    public Builders builders;

    public Shape circle1; //Круг диаметром в 1 пиксель.
    public Shape circle10; //Круг диаметром в 10 пикселей.
    public Shape circle15; //Круг диаметром в 15 пикселей.
    public Shape itemShapeClient;

    private static final Vector2 tempVec = new Vector2();

    @Override
    protected void loadImpl() throws Exception {
        world = new World(new Vector2(0, 0), false);
        triangulator = new DelaunayTriangulator();
        builders = new Builders(world, Game.scale);
        circle1 = builders.buildCircle(0, 0, 1);
        circle10 = builders.buildCircle(0, 0, 10);
        circle15 = builders.buildCircle(0, 0, 15);
        itemShapeClient = builders.buildRectangleCentered(0, 0, 60, 60);
    }

    @Override
    protected void disposeImpl() throws Exception {
        circle1.dispose();
        circle10.dispose();
        circle15.dispose();
        
        world.dispose();
        disposeCustomShapes();
        world = null;
        triangulator = null;
    }

    /** Removes all bodies, joints and listeners from the world. Cleans it up **/
    public void clearWorld(){
        clearWorld(world);
    }

    public World clearWorld(World world){
        Array<Body> bodies = new Array<>();
        Array<Joint> joints = new Array<>();
        world.getBodies(bodies);
        world.getJoints(joints);

        world.clearForces();
        for (Joint joint : joints) {
            world.destroyJoint(joint);
        }

        for (Body body: bodies) {
            world.destroyBody(body);
        }
        world.setContactFilter(null);
        world.setContactListener(null);
        return world;
    }

    private final Array<Shape> customShapes = new Array<>();
    public void disposeCustomShapes(){
        customShapes.foreach(Shape::dispose);
        customShapes.clear();
    }

    public Shape addCustomShape(Shape shape){
        customShapes.add(shape);
        return shape;
    }

    /** setting velocity using linearImpulse **/
    public static void setVelocity(Body body, Vector2 vel){
        MassData massData = body.getMassData();

        Vector2 currentVelocity = body.getLinearVelocity();
        Vector2 velDiff = tempVec.set(vel).sub(currentVelocity).scl(massData.mass);

        Vector2 worldPoint = body.getWorldPoint(massData.center);
        body.applyLinearImpulse(velDiff, worldPoint, true);
    }

    /**
     * setting velocity using linearImpulse
     * Positive means counterclockwise
     */
    public static void setAngularVelocity(Body body, float degToLeft){
        degToLeft *= MathUtils.degRad;

        float currentVelocity = body.getAngularVelocity();
        float velDiff = (degToLeft - currentVelocity);

        body.applyAngularImpulse(velDiff * body.getInertia(), true);
    }

    /** Rotates body to a desired angle. Must be called every frame for stabilization **/
    public static void rotateToAngle(Body body, float angle, float dt){
        angle *= MathUtils.degRad;
        float bodyAngle = body.getAngle();
        float nextAngle = bodyAngle + body.getAngularVelocity() * dt;
        float totalRotation = angle - nextAngle;
        while (totalRotation < -180 * MathUtils.degRad ) totalRotation += 360 * MathUtils.degRad;
        while (totalRotation >  180 * MathUtils.degRad ) totalRotation -= 360 * MathUtils.degRad;
        float desiredAngularVelocity = totalRotation / dt;
        body.applyAngularImpulse(body.getInertia() * desiredAngularVelocity, true);
    }

    /** Rotates body to a desired angle using impulses. Must be called every frame for stabilization **/
    public static void rotateToAngle(Body body, float desiredAngle, float dt, float maxAngVel){
        desiredAngle *= MathUtils.degRad;
        maxAngVel *= MathUtils.degRad;
        float bodyAngle = body.getAngle();
        while (desiredAngle < bodyAngle -180 * MathUtils.degRad ) desiredAngle += 360 * MathUtils.degRad;
        while (desiredAngle >  bodyAngle + 180 * MathUtils.degRad ) desiredAngle -= 360 * MathUtils.degRad;
        float nextFrameAnglePrediction = bodyAngle + body.getAngularVelocity() * dt; //Angle we get on the next frame if we do nothing.
        float targetAngleNextFrame = MathUtils.clamp(desiredAngle, bodyAngle - maxAngVel * dt, bodyAngle + maxAngVel * dt); //Angle we want to have on the next frame
        float totalRotation = targetAngleNextFrame - nextFrameAnglePrediction; //Разница между будущим углом следующего кадра и желаемым.
        float desiredAngularVelocity = totalRotation / dt;  //Получаем нужную угловую скорость
        body.applyAngularImpulse(body.getInertia() * desiredAngularVelocity, true);
    }

    public float normalize180(float angle) {
        angle %= 360;
        angle = (angle + 360) % 360;
        if (angle > 180)
            angle -= 360;
        return angle > 180 ? angle - 360 : angle;
    }

    public float normalize180Rad(float angle) {
        angle %= 6.2831855f;
        angle = (angle + 6.2831855f) % 6.2831855f;
        if (angle > 3.1415927f)
            angle -= 6.2831855f;
        if (angle > 3.1415927f) return angle - 6.2831855f;
        else return angle;
    }

    public static float normalizeAngle360(float angle){
        return (angle %= 360) < 0 ? angle + 360 : angle;
    }

    public static float normalizeAngle360Rad(float angle){
        return (angle %= 6.2831855f) < 0 ? angle + 6.2831855f : angle;
    }

    /** Automatically translates box size and position into world scale!!! **/
    public static Array<Entity> queryForEntities(World world, float minX, float minY, float maxX, float maxY, Predicate<Entity> predicate){
        Array<Entity> entities = new Array<>();
        world.QueryAABB((f) -> {
            Entity e = (Entity) f.getBody().getUserData();
            if (e != null && predicate.evaluate(e)){
                entities.add(e);
            }
            return true;
        }, minX * Game.scaleReversed, minY * Game.scaleReversed, maxX * Game.scaleReversed, maxY * Game.scaleReversed);

        return entities;
    }

    /** Translate box size and position into world scale!!! **/
    public static Array<Entity> queryForEntitiesInCircle(World world, float x, float y, float range, int lines, Predicate<Entity> predicate){
        x *= Game.scaleReversed;
        y *= Game.scaleReversed;
        Array<Entity> entities = new Array<>();
        Vector2 rotVec = new Vector2(range * Game.scaleReversed, 0);
        for (int i = 0; i < lines; i++) {
            world.rayCast((fixture, point, normal, fraction) -> {
                Object userData = fixture.getBody().getUserData();
                if (userData != null && predicate.evaluate((Entity) userData)){
                    entities.add((Entity) userData);
                }
                return -1;
            }, x, y, x + rotVec.x, y + rotVec.y);
            rotVec.rotate(360f / lines);
        }
        return entities.removeDuplicates(true);
    }

    /** Translate box size and position into world scale!!! **/
    public static void queryAABB(World world, float minX, float minY, float maxX, float maxY, QueryCallback callback){
        world.QueryAABB(callback, minX * Game.scaleReversed, minY * Game.scaleReversed, maxX * Game.scaleReversed, maxY * Game.scaleReversed);
    }

}
