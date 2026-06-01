package com.demodev.fabricsk.command;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandManager {

    private static final Map<String, ScriptCommand> COMMANDS =
        new ConcurrentHashMap<>();

    private CommandManager() {}

    public static void register(ScriptCommand command) {
        COMMANDS.put(command.getName().toLowerCase(), command);

        for (String alias : command.getAliases()) {
            COMMANDS.put(alias.toLowerCase(), command);
        }
    }

    public static void unregister(String name) {
        COMMANDS.remove(name.toLowerCase());
    }

    public static ScriptCommand get(String name) {
        return COMMANDS.get(name.toLowerCase());
    }

    public static Collection<ScriptCommand> getCommands() {
        return Collections.unmodifiableCollection(COMMANDS.values());
    }

    public static void clear() {
        COMMANDS.clear();
    }

    public static int size() {
        return COMMANDS.size();
    }
}
