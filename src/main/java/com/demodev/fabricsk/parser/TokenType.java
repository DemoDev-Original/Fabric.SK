package com.demodev.fabricsk.parser;

public enum TokenType {
   WORD,
   STRING,
   NEWLINE,
   ARROW,
   COLON;

   // $FF: synthetic method
   private static TokenType[] $values() {
      return new TokenType[]{WORD, STRING, NEWLINE, ARROW, COLON};
   }
}
