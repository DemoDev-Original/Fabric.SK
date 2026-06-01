package com.demodev.fabricsk.parser;

import java.util.List;

public final class SkParser {

    private SkParser() {}

    public static ParsedScript parse(String name, String source) {
        ParsedScript script = new ParsedScript(name);

        String[] lines = source.split("\\R");

        ScriptSection currentSection = null;

        for (String raw : lines) {
            if (raw.trim().isEmpty()) {
                continue;
            }

            int indent = countIndent(raw);
            String line = raw.trim();

            // EVENT / SECTION HEADER
            if (indent == 0 && isSection(line)) {
                currentSection = new ScriptSection(line);
                script.addSection(currentSection);
                continue;
            }

            // INSIDE SECTION
            if (currentSection != null) {
                currentSection.addLine(new ScriptLine(line, indent));
            }
        }

        debug(script);

        return script;
    }

    private static boolean isSection(String line) {
        return (
            line.startsWith("on ") ||
            line.startsWith("command ") ||
            line.startsWith("every ") ||
            line.startsWith("function ")
        );
    }

    private static int countIndent(String line) {
        int count = 0;

        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }

        return count;
    }

    private static void debug(ParsedScript script) {
        System.out.println("[Fabric.SK Parser] Parsed: " + script.getName());

        for (ScriptSection section : script.getSections()) {
            System.out.println("SECTION: " + section.getHeader());

            for (ScriptLine line : section.getLines()) {
                System.out.println(
                    "  [" + line.getIndent() + "] " + line.getContent()
                );
            }
        }
    }
}
