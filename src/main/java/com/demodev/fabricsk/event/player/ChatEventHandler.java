package com.demodev.fabricsk.event.player;

import com.demodev.fabricsk.event.EventManager;
import com.demodev.fabricsk.event.ScriptEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ChatEventHandler {
   public static void register() {
         ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            ScriptEvent event = new ScriptEvent("chat");
            event.set("player", sender);
            event.set("message", message.getContent());
            EventManager.fire(event);
         });
   }
}
