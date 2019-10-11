package ru.maklas.melnikov.engine.physics.world_listeners;

import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;

public class DefaultCollisionFilter implements ContactFilter{

    @Override
    public boolean shouldCollide(Fixture fA, Fixture fB) {
        return collideByMask(fA, fB);
    }

    private boolean collideByMask(Fixture a, Fixture b){
        Filter filterA = a.getFilterData();
        Filter filterB = b.getFilterData();
        return (filterA.maskBits & filterB.categoryBits) != 0 &&
                (filterA.categoryBits & filterB.maskBits) != 0;
    }
}
