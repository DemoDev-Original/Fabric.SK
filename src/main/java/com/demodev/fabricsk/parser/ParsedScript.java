package com.demodev.fabricsk.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsedScript {
   private final String name;
   private final List<ScriptSection> sections = new ArrayList();

   public ParsedScript(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public List<ScriptSection> getSections() {
      return this.sections;
   }

   public void addSection(ScriptSection section) {
      this.sections.add(section);
   }
}
