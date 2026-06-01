package com.demodev.fabricsk.command;

import java.util.ArrayList;
import java.util.List;

public class ScriptCommand {

    private final String name;
    private String permission;
    private final List<String> aliases = new ArrayList<>();

    public ScriptCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }
}
