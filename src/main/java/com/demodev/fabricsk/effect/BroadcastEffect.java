package com.demodev.fabricsk.effect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public class BroadcastEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 2) {
            return;
        }

        if (!(args[0] instanceof MinecraftServer server)) {
            return;
        }

        if (!(args[1] instanceof String message)) {
            return;
        }

        server.getPlayerManager().broadcast(Text.literal(message), false);
    }
}
