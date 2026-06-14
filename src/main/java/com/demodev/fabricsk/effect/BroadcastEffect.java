package com.demodev.fabricsk.effect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class BroadcastEffect implements Effect {
   public void execute(Object... args) {
      if (args.length >= 2) {
         Object var2 = args[0];
         if (var2 instanceof MinecraftServer server) {
            Object var3 = args[1];
               if (var3 instanceof String message) {
                  server.getPlayerManager().broadcast(Text.literal(message), false);
               }
         }
      }
   }
}
