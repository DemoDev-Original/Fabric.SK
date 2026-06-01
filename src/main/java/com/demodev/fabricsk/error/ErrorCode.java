package com.demodev.fabricsk.error;

public enum ErrorCode {
    FSK001("Unknown syntax"),
    FSK002("Missing section"),
    FSK003("Invalid event"),
    FSK004("Invalid command"),
    FSK005("Script not found"),
    FSK006("Manifest error"),
    FSK007("Package error"),
    FSK008("Validation error"),
    FSK009("Runtime error"),
    FSK010("Internal error");

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
