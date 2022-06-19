package com.sun.tools.javac.parser;

import com.sun.tools.javac.util.Position;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class Scanner implements Lexer {
   private Tokens tokens;
   private Tokens.Token token;
   private Tokens.Token prevToken;
   private List savedTokens;
   private JavaTokenizer tokenizer;

   protected Scanner(ScannerFactory var1, CharBuffer var2) {
      this(var1, new JavaTokenizer(var1, var2));
   }

   protected Scanner(ScannerFactory var1, char[] var2, int var3) {
      this(var1, new JavaTokenizer(var1, var2, var3));
   }

   protected Scanner(ScannerFactory var1, JavaTokenizer var2) {
      this.savedTokens = new ArrayList();
      this.tokenizer = var2;
      this.tokens = var1.tokens;
      this.token = this.prevToken = Tokens.DUMMY;
   }

   public Tokens.Token token() {
      return this.token(0);
   }

   public Tokens.Token token(int var1) {
      if (var1 == 0) {
         return this.token;
      } else {
         this.ensureLookahead(var1);
         return (Tokens.Token)this.savedTokens.get(var1 - 1);
      }
   }

   private void ensureLookahead(int var1) {
      for(int var2 = this.savedTokens.size(); var2 < var1; ++var2) {
         this.savedTokens.add(this.tokenizer.readToken());
      }

   }

   public Tokens.Token prevToken() {
      return this.prevToken;
   }

   public void nextToken() {
      this.prevToken = this.token;
      if (!this.savedTokens.isEmpty()) {
         this.token = (Tokens.Token)this.savedTokens.remove(0);
      } else {
         this.token = this.tokenizer.readToken();
      }

   }

   public Tokens.Token split() {
      Tokens.Token[] var1 = this.token.split(this.tokens);
      this.prevToken = var1[0];
      this.token = var1[1];
      return this.token;
   }

   public Position.LineMap getLineMap() {
      return this.tokenizer.getLineMap();
   }

   public int errPos() {
      return this.tokenizer.errPos();
   }

   public void errPos(int var1) {
      this.tokenizer.errPos(var1);
   }
}
