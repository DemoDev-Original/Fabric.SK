package com.demodev.fabricsk.script;

import java.util.HashMap;
import java.util.Map;

public class ScriptContext {
   private final Map<String, Object> values = new HashMap();

   public void set(String key, Object value) {
      this.values.put(key, value);
   }

   public Object get(String key) {
      return this.values.get(key);
   }

   public <T> T get(String key, Class<T> type) {
      Object value = this.values.get(key);
      return (T)(type.isInstance(value) ? type.cast(value) : null);
   }

   public Map<String, Object> raw() {
      return this.values;
   }

   public void clear() {
      this.values.clear();
   }
}
