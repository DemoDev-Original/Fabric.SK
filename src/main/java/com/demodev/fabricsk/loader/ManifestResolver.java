package com.demodev.fabricsk.loader;

import com.demodev.fabricsk.util.FileUtil;
import com.demodev.fabricsk.util.JsonUtil;
import com.demodev.fabricsk.util.TomlUtil;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.tomlj.TomlParseResult;

public final class ManifestResolver {

    private ManifestResolver() {}

    public static ScriptManifest resolve(FileSystem zipFs) throws IOException {
        Path json = zipFs.getPath("/manifest.json");

        if (Files.exists(json)) {
            return JsonUtil.fromJson(
                FileUtil.readFile(json),
                ScriptManifest.class
            );
        }

        Path toml = zipFs.getPath("/manifest.sk.toml");

        if (Files.exists(toml)) {
            TomlParseResult result = TomlUtil.parse(FileUtil.readFile(toml));

            ScriptManifest manifest = new ScriptManifest();

            manifest.setName(result.getString("name"));

            manifest.setVersion(result.getString("version"));

            manifest.setMain(result.getString("main"));

            manifest.setPackageName(result.getString("package"));

            return manifest;
        }

        Path skmeta = zipFs.getPath("/package.skmeta");

        if (Files.exists(skmeta)) {
            return JsonUtil.fromJson(
                FileUtil.readFile(skmeta),
                ScriptManifest.class
            );
        }

        return new ScriptManifest("Unknown", "0.0.0", "main.sk", "");
    }
}
