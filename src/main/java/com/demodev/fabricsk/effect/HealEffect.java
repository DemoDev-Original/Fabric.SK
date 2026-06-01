package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;

public class HealEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length == 0) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        player.setHealth(player.getMaxHealth());
    }
}
