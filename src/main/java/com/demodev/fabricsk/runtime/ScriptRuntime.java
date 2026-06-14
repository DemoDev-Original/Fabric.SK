package com.demodev.fabricsk.runtime;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptRuntime {
   private static final Map<String, RuntimeCommand> COMMANDS = new ConcurrentHashMap();
   private static final Map<String, RuntimeEvent> EVENTS = new ConcurrentHashMap();

   private ScriptRuntime() {
   }

   public static void registerCommand(RuntimeCommand command) {
      COMMANDS.put(command.getName().toLowerCase(), command);
   }

   public static RuntimeCommand getCommand(String name) {
      return (RuntimeCommand)COMMANDS.get(name.toLowerCase());
   }

   public static Collection<RuntimeCommand> getCommands() {
      return Collections.unmodifiableCollection(COMMANDS.values());
   }

   public static void unregisterCommand(String name) {
      COMMANDS.remove(name.toLowerCase());
   }

   public static void registerEvent(RuntimeEvent event) {
      EVENTS.put(event.getEventName().toLowerCase(), event);
   }

   public static RuntimeEvent getEvent(String name) {
      return (RuntimeEvent)EVENTS.get(name.toLowerCase());
   }

   public static Collection<RuntimeEvent> getEvents() {
      return Collections.unmodifiableCollection(EVENTS.values());
   }

   public static void unregisterEvent(String name) {
      EVENTS.remove(name.toLowerCase());
   }

   public static void clear() {
      COMMANDS.clear();
      EVENTS.clear();
   }

   public static int commandCount() {
      return COMMANDS.size();
   }

   public static int eventCount() {
      return EVENTS.size();
   }
}
