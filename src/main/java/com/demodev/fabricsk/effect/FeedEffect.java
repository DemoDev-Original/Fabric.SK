package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;

public class FeedEffect implements Effect {
   public void execute(Object... args) {
      if (args.length != 0) {
         Object arg0 = args[0];
         if (arg0 instanceof ServerPlayerEntity player) {
            player.getHungerManager().setFoodLevel(20);
         }
      }
   }
}
