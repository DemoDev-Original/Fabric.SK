package com.demodev.fabricsk.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptManager {

    private static final Map<String, LoadedScript> LOADED_SCRIPTS =
        new ConcurrentHashMap<>();

    private ScriptManager() {}

    public static void initialize() {
        LOADED_SCRIPTS.clear();
    }

    public static void register(LoadedScript script) {
        LOADED_SCRIPTS.put(script.getName().toLowerCase(), script);
    }

    public static LoadedScript get(String scriptName) {
        return LOADED_SCRIPTS.get(scriptName.toLowerCase());
    }

    public static boolean exists(String scriptName) {
        return LOADED_SCRIPTS.containsKey(scriptName.toLowerCase());
    }

    public static void unregister(String scriptName) {
        LOADED_SCRIPTS.remove(scriptName.toLowerCase());
    }

    public static Collection<LoadedScript> getAll() {
        return Collections.unmodifiableCollection(LOADED_SCRIPTS.values());
    }

    public static int count() {
        return LOADED_SCRIPTS.size();
    }

    public static int loadedCount() {
        return (int) LOADED_SCRIPTS.values()
            .stream()
            .filter(LoadedScript::isLoaded)
            .count();
    }

    public static int enabledCount() {
        return (int) LOADED_SCRIPTS.values()
            .stream()
            .filter(LoadedScript::isEnabled)
            .count();
    }

    public static void clear() {
        LOADED_SCRIPTS.clear();
    }

    public static void disableAll() {
        for (LoadedScript script : LOADED_SCRIPTS.values()) {
            script.setEnabled(false);
        }
    }

    public static void enableAll() {
        for (LoadedScript script : LOADED_SCRIPTS.values()) {
            script.setEnabled(true);
        }
    }
}
