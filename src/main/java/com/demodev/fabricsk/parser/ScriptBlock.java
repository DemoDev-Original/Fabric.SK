package com.demodev.fabricsk.parser;

import java.util.ArrayList;
import java.util.List;

public class ScriptBlock {

    private final String type;
    private final List<String> lines = new ArrayList<>();

    public ScriptBlock(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String line) {
        lines.add(line);
    }
}
