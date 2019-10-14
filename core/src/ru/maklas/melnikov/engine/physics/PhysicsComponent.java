package ru.maklas.melnikov.engine.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import ru.maklas.melnikov.statics.Game;
import ru.maklas.melnikov.utils.StringUtils;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Entity;

/**
 * Компонент привязывающий {@link Entity} к Box2d's {@link Body}.
 * Если у Entity присутствует данный компонент,
 * его {@link Entity#x x}, {@link Entity#y y} и {@link Entity#angle angle} контроллируются указанным Body.
 */
public class PhysicsComponent implements Component {

    public Body body;

    public PhysicsComponent(Body body) {
        this.body = body;
    }

    public void updateEntity(Entity e){
        Vector2 bodyPos = body.getPosition();
        e.x = bodyPos.x * Game.scale;
        e.y = bodyPos.y * Game.scale;
        e.setAngle(body.getAngle() * MathUtils.radiansToDegrees);
    }

    @Override
    public String toString() {
        if (body == null)
            return "Physics.NULL";
        else
            return "Physics{" +
                "t=" + body.getType().name().replace("Body", "") +
                ", m=" + StringUtils.ff(body.getMass()) +
                ", v=" + StringUtils.vec(body.getLinearVelocity().scl(Game.scale), 1) +
                '}';
    }
}
