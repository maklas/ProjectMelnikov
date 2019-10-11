package ru.maklas.melnikov.utils.gsm_lib;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

/** Allows to run multiple states in parallel **/
public class MultiState extends State {

    private final Array<State> states;
    private InputMultiplexer multiplexer = new InputMultiplexer();

    public MultiState(State... states) {
        this.states = Array.with(states);
    }

    @Override
    void inject(GameStateManager gsm, Batch batch) {
        for (State state : states) {
            state.inject(gsm, batch);
        }
        super.inject(gsm, batch);
    }

    @Override
    protected void onCreate() {
        for (State state : states) {
            state.onCreate();
        }
    }

    @Override
    protected InputProcessor getInput() {
        multiplexer.clear();
        for (State state : states) {
            InputProcessor input = state.getInput();
            if (input != null){
                multiplexer.addProcessor(input);
            }
        }
        return multiplexer;
    }

    @Override
    protected void update(float dt) {
        for (State state : states) {
            state.update(dt);
        }
    }

    @Override
    protected void render(Batch batch) {
        for (State state : states) {
            state.render(batch);
        }
    }

    @Override
    protected void onPause() {
        for (State state : states) {
            state.onPause();
        }
    }

    @Override
    protected void onResume(State from) {
        for (State state : states) {
            state.onResume(from);
        }
    }

    @Override
    protected void toBackground() {
        for (State state : states) {
            state.toBackground();
        }
    }

    @Override
    protected void toForeground() {
        for (State state : states) {
            state.toForeground();
        }
    }

    @Override
    public void resize(int width, int height) {
        for (State state : states) {
            state.resize(width, height);
        }
    }

    @Override
    protected void dispose() {
        for (State state : states) {
            state.dispose();
        }
    }
}
