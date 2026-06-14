package com.demodev.fabricsk.error;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ScriptErrorLogger {
   private static final List<ScriptError> ERRORS = new ArrayList();
   private static final Path LOG_FILE = Path.of("fabricsk", "logs", "script-errors.log");

   private ScriptErrorLogger() {
   }

   public static void log(ScriptException exception) {
      ScriptError error = ScriptError.fromException(exception);
      ERRORS.add(error);
      printConsole(error);
      writeToFile(error);
   }

   public static void log(Throwable throwable) {
      ScriptError error = new ScriptError(Instant.now(), ErrorCode.FSK010, throwable.getMessage(), "unknown", -1, -1, "");
      ERRORS.add(error);
      printConsole(error);
      writeToFile(error);
   }

   public static List<ScriptError> getErrors() {
      return Collections.unmodifiableList(ERRORS);
   }

   public static void clear() {
      ERRORS.clear();
   }

   private static void printConsole(ScriptError error) {
      System.err.println();
      System.err.println("[Fabric.SK] " + error.code().name());
      System.err.println("File: " + error.file());
      if (error.line() > 0) {
         System.err.println("Line: " + error.line());
      }

      System.err.println("Error: " + error.message());
      if (!error.sourceLine().isBlank()) {
         System.err.println();
         System.err.println(error.sourceLine());
         if (error.column() >= 0) {
            StringBuilder pointer = new StringBuilder();

            for(int i = 0; i < error.column(); ++i) {
               pointer.append(' ');
            }

            pointer.append('^');
            System.err.println(pointer);
         }
      }

      System.err.println();
   }

   private static void writeToFile(ScriptError error) {
      try {
         Files.createDirectories(LOG_FILE.getParent());
         StringBuilder builder = new StringBuilder();
         builder.append("[" + String.valueOf(LocalDateTime.now()) + "]").append("\n");
         builder.append(error.code().name()).append("\n");
         builder.append("File: ").append(error.file()).append("\n");
         builder.append("Line: ").append(error.line()).append("\n");
         builder.append("Column: ").append(error.column()).append("\n");
         builder.append("Message: ").append(error.message()).append("\n");
         if (!error.sourceLine().isBlank()) {
            builder.append("Source: ").append(error.sourceLine()).append("\n");
         }

         builder.append("\n");
         Files.writeString(LOG_FILE, builder.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      } catch (IOException e) {
         System.err.println("[Fabric.SK] Failed to write error log.");
         e.printStackTrace();
      }

   }
}
