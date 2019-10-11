package ru.maklas.melnikov.statics;

import ru.maklas.libs.Counter;

public class ID {

    public static final int camera = 2;
    public static final int bullets = 5;

    public static Counter counterForPlayers(){
        return new Counter(100, 200);
    }

    public static Counter counterForItems() {
        return new Counter(1_000_000, 2_000_000);
    }

    public static Counter counterForEnemies(){
        return new Counter(2_000_000, 3_000_000);
    }

    public static Counter counterForEnvironment(){
        return new Counter(3_000_000, 4_000_000);
    }
}
