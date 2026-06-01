package com.demodev.fabricsk;

import com.demodev.fabricsk.command.FabricSkriptCommand;
import com.demodev.fabricsk.effect.EffectRegistry;
import com.demodev.fabricsk.event.player.*;
import com.demodev.fabricsk.event.server.*;
import com.demodev.fabricsk.loader.ScriptLoader;
import com.demodev.fabricsk.scheduler.ScriptScheduler;
import com.demodev.fabricsk.script.ScriptManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricSK implements ModInitializer {

    @Override
    public void onInitialize() {
        ScriptManager.initialize();

        FabricSkriptCommand.register();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ScriptScheduler.tick();
        });

        System.out.println("[Fabric.SK] 0.2.0 loaded!");
    }

    private static void registerEvents() {
        JoinEventHandler.register();
        QuitEventHandler.register();
        ChatEventHandler.register();
        DeathEventHandler.register();
        RespawnEventHandler.register();

        TickEventHandler.register();
    }
}
