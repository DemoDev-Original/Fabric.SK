package com.demodev.fabricsk.script;

import com.demodev.fabricsk.loader.ScriptLoader;

public final class ScriptManager {
   private static boolean initialized = false;

   private ScriptManager() {
   }

   public static void initialize() {
      if (initialized) {
         System.out.println("[Fabric.SK] ScriptManager already initialized.");
      } else {
         try {
            System.out.println("[Fabric.SK] Initializing ScriptManager...");
            ScriptLoader.loadAll();
            initialized = true;
            System.out.println("[Fabric.SK] ScriptManager ready.");
         } catch (Exception e) {
            System.err.println("[Fabric.SK] Failed to initialize ScriptManager.");
            e.printStackTrace();
         }

      }
   }

   public static boolean load(String name) {
      try {
         LoadedScript script = ScriptLoader.loadSingle(name);
         if (script == null) {
            return false;
         } else {
            register(script);
            return true;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public static boolean reload(String name) {
      try {
         unregister(name);
         LoadedScript script = ScriptLoader.loadSingle(name);
         if (script == null) {
            return false;
         } else {
            register(script);
            return true;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public static boolean unload(String name) {
      try {
         if (get(name) == null) {
            return false;
         } else {
            unregister(name);
            return true;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public static boolean validate(String name) {
      try {
         LoadedScript script = ScriptLoader.preview(name);
         return script != null;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   public static ScriptInfo getInfo(String name) {
      LoadedScript script = get(name);
      return script == null ? null : new ScriptInfo(script.getName(), script.isEnabled() ? "ENABLED" : "DISABLED", 0L);
   }

   public static void register(LoadedScript script) {
      ScriptRegistry.register(script);
   }

   public static LoadedScript get(String name) {
      return ScriptRegistry.get(name);
   }

   public static void unregister(String name) {
      ScriptRegistry.unregister(name);
   }

   public static void clear() {
      ScriptRegistry.clear();
   }

   public static int reloadAll() {
      try {
         clear();
         return ScriptLoader.loadAll();
      } catch (Exception e) {
         e.printStackTrace();
         return 0;
      }
   }

   public static boolean isInitialized() {
      return initialized;
   }
}
