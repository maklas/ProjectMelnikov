package ru.maklas.melnikov.engine.physics.world_listeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import ru.maklas.melnikov.engine.other.Event;
import ru.maklas.mengine.Entity;

/**
 * Called during world.step().
 */
public class PreCollisionEvent implements Event {

    Entity a;
    Entity b;
    Fixture fixA;
    Fixture fixB;
    final Vector2 point = new Vector2();
    final Vector2 normal = new Vector2();

    public PreCollisionEvent() {

    }

    public PreCollisionEvent(Entity a, Entity b, Fixture fixA, Fixture fixB, Vector2 point, Vector2 normal) {
        this.a = a;
        this.b = b;
        this.fixA = fixA;
        this.fixB = fixB;
        this.point.set(point);
        this.normal.set(normal);
    }

    public PreCollisionEvent init(Entity a, Entity b, Fixture fixA, Fixture fixB, Vector2 point, Vector2 normal) {
        this.a = a;
        this.b = b;
        this.fixA = fixA;
        this.fixB = fixB;
        this.point.set(point);
        this.normal.set(normal);
        return this;
    }

    public Entity getA() {
        return a;
    }

    public Entity getB() {
        return b;
    }

    public Fixture getFixA() {
        return fixA;
    }

    public Fixture getFixB() {
        return fixB;
    }

    public Vector2 getPoint() {
        return point;
    }

    public Vector2 getNormal() {
        return normal;
    }


}
