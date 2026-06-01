package com.demodev.fabricsk.event.player;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public final class RespawnEventHandler {

    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register(
            (oldPlayer, newPlayer, alive) -> {
                ScriptEvent event = new ScriptEvent("respawn");
                event.set("player", newPlayer);

                EventManager.fire(event);
            }
        );
    }
}
