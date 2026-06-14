package com.demodev.fabricsk.command;

import com.demodev.fabricsk.script.ScriptInfo;
import com.demodev.fabricsk.script.ScriptManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class FabricSkriptCommand {
   private FabricSkriptCommand() {
   }

   public static void register() {
      CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
         registerRoot(dispatcher);
         dispatcher.register(CommandManager.literal("skript").redirect(dispatcher.getRoot().getChild("fabricskript")));
         dispatcher.register(CommandManager.literal("fsk").redirect(dispatcher.getRoot().getChild("fabricskript")));
         dispatcher.register(CommandManager.literal("sk").redirect(dispatcher.getRoot().getChild("fabricskript")));
      });
   }

   private static void registerRoot(CommandDispatcher<ServerCommandSource> dispatcher) {
      dispatcher.register(
         CommandManager.literal("fabricskript")
            .then(CommandManager.literal("help").executes(ctx -> help(ctx.getSource())))
            .then(CommandManager.literal("version").executes(ctx -> version(ctx.getSource())))
            .then(CommandManager.literal("list").executes(ctx -> list(ctx.getSource())))
            .then(CommandManager.literal("reloadall").executes(ctx -> reloadAll(ctx.getSource())))
            .then(CommandManager.literal("errors").executes(ctx -> errors(ctx.getSource())))
            .then(CommandManager.literal("reload")
               .then(CommandManager.argument("script", StringArgumentType.greedyString())
                  .executes(ctx -> reload(ctx.getSource(), StringArgumentType.getString(ctx, "script")))))
            .then(CommandManager.literal("load")
               .then(CommandManager.argument("script", StringArgumentType.greedyString())
                  .executes(ctx -> load(ctx.getSource(), StringArgumentType.getString(ctx, "script")))))
            .then(CommandManager.literal("unload")
               .then(CommandManager.argument("script", StringArgumentType.greedyString())
                  .executes(ctx -> unload(ctx.getSource(), StringArgumentType.getString(ctx, "script")))))
            .then(CommandManager.literal("validate")
               .then(CommandManager.argument("script", StringArgumentType.greedyString())
                  .executes(ctx -> validate(ctx.getSource(), StringArgumentType.getString(ctx, "script")))))
            .then(CommandManager.literal("info")
               .then(CommandManager.argument("script", StringArgumentType.greedyString())
                  .executes(ctx -> info(ctx.getSource(), StringArgumentType.getString(ctx, "script")))))
            .then(CommandManager.literal("toggle")
               .then(CommandManager.argument("feature", StringArgumentType.word())
                  .then(CommandManager.argument("mode", StringArgumentType.word())
                     .then(CommandManager.argument("script", StringArgumentType.greedyString())
                        .executes(ctx -> toggle(ctx.getSource(), StringArgumentType.getString(ctx, "feature"), StringArgumentType.getString(ctx, "mode"), StringArgumentType.getString(ctx, "script"))))))));
   }

   private static int help(ServerCommandSource source) {
      source.sendFeedback(() -> Text.literal("Fabric.SK Commands\n\n/fsk help\n/fsk version\n/fsk list\n/fsk reload <script>\n/fsk reloadall\n/fsk load <script>\n/fsk unload <script>\n/fsk validate <script>\n/fsk info <script>\n/fsk errors\n/fsk toggle syntaxcorrect <auto|partial> <script>\n"), false);
      return 1;
   }

   private static int version(ServerCommandSource source) {
      source.sendFeedback(() -> Text.literal("--- Fabric.SK Version/Info ---"), false);
      source.sendFeedback(() -> Text.literal("Fabric.SK: 0.2.5-ALPHA"), false);
      source.sendFeedback(() -> Text.literal("Author: DemoDev"), false);
      return 1;
   }

   private static int list(ServerCommandSource source) {
      source.sendFeedback(() -> Text.literal("Script listing not implemented yet."), false);
      return 1;
   }

   private static int reload(ServerCommandSource source, String script) {
      try {
         boolean success = ScriptManager.reload(script);
         source.sendFeedback(() -> Text.literal(success ? "Reloaded script: " + script : "Failed to reload script: " + script), false);
         return success ? 1 : 0;
      } catch (Exception e) {
         source.sendFeedback(() -> Text.literal("Error reloading script: " + script), false);
         e.printStackTrace();
         return 0;
      }
   }

   private static int load(ServerCommandSource source, String script) {
      try {
         boolean success = ScriptManager.load(script);
         source.sendFeedback(() -> Text.literal(success ? "Loaded script: " + script : "Failed to load script: " + script), false);
         return success ? 1 : 0;
      } catch (Exception e) {
         e.printStackTrace();
         return 0;
      }
   }

   private static int unload(ServerCommandSource source, String script) {
      boolean success = ScriptManager.unload(script);
      source.sendFeedback(() -> Text.literal(success ? "Unloaded script: " + script : "Script not found: " + script), false);
      return success ? 1 : 0;
   }

   private static int validate(ServerCommandSource source, String script) {
      try {
         boolean valid = ScriptManager.validate(script);
         source.sendFeedback(() -> Text.literal(valid ? "Script is valid: " + script : "Script has errors: " + script), false);
         return valid ? 1 : 0;
      } catch (Exception e) {
         e.printStackTrace();
         return 0;
      }
   }

   private static int info(ServerCommandSource source, String script) {
      ScriptInfo info = ScriptManager.getInfo(script);
      if (info == null) {
         source.sendFeedback(() -> Text.literal("No script found: " + script), false);
         return 0;
      } else {
         source.sendFeedback(() -> Text.literal("Script: " + info.name()), false);
         source.sendFeedback(() -> Text.literal("Status: " + info.status()), false);
         return 1;
      }
   }

   private static int errors(ServerCommandSource source) {
      source.sendFeedback(() -> Text.literal("Recent script errors."), false);
      return 1;
   }

   private static int reloadAll(ServerCommandSource source) {
      try {
         int count = ScriptManager.reloadAll();
         source.sendFeedback(() -> Text.literal("Reloaded " + count + " scripts."), false);
         return 1;
      } catch (Exception e) {
         e.printStackTrace();
         return 0;
      }
   }

   private static int toggle(ServerCommandSource source, String feature, String mode, String script) {
      source.sendFeedback(() -> Text.literal("Feature " + feature + " set to " + mode + " for " + script), false);
      return 1;
   }
}
