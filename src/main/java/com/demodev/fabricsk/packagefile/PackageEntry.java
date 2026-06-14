package com.demodev.fabricsk.packagefile;

import java.nio.file.Path;

public class PackageEntry {
   private final String name;
   private final Path path;
   private final String relativePath;

   public PackageEntry(String name, Path path, String relativePath) {
      this.name = name;
      this.path = path;
      this.relativePath = relativePath;
   }

   public String getName() {
      return this.name;
   }

   public Path getPath() {
      return this.path;
   }

   public String getRelativePath() {
      return this.relativePath;
   }
}
