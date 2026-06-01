package com.demodev.fabricsk.util;

import java.util.Locale;

public final class TimeParser {

    private TimeParser() {}

    public static long parseTicks(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Time string cannot be empty.");
        }

        String[] split = text.trim().toLowerCase(Locale.ROOT).split("\\s+");

        if (split.length < 2) {
            throw new IllegalArgumentException("Invalid time format: " + text);
        }

        long value = Long.parseLong(split[0]);
        String unit = split[1];

        return switch (unit) {
            case "tick", "ticks" -> value;
            case "second", "seconds" -> value * 20L;
            case "minute", "minutes" -> value * 20L * 60L;
            case "hour", "hours" -> value * 20L * 60L * 60L;
            default -> throw new IllegalArgumentException(
                "Unknown time unit: " + unit
            );
        };
    }
}
