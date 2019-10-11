package ru.maklas.melnikov.mnw;


import com.badlogic.gdx.graphics.Color;
import ru.maklas.melnikov.utils.gsm_lib.GameStateManager;

public class MNW {

    public static final String VERSION = "1.0.0";
    public static final String GAME_NAME = "Project Template";
    public static final String PACKAGE = "ru.maklas.melnikov";

    public static Color backgroundColor = Color.GRAY.cpy();
    public static Strings strings;
    public static Device device;
    public static Analytics analytics;
    public static GameSave save;
    public static GameStateManager gsm;
    public static CrashReport crash;
    public static Statistics statistics;
    public static Ads ads;

    public static long frameTimeNano;
}
