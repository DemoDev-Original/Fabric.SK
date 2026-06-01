package com.demodev.fabricsk.script;

public record SourceLocation(
    String file,

    int line,

    int column,

    String source
) {}
