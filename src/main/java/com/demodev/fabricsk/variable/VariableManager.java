package com.demodev.fabricsk.variable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class VariableManager {
   private static final Map<String, ScriptVariable> GLOBAL = new ConcurrentHashMap();
   private static final Map<String, ScriptVariable> SCRIPT = new ConcurrentHashMap();

   private VariableManager() {
   }

   public static void setGlobal(String name, Object value, VariableType type) {
      GLOBAL.put(name.toLowerCase(), new ScriptVariable(name, value, VariableScope.GLOBAL, type));
   }

   public static ScriptVariable getGlobal(String name) {
      return (ScriptVariable)GLOBAL.get(name.toLowerCase());
   }

   public static void setScript(String script, String name, Object value, VariableType type) {
      SCRIPT.put(script.toLowerCase() + ":" + name.toLowerCase(), new ScriptVariable(name, value, VariableScope.SCRIPT, type));
   }

   public static ScriptVariable getScript(String script, String name) {
      Map var10000 = SCRIPT;
      String var10001 = script.toLowerCase();
      return (ScriptVariable)var10000.get(var10001 + ":" + name.toLowerCase());
   }

   public static void clearScript(String script) {
      String prefix = script.toLowerCase() + ":";
      SCRIPT.entrySet().removeIf((e) -> ((String)e.getKey()).startsWith(prefix));
   }

   public static void clearAll() {
      GLOBAL.clear();
      SCRIPT.clear();
   }

   public static int globalCount() {
      return GLOBAL.size();
   }

   public static int scriptCount() {
      return SCRIPT.size();
   }
}
