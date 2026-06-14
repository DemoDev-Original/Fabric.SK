package com.demodev.fabricsk.command.util;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandUtils {
   public static String getArg(String[] args, int index, String fallback) {
      return args != null && args.length > index ? args[index] : fallback;
   }

   public static boolean hasArgs(String[] args, int required) {
      return args != null && args.length >= required;
   }

   public static void sendUsage(ServerCommandSource source, String usage) {
      source.sendFeedback(() -> Text.literal("Usage: " + usage), false);
   }
}
