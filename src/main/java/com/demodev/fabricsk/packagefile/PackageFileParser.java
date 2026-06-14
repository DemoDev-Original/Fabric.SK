package com.demodev.fabricsk.packagefile;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class PackageFileParser {
   private PackageFileParser() {
   }

   public static PackageFile fromDirectory(Path path) {
      PackageFile pkg = new PackageFile(path.getFileName().toString(), PackageType.DIRECTORY, path);

      try {
         Stream<Path> files = Files.walk(path);

         try {
            files.filter((x$0) -> Files.isRegularFile(x$0, new LinkOption[0])).forEach((file) -> {
               String relative = path.relativize(file).toString();
               pkg.addEntry(new PackageEntry(file.getFileName().toString(), file, relative));
            });
         } catch (Throwable var6) {
            if (files != null) {
               try {
                  files.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (files != null) {
            files.close();
         }
      } catch (Exception e) {
         System.err.println("[Fabric.SK Package] Failed to parse directory");
         e.printStackTrace();
      }

      return pkg;
   }

   public static PackageFile fromZip(Path zipPath) {
      PackageFile pkg = new PackageFile(zipPath.getFileName().toString(), PackageType.ZIP, zipPath);

      try {
         FileSystem fs = FileSystems.newFileSystem(zipPath);

         try {
            Path root = fs.getPath("/");
            Files.walk(root).filter((x$0) -> Files.isRegularFile(x$0, new LinkOption[0])).forEach((file) -> pkg.addEntry(new PackageEntry(file.getFileName().toString(), file, file.toString())));
         } catch (Throwable var6) {
            if (fs != null) {
               try {
                  fs.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (fs != null) {
            fs.close();
         }
      } catch (Exception e) {
         System.err.println("[Fabric.SK Package] Failed to parse ZIP");
         e.printStackTrace();
      }

      return pkg;
   }
}
