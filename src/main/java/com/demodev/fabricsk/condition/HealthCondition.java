package com.demodev.fabricsk.condition;

import net.minecraft.server.network.ServerPlayerEntity;

public class HealthCondition implements Condition {

    @Override
    public boolean evaluate(Object left, Object right) {
        if (!(left instanceof ServerPlayerEntity player)) {
            return false;
        }

        if (!(right instanceof Number number)) {
            return false;
        }

        return player.getHealth() > number.doubleValue();
    }
}
