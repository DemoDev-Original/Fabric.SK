package com.demodev.fabricsk.variable;

public class ScriptVariable {
   private final String name;
   private Object value;
   private final VariableScope scope;
   private final VariableType type;

   public ScriptVariable(String name, Object value, VariableScope scope, VariableType type) {
      this.name = name;
      this.value = value;
      this.scope = scope;
      this.type = type;
   }

   public String getName() {
      return this.name;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   public VariableScope getScope() {
      return this.scope;
   }

   public VariableType getType() {
      return this.type;
   }

   public String asString() {
      return String.valueOf(this.value);
   }

   public int asInt() {
      Object var2 = this.value;
      if (var2 instanceof Number n) {
         return n.intValue();
      } else {
         return 0;
      }
   }

   public boolean asBoolean() {
      Object var2 = this.value;
      if (var2 instanceof Boolean b) {
         return b;
      } else {
         return false;
      }
   }
}
