package com.demodev.fabricsk.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsedScript {

    private final String name;
    private final List<ScriptSection> sections = new ArrayList<>();

    public ParsedScript(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<ScriptSection> getSections() {
        return sections;
    }

    public void addSection(ScriptSection section) {
        sections.add(section);
    }
}
