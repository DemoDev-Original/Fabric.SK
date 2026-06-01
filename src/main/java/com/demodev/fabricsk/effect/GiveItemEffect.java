package com.demodev.fabricsk.effect;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class GiveItemEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 2) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        if (!(args[1] instanceof Item item)) {
            return;
        }

        player.giveItemStack(new ItemStack(item));
    }
}
