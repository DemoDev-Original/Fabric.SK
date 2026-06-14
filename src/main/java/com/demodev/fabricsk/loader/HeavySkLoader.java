package com.demodev.fabricsk.loader;

import com.demodev.fabricsk.error.ScriptErrorLogger;
import com.demodev.fabricsk.parser.SkParser;
import com.demodev.fabricsk.script.LoadedScript;
import com.demodev.fabricsk.script.ScriptManager;
import com.demodev.fabricsk.script.ScriptType;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public final class HeavySkLoader {
   private HeavySkLoader() {
   }

   public static void load(Path path) {
      try {
         FileSystem zip = FileSystems.newFileSystem(path);

         try {
            ScriptManifest manifest = ManifestResolver.resolve(zip);
            Files.walk(zip.getPath("/")).filter((p) -> p.toString().endsWith(".sk")).forEach((script) -> {
               try {
                  String source = Files.readString(script);
                  SkParser.parse(script.toString(), source);
               } catch (Exception e) {
                  ScriptErrorLogger.log((Throwable)e);
               }

            });
            LoadedScript loaded = new LoadedScript(path.getFileName().toString(), path, ScriptType.HEAVYSK);
            loaded.setLoaded(true);
            loaded.setEnabled(true);
            loaded.setVersion(manifest.getVersion());
            loaded.setPackageName(manifest.getPackageName());
            ScriptManager.register(loaded);
         } catch (Throwable var5) {
            if (zip != null) {
               try {
                  zip.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (zip != null) {
            zip.close();
         }
      } catch (Exception e) {
         ScriptErrorLogger.log((Throwable)e);
      }

   }
}
