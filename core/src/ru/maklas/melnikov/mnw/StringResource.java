package ru.maklas.melnikov.mnw;

import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.melnikov.utils.Config;
import ru.maklas.melnikov.utils.Log;
import ru.maklas.melnikov.utils.StringUtils;

public class StringResource {

    ObjectMap<String, String> strings = new ObjectMap<>();

    public String get(String key){
        String s = strings.get(key);
        if (Config.CHECK_STRINGS) {
            if (StringUtils.isEmpty(s)) {
                Log.error("No string for key: " + key);
                return "*" + key + "*";
            }
        }
        return s;
    }

    public String get(String key, String def){
        String s = strings.get(key);
        if (Config.CHECK_STRINGS) {
            if (StringUtils.isEmpty(s)) {
                Log.error("No string for key: " + key);
                return def;
            }
        }
        return s;
    }

}
