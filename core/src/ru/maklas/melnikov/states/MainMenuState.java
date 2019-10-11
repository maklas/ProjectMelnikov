package ru.maklas.melnikov.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import ru.maklas.melnikov.engine.functions.BiFunctionComponent;
import ru.maklas.melnikov.engine.functions.FunctionComponent;
import ru.maklas.melnikov.engine.point.PointComponent;
import ru.maklas.melnikov.engine.point.PointType;
import ru.maklas.melnikov.functions.CustomFunction;
import ru.maklas.melnikov.functions.SineWaveFunction;
import ru.maklas.melnikov.functions.bi_functions.LogisticBiFunction;
import ru.maklas.melnikov.mnw.MNW;
import ru.maklas.melnikov.utils.gsm_lib.State;
import ru.maklas.mengine.Entity;

public class MainMenuState extends State {

    @Override
    protected void onCreate() {

        Entity a = new Entity();
        FunctionComponent fc = new FunctionComponent(new SineWaveFunction(50, 100));
        fc.color = Color.BLACK;
        a.add(fc);

        Entity b = new Entity(100, 200, 0).add(new PointComponent(PointType.RED));
        Entity c = new Entity(200, 250, 0).add(new PointComponent(PointType.BLUE));

        Entity d = new Entity().add(new BiFunctionComponent(new LogisticBiFunction(-3, 1, 1)));
        Entity e = new Entity().add(new FunctionComponent(new CustomFunction()).color(Color.BLUE));
        MNW.backgroundColor = new Color(0.95f, 0.95f, 0.95f, 1f);
        pushState(new FunctionGraphState(Array.with(a, b, c, d, e), -1000, 1000));
    }

    @Override
    protected void update(float dt) {

    }

    @Override
    protected void render(Batch batch) {

    }

    @Override
    protected void dispose() {

    }
}
