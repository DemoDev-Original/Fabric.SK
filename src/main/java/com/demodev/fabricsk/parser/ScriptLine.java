package com.demodev.fabricsk.parser;

public class ScriptLine {
   private final String content;
   private final int indent;

   public ScriptLine(String content, int indent) {
      this.content = content;
      this.indent = indent;
   }

   public String getContent() {
      return this.content;
   }

   public int getIndent() {
      return this.indent;
   }
}
