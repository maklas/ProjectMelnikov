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

        Array<Entity> entities = new Array<>();
        //entities.addRow(new Entity().addRow(new FunctionComponent(new SineWaveFunction(50, 100)).color(Color.BLACK)));
        //entities.addRow(new Entity(100, 200, 0).addRow(new PointComponent(PointType.RED)));
        //entities.addRow(new Entity(200, 250, 0).addRow(new PointComponent(PointType.BLUE)));
        entities.add(new Entity().add(new BiFunctionComponent(new LogisticBiFunction(1, 1, 1)).setColor(Color.BLACK)));
        //entities.addRow(new Entity().addRow(new FunctionComponent(new CustomFunction()).color(Color.BLUE)));

        MNW.backgroundColor = new Color(0.95f, 0.95f, 0.95f, 1f);
        pushState(new FunctionGraphState(entities, new Parameters()));
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
