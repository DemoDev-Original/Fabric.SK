package com.demodev.fabricsk.runtime;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {
   private final Map<String, Object> variables = new HashMap();

   public void setVariable(String name, Object value) {
      this.variables.put(name, value);
   }

   public Object getVariable(String name) {
      return this.variables.get(name);
   }

   public boolean hasVariable(String name) {
      return this.variables.containsKey(name);
   }
}
