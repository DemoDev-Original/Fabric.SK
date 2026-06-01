package com.demodev.fabricsk.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventManager {

    private static final ConcurrentHashMap<
        String,
        List<Consumer<ScriptEvent>>
    > EVENTS = new ConcurrentHashMap<>();

    private EventManager() {}

    public static void register(String event, Consumer<ScriptEvent> handler) {
        EVENTS.computeIfAbsent(event.toLowerCase(), k -> new ArrayList<>()).add(
            handler
        );
    }

    public static void fire(ScriptEvent event) {
        List<Consumer<ScriptEvent>> handlers = EVENTS.get(
            event.getName().toLowerCase()
        );

        if (handlers == null) return;

        for (Consumer<ScriptEvent> handler : handlers) {
            try {
                handler.accept(event);
            } catch (Exception e) {
                System.err.println(
                    "[Fabric.SK] Event error: " + event.getName()
                );
                e.printStackTrace();
            }
        }
    }

    public static void clear() {
        EVENTS.clear();
    }

    public static int size() {
        return EVENTS.size();
    }
}
