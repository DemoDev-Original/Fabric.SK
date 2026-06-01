package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;

public class DamageEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 2) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        if (!(args[1] instanceof Number amount)) {
            return;
        }

        player.damage(
            player.getServerWorld(),
            player.getDamageSources().generic(),
            amount.floatValue()
        );
    }
}
