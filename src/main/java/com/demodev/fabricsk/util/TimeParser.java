package com.demodev.fabricsk.util;

import java.util.Locale;

public final class TimeParser {
   private TimeParser() {
   }

   public static long parseTicks(String text) {
      if (text != null && !text.isBlank()) {
         String[] split = text.trim().toLowerCase(Locale.ROOT).split("\\s+");
         if (split.length < 2) {
            throw new IllegalArgumentException("Invalid time format: " + text);
         } else {
            long value = Long.parseLong(split[0]);
            long var10000;
            switch (split[1]) {
               case "tick":
               case "ticks":
                  var10000 = value;
                  break;
               case "second":
               case "seconds":
                  var10000 = value * 20L;
                  break;
               case "minute":
               case "minutes":
                  var10000 = value * 20L * 60L;
                  break;
               case "hour":
               case "hours":
                  var10000 = value * 20L * 60L * 60L;
                  break;
               default:
                  throw new IllegalArgumentException("Unknown time unit: " + split[1]);
            }

            return var10000;
         }
      } else {
         throw new IllegalArgumentException("Time string cannot be empty.");
      }
   }
}
