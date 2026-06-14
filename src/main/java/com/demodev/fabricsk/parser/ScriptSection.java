package com.demodev.fabricsk.parser;

import java.util.ArrayList;
import java.util.List;

public class ScriptSection {
   private final String header;
   private final List<ScriptLine> lines = new ArrayList();

   public ScriptSection(String header) {
      this.header = header;
   }

   public String getHeader() {
      return this.header;
   }

   public List<ScriptLine> getLines() {
      return this.lines;
   }

   public void addLine(ScriptLine line) {
      this.lines.add(line);
   }
}
