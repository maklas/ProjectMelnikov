package ru.maklas.melnikov.engine.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import com.badlogic.gdx.utils.Pool;
import ru.maklas.melnikov.engine.B;
import ru.maklas.melnikov.engine.physics.world_listeners.DefaultCollisionFilter;
import ru.maklas.melnikov.statics.Game;
import ru.maklas.melnikov.utils.Log;
import ru.maklas.melnikov.utils.StringUtils;
import ru.maklas.mengine.*;

/**
 * <li><b>Requires:</b> world
 * <li><b>Subscribes:</b> none
 * <li><b>Emits:</b> {@link CollisionEvent}
 * <li><b>Description:</b> Обновляет физику игры, позиции Entity с CollisionComponent и диспатчит Ивенты о коллизии Entity.
 */
public class PhysicsSystem extends EntitySystem implements EntityListener, ContactListener {

    private ComponentMapper<PhysicsComponent> mapper;
    private World world;
    private ImmutableArray<Entity> entities;
    private Array<PhysicsComponent> toDestroy = new Array<>();
    private Pool<CollisionEvent> collisionEventPool = new Pool<CollisionEvent>() {
        @Override
        protected CollisionEvent newObject() {
            return new CollisionEvent();
        }
    };
    private Array<CollisionEvent> collisionEvents = new Array<>();

    @Override
    public void onAddedToEngine(final Engine engine) {
        this.world = engine.getBundler().get(B.world);
        world.setContactListener(this);
        world.setContactFilter(new DefaultCollisionFilter());

        entities = engine.entitiesFor(PhysicsComponent.class);
        mapper = ComponentMapper.of(PhysicsComponent.class);
        for (Entity entity : entities) {
            entity.get(mapper).body.setUserData(entity);
        }
        engine.addListener(this);
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();
        Entity a = (Entity) fA.getBody().getUserData();
        Entity b = (Entity) fB.getBody().getUserData();

        for (CollisionEvent cpe : collisionEvents) {
            if ((cpe.getA() == a && cpe.getB() == b) ||(cpe.getB() == a && cpe.getA() == b)){
                Log.debug("2 Entities collided 2 times in a single PhysicsEngine.update():\n" +
                        StringUtils.entityToString(a) + '\n' +
                        StringUtils.entityToString(b)
                );
                return;
            }
        }


        WorldManifold worldManifold = contact.getWorldManifold();
        Vector2 point = worldManifold.getPoints()[0].scl(Game.scale);
        Vector2 normal = worldManifold.getNormal();
        collisionEvents.add(collisionEventPool.obtain().init(a, b, fA, fB, point, normal));
    }

    @Override
    public void update(float dt) {
        World world = this.world;
        destroyPendings();

        world.step(dt, 8, 3);

        ComponentMapper<PhysicsComponent> collisionM = mapper;
        for (Entity entity : entities) {
            entity.get(collisionM).updateEntity(entity);
        }

        dispatchCollisionEvents();
    }

    private void dispatchCollisionEvents() {
        Pool<CollisionEvent> pool = this.collisionEventPool;
        Array<CollisionEvent> pendingEvents = this.collisionEvents;

        for (CollisionEvent event : pendingEvents) {
            dispatch(event);
            pool.free(event);
        }
        pendingEvents.clear();
    }

    private void destroyPendings() {
        Array<PhysicsComponent> toDestroy = this.toDestroy;
        if (toDestroy.size > 0) {
            for (PhysicsComponent pc : toDestroy) {
                if (pc.body != null) {
                    world.destroyBody(pc.body);
                    pc.body = null;
                }
            }
            toDestroy.clear();
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // ENTITY LISTENER
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void entityAdded(Entity entity) {
        PhysicsComponent cc = entity.get(mapper);
        if (cc != null) {
            cc.body.setUserData(entity);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
        PhysicsComponent cc = entity.get(mapper);
        if (cc != null) {
            toDestroy.add(cc);
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // DEBUG RENDERING
    ///////////////////////////////////////////////////////////////////////////

    OrthographicCamera worldCamera;
    OrthographicCamera gameCamera;
    PhysicsDebugRenderer renderer;

    public final PhysicsDebugRenderer getDebugRenderer() {
        if (renderer == null) {
            renderer = new PhysicsDebugRenderer(world, getEngine().getBundler().get(B.cam), getWorldCamera(), Game.scale, false);
        }
        return renderer;
    }

    public final OrthographicCamera getWorldCamera() {
        float scale = Game.scaleReversed;
        if (worldCamera == null) {
            this.gameCamera = getEngine().getBundler().get(B.cam);
            this.worldCamera = new OrthographicCamera(gameCamera.viewportWidth * scale, gameCamera.viewportHeight * scale);
        }
        Vector3 position = gameCamera.position;
        worldCamera.position.set(position.x * scale, position.y * scale, position.z * scale);
        return worldCamera;
    }

    public final void renderDebug() {
        getDebugRenderer().render();
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
