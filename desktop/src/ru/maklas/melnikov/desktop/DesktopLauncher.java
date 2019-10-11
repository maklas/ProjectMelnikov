package ru.maklas.melnikov.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.melnikov.ProjectTemplate;
import ru.maklas.melnikov.mnw.MNW;
import ru.maklas.melnikov.states.FunctionGraphState;
import ru.maklas.melnikov.states.MainMenuState;
import ru.maklas.melnikov.utils.Log;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 360;
        config.width = 640;
        config.resizable = true;
        config.title = MNW.GAME_NAME;
        Log.logger = new FileLogger();
        new LwjglApplication(new ProjectTemplate(new MainMenuState()), config);
    }
}
