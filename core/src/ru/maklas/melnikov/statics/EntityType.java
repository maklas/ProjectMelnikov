package ru.maklas.melnikov.statics;


import ru.maklas.mengine.Entity;

public class EntityType {

    public static final int BACKGROUND = 1;
    public static final int CHARACTER = 2;
    public static final int PROJECTILE = 3;
    public static final int ENEMY = 4;
    public static final int ITEM = 5;
    public static final int ENVIRONMENT = 6;

    private static final EntityType[] map;

    static {

        //masks
        int none = 1;
        int background = 2;
        int player = 4;
        int projectile = 8;
        int enemy = 16;
        int item = 32;
        int environment = 64;

        int _placeHolder8 = 128;
        int _placeHolder7 = 256;
        int _placeHolder6 = 512;
        int _placeHolder5 = 1024;
        int _placeHolder4 = 2048;
        int _placeHolder3 = 4096;
        int _placeHolder2 = 8192;
        int _placeHolder1 = 16384;




        map = new EntityType[16];
        map[BACKGROUND]     = new EntityType(background, none);
        map[CHARACTER]      = new EntityType(player, enemy | item | environment);
        map[PROJECTILE]     = new EntityType(projectile, enemy | environment | item);
        map[ENEMY]          = new EntityType(enemy, player | projectile | environment);
        map[ITEM]           = new EntityType(item, player);
        map[ENVIRONMENT]    = new EntityType(environment, player | projectile | enemy);
        //Маски можно перечислять через "|", чтобы их комбинировать: new EntityType(bird, pipe | coin | feather)
    }

    public static EntityType of(int code){
        if (code == 0){
            throw new RuntimeException("Code must be 1 or larger");
        }
        return map[code];
    }

    public static EntityType of(int code, EntityType def){
        if (code > 0 && code < map.length) {
            return map[code];
        }
        return def;
    }

    public static boolean isOneOf(Entity e, int... types){
        for (int type : types) {
            if (e.type == type) return true;
        }
        return false;
    }

    public final short category;
    public final short mask;

    public EntityType(int category, int mask) {
        this.category = (short) category;
        this.mask = (short) mask;
    }


    public static String typeToString(int code) {
        switch (code) {
            case BACKGROUND: return "BACKGROUND";
            case CHARACTER: return "CHARACTER";
            case PROJECTILE: return "PROJECTILE";
            case ENEMY: return "ENEMY";
            case ITEM: return "ITEM";
            default: return "UNKNOWN";
        }
    }
}
