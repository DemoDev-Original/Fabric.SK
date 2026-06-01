package com.demodev.fabricsk.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {

    private FileUtil() {}

    public static String readFile(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public static void writeFile(Path path, String content) throws IOException {
        createParentDirectories(path);

        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    public static void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public static void createParentDirectories(Path path) throws IOException {
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
    }

    public static String fileName(Path path) {
        return path.getFileName().toString();
    }

    public static String extension(Path path) {
        String file = fileName(path);

        int index = file.lastIndexOf('.');

        if (index == -1) {
            return "";
        }

        return file.substring(index + 1);
    }

    public static boolean isSk(Path path) {
        return path.toString().toLowerCase().endsWith(".sk");
    }

    public static boolean isHeavySk(Path path) {
        return path.toString().toLowerCase().endsWith(".heavysk");
    }
}
