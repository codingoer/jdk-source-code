package com.sun.tools.javac.util;

public class Abort extends Error {
   private static final long serialVersionUID = 0L;

   public Abort(Throwable var1) {
      super(var1);
   }

   public Abort() {
   }
}
