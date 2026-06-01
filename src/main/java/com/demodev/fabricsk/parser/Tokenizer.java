package com.demodev.fabricsk.parser;

import java.util.ArrayList;
import java.util.List;

public final class Tokenizer {

    private Tokenizer() {}

    public static List<TokenLine> tokenize(String source) {
        List<TokenLine> result = new ArrayList<>();

        String[] lines = source.split("\\R", -1);

        for (int i = 0; i < lines.length; i++) {
            result.add(new TokenLine(i + 1, lines[i]));
        }

        return result;
    }

    public record TokenLine(int lineNumber, String content) {}
}
