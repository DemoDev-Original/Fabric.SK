package dev.demodev.fabricsk.engine;

import net.fabricmc.api.ModInitializer;

/** Entry point for the nested Engine mod. Commands are registered by the main Fabric.SK mod. */
public final class FabricSKEngine implements ModInitializer {
    public static final String MOD_ID = "fabricsk-engine";
    public static final String VERSION = "0.2x";
    private static final FabricSKEngine INSTANCE = new FabricSKEngine();
    private final ScriptManager scriptManager = new ScriptManager();
    private boolean initialized;

    public FabricSKEngine() { }
    public static FabricSKEngine getInstance() { return INSTANCE; }

    @Override public void onInitialize() { initialize(); }

    public synchronized void initialize() {
        if (initialized) return;
        initialized = true;
        scriptManager.initialize();
        System.out.println("[Fabric.SK Engine] Engine " + VERSION + " initialized.");
    }

    public String getVersion() { return VERSION; }
    public ScriptManager getScriptManager() { return scriptManager; }
    public ScriptManager.OperationResult reload() { return scriptManager.reload(); }
    public ScriptManager.OperationResult load(String name) { return scriptManager.load(name); }
    public ScriptManager.OperationResult parse() { return scriptManager.parseAll(); }
    public ScriptManager.OperationResult validate() { return scriptManager.validateAll(); }
}
