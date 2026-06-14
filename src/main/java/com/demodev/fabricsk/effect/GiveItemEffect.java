package com.demodev.fabricsk.effect;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GiveItemEffect implements Effect {
   public void execute(Object... args) {
      if (args.length >= 2) {
         Object arg0 = args[0];
         if (arg0 instanceof ServerPlayerEntity player) {
            Object arg1 = args[1];
            if (arg1 instanceof Item item) {
               player.giveItemStack(new ItemStack(item));
            }
         }
      }
   }
}
