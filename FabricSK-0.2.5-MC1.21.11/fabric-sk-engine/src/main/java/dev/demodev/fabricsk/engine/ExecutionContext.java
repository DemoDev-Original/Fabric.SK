package dev.demodev.fabricsk.engine;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
public record ExecutionContext(CommandSourceStack source, ServerPlayer player) { }
