package com.sun.tools.hat.internal.model;

public class JavaBoolean extends JavaValue {
   boolean value;

   public JavaBoolean(boolean var1) {
      this.value = var1;
   }

   public String toString() {
      return "" + this.value;
   }
}
