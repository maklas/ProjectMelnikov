package ru.maklas.melnikov.states;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import ru.maklas.melnikov.mnw.MNW;
import ru.maklas.melnikov.user_interface.LogisticRegressionSetupUI;
import ru.maklas.melnikov.utils.gsm_lib.State;
import ru.maklas.mengine.Entity;

public class LogisticRegressionSetupState extends State {

	private LogisticRegressionSetupUI ui;

    @Override
    protected void onCreate() {
		ui = new LogisticRegressionSetupUI();

		ui.onPointButton(() -> launch(Parameters.Mode.POINT));
		ui.onCloudButton(() -> launch(Parameters.Mode.CLOUD));
		ui.onCircleButton(() -> launch(Parameters.Mode.CIRCLE));
		ui.onMultipleButton(() -> launch(Parameters.Mode.MULTIPLE));
    }

    private void launch(Parameters.Mode mode){
		Array<Entity> entities = new Array<>();
		Parameters parameters = new Parameters();
		parameters.setCloudRadius(ui.getCloudRadius());
		parameters.setCloudSize(ui.getCloudSize());
		parameters.setLearningRate(ui.getLearningRate());
		parameters.setMode(mode);
		parameters.setClassCount(ui.getClassCount());
		pushState(new LogisticRegressionState(entities, parameters));
	}

	@Override
	protected InputProcessor getInput() {
		return ui;
	}

	@Override
	public void resize(int width, int height) {
		ui.resize(width, height);
	}

	@Override
    protected void update(float dt) {
		ui.act(dt);
    }

    @Override
    protected void render(Batch batch) {
		MNW.backgroundColor = new Color(0.25f, 0.25f, 0.25f, 1f);
		ui.draw();
    }

    @Override
    protected void dispose() {
		ui.dispose();
    }
}
