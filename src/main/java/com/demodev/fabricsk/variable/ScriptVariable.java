package com.demodev.fabricsk.variable;

public class ScriptVariable {

    private final String name;
    private Object value;
    private final VariableScope scope;
    private final VariableType type;

    public ScriptVariable(
        String name,
        Object value,
        VariableScope scope,
        VariableType type
    ) {
        this.name = name;
        this.value = value;
        this.scope = scope;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public VariableScope getScope() {
        return scope;
    }

    public VariableType getType() {
        return type;
    }

    public String asString() {
        return String.valueOf(value);
    }

    public int asInt() {
        if (value instanceof Number n) {
            return n.intValue();
        }
        return 0;
    }

    public boolean asBoolean() {
        if (value instanceof Boolean b) {
            return b;
        }
        return false;
    }
}
