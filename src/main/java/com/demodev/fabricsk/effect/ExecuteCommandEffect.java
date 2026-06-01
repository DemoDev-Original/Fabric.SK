package com.demodev.fabricsk.effect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

public class ExecuteCommandEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 2) {
            return;
        }

        if (!(args[0] instanceof MinecraftServer server)) {
            return;
        }

        if (!(args[1] instanceof String command)) {
            return;
        }

        ServerCommandSource source = server.getCommandSource();

        server.getCommandManager().executeWithPrefix(source, command);
    }
}
