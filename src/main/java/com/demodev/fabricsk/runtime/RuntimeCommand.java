package com.demodev.fabricsk.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuntimeCommand {
   private final String name;
   private final String sourceFile;
   private final List<String> triggerLines = new ArrayList();

   public RuntimeCommand(String name, String sourceFile) {
      this.name = name;
      this.sourceFile = sourceFile;
   }

   public String getName() {
      return this.name;
   }

   public String getSourceFile() {
      return this.sourceFile;
   }

   public void addTriggerLine(String line) {
      this.triggerLines.add(line);
   }

   public List<String> getTriggerLines() {
      return Collections.unmodifiableList(this.triggerLines);
   }
}
