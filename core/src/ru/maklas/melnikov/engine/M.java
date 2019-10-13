package ru.maklas.melnikov.engine;


import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.functions.FunctionComponent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.mengine.ComponentMapper;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.EntitySystem;
import ru.maklas.mengine.UpdatableEntitySystem;
import ru.maklas.melnikov.engine.other.EntityDebugSystem;
import ru.maklas.melnikov.engine.other.TTLComponent;
import ru.maklas.melnikov.engine.other.TTLSystem;
import ru.maklas.melnikov.engine.physics.PhysicsComponent;
import ru.maklas.melnikov.engine.physics.PhysicsSystem;
import ru.maklas.melnikov.engine.rendering.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Маппинг компонентов и последовательность работы систем
 * @author maklas. Created on 05.06.2017.
 */

public class M {

    public static final int totalComponents = 30;

    public static final ComponentMapper<RenderComponent>                render = ComponentMapper.of(RenderComponent.class);         //Картинки Entity
    public static final ComponentMapper<CameraComponent>                camera = ComponentMapper.of(CameraComponent.class);         //Движение камеры
    public static final ComponentMapper<AnimationComponent>             anim = ComponentMapper.of(AnimationComponent.class);        //Анимации
    public static final ComponentMapper<PhysicsComponent>               physics = ComponentMapper.of(PhysicsComponent.class);       //Тело Box2d
    public static final ComponentMapper<TTLComponent>                   ttl = ComponentMapper.of(TTLComponent.class);               //Удаление Entity с задержкой

    public static final ComponentMapper<FunctionComponent>              fun = ComponentMapper.of(FunctionComponent.class);
    public static final ComponentMapper<BiFunctionComponent>            biFun = ComponentMapper.of(BiFunctionComponent.class);
    public static final ComponentMapper<PointComponent>                 point = ComponentMapper.of(PointComponent.class);

    /** Сортируем системы в порядке их обновления в Engine.update() **/
    public static void initialize(){
        int mappers = getMappersReflection() + 2;
        Engine.TOTAL_COMPONENTS = Math.max(mappers, totalComponents);
        ObjectMap<Class<? extends EntitySystem>, Integer> map = Engine.systemOrderMap;
        int i = 1;


        map.put(TTLSystem.class, i++);
        map.put(PhysicsSystem.class, i++);
        map.put(UpdatableEntitySystem.class, i++);
        map.put(AnimationSystem.class, i++);

        map.put(CameraSystem.class, i++);
        map.put(GradientRenderSystem.class, i++);
        map.put(RenderingSystem.class, i++);
        map.put(EntityDebugSystem.class, i++);
        map.put(FunctionRenderSystem.class, i++);
        map.put(ScalableFunctionRenderSystem.class, i++);
        map.put(FunctionTrackingRenderSystem.class, i++);
        map.put(PointRenderSystem.class, i++);

        map.put(BiFunctionRenderSystem.class, i++);
        map.put(DevelopmentSystem.class, i++);
    }

    /** Устанавливаем длинну массива Entity.components[] в зависимости от количества компонентов. **/
    private static int getMappersReflection(){
        int counter = 0;
        try {
            Field[] fields = M.class.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers()) && ComponentMapper.class.isAssignableFrom(field.getType())){
                    counter++;
                }
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return counter;
    }

}
