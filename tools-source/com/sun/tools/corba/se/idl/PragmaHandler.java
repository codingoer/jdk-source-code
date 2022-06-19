package com.sun.tools.corba.se.idl;

import java.io.IOException;

public abstract class PragmaHandler {
   private Preprocessor preprocessor = null;

   public abstract boolean process(String var1, String var2) throws IOException;

   void init(Preprocessor var1) {
      this.preprocessor = var1;
   }

   protected String currentToken() {
      return this.preprocessor.currentToken();
   }

   protected SymtabEntry getEntryForName(String var1) {
      return this.preprocessor.getEntryForName(var1);
   }

   protected String getStringToEOL() throws IOException {
      return this.preprocessor.getStringToEOL();
   }

   protected String getUntil(char var1) throws IOException {
      return this.preprocessor.getUntil(var1);
   }

   protected String nextToken() throws IOException {
      return this.preprocessor.nextToken();
   }

   protected SymtabEntry scopedName() throws IOException {
      return this.preprocessor.scopedName();
   }

   protected void skipToEOL() throws IOException {
      this.preprocessor.skipToEOL();
   }

   protected String skipUntil(char var1) throws IOException {
      return this.preprocessor.skipUntil(var1);
   }

   protected void parseException(String var1) {
      this.preprocessor.parseException(var1);
   }

   protected void openScope(SymtabEntry var1) {
   }

   protected void closeScope(SymtabEntry var1) {
   }
}
