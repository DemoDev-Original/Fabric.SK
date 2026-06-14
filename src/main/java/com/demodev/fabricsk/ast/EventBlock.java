package com.demodev.fabricsk.ast;

import java.util.List;

public record EventBlock(String eventName, List<Statement> body) implements Statement {
}
