package com.demodev.fabricsk.runtime;

import com.demodev.fabricsk.ast.BinaryExpression;
import com.demodev.fabricsk.ast.Expression;
import com.demodev.fabricsk.ast.LiteralExpression;
import com.demodev.fabricsk.ast.VariableReferenceExpression;

public class ExpressionEvaluator {
   public Object evaluate(Expression expression, ScriptContext context) {
      if (expression instanceof LiteralExpression literal) {
         return literal.value();
      } else if (expression instanceof VariableReferenceExpression variable) {
         return context.getVariable(variable.name());
      } else if (expression instanceof BinaryExpression) {
         BinaryExpression binary = (BinaryExpression)expression;
         Object left = this.evaluate(binary.left(), context);
         Object right = this.evaluate(binary.right(), context);
         Boolean var10000;
         switch (binary.operator()) {
            case "==" -> var10000 = left.equals(right);
            case "!=" -> var10000 = !left.equals(right);
            default -> var10000 = false;
         }

         return var10000;
      } else {
         return null;
      }
   }
}
