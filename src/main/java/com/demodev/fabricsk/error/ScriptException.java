package com.demodev.fabricsk.error;

public class ScriptException extends Exception {
   private final ErrorCode errorCode;
   private final String file;
   private final int line;
   private final int column;
   private final String sourceLine;

   public ScriptException(ErrorCode errorCode, String message, String file, int line, int column, String sourceLine) {
      super(message);
      this.errorCode = errorCode;
      this.file = file;
      this.line = line;
      this.column = column;
      this.sourceLine = sourceLine;
   }

   public ErrorCode getErrorCode() {
      return this.errorCode;
   }

   public String getFile() {
      return this.file;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   public String getSourceLine() {
      return this.sourceLine;
   }
}
