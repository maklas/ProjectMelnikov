package ru.maklas.melnikov.utils.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import ru.maklas.mengine.Entity;
import ru.maklas.melnikov.engine.physics.PhysicsComponent;
import ru.maklas.melnikov.statics.EntityType;
import ru.maklas.melnikov.utils.Log;

public class FastBodyBuilder {

    private BodyBuilder bb;
    private FDefBuilder fb;
    private boolean building = false;
    private EntityType et;
    private Entity addComponent;

    public FastBodyBuilder(BodyBuilder bb, FDefBuilder fb) {
        this.bb = bb;
        this.fb = fb;
    }

    public FastBodyBuilder start(Entity e){
        if (building){
            throw new RuntimeException("Didn't finish last building process");
        }
        building = true;
        bb.newBody().pos(e).angle(e.getAngle());
        et = EntityType.of(e.type, null);
        addComponent = e;
        return this;
    }

    public FastBodyBuilder start(float x, float y, int entityType){
        if (building){
            throw new RuntimeException("Didn't finish last building process");
        }
        building = true;
        bb.newBody().pos(x, y);
        et = EntityType.of(entityType, null);
        return this;
    }

    public FastBodyBuilder type(BodyDef.BodyType type){
        bb.type(type);
        return this;
    }

    public FastBodyBuilder vel(float vX, float vY){
        bb.vel(vX, vY);
        return this;
    }

    public FastBodyBuilder vel(Vector2 vel){
        bb.vel(vel);
        return this;
    }

    public FastBodyBuilder angVel(float degToTheLeft){
        bb.angVel(degToTheLeft);
        return this;
    }

    public FastBodyBuilder angle(float deg){
        bb.angle(deg);
        return this;
    }

    /** Def = 0 **/
    public FastBodyBuilder linearDamp(float damping){
        bb.linearDamp(damping);
        return this;
    }

    /** Def = 1 **/
    public FastBodyBuilder gravityScale(float scale){
        bb.gravityScale(scale);
        return this;
    }

    /** Def = 0 **/
    public FastBodyBuilder angularDamp(float damping){
        bb.angularDamp(damping);
        return this;
    }

    /** Центр массы тела будет в координате (0, 0) **/
    public FastBodyBuilder setCenterOfMassToOrigin(){
        bb.setCenterOfMassToOrigin();
        return this;
    }

    public FastBodyBuilder fixRotation(){
        bb.fixRotation();
        return this;
    }

    /** Def = false **/
    public FastBodyBuilder setBullet(){
        bb.setBullet();
        return this;
    }

    public FastBodyBuilder addShape(Shape shape){
        return addShape(shape, false);
    }
    public FastBodyBuilder addShape(Shape shape, boolean sensor){
        fb.newFixture()
                .shape(shape);
        if (et != null) {
            fb.mask(et.category, et.mask);
        }
        if (sensor){
            fb.sensor();
        }
        fb.density(1f);
        bb.addFixture(fb.build());
        return this;
    }

    /**
     * @param density def = 0.0f
     * @param friction def = 0.2f
     * @param bounciness def = 0.0f
     */
    public FastBodyBuilder addShape(Shape shape, boolean sensor, float density, float friction, float bounciness){
        fb.newFixture()
                .shape(shape);
        if (et != null) {
            fb.mask(et.category, et.mask);
        }
        if (sensor){
            fb.sensor();
        }
        fb.density(density);
        fb.friction(friction);
        fb.bounciness(bounciness);
        bb.addFixture(fb.build());
        return this;
    }

    public Body build(){
        if (!building){
            throw new RuntimeException("forEntity() should be called before build()");
        }
        building = false;
        return bb.build();
    }

    public PhysicsComponent buildPC(){
        PhysicsComponent pc = buildPC(addComponent);
        addComponent = null;
        return pc;
    }
    /** Builds physics component and adds it to the entity**/
    public PhysicsComponent buildPC(Entity e){
        if (!building){
            throw new RuntimeException("forEntity() should be called before build()");
        }
        building = false;
        Body body = bb.build();
        if (e != null){
            PhysicsComponent pc = new PhysicsComponent(body);
            e.add(pc);
            return pc;
        } else {
            Log.error(new Exception("No Entity was provided in order to attach PhysicsComponent"));
            return null;
        }
    }


}
