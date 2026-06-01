package com.demodev.fabricsk.parser;

import com.demodev.fabricsk.error.ScriptError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final List<String> warnings = new ArrayList<>();

    private final List<ScriptError> errors = new ArrayList<>();

    public void addWarning(String warning) {
        warnings.add(warning);
    }

    public void addError(ScriptError error) {
        errors.add(error);
    }

    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }

    public List<ScriptError> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean success() {
        return errors.isEmpty();
    }

    public int warningCount() {
        return warnings.size();
    }

    public int errorCount() {
        return errors.size();
    }
}
