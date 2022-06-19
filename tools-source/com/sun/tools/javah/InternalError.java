package com.sun.tools.javah;

public class InternalError extends Error {
   private static final long serialVersionUID = 8411861562497165022L;

   InternalError(String var1, Throwable var2) {
      super("Internal error: " + var1);
      this.initCause(var2);
   }
}
