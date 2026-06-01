package com.demodev.fabricsk.script;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {

    private final Map<String, Object> values = new HashMap<>();

    public void set(String key, Object value) {
        values.put(key, value);
    }

    public Object get(String key) {
        return values.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = values.get(key);

        if (type.isInstance(value)) {
            return type.cast(value);
        }

        return null;
    }

    public Map<String, Object> raw() {
        return values;
    }

    public void clear() {
        values.clear();
    }
}
