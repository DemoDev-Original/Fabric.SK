package com.demodev.fabricsk.error;

public class ScriptException extends Exception {

    private final ErrorCode errorCode;

    private final String file;

    private final int line;

    private final int column;

    private final String sourceLine;

    public ScriptException(
        ErrorCode errorCode,
        String message,
        String file,
        int line,
        int column,
        String sourceLine
    ) {
        super(message);

        this.errorCode = errorCode;
        this.file = file;
        this.line = line;
        this.column = column;
        this.sourceLine = sourceLine;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getSourceLine() {
        return sourceLine;
    }
}
