package com.demodev.fabricsk.event;

import com.demodev.fabricsk.ast.EventBlock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventRegistry {
   private static final Map<String, List<EventBlock>> EVENTS = new HashMap();

   public static void register(EventBlock block) {
      ((List)EVENTS.computeIfAbsent(block.eventName(), (k) -> new ArrayList())).add(block);
   }

   public static List<EventBlock> get(String event) {
      return (List)EVENTS.getOrDefault(event, List.of());
   }
}
