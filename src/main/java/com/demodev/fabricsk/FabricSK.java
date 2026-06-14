package com.demodev.fabricsk;

import com.demodev.fabricsk.command.FabricSkriptCommand;
import com.demodev.fabricsk.event.player.ChatEventHandler;
import com.demodev.fabricsk.event.player.DeathEventHandler;
import com.demodev.fabricsk.event.player.JoinEventHandler;
import com.demodev.fabricsk.event.player.QuitEventHandler;
import com.demodev.fabricsk.event.player.RespawnEventHandler;
import com.demodev.fabricsk.event.server.TickEventHandler;
import com.demodev.fabricsk.scheduler.ScriptScheduler;
import com.demodev.fabricsk.script.ScriptManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricSK implements ModInitializer {
   public void onInitialize() {
      ScriptManager.initialize();
      FabricSkriptCommand.register();
      ServerTickEvents.END_SERVER_TICK.register((ServerTickEvents.EndTick)(server) -> ScriptScheduler.tick());
      System.out.println("[Fabric.SK] 0.2.5-ALPHA loaded!");
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
