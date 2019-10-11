package ru.maklas.melnikov.statics;

public class Game {


    public static final float scale = 200;
    public static final float scaleReversed = 1 / scale;

    public static final float width = 720;
    public static float height = 1280;


    public static final float hWidth = width / 2;
    public static float hHeight = height/2;

    public static float maxVelocity(){
        return 120 * scale;
    }

}
