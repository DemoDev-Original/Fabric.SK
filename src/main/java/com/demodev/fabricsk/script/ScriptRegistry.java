package com.demodev.fabricsk.script;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptRegistry {
   private static final Map<String, LoadedScript> SCRIPTS = new ConcurrentHashMap();

   private ScriptRegistry() {
   }

   public static void register(LoadedScript script) {
      if (script != null) {
         SCRIPTS.put(script.getName().toLowerCase(), script);
      }
   }

   public static LoadedScript get(String name) {
      return name == null ? null : (LoadedScript)SCRIPTS.get(name.toLowerCase());
   }

   public static void unregister(String name) {
      if (name != null) {
         SCRIPTS.remove(name.toLowerCase());
      }
   }

   public static Collection<LoadedScript> all() {
      return SCRIPTS.values();
   }

   public static void clear() {
      SCRIPTS.clear();
   }

   public static int size() {
      return SCRIPTS.size();
   }

   public static boolean contains(String name) {
      return name == null ? false : SCRIPTS.containsKey(name.toLowerCase());
   }
}
