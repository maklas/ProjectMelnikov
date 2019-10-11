package ru.maklas.melnikov.utils.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Entity;
import ru.maklas.melnikov.statics.EntityType;

public class FDefBuilder {

    private FixtureDef fDef = new FixtureDef();

    public FDefBuilder() {

    }

    public FDefBuilder newFixture(){
        fDef = new FixtureDef();
        return this;
    }

    public FDefBuilder newFixture(FixtureDef fDef){
        this.fDef = fDef;
        return this;
    }

    public FDefBuilder sensor(){
        fDef.isSensor = true;
        return this;
    }

    public FDefBuilder mask(int category, int collisionMask){
        fDef.filter.categoryBits = (short) category;
        fDef.filter.maskBits = (short) collisionMask;
        return this;
    }

    public FDefBuilder mask(int type){
        EntityType eType = EntityType.of(type);
        fDef.filter.categoryBits = eType.category;
        fDef.filter.maskBits = eType.mask;
        return this;
    }

    public FDefBuilder mask(Entity e){
        EntityType eType = EntityType.of(e.type);
        fDef.filter.categoryBits = eType.category;
        fDef.filter.maskBits = eType.mask;
        return this;
    }

    public FDefBuilder shape(Shape shape){
        fDef.shape = shape;
        return this;
    }

    public FDefBuilder shape(Array<Vector2> polygon){
        PolygonShape shape = new PolygonShape();
        shape.set(polygon.toArray(Vector2.class));
        fDef.shape = shape;
        return this;
    }

    public FDefBuilder shape(float[] polygon){
        PolygonShape shape = new PolygonShape();
        shape.set(polygon);
        fDef.shape = shape;
        return this;
    }

    /** Определяет массу на квадратный метр объекта. дефолтное - 0. Позвоялет вращаться. **/
    public FDefBuilder density(float density){
        fDef.density = density;
        return this;
    }

    /** Default = 0.2 **/
    public FDefBuilder friction(float friction){
        fDef.friction = friction;
        return this;
    }

    /** Default = 0 **/
    public FDefBuilder bounciness(float b){
        fDef.restitution = b;
        return this;
    }

    public FixtureDef build(){
        return fDef;
    }
}
