package com.sun.tools.hat.internal.model;

public class JavaDouble extends JavaValue {
   double value;

   public JavaDouble(double var1) {
      this.value = var1;
   }

   public String toString() {
      return Double.toString(this.value);
   }
}
