package ru.maklas.melnikov.utils.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Entity;

public class BodyBuilder {

    private final World world;
    private final BodyDef bDef = new BodyDef();
    private final Array<FixtureDef> fixDefs = new Array<>();
    private final Array<Fixture> lastFixtures = new Array<>();
    private boolean building = false;
    private float scale;
    private boolean centerOfMassToOrigin;

    public BodyBuilder(World world, float box2dScale) {
        this.world = world;
        this.scale = box2dScale;
    }


    public BodyBuilder newBody(){
        if (building){
            throw new RuntimeException("newBody(); before build();");
        }

        building = true;
        restoreDefs();
        return this;
    }

    public BodyBuilder addFixtures(Array<FixtureDef> defs){
        for (FixtureDef def : defs) {
            addFixture(def);
        }
        return this;
    }

    public BodyBuilder addFixture(FixtureDef fDef){
        fixDefs.add(fDef);
        return this;
    }

    public Fixture getCreatedFixture(int i){
        return lastFixtures.get(i);
    }

    public Body build(){
        if (!building){
            throw new RuntimeException("build() before newBody()");
        }
        building = false;

        Body body = world.createBody(bDef);
        int size = fixDefs.size;
        for (int i = 0; i < size; i++) {
            Fixture fixture = body.createFixture(fixDefs.get(i));
            lastFixtures.add(fixture);
        }

        if (centerOfMassToOrigin){
            MassData massData = body.getMassData();
            massData.center.set(0, 0);
            body.setMassData(massData);
        }
        return body;
    }




    // PARAMETERS


    public BodyBuilder pos(Vector2 v){
        return pos(v.x, v.y);
    }

    public BodyBuilder pos(float x, float y){
        bDef.position.set(x/scale, y/scale);
        return this;
    }


    public BodyBuilder pos(Entity e){
        bDef.position.set(e.x/scale, e.y/scale);
        return this;
    }

    public BodyBuilder vel(float x, float y){
        bDef.linearVelocity.set(x/scale, y/scale);
        return this;
    }

    public BodyBuilder vel(Vector2 v){
        bDef.linearVelocity.set(v.x/scale, v.y/scale);
        return this;
    }

    public BodyBuilder angVel(float degToTheLeft){
        bDef.angularVelocity = degToTheLeft * MathUtils.degRad;
        return this;
    }

    /** >0 - против часовой **/
    public BodyBuilder angle(float deg){
        bDef.angle = deg * MathUtils.degRad;
        return this;
    }

    public BodyBuilder type(BodyDef.BodyType type){
        bDef.type = type;
        return this;
    }

    public BodyBuilder fixRotation(){
        bDef.fixedRotation = true;
        return this;
    }

    /** Def = false **/
    public BodyBuilder setBullet(){
        bDef.bullet = true;
        return this;
    }

    /** Def = 0 **/
    public BodyBuilder linearDamp(float damping){
        bDef.linearDamping = damping;
        return this;
    }

    /** Def = 1 **/
    public BodyBuilder gravityScale(float scale){
        bDef.gravityScale = scale;
        return this;
    }

    /** Def = 0 **/
    public BodyBuilder angularDamp(float damping){
        bDef.angularDamping = damping;
        return this;
    }

    /** Центр массы тела будет в координате (0, 0) **/
    public BodyBuilder setCenterOfMassToOrigin(){
        centerOfMassToOrigin = true;
        return this;
    }

    public void clear(){
        fixDefs.clear();
        lastFixtures.clear();
    }

    private void restoreDefs(){
        bDef.linearVelocity.set(0, 0);
        bDef.fixedRotation = false;
        bDef.angularVelocity = 0f;
        bDef.angularDamping = 0;
        bDef.angle = 0;
        bDef.bullet = false;
        bDef.gravityScale = 1;
        bDef.awake = true;
        bDef.active = true;
        bDef.position.set(0, 0);
        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.linearDamping = 0;

        centerOfMassToOrigin = false;
        fixDefs.clear();
        lastFixtures.clear();
    }

}
