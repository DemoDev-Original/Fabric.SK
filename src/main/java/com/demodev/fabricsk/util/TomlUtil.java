package com.demodev.fabricsk.util;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

public final class TomlUtil {

    private TomlUtil() {}

    public static TomlParseResult parse(String content) {
        return Toml.parse(content);
    }

    public static String getString(TomlParseResult result, String key) {
        return result.getString(key);
    }

    public static long getLong(TomlParseResult result, String key) {
        Long value = result.getLong(key);

        return value == null ? 0L : value;
    }

    public static boolean getBoolean(TomlParseResult result, String key) {
        Boolean value = result.getBoolean(key);

        return value != null && value;
    }
}
