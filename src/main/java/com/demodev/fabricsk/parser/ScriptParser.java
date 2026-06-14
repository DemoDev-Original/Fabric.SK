package com.demodev.fabricsk.parser;

import com.demodev.fabricsk.ast.CommandStatement;
import com.demodev.fabricsk.ast.EventBlock;
import com.demodev.fabricsk.ast.IfStatement;
import com.demodev.fabricsk.ast.LiteralExpression;
import com.demodev.fabricsk.ast.Script;
import com.demodev.fabricsk.ast.Statement;
import com.demodev.fabricsk.ast.VariableDeclarationStatement;
import java.util.ArrayList;
import java.util.List;

public class ScriptParser {
   private final Tokenizer tokenizer = new Tokenizer();

   public Script parse(String name, String content) {
      List<Token> tokens = this.tokenizer.tokenize(content);
      List<Statement> statements = new ArrayList();
      List<String> buffer = new ArrayList();

      for(Token token : tokens) {
         if (token.type() == TokenType.NEWLINE) {
            if (!buffer.isEmpty()) {
               Statement statement = this.parseLine(buffer);
               if (statement != null) {
                  statements.add(statement);
               }

               buffer.clear();
            }
         } else {
            buffer.add(token.value());
         }
      }

      if (!buffer.isEmpty()) {
         Statement statement = this.parseLine(buffer);
         if (statement != null) {
            statements.add(statement);
         }
      }

      return new Script(name, statements);
   }

   private Statement parseLine(List<String> parts) {
      if (parts.isEmpty()) {
         return null;
      } else {
         String first = (String)parts.get(0);
         if (first.equalsIgnoreCase("let") && parts.size() >= 4) {
            String variableName = (String)parts.get(1);
            if (!((String)parts.get(2)).equals("=")) {
               return null;
            } else {
               String value = String.join(" ", parts.subList(3, parts.size()));
               return new VariableDeclarationStatement(variableName, new LiteralExpression(value));
            }
         } else if (first.equalsIgnoreCase("if")) {
            String condition = String.join(" ", parts.subList(1, parts.size()));
            return new IfStatement(new LiteralExpression(condition), List.of(), List.of());
         } else if (!first.equalsIgnoreCase("else") && !first.equalsIgnoreCase("else:")) {
            if (first.endsWith(":") && first.toLowerCase().startsWith("on")) {
               String eventName = first.substring(0, first.length() - 1);
               return new EventBlock(eventName, List.of());
            } else {
               List<String> args = new ArrayList(parts.subList(1, parts.size()));
               return new CommandStatement(first, args);
            }
         } else {
            return new CommandStatement("__else__", List.of());
         }
      }
   }
}
