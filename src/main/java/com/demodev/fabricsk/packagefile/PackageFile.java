package com.demodev.fabricsk.packagefile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PackageFile {
   private final String name;
   private final PackageType type;
   private final Path source;
   private final List<PackageEntry> entries = new ArrayList();

   public PackageFile(String name, PackageType type, Path source) {
      this.name = name;
      this.type = type;
      this.source = source;
   }

   public String getName() {
      return this.name;
   }

   public PackageType getType() {
      return this.type;
   }

   public Path getSource() {
      return this.source;
   }

   public List<PackageEntry> getEntries() {
      return this.entries;
   }

   public void addEntry(PackageEntry entry) {
      this.entries.add(entry);
   }

   public int size() {
      return this.entries.size();
   }
}
