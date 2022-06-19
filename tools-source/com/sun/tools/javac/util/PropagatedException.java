package com.sun.tools.javac.util;

public class PropagatedException extends RuntimeException {
   static final long serialVersionUID = -6065309339888775367L;

   public PropagatedException(RuntimeException var1) {
      super(var1);
   }

   public RuntimeException getCause() {
      return (RuntimeException)super.getCause();
   }
}
