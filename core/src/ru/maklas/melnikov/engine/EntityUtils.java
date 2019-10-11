package ru.maklas.melnikov.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import ru.maklas.melnikov.engine.rendering.CameraMode;
import ru.maklas.mengine.Entity;
import ru.maklas.melnikov.engine.rendering.CameraComponent;
import ru.maklas.melnikov.statics.EntityType;
import ru.maklas.melnikov.statics.ID;
import ru.maklas.melnikov.statics.Layers;

public class EntityUtils {

    public static Entity camera(OrthographicCamera cam) {
        return new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, Layers.camera).add(new CameraComponent(cam));
    }

    public static Entity camera(OrthographicCamera cam, CameraMode mode) {
        return new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, Layers.camera).add(new CameraComponent(cam).mode(mode));
    }

    public static Entity camera(OrthographicCamera cam, int followId) {
        return new Entity(ID.camera, EntityType.BACKGROUND, 0, 0, Layers.camera).add(new CameraComponent(cam).setFollowEntity(followId, true, true));
    }
}
