package com.demodev.fabricsk.script;

import com.demodev.fabricsk.loader.ScriptLoader;

public final class ScriptManager {

    private static boolean initialized = false;

    private ScriptManager() {}

    public static void initialize() {
        if (initialized) {
            return;
        }

        System.out.println("[Fabric.SK] Initializing ScriptManager...");

        ScriptLoader.loadAll();

        initialized = true;
    }

    public static void register(LoadedScript script) {
        ScriptRegistry.register(script);
    }

    public static LoadedScript get(String name) {
        return ScriptRegistry.get(name);
    }

    public static void unregister(String name) {
        ScriptRegistry.unregister(name);
    }

    public static void clear() {
        ScriptRegistry.clear();
    }

    public static void reloadAll() {
        clear();
        ScriptLoader.loadAll();
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
