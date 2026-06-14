package com.demodev.fabricsk.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

public final class FileUtils {
   private FileUtils() {
   }

   public static String readFile(Path path) throws IOException {
      return Files.readString(path, StandardCharsets.UTF_8);
   }

   public static void writeFile(Path path, String content) throws IOException {
      if (path.getParent() != null) {
         Files.createDirectories(path.getParent());
      }

      Files.writeString(path, content, StandardCharsets.UTF_8);
   }

   public static boolean exists(Path path) {
      return Files.exists(path, new LinkOption[0]);
   }

   public static void createDirectories(Path path) throws IOException {
      Files.createDirectories(path);
   }
}
