package ru.maklas.melnikov.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;
import ru.maklas.libs.Counter;
import ru.maklas.mengine.Bundler;
import ru.maklas.mengine.BundlerKey;
import ru.maklas.melnikov.statics.ID;
import ru.maklas.melnikov.utils.Profiler;
import ru.maklas.melnikov.utils.TimeSlower;
import ru.maklas.melnikov.utils.gsm_lib.State;
import ru.maklas.melnikov.utils.physics.Builders;

/** Для Engine.bundler **/
public class B {

    public static final BundlerKey<Batch> batch = BundlerKey.of("batch");
    public static final BundlerKey<OrthographicCamera> cam = BundlerKey.of("cam");
    public static final BundlerKey<World> world = BundlerKey.of("world");
    public static final BundlerKey<Builders> builders = BundlerKey.of("builders");
    public static final BundlerKey<Float> dt = BundlerKey.of("dt");
    public static final BundlerKey<TimeSlower> timeSlower = BundlerKey.of("timeSlower");
    public static final BundlerKey<State> gsmState = BundlerKey.of("state");
    public static final BundlerKey<ShapeRenderer> sr = BundlerKey.of("shapeRenderer");

}
