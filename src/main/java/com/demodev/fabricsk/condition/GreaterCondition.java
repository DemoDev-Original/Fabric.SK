package com.demodev.fabricsk.condition;

public class GreaterCondition implements Condition {

    @Override
    public boolean evaluate(Object left, Object right) {
        if (!(left instanceof Number a) || !(right instanceof Number b)) {
            return false;
        }

        return a.doubleValue() > b.doubleValue();
    }
}
