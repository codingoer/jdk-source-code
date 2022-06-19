package com.sun.tools.javap;

public class InternalError extends Error {
   private static final long serialVersionUID = 8114054446416187030L;
   public final Object[] args;

   InternalError(Throwable var1, Object... var2) {
      super("Internal error", var1);
      this.args = var2;
   }

   InternalError(Object... var1) {
      super("Internal error");
      this.args = var1;
   }
}
