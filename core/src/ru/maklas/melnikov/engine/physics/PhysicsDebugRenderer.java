package ru.maklas.melnikov.engine.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * Вырисовывает на экран все физические тела.
 */
public class PhysicsDebugRenderer {

    private final OrthographicCamera worldCamera;
    private final float scale;
    private Box2DDebugRenderer debugRenderer;
    private ShapeRenderer shapeR = new ShapeRenderer();
    private World world;
    private OrthographicCamera camera;
    private boolean enabled;
    private boolean renderCenter = false;

    public PhysicsDebugRenderer(World world, OrthographicCamera gameCamera, float scale, boolean enabled){
        this(world, gameCamera, new OrthographicCamera(gameCamera.viewportWidth / scale, gameCamera.viewportHeight / scale), scale, enabled);
    }

    public PhysicsDebugRenderer(World world, OrthographicCamera gameCamera, OrthographicCamera worldCamera, float scale, boolean enabled){
        this.scale = scale;
        this.enabled = enabled;
        this.debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.camera = gameCamera;
        this.worldCamera = worldCamera;
    }

    private Array<Body> bodies = new Array<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void resize(){
        this.worldCamera.setToOrtho(camera.viewportWidth / scale, camera.viewportHeight / scale);
        this.worldCamera.update(true);
    }

    private final Vector2 tempVec1 = new Vector2();
    private final Vector2 tempVec2 = new Vector2();

    public void render() {
        if (!enabled) return;

        Vector3 position = camera.position;
        worldCamera.position.set(position.x/scale, position.y/scale, position.z/scale);
        worldCamera.zoom = camera.zoom;
        worldCamera.update();
        debugRenderer.render(world, worldCamera.combined);

        if (renderCenter) {
            shapeR.setProjectionMatrix(worldCamera.combined);
            shapeR.begin(ShapeRenderer.ShapeType.Line);
            world.getBodies(bodies);
            for (Body body : bodies) {
                final Vector2 origin = tempVec1.set(body.getPosition());
                final Vector2 center = tempVec2.set(body.getWorldPoint(body.getMassData().center));
                shapeR.line(origin, center);
                shapeR.setColor(Color.RED);
                shapeR.circle(origin.x, origin.y, 7 / scale, 6);
                shapeR.setColor(Color.GREEN);
                shapeR.circle(center.x, center.y, 7 / scale, 6);
            }

            shapeR.end();
        }
    }

    public void dispose(){
        debugRenderer.dispose();
        shapeR.dispose();
    }
}
