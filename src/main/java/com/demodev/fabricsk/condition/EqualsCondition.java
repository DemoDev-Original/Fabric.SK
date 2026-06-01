package com.demodev.fabricsk.condition;

import java.util.Objects;

public class EqualsCondition implements Condition {

    @Override
    public boolean evaluate(Object left, Object right) {
        return Objects.equals(left, right);
    }
}
