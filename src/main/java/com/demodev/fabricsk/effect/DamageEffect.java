package com.demodev.fabricsk.effect;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DamageEffect implements Effect {
   public void execute(Object... args) {
      if (args.length >= 2) {
         Object var3 = args[0];
         if (var3 instanceof ServerPlayerEntity player) {
            Object var4 = args[1];
            if (var4 instanceof Number amount) {
               float dmg = amount.floatValue();
               float newHealth = player.getHealth() - dmg;
               player.setHealth(Math.max(0.0F, newHealth));
            }
         }
      }
   }
}
