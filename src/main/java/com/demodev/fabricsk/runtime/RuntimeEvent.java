package com.demodev.fabricsk.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeEvent {

    private final String eventName;

    private final String sourceFile;

    private final List<String> triggerLines = new ArrayList<>();

    public RuntimeEvent(String eventName, String sourceFile) {
        this.eventName = eventName;
        this.sourceFile = sourceFile;
    }

    public String getEventName() {
        return eventName;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void addTriggerLine(String line) {
        triggerLines.add(line);
    }

    public List<String> getTriggerLines() {
        return Collections.unmodifiableList(triggerLines);
    }
}
