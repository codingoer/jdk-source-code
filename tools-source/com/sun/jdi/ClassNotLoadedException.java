package com.sun.jdi;

import jdk.Exported;

@Exported
public class ClassNotLoadedException extends Exception {
   private static final long serialVersionUID = -6242978768444298722L;
   private String className;

   public ClassNotLoadedException(String var1) {
      this.className = var1;
   }

   public ClassNotLoadedException(String var1, String var2) {
      super(var2);
      this.className = var1;
   }

   public String className() {
      return this.className;
   }
}
