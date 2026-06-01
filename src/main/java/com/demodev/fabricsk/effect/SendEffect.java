package com.demodev.fabricsk.effect;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SendEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 2) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        if (!(args[1] instanceof String message)) {
            return;
        }

        player.sendMessage(Text.literal(message), false);
    }
}
