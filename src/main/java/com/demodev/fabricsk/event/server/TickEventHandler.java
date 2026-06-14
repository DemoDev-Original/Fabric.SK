package com.demodev.fabricsk.event.server;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class TickEventHandler {
   public static void register() {
      ServerTickEvents.END_SERVER_TICK.register((ServerTickEvents.EndTick)(server) -> {
         ScriptEvent event = new ScriptEvent("tick");
         event.set("server", server);
         EventManager.fire(event);
      });
   }
}
