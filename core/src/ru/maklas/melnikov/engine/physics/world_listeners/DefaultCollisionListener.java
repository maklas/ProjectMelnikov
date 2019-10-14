package ru.maklas.melnikov.engine.physics.world_listeners;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ru.maklas.melnikov.statics.Game;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

/**
 * Работает в паре с PhysicsSystem. Генерирует PreCollisionEvent.
 */
public class DefaultCollisionListener implements ContactListener {

    private Engine engine;
    private PreCollisionEvent collEvent;

    public DefaultCollisionListener(Engine engine) {
        this.engine = engine;
        this.collEvent = new PreCollisionEvent();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();
        Entity a = (Entity) fA.getBody().getUserData();
        Entity b = (Entity) fB.getBody().getUserData();
        WorldManifold worldManifold = contact.getWorldManifold();
        Vector2 point = worldManifold.getPoints()[0].scl(Game.scale);
        Vector2 normal = worldManifold.getNormal();

        engine.dispatch(collEvent.init(a, b, fA, fB, point, normal));
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    @Override
    public void endContact(Contact contact) {

    }
}
