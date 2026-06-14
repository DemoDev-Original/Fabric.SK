package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SendEffect implements Effect {
   public void execute(Object... args) {
      if (args.length >= 2) {
         Object arg0 = args[0];
         if (arg0 instanceof ServerPlayerEntity player) {
            Object arg1 = args[1];
            if (arg1 instanceof String message) {
               player.sendMessage(Text.literal(message), false);
            }
         }
      }
   }
}
