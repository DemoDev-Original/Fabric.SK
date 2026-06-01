package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;

public class FeedEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length == 0) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        player.getHungerManager().setFoodLevel(20);
    }
}
