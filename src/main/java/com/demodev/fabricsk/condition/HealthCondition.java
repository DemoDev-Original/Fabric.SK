package com.demodev.fabricsk.condition;

import net.minecraft.server.network.ServerPlayerEntity;

public class HealthCondition implements Condition {
   public boolean evaluate(Object left, Object right) {
      if (left instanceof ServerPlayerEntity player) {
         if (right instanceof Number number) {
            return player.getHealth() > number.doubleValue();
         }
      }
      return false;
   }
}
