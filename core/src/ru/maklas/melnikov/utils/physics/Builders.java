package ru.maklas.melnikov.utils.physics;

import com.badlogic.gdx.physics.box2d.*;
import ru.maklas.mengine.Entity;

public class Builders extends ShapeBuilder{

    private BodyBuilder bodyB;
    private FDefBuilder fixB;
    private FastBodyBuilder fastBB;

    public Builders(World world, float scale) {
        this(new BodyBuilder(world, scale), new FDefBuilder(), scale);
    }
    public Builders(BodyBuilder bodyB, FDefBuilder fixB, float scale) {
        super(scale);
        this.bodyB = bodyB;
        this.fixB = fixB;
        fastBB = new FastBodyBuilder(bodyB, fixB);
    }

    public BodyBuilder getBodyB() {
        return bodyB;
    }

    public FDefBuilder getFixB() {
        return fixB;
    }

    public BodyBuilder newBody(BodyDef.BodyType type){
        return bodyB.newBody().type(type);
    }

    public FDefBuilder newFixture(){
        return fixB.newFixture();
    }

    public FDefBuilder newFixture(Shape shape){
        return fixB.newFixture().shape(shape);
    }

    public FDefBuilder newFixture(FixtureDef fDef){
        return fixB.newFixture(fDef);
    }

    public Fixture getCreatedFixture(int i){
        return bodyB.getCreatedFixture(i);
    }

    public FastBodyBuilder fast(Entity e){
        return fastBB.start(e);
    }

    public FastBodyBuilder fast(float x, float y, int entityType){
        return fastBB.start(x, y, entityType);
    }

    public FastBodyBuilder fast(Entity e, BodyDef.BodyType type){
        return fastBB.start(e).type(type);
    }

    public FastBodyBuilder fast(Entity e, BodyDef.BodyType type, Shape shape){
        return fastBB.start(e).type(type).addShape(shape);
    }

}
