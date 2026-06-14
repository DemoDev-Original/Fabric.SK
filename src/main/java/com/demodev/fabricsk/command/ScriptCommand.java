package com.demodev.fabricsk.command;

import java.util.ArrayList;
import java.util.List;

public class ScriptCommand {
   private final String name;
   private String permission;
   private final List<String> aliases = new ArrayList();

   public ScriptCommand(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public String getPermission() {
      return this.permission;
   }

   public void setPermission(String permission) {
      this.permission = permission;
   }

   public List<String> getAliases() {
      return this.aliases;
   }

   public void addAlias(String alias) {
      this.aliases.add(alias);
   }
}
