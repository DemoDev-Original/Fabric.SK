package com.demodev.fabricsk.loader;

import com.demodev.fabricsk.script.LoadedScript;
import com.demodev.fabricsk.script.ScriptRegistry;
import com.demodev.fabricsk.script.ScriptType;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class ScriptLoader {
   private ScriptLoader() {
   }

   public static int loadAll() {
      int count = 0;

      try {
         Path folder = getScriptsFolder();
         if (!Files.exists(folder, new LinkOption[0])) {
            Files.createDirectories(folder);
            return 0;
         }

         Stream<Path> stream = Files.list(folder);

         try {
            for(Path path : stream.toList()) {
               if (path.toString().endsWith(".sk")) {
                  loadFile(path);
                  ++count;
               }
            }
         } catch (Throwable var6) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return count;
   }

   public static LoadedScript loadSingle(String name) {
      try {
         Path path = getScriptPath(name);
         if (!Files.exists(path, new LinkOption[0])) {
            return null;
         } else {
            LoadedScript script = new LoadedScript(name, path, ScriptType.SK);
            script.setLoaded(true);
            script.setEnabled(true);
            return script;
         }
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   private static void loadFile(Path path) {
      try {
         String name = path.getFileName().toString().replace(".sk", "");
         LoadedScript script = new LoadedScript(name, path, ScriptType.SK);
         script.setLoaded(true);
         script.setEnabled(true);
         ScriptRegistry.register(script);
      } catch (Exception e) {
         e.printStackTrace();
      }

   }

   public static LoadedScript preview(String name) {
      return loadSingle(name);
   }

   public static Path getScriptPath(String name) {
      return Path.of("config", "fabricsk", name + ".sk");
   }

   private static Path getScriptsFolder() {
      return Path.of("config", "fabricsk");
   }
}
