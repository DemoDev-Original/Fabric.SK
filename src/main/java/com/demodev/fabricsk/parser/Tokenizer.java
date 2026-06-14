package com.demodev.fabricsk.parser;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
   public List<Token> tokenize(String input) {
      List<Token> tokens = new ArrayList();
      String[] lines = input.split("\n");

      for(String line : lines) {
         line = line.trim();
         if (!line.isEmpty()) {
            String[] parts = line.split(" ");

            for(String part : parts) {
               tokens.add(new Token(TokenType.WORD, part));
            }

            tokens.add(new Token(TokenType.NEWLINE, "\\n"));
         }
      }

      return tokens;
   }
}
