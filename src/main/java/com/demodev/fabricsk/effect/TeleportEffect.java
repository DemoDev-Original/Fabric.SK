package com.demodev.fabricsk.effect;

import java.util.Set;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TeleportEffect implements Effect {

    @Override
    public void execute(Object... args) {
        if (args.length < 5) {
            return;
        }

        if (!(args[0] instanceof ServerPlayerEntity player)) {
            return;
        }

        if (!(args[1] instanceof ServerWorld world)) {
            return;
        }

        if (!(args[2] instanceof Number x)) {
            return;
        }

        if (!(args[3] instanceof Number y)) {
            return;
        }

        if (!(args[4] instanceof Number z)) {
            return;
        }

        player.teleport(
            player.getServerWorld(),
            x.doubleValue(),
            y.doubleValue(),
            z.doubleValue(),
            Set.of(),
            player.getYaw(),
            player.getPitch(),
            true
        );
    }
}
