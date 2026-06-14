package com.demodev.fabricsk.condition;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class OpCondition implements Condition {
   public boolean evaluate(Object left, Object right) {
      if (left instanceof ServerPlayerEntity player) {
         return player.hasPermissionLevel(4);
      }
      return false;
   }
}
