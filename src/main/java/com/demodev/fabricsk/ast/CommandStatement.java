package com.demodev.fabricsk.ast;

import java.util.List;

public record CommandStatement(String command, List<String> args) implements Statement {
}
