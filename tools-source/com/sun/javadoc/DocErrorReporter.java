package com.sun.javadoc;

public interface DocErrorReporter {
   void printError(String var1);

   void printError(SourcePosition var1, String var2);

   void printWarning(String var1);

   void printWarning(SourcePosition var1, String var2);

   void printNotice(String var1);

   void printNotice(SourcePosition var1, String var2);
}
