package com.demodev.fabricsk.condition;

public class GreaterCondition implements Condition {
   public boolean evaluate(Object left, Object right) {
      if (left instanceof Number a) {
         if (right instanceof Number b) {
            return a.doubleValue() > b.doubleValue();
         }
      }

      return false;
   }
}
