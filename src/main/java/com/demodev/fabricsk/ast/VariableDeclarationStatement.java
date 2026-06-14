package com.demodev.fabricsk.ast;

public record VariableDeclarationStatement(String name, Expression value) implements Statement {
}
