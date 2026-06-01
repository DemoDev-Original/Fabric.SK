package com.demodev.fabricsk.error;

import java.time.Instant;

public record ScriptError(
    Instant timestamp,

    ErrorCode code,

    String message,

    String file,

    int line,

    int column,

    String sourceLine
) {
    public static ScriptError fromException(ScriptException exception) {
        return new ScriptError(
            Instant.now(),
            exception.getErrorCode(),
            exception.getMessage(),
            exception.getFile(),
            exception.getLine(),
            exception.getColumn(),
            exception.getSourceLine()
        );
    }
}
