package com.sun.xml.internal.rngom.parse.compact;

class EscapeSyntaxException extends RuntimeException {
   private final String key;
   private final int lineNumber;
   private final int columnNumber;

   EscapeSyntaxException(String key, int lineNumber, int columnNumber) {
      this.key = key;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
   }

   String getKey() {
      return this.key;
   }

   int getLineNumber() {
      return this.lineNumber;
   }

   int getColumnNumber() {
      return this.columnNumber;
   }
}
