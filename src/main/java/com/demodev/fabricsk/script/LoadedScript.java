package com.demodev.fabricsk.script;

import com.demodev.fabricsk.error.ScriptError;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadedScript {

    private final String name;

    private final Path path;

    private final ScriptType type;

    private boolean loaded;

    private boolean enabled;

    private String version = "unknown";

    private String packageName = "";

    private Instant loadTime;

    private int commandCount;

    private int eventCount;

    private final List<ScriptError> errors = new ArrayList<>();

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

        if (loaded) {
            this.loadTime = Instant.now();
        }
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

    public Instant getLoadTime() {
        return loadTime;
    }

    public int getCommandCount() {
        return commandCount;
    }

    public void setCommandCount(int commandCount) {
        this.commandCount = commandCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public void setEventCount(int eventCount) {
        this.eventCount = eventCount;
    }

    public void addError(ScriptError error) {
        errors.add(error);
    }

    public List<ScriptError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public int getErrorCount() {
        return errors.size();
    }

    @Override
    public String toString() {
        return (
            "LoadedScript{" +
            "name='" +
            name +
            '\'' +
            ", type=" +
            type +
            ", loaded=" +
            loaded +
            ", enabled=" +
            enabled +
            '}'
        );
    }
}
