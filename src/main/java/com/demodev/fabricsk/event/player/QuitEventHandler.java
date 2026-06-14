package com.demodev.fabricsk.event.player;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public final class QuitEventHandler {
   public static void register() {
      ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
         ServerPlayerEntity player = handler.getPlayer();
         ScriptEvent event = new ScriptEvent("quit");
         event.set("player", player);
         event.set("server", server);
         EventManager.fire(event);
      });
   }
}
