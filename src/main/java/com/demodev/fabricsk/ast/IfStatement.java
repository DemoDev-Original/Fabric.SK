package com.demodev.fabricsk.ast;

import java.util.List;

public record IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) implements Statement {
}
