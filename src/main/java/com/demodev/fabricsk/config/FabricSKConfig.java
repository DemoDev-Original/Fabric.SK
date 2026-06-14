package com.demodev.fabricsk.config;

import com.demodev.fabricsk.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.file.Path;

public final class FabricSKConfig {
   public static final Path CONFIG_PATH = Path.of("config", "fabricsk.json");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private static ConfigData config;

   private FabricSKConfig() {
   }

   public static void load() {
      try {
         if (!FileUtils.exists(CONFIG_PATH)) {
            config = createDefault();
            save();
            return;
         }

         String json = FileUtils.readFile(CONFIG_PATH);
         config = (ConfigData)GSON.fromJson(json, ConfigData.class);
         if (config == null) {
            config = createDefault();
            save();
         }
      } catch (Exception e) {
         System.err.println("[Fabric.SK] Failed to load config.");
         e.printStackTrace();
         config = createDefault();
      }

   }

   public static void save() {
      try {
         String json = GSON.toJson(config);
         FileUtils.writeFile(CONFIG_PATH, json);
      } catch (IOException e) {
         System.err.println("[Fabric.SK] Failed to save config.");
         e.printStackTrace();
      }

   }

   public static ConfigData get() {
      if (config == null) {
         load();
      }

      return config;
   }

   private static ConfigData createDefault() {
      ConfigData data = new ConfigData();
      data.version = "0.2.5-ALPHA";
      data.autoLoadScripts = true;
      data.enableHeavySk = true;
      data.enableErrorLogging = true;
      data.enableLineMapping = true;
      data.scriptDirectory = "fabricsk/scripts";
      data.logDirectory = "fabricsk/logs";
      data.cacheDirectory = "fabricsk/cache";
      data.disabledDirectory = "fabricsk/disabled";
      data.syntaxCorrection = FabricSKConfig.SyntaxCorrection.PARTIAL;
      data.maxStoredErrors = 250;
      return data;
   }

   public static enum SyntaxCorrection {
      @SerializedName("auto")
      AUTO,
      @SerializedName("partial")
      PARTIAL,
      @SerializedName("disabled")
      DISABLED;

      // $FF: synthetic method
      private static SyntaxCorrection[] $values() {
         return new SyntaxCorrection[]{AUTO, PARTIAL, DISABLED};
      }
   }

   public static class ConfigData {
      public String version;
      public boolean autoLoadScripts;
      public boolean enableHeavySk;
      public boolean enableErrorLogging;
      public boolean enableLineMapping;
      public String scriptDirectory;
      public String logDirectory;
      public String cacheDirectory;
      public String disabledDirectory;
      public SyntaxCorrection syntaxCorrection;
      public int maxStoredErrors;

      public String version() {
         return this.version;
      }

      public boolean autoLoadScripts() {
         return this.autoLoadScripts;
      }

      public boolean enableHeavySk() {
         return this.enableHeavySk;
      }

      public boolean enableErrorLogging() {
         return this.enableErrorLogging;
      }

      public boolean enableLineMapping() {
         return this.enableLineMapping;
      }

      public String scriptDirectory() {
         return this.scriptDirectory;
      }

      public String logDirectory() {
         return this.logDirectory;
      }

      public String cacheDirectory() {
         return this.cacheDirectory;
      }

      public String disabledDirectory() {
         return this.disabledDirectory;
      }

      public SyntaxCorrection syntaxCorrection() {
         return this.syntaxCorrection;
      }

      public int maxStoredErrors() {
         return this.maxStoredErrors;
      }
   }
}
