package com.demodev.fabricsk.event.player;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public final class DeathEventHandler {
   public static void register() {
      ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
         if (entity instanceof ServerPlayerEntity player) {
            ScriptEvent event = new ScriptEvent("death");
            event.set("player", player);
            event.set("source", source);
            EventManager.fire(event);
         }
      });
   }
}
