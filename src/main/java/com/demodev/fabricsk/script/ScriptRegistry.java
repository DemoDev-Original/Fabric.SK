package com.demodev.fabricsk.script;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptRegistry {

    private static final Map<String, LoadedScript> SCRIPTS =
        new ConcurrentHashMap<>();

    private ScriptRegistry() {}

    public static void register(LoadedScript script) {
        SCRIPTS.put(script.getName().toLowerCase(), script);
    }

    public static LoadedScript get(String name) {
        return SCRIPTS.get(name.toLowerCase());
    }

    public static void unregister(String name) {
        SCRIPTS.remove(name.toLowerCase());
    }

    public static Collection<LoadedScript> all() {
        return SCRIPTS.values();
    }

    public static void clear() {
        SCRIPTS.clear();
    }

    public static int size() {
        return SCRIPTS.size();
    }
}
