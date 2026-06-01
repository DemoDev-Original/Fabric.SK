package com.demodev.fabricsk.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptScheduler {

    private static final Map<String, ScheduledTask> TASKS =
        new ConcurrentHashMap<>();

    private static long serverTick = 0;

    private ScriptScheduler() {}

    // ---------------- REGISTER ----------------

    public static void schedule(ScheduledTask task) {
        TASKS.put(task.getId(), task);
    }

    public static void cancel(String id) {
        ScheduledTask task = TASKS.get(id);

        if (task != null) {
            task.cancel();
        }

        TASKS.remove(id);
    }

    // ---------------- TICK LOOP ----------------

    public static void tick() {
        serverTick++;

        for (ScheduledTask task : TASKS.values()) {
            task.tick(serverTick);
        }

        TASKS.entrySet().removeIf(e -> e.getValue().isCancelled());
    }

    // ---------------- UTIL ----------------

    public static long getServerTick() {
        return serverTick;
    }

    public static int size() {
        return TASKS.size();
    }

    public static void clear() {
        TASKS.clear();
    }
}
