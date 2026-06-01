package com.demodev.fabricsk.loader;

import com.demodev.fabricsk.util.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.tomlj.TomlParseResult;

public final class ManifestResolver {

    private static final Gson GSON = new GsonBuilder().create();

    private ManifestResolver() {}

    public static ScriptManifest resolve(FileSystem zipFs) {
        try {
            Path json = zipFs.getPath("/manifest.json");

            if (Files.exists(json)) {
                return GSON.fromJson(
                    FileUtils.readFile(json),
                    ScriptManifest.class
                );
            }

            Path toml = zipFs.getPath("/manifest.sk.toml");

            if (Files.exists(toml)) {
                TomlParseResult result = org.tomlj.Toml.parse(
                    FileUtils.readFile(toml)
                );

                ScriptManifest manifest = new ScriptManifest();

                manifest.setName(result.getString("name"));
                manifest.setVersion(result.getString("version"));
                manifest.setMain(result.getString("main"));
                manifest.setPackageName(result.getString("package"));

                return manifest;
            }

            Path meta = zipFs.getPath("/package.skmeta");

            if (Files.exists(meta)) {
                return GSON.fromJson(
                    FileUtils.readFile(meta),
                    ScriptManifest.class
                );
            }
        } catch (Exception e) {
            System.err.println("[Fabric.SK] Manifest parse error");
            e.printStackTrace();
        }

        return new ScriptManifest("Unknown", "0.0.0", "main.sk", "");
    }
}
