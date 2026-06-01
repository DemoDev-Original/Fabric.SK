package com.demodev.fabricsk;

import com.demodev.fabricsk.command.FabricSkriptCommand;
import com.demodev.fabricsk.loader.ScriptLoader;
import com.demodev.fabricsk.script.ScriptManager;
import net.fabricmc.api.ModInitializer;

public class FabricSK implements ModInitializer {

    @Override
    public void onInitialize() {
        ScriptManager.initialize();
        FabricSkriptCommand.register();
        ScriptLoader.loadAll();

        System.out.println("[Fabric.SK] 0.2-SNAPSHOT loaded");
    }
}
