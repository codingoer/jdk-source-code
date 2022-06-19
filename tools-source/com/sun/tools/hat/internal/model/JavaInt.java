package com.sun.tools.hat.internal.model;

public class JavaInt extends JavaValue {
   int value;

   public JavaInt(int var1) {
      this.value = var1;
   }

   public String toString() {
      return "" + this.value;
   }
}
