package com.demodev.fabricsk.error;

public enum ErrorCode {
   FSK001("Unknown syntax"),
   FSK002("Missing section"),
   FSK003("Invalid event"),
   FSK004("Invalid command"),
   FSK005("Script not found"),
   FSK006("Manifest error"),
   FSK007("Package error"),
   FSK008("Validation error"),
   FSK009("Runtime error"),
   FSK010("Internal error");

   private final String description;

   private ErrorCode(String description) {
      this.description = description;
   }

   public String description() {
      return this.description;
   }

   // $FF: synthetic method
   private static ErrorCode[] $values() {
      return new ErrorCode[]{FSK001, FSK002, FSK003, FSK004, FSK005, FSK006, FSK007, FSK008, FSK009, FSK010};
   }
}
