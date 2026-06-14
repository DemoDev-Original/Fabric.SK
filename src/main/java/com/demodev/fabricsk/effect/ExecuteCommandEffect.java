package com.demodev.fabricsk.effect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class ExecuteCommandEffect implements Effect {
   public void execute(Object... args) {
      if (args.length >= 2) {
         Object var3 = args[0];
         if (var3 instanceof MinecraftServer server) {
            Object var4 = args[1];
            if (var4 instanceof String command) {
               ServerCommandSource source = server.getCommandSource();
               CommandManager commandManager = server.getCommandManager();
               commandManager.executeWithPrefix(source, command);
            }
         }
      }
   }
}
