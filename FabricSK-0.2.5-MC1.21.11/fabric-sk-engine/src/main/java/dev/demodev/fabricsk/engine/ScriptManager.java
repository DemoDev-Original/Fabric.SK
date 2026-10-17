package dev.demodev.fabricsk.engine;

import com.mojang.brigadier.CommandDispatcher;
import dev.demodev.fabricsk.engine.ScriptModel.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Owns script discovery, parsing, package loading, errors, and command execution. */
public final class ScriptManager {
    public record OperationResult(boolean success, int count, String message) { }
    public record ScriptInfo(String name, int commands, String type) { }

    private final Path configDirectory = FabricLoader.getInstance().getConfigDir().toAbsolutePath().normalize();
    private final Path scriptsDirectory = configDirectory.resolve("fabricsk/scripts").normalize();
    private final Path errorLog = FabricLoader.getInstance().getGameDir().resolve("fabricsk/logs/script-errors.log").toAbsolutePath().normalize();
    private final Path configFile = configDirectory.resolve("fabricsk.json");
    private final SyntaxRegistry syntaxRegistry = new SyntaxRegistry();
    private final Map<Path, Script> scripts = new LinkedHashMap<>();
    private final Map<String, ScriptCommand> commands = new LinkedHashMap<>();
    private final Map<String, String> errors = new LinkedHashMap<>();
    private final Map<String, String> syntaxCorrectionModes = new LinkedHashMap<>();
    private boolean autoLoadScripts = true;
    private boolean enableHeavySk = true;
    private boolean enableErrorLogging = true;

    public synchronized void initialize() {
        try {
            Files.createDirectories(scriptsDirectory);
            Files.createDirectories(errorLog.getParent());
            writeDefaultConfigIfMissing();
            readConfig();
            createExampleIfMissing();
        } catch (IOException e) {
            System.err.println("[Fabric.SK Engine] Could not initialize files: " + e.getMessage());
        }
        OperationResult result = autoLoadScripts ? reload() : new OperationResult(true, 0, "Automatic script loading is disabled in config.");
        System.out.println("[Fabric.SK Engine] Script manager ready: " + result.message());
    }

    private void writeDefaultConfigIfMissing() throws IOException {
        if (!Files.exists(configFile)) {
            String config = "{\n  \"version\": \"0.2.5-ALPHA\",\n  \"autoLoadScripts\": true,\n  \"enableHeavySk\": true,\n  \"enableErrorLogging\": true\n}\n";
            Files.writeString(configFile, config, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        }
    }

    private void readConfig() throws IOException {
        String json = Files.readString(configFile, StandardCharsets.UTF_8);
        autoLoadScripts = readBoolean(json, "autoLoadScripts", true);
        enableHeavySk = readBoolean(json, "enableHeavySk", true);
        enableErrorLogging = readBoolean(json, "enableErrorLogging", true);
    }

    private boolean readBoolean(String json, String key, boolean fallback) {
        var matcher = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE).matcher(json);
        return matcher.find() ? Boolean.parseBoolean(matcher.group(1)) : fallback;
    }

    private void createExampleIfMissing() throws IOException {
        Path example = scriptsDirectory.resolve("example/example.sk");
        Files.createDirectories(example.getParent());
        if (!Files.exists(example)) {
            String contents = "command /hello:\n"
                    + "    trigger:\n"
                    + "        send \"Hello, %player%!\" to player\n";
            Files.writeString(example, contents, StandardCharsets.UTF_8);
        }
    }

    public synchronized OperationResult reload() {
        scripts.clear(); commands.clear(); errors.clear();
        try {
            Files.createDirectories(scriptsDirectory);
            try (var paths = Files.list(scriptsDirectory)) {
                for (Path path : paths.filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".sk") || (enableHeavySk && p.getFileName().toString().endsWith(".heavysk")))
                        .sorted().toList()) parsePath(path);
            }
        } catch (IOException e) {
            return new OperationResult(false, 0, "Could not scan scripts: " + e.getMessage());
        }
        return new OperationResult(errors.isEmpty(), scripts.size(), "Loaded " + scripts.size() + " script(s)/package(s), registered " + commands.size() + " command(s), " + errors.size() + " error(s).");
    }

    public synchronized OperationResult load(String name) {
        if (name == null || name.isBlank()) return new OperationResult(false, 0, "Specify a script path, e.g. example/example.sk.");
        if (name.equalsIgnoreCase("-example-/example.sk")) name = "example/example.sk";
        String filename = name.replace('\\', '/');
        if (!filename.endsWith(".sk") && !filename.endsWith(".heavysk")) filename += ".sk";
        Path path = scriptsDirectory.resolve(filename).normalize();
        if (!path.startsWith(scriptsDirectory) || path.equals(scriptsDirectory)) return new OperationResult(false, 0, "Invalid script path. Paths must stay inside config/fabricsk/scripts.");
        if (!Files.isRegularFile(path)) return new OperationResult(false, 0, "Script not found: " + filename);
        if (filename.endsWith(".heavysk") && !enableHeavySk) return new OperationResult(false, 0, "Heavy Skript packages are disabled in config/fabricsk.json.");
        removeLoadedPath(path);
        errors.remove(path.toString());
        boolean ok = parsePath(path);
        return new OperationResult(ok, ok ? 1 : 0, ok ? "Loaded " + scriptsDirectory.relativize(path).toString().replace('\\', '/') + "." : errors.getOrDefault(path.toString(), "Load failed."));
    }

    public synchronized OperationResult unload(String name) {
        if (name == null || name.isBlank()) return new OperationResult(false, 0, "Specify a loaded script name.");
        String filename = name.replace('\\', '/');
        Path path = scriptsDirectory.resolve(filename).normalize();
        if (!path.startsWith(scriptsDirectory)) return new OperationResult(false, 0, "Invalid script path.");
        if (!scripts.containsKey(path)) {
            // Permit matching a path without its extension.
            if (!filename.endsWith(".sk") && !filename.endsWith(".heavysk")) {
                Path sk = scriptsDirectory.resolve(filename + ".sk").normalize();
                Path heavy = scriptsDirectory.resolve(filename + ".heavysk").normalize();
                if (scripts.containsKey(sk)) path = sk; else if (scripts.containsKey(heavy)) path = heavy;
            }
        }
        if (!scripts.containsKey(path)) return new OperationResult(false, 0, "That script is not currently loaded: " + filename);
        removeLoadedPath(path);
        errors.remove(path.toString());
        return new OperationResult(true, 1, "Unloaded " + scriptsDirectory.relativize(path).toString().replace('\\', '/') + ".");
    }

    private void removeLoadedPath(Path path) {
        Script previous = scripts.remove(path);
        if (previous != null) {
            for (ScriptCommand c : previous.commands()) {
                String key = c.name().toLowerCase(Locale.ROOT);
                // Don't remove a command if another loaded script still owns that name.
                boolean stillUsed = scripts.values().stream().flatMap(s -> s.commands().stream())
                        .anyMatch(other -> other.name().equalsIgnoreCase(c.name()));
                if (!stillUsed) commands.remove(key);
            }
        }
    }

    private boolean parsePath(Path path) {
        try {
            List<ScriptCommand> parsedCommands = new ArrayList<>();
            if (path.getFileName().toString().endsWith(".heavysk")) {
                try (ZipFile zip = new ZipFile(path.toFile())) {
                    List<? extends ZipEntry> entries = Collections.list(zip.entries()).stream()
                            .filter(e -> !e.isDirectory() && e.getName().toLowerCase(Locale.ROOT).endsWith(".sk"))
                            .sorted(Comparator.comparing(ZipEntry::getName)).toList();
                    if (entries.isEmpty()) throw new IOException("Package contains no .sk files.");
                    for (ZipEntry entry : entries) {
                        String source;
                        try (var in = zip.getInputStream(entry)) { source = new String(in.readAllBytes(), StandardCharsets.UTF_8); }
                        parsedCommands.addAll(new Parser().parse(path.resolve(entry.getName()), source).commands());
                    }
                }
            } else {
                parsedCommands.addAll(new Parser().parse(path, Files.readString(path, StandardCharsets.UTF_8)).commands());
            }
            Set<String> localNames = new HashSet<>();
            for (ScriptCommand command : parsedCommands) {
                String key = command.name().toLowerCase(Locale.ROOT);
                if (!localNames.add(key) || (commands.containsKey(key) && scripts.values().stream().noneMatch(s -> s.path().equals(path) && s.commands().stream().anyMatch(old -> old.name().equalsIgnoreCase(command.name()))))) {
                    throw new ScriptSyntaxException(command.line(), 1, "Duplicate scripted command /" + command.name() + ".");
                }
            }
            Script script = new Script(path, List.copyOf(parsedCommands));
            scripts.put(path, script);
            for (ScriptCommand command : parsedCommands) commands.put(command.name().toLowerCase(Locale.ROOT), command);
            return true;
        } catch (IOException | ScriptSyntaxException e) {
            String message = e instanceof ScriptSyntaxException se ? "Line " + se.getLine() + ", column " + se.getColumn() + ": " + se.getMessage() : e.getMessage();
            errors.put(path.toString(), message);
            logError(path, message);
            System.err.println("[Fabric.SK] " + path.getFileName() + ": " + message);
            return false;
        }
    }

    private void logError(Path path, String message) {
        if (!enableErrorLogging) return;
        try {
            Files.createDirectories(errorLog.getParent());
            Files.writeString(errorLog, "[" + LocalDateTime.now() + "] [Fabric.SK] FSK001\nFile: " + path + "\n" + message + "\n\n",
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) { System.err.println("[Fabric.SK] Could not write error log: " + e.getMessage()); }
    }

    public synchronized OperationResult parseAll() {
        int good = 0; errors.clear();
        for (Path path : scriptFilesForValidation()) {
            try {
                if (path.toString().endsWith(".heavysk")) {
                    try (ZipFile zip = new ZipFile(path.toFile())) {
                        List<? extends ZipEntry> entries = Collections.list(zip.entries()).stream().filter(e -> !e.isDirectory() && e.getName().endsWith(".sk")).toList();
                        if (entries.isEmpty()) throw new IOException("Package contains no .sk files.");
                        for (ZipEntry entry : entries) try (var in = zip.getInputStream(entry)) { new Parser().parse(path.resolve(entry.getName()), new String(in.readAllBytes(), StandardCharsets.UTF_8)); }
                    }
                } else new Parser().parse(path, Files.readString(path, StandardCharsets.UTF_8));
                good++;
            } catch (Exception e) { errors.put(path.toString(), e.getMessage()); }
        }
        return new OperationResult(errors.isEmpty(), good, "Parsed " + good + " script(s)/package(s); " + errors.size() + " error(s)." + (errors.isEmpty() ? "" : " First error: " + errors.values().iterator().next()));
    }

    public synchronized OperationResult validateAll() {
        OperationResult parsed = parseAll();
        if (!parsed.success()) return new OperationResult(false, parsed.count(), "Validation failed. " + parsed.message());
        return new OperationResult(true, parsed.count(), "Validation passed for " + parsed.count() + " script(s)/package(s).");
    }

    public synchronized OperationResult validate(String name) {
        if (name == null || name.isBlank()) return validateAll();
        String filename = name.replace('\\', '/');
        if (!filename.endsWith(".sk") && !filename.endsWith(".heavysk")) filename += ".sk";
        Path path = scriptsDirectory.resolve(filename).normalize();
        if (!path.startsWith(scriptsDirectory) || !Files.isRegularFile(path)) return new OperationResult(false, 0, "Script not found: " + filename);
        try {
            if (filename.endsWith(".heavysk")) {
                try (ZipFile zip = new ZipFile(path.toFile())) {
                    List<? extends ZipEntry> entries = Collections.list(zip.entries()).stream().filter(e -> !e.isDirectory() && e.getName().endsWith(".sk")).toList();
                    if (entries.isEmpty()) return new OperationResult(false, 0, "Package contains no .sk files.");
                    for (ZipEntry entry : entries) try (var in = zip.getInputStream(entry)) { new Parser().parse(path.resolve(entry.getName()), new String(in.readAllBytes(), StandardCharsets.UTF_8)); }
                }
            } else new Parser().parse(path, Files.readString(path, StandardCharsets.UTF_8));
            errors.remove(path.toString()); return new OperationResult(true, 1, "Validation passed for " + filename + ".");
        } catch (Exception e) {
            errors.put(path.toString(), e.getMessage()); logError(path, e.getMessage());
            return new OperationResult(false, 0, "Validation failed for " + filename + ": " + e.getMessage());
        }
    }

    private List<Path> scriptFilesForValidation() {
        try (var paths = Files.walk(scriptsDirectory)) {
            return paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".sk") || (enableHeavySk && p.toString().endsWith(".heavysk"))).sorted().toList();
        } catch (IOException e) { return List.of(); }
    }

    public synchronized void registerScriptCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String commandName : commands.keySet()) {
            // Resolve the current command at execution time so reloads update the behavior.
            dispatcher.register(Commands.literal(commandName).executes(ctx -> {
                ScriptCommand scriptCommand;
                synchronized (ScriptManager.this) { scriptCommand = commands.get(commandName); }
                if (scriptCommand == null) { ctx.getSource().sendFailure(Component.literal("That Fabric.SK command is no longer loaded.")); return 0; }
                ServerPlayer player = ctx.getSource().getPlayer();
                ExecutionContext execution = new ExecutionContext(ctx.getSource(), player);
                try {
                    for (var statement : scriptCommand.statements()) syntaxRegistry.execute(statement, execution);
                    return 1;
                } catch (RuntimeException e) {
                    ctx.getSource().sendFailure(Component.literal("Fabric.SK script error: " + e.getMessage()));
                    return 0;
                }
            }));
        }
    }

    public synchronized List<String> getLoadedScripts() {
        return scripts.keySet().stream().map(p -> scriptsDirectory.relativize(p).toString().replace('\\', '/')).toList();
    }
    public synchronized List<ScriptInfo> getScriptInfo() {
        return scripts.entrySet().stream().map(e -> new ScriptInfo(scriptsDirectory.relativize(e.getKey()).toString().replace('\\', '/'), e.getValue().commands().size(), e.getKey().toString().endsWith(".heavysk") ? "Heavy Skript package" : "Skript file")).toList();
    }
    public synchronized String info(String name) {
        String normalized = name.replace('\\', '/');
        if (!normalized.endsWith(".sk") && !normalized.endsWith(".heavysk")) normalized += ".sk";
        for (ScriptInfo info : getScriptInfo()) if (info.name().equalsIgnoreCase(normalized)) return info.name() + " | " + info.type() + " | commands: " + info.commands();
        return "Script is not loaded: " + normalized;
    }
    public synchronized Map<String, String> getErrors() { return Collections.unmodifiableMap(new LinkedHashMap<>(errors)); }
    public synchronized OperationResult setSyntaxCorrection(String mode, String script) {
        if (!mode.equalsIgnoreCase("auto") && !mode.equalsIgnoreCase("partial")) return new OperationResult(false, 0, "Mode must be auto or partial.");
        syntaxCorrectionModes.put(script.replace('\\', '/'), mode.toLowerCase(Locale.ROOT));
        return new OperationResult(true, 1, "Syntax-correction preference set to " + mode.toLowerCase(Locale.ROOT) + " for " + script + ". The correction engine is experimental; this setting does not rewrite scripts yet.");
    }
    public Path getScriptsDirectory() { return scriptsDirectory; }
    public Path getErrorLog() { return errorLog; }
    public synchronized int getCommandCount() { return commands.size(); }
    public synchronized int getSyntaxCount() { return syntaxRegistry.size(); }
}
