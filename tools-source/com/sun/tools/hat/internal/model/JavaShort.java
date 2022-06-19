package com.sun.tools.hat.internal.model;

public class JavaShort extends JavaValue {
   short value;

   public JavaShort(short var1) {
      this.value = var1;
   }

   public String toString() {
      return "" + this.value;
   }
}
