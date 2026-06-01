package com.demodev.fabricsk.script;

import java.nio.file.Path;

public class LoadedScript {

    private final String name;
    private final Path path;
    private final ScriptType type;

    private boolean loaded;
    private boolean enabled;

    private String version;
    private String packageName;

    public LoadedScript(String name, Path path, ScriptType type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public ScriptType getType() {
        return type;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
