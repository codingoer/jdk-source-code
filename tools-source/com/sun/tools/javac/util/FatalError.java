package com.sun.tools.javac.util;

public class FatalError extends Error {
   private static final long serialVersionUID = 0L;

   public FatalError(JCDiagnostic var1) {
      super(var1.toString());
   }

   public FatalError(JCDiagnostic var1, Throwable var2) {
      super(var1.toString(), var2);
   }

   public FatalError(String var1) {
      super(var1);
   }
}
