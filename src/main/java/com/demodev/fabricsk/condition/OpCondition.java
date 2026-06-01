package com.demodev.fabricsk.condition;

import net.minecraft.server.network.ServerPlayerEntity;

public class OpCondition implements Condition {

    @Override
    public boolean evaluate(Object left, Object right) {
        if (!(left instanceof ServerPlayerEntity player)) {
            return false;
        }

        if (player.getServer() == null) {
            return false;
        }

        return player
            .getServer()
            .getPlayerManager()
            .isOperator(player.getGameProfile());
    }
}
