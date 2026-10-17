package dev.demodev.fabricsk.engine;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record ExecutionContext(CommandSourceStack source, ServerPlayer player, ServerPlayer attacker,
                               LivingEntity victim, ServerPlayer argumentPlayer, String argumentName, Entity loopEntity, float damageTaken) {
    public ExecutionContext(CommandSourceStack source, ServerPlayer player, ServerPlayer attacker,
                            LivingEntity victim, ServerPlayer argumentPlayer, String argumentName) {
        this(source, player, attacker, victim, argumentPlayer, argumentName, null, 0.0F);
    }
    public ExecutionContext(CommandSourceStack source, ServerPlayer player) {
        this(source, player, player, null, null, null, null, 0.0F);
    }
    public ExecutionContext withPlayer(ServerPlayer nextPlayer) {
        return new ExecutionContext(source, nextPlayer, attacker, victim, argumentPlayer, argumentName, loopEntity, damageTaken);
    }
    public ExecutionContext withLoopEntity(Entity entity) {
        return new ExecutionContext(source, player, attacker, victim, argumentPlayer, argumentName, entity, damageTaken);
    }
}
