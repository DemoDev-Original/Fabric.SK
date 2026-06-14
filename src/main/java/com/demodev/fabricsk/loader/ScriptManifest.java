package com.demodev.fabricsk.loader;

public class ScriptManifest {
   private String name;
   private String version;
   private String main;
   private String packageName;

   public ScriptManifest() {
   }

   public ScriptManifest(String name, String version, String main, String packageName) {
      this.name = name;
      this.version = version;
      this.main = main;
      this.packageName = packageName;
   }

   public String getName() {
      return this.name;
   }

   public String getVersion() {
      return this.version;
   }

   public String getMain() {
      return this.main;
   }

   public String getPackageName() {
      return this.packageName;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public void setMain(String main) {
      this.main = main;
   }

   public void setPackageName(String packageName) {
      this.packageName = packageName;
   }
}
