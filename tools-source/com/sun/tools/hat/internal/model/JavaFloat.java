package com.sun.tools.hat.internal.model;

public class JavaFloat extends JavaValue {
   float value;

   public JavaFloat(float var1) {
      this.value = var1;
   }

   public String toString() {
      return Float.toString(this.value);
   }
}
