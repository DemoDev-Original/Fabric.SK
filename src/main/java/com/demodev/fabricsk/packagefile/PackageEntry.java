package com.demodev.fabricsk.packagefile;

import java.nio.file.Path;

public class PackageEntry {

    private final String name;
    private final Path path;
    private final String relativePath;

    public PackageEntry(String name, Path path, String relativePath) {
        this.name = name;
        this.path = path;
        this.relativePath = relativePath;
    }

    public String getName() {
        return name;
    }

    public Path getPath() {
        return path;
    }

    public String getRelativePath() {
        return relativePath;
    }
}
