package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class KickEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 2) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        if (!(args[1] instanceof String reason)) {
            return;
        }

        player.networkHandler.disconnect(Text.literal(reason));
    }
}
