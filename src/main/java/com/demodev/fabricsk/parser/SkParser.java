package com.demodev.fabricsk.parser;

import com.demodev.fabricsk.error.ErrorCode;
import com.demodev.fabricsk.error.ScriptException;
import java.util.List;

public final class SkParser {

    private SkParser() {}

    public static ValidationResult validate(String fileName, String source) {
        ValidationResult result = new ValidationResult();

        List<Tokenizer.TokenLine> lines = Tokenizer.tokenize(source);

        for (Tokenizer.TokenLine token : lines) {
            String line = token.content().trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("#")) {
                continue;
            }

            try {
                validateLine(fileName, token.lineNumber(), line);
            } catch (ScriptException e) {
                result.addError(
                    new com.demodev.fabricsk.error.ScriptError(
                        java.time.Instant.now(),
                        e.getErrorCode(),
                        e.getMessage(),
                        e.getFile(),
                        e.getLine(),
                        e.getColumn(),
                        e.getSourceLine()
                    )
                );
            }
        }

        return result;
    }

    public static void parse(String fileName, String source)
        throws ScriptException {
        List<Tokenizer.TokenLine> lines = Tokenizer.tokenize(source);

        for (Tokenizer.TokenLine token : lines) {
            String line = token.content().trim();

            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("#")) {
                continue;
            }

            validateLine(fileName, token.lineNumber(), line);
        }
    }

    private static void validateLine(
        String fileName,
        int lineNumber,
        String line
    ) throws ScriptException {
        if (line.startsWith("command ")) {
            validateCommand(fileName, lineNumber, line);

            return;
        }

        if (line.startsWith("on ")) {
            validateEvent(fileName, lineNumber, line);

            return;
        }

        if (line.startsWith("trigger:")) {
            return;
        }

        if (line.startsWith("send ")) {
            return;
        }

        if (line.startsWith("broadcast ")) {
            return;
        }

        if (line.startsWith("set ")) {
            return;
        }

        throw new ScriptException(
            ErrorCode.FSK001,
            "Unknown syntax",
            fileName,
            lineNumber,
            0,
            line
        );
    }

    private static void validateCommand(
        String fileName,
        int lineNumber,
        String line
    ) throws ScriptException {
        if (!line.endsWith(":")) {
            throw new ScriptException(
                ErrorCode.FSK004,
                "Command declaration must end with ':'",
                fileName,
                lineNumber,
                line.length(),
                line
            );
        }

        if (!line.contains("/")) {
            throw new ScriptException(
                ErrorCode.FSK004,
                "Missing command name",
                fileName,
                lineNumber,
                0,
                line
            );
        }
    }

    private static void validateEvent(
        String fileName,
        int lineNumber,
        String line
    ) throws ScriptException {
        if (!line.endsWith(":")) {
            throw new ScriptException(
                ErrorCode.FSK003,
                "Event declaration must end with ':'",
                fileName,
                lineNumber,
                line.length(),
                line
            );
        }
    }
}
