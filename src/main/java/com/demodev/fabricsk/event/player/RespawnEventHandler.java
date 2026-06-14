package com.demodev.fabricsk.event.player;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public final class RespawnEventHandler {
   public static void register() {
      ServerPlayerEvents.AFTER_RESPAWN.register((ServerPlayerEvents.AfterRespawn)(oldPlayer, newPlayer, alive) -> {
         ScriptEvent event = new ScriptEvent("respawn");
         event.set("player", newPlayer);
         EventManager.fire(event);
      });
   }
}
