package com.demodev.fabricsk.condition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConditionRegistry {

    private static final Map<String, Condition> CONDITIONS =
        new ConcurrentHashMap<>();

    private ConditionRegistry() {}

    public static void register(String pattern, Condition condition) {
        CONDITIONS.put(pattern.toLowerCase(), condition);
    }

    public static Condition get(String pattern) {
        return CONDITIONS.get(pattern.toLowerCase());
    }

    public static boolean contains(String pattern) {
        return CONDITIONS.containsKey(pattern.toLowerCase());
    }

    public static Map<String, Condition> getConditions() {
        return Map.copyOf(CONDITIONS);
    }

    public static void clear() {
        CONDITIONS.clear();
    }

    public static void registerDefaults() {
        register("=", new EqualsCondition());

        register("==", new EqualsCondition());

        register(">", new GreaterCondition());

        register("<", new LessCondition());

        register("player is op", new OpCondition());

        register("player health >", new HealthCondition());
    }
}
