package com.demodev.fabricsk.config;

import com.demodev.fabricsk.util.FileUtil;
import com.demodev.fabricsk.util.JsonUtil;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.nio.file.Path;

public final class FabricSKConfig {

    public static final Path CONFIG_PATH = Path.of("config", "fabricsk.json");

    private static ConfigData config;

    private FabricSKConfig() {}

    public static void load() {
        try {
            if (!FileUtil.exists(CONFIG_PATH)) {
                config = createDefault();

                save();

                return;
            }

            String json = FileUtil.readFile(CONFIG_PATH);

            config = JsonUtil.fromJson(json, ConfigData.class);

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
            String json = JsonUtil.toJson(config);

            FileUtil.writeFile(CONFIG_PATH, json);
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

        data.version = "0.2-SNAPSHOT";

        data.autoLoadScripts = true;

        data.enableHeavySk = true;

        data.enableErrorLogging = true;

        data.enableLineMapping = true;

        data.scriptDirectory = "fabricsk/scripts";

        data.logDirectory = "fabricsk/logs";

        data.cacheDirectory = "fabricsk/cache";

        data.disabledDirectory = "fabricsk/disabled";

        data.syntaxCorrection = SyntaxCorrection.PARTIAL;

        data.maxStoredErrors = 250;

        return data;
    }

    public enum SyntaxCorrection {
        @SerializedName("auto")
        AUTO,

        @SerializedName("partial")
        PARTIAL,

        @SerializedName("disabled")
        DISABLED,
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
            return version;
        }

        public boolean autoLoadScripts() {
            return autoLoadScripts;
        }

        public boolean enableHeavySk() {
            return enableHeavySk;
        }

        public boolean enableErrorLogging() {
            return enableErrorLogging;
        }

        public boolean enableLineMapping() {
            return enableLineMapping;
        }

        public String scriptDirectory() {
            return scriptDirectory;
        }

        public String logDirectory() {
            return logDirectory;
        }

        public String cacheDirectory() {
            return cacheDirectory;
        }

        public String disabledDirectory() {
            return disabledDirectory;
        }

        public SyntaxCorrection syntaxCorrection() {
            return syntaxCorrection;
        }

        public int maxStoredErrors() {
            return maxStoredErrors;
        }
    }
}
