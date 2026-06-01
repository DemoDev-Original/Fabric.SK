package com.demodev.fabricsk.loader;

import com.demodev.fabricsk.error.ScriptErrorLogger;
import com.demodev.fabricsk.parser.SkParser;
import com.demodev.fabricsk.script.LoadedScript;
import com.demodev.fabricsk.script.ScriptManager;
import com.demodev.fabricsk.script.ScriptType;
import com.demodev.fabricsk.util.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class ScriptLoader {

    public static final Path SCRIPT_DIR = Path.of("fabricsk", "scripts");

    private ScriptLoader() {}

    public static void loadAll() {
        try {
            Files.createDirectories(SCRIPT_DIR);

            try (Stream<Path> files = Files.walk(SCRIPT_DIR)) {
                files.filter(Files::isRegularFile).forEach(ScriptLoader::load);
            }
        } catch (Exception e) {
            ScriptErrorLogger.log(e);
        }
    }

    public static void load(Path path) {
        try {
            String name = path.getFileName().toString();

            if (FileUtils.exists(path)) {
                loadSk(path);
            }
        } catch (Exception e) {
            ScriptErrorLogger.log(e);
        }
    }

    public static void loadSk(Path path) throws Exception {
        String source = FileUtils.readFile(path);

        SkParser.parse(path.getFileName().toString(), source);

        LoadedScript script = new LoadedScript(
            path.getFileName().toString(),
            path,
            ScriptType.SK
        );

        script.setLoaded(true);
        script.setEnabled(true);

        ScriptManager.register(script);
    }

    public static void unload(String scriptName) {
        ScriptManager.unregister(scriptName);
    }

    public static void reload(String scriptName) {
        LoadedScript script = ScriptManager.get(scriptName);

        if (script == null) {
            return;
        }

        unload(scriptName);
        load(script.getPath());
    }

    public static void reloadAll() {
        ScriptManager.clear();
        loadAll();
    }
}
