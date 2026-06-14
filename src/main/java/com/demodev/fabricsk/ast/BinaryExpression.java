package com.demodev.fabricsk.ast;

public record BinaryExpression(Expression left, String operator, Expression right) implements Expression {
}
