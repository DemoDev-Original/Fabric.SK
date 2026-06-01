package com.demodev.fabricsk.event;

import java.util.HashMap;
import java.util.Map;

public class ScriptEvent {

    private final String name;
    private final Map<String, Object> context = new HashMap<>();

    public ScriptEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void set(String key, Object value) {
        context.put(key, value);
    }

    public Object get(String key) {
        return context.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        Object value = context.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
