package com.demodev.fabricsk.effect;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EffectRegistry {
   private static final Map<String, Effect> EFFECTS = new ConcurrentHashMap();

   private EffectRegistry() {
   }

   public static void register(String pattern, Effect effect) {
      EFFECTS.put(pattern.toLowerCase(), effect);
   }

   public static Effect get(String pattern) {
      return (Effect)EFFECTS.get(pattern.toLowerCase());
   }

   public static boolean contains(String pattern) {
      return EFFECTS.containsKey(pattern.toLowerCase());
   }

   public static Map<String, Effect> getEffects() {
      return Map.copyOf(EFFECTS);
   }

   public static void clear() {
      EFFECTS.clear();
   }

   public static void registerDefaults() {
      register("send", new SendEffect());
      register("broadcast", new BroadcastEffect());
      register("teleport", new TeleportEffect());
      register("kick", new KickEffect());
      register("heal", new HealEffect());
      register("feed", new FeedEffect());
      register("damage", new DamageEffect());
      register("execute command", new ExecuteCommandEffect());
      register("give item", new GiveItemEffect());
   }
}
