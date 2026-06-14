package com.demodev.fabricsk.runtime;

import com.demodev.fabricsk.ast.CommandStatement;
import com.demodev.fabricsk.ast.EventBlock;
import com.demodev.fabricsk.ast.Expression;
import com.demodev.fabricsk.ast.IfStatement;
import com.demodev.fabricsk.ast.LiteralExpression;
import com.demodev.fabricsk.ast.Script;
import com.demodev.fabricsk.ast.Statement;
import com.demodev.fabricsk.ast.VariableDeclarationStatement;
import java.io.PrintStream;

public class ScriptExecutor {
   private final ScriptContext context = new ScriptContext();

   public void execute(Script script) {
      for(Statement stmt : script.statements()) {
         this.executeStatement(stmt);
      }

   }

   private void executeStatement(Statement stmt) {
      if (stmt instanceof CommandStatement cmd) {
         this.executeCommand(cmd);
      } else if (stmt instanceof VariableDeclarationStatement variable) {
         this.executeVariable(variable);
      } else if (stmt instanceof IfStatement ifStatement) {
         this.executeIf(ifStatement);
      } else {
         if (stmt instanceof EventBlock eventBlock) {
            this.registerEvent(eventBlock);
         }

      }
   }

   private void executeCommand(CommandStatement cmd) {
      PrintStream var10000 = System.out;
      String var10001 = cmd.command();
      var10000.println("[Fabric.SK] Running command: " + var10001 + " " + String.join(" ", cmd.args()));
   }

   private void executeVariable(VariableDeclarationStatement variable) {
      Object value = null;
      Expression var4 = variable.value();
      if (var4 instanceof LiteralExpression literal) {
         value = literal.value();
      }

      this.context.setVariable(variable.name(), value);
      PrintStream var10000 = System.out;
      String var10001 = variable.name();
      var10000.println("[Fabric.SK] Variable set: " + var10001 + " = " + String.valueOf(value));
   }

   private void executeIf(IfStatement statement) {
      System.out.println("[Fabric.SK] Evaluating if statement");
   }

   private void registerEvent(EventBlock eventBlock) {
      System.out.println("[Fabric.SK] Registered event: " + eventBlock.eventName());
   }
}
