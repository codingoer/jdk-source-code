package com.sun.xml.internal.rngom.ast.builder;

public class BuildException extends RuntimeException {
   private final Throwable cause;

   public BuildException(Throwable cause) {
      if (cause == null) {
         throw new NullPointerException("null cause");
      } else {
         this.cause = cause;
      }
   }

   public Throwable getCause() {
      return this.cause;
   }
}
