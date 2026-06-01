package com.demodev.fabricsk.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    private JsonUtil() {}

    public static Gson gson() {
        return GSON;
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
