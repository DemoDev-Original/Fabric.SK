package com.demodev.fabricsk.packagefile;

import java.nio.file.*;
import java.util.stream.Stream;

public final class PackageFileParser {

    private PackageFileParser() {}

    public static PackageFile fromDirectory(Path path) {
        PackageFile pkg = new PackageFile(
            path.getFileName().toString(),
            PackageType.DIRECTORY,
            path
        );

        try (Stream<Path> files = Files.walk(path)) {
            files.filter(Files::isRegularFile).forEach(file -> {
                String relative = path.relativize(file).toString();

                pkg.addEntry(
                    new PackageEntry(
                        file.getFileName().toString(),
                        file,
                        relative
                    )
                );
            });
        } catch (Exception e) {
            System.err.println("[Fabric.SK Package] Failed to parse directory");
            e.printStackTrace();
        }

        return pkg;
    }

    public static PackageFile fromZip(Path zipPath) {
        PackageFile pkg = new PackageFile(
            zipPath.getFileName().toString(),
            PackageType.ZIP,
            zipPath
        );

        try (FileSystem fs = FileSystems.newFileSystem(zipPath)) {
            Path root = fs.getPath("/");

            Files.walk(root)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    pkg.addEntry(
                        new PackageEntry(
                            file.getFileName().toString(),
                            file,
                            file.toString()
                        )
                    );
                });
        } catch (Exception e) {
            System.err.println("[Fabric.SK Package] Failed to parse ZIP");
            e.printStackTrace();
        }

        return pkg;
    }
}
