package com.demodev.fabricsk.util;

import java.util.ArrayList;
import java.util.List;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isQuoted(String text) {
        return (
            text != null &&
            text.length() >= 2 &&
            text.startsWith("\"") &&
            text.endsWith("\"")
        );
    }

    public static String unquote(String text) {
        if (!isQuoted(text)) {
            return text;
        }

        return text.substring(1, text.length() - 1);
    }

    public static String repeat(String value, int count) {
        return value.repeat(Math.max(0, count));
    }

    public static int indentationLevel(String line) {
        int count = 0;

        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }

        return count;
    }

    public static List<String> nonEmptyLines(String content) {
        List<String> result = new ArrayList<>();

        for (String line : content.split("\\R")) {
            String trimmed = line.trim();

            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }

        return result;
    }

    public static boolean startsWithIgnoreCase(String text, String prefix) {
        return text.regionMatches(true, 0, prefix, 0, prefix.length());
    }
}
