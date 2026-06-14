package com.demodev.fabricsk.event;

import java.util.HashMap;
import java.util.Map;

public class ScriptEvent {
   private final String name;
   private final Map<String, Object> context = new HashMap();

   public ScriptEvent(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void set(String key, Object value) {
      this.context.put(key, value);
   }

   public Object get(String key) {
      return this.context.get(key);
   }

   public <T> T get(String key, Class<T> type) {
      Object value = this.context.get(key);
      return (T)(type.isInstance(value) ? type.cast(value) : null);
   }

   public Map<String, Object> getContext() {
      return this.context;
   }
}
