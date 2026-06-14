package com.demodev.fabricsk.variable;

public enum VariableScope {
   GLOBAL,
   SCRIPT,
   RUNTIME;

   // $FF: synthetic method
   private static VariableScope[] $values() {
      return new VariableScope[]{GLOBAL, SCRIPT, RUNTIME};
   }
}
