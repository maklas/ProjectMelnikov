package ru.maklas.melnikov.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.melnikov.ProjectMelnikov;
import ru.maklas.melnikov.states.LogisticRegressionSetupState;
import ru.maklas.melnikov.utils.Log;

public class LRDesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 360;
        config.width = 640;
        config.resizable = true;
        config.title = "Logistic Regression";
        Log.logger = new FileLogger();
        new LwjglApplication(new ProjectMelnikov(new LogisticRegressionSetupState()), config);
    }
}
