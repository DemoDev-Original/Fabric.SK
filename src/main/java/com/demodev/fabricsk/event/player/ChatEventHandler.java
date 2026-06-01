package com.demodev.fabricsk.event.player;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ChatEventHandler {

    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            ServerPlayerEntity player = sender;

            ScriptEvent event = new ScriptEvent("chat");
            event.set("player", player);
            event.set("message", message.getContent().getString());

            EventManager.fire(event);
        });
    }
}
