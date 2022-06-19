package com.sun.tools.hat.internal.model;

public class JavaChar extends JavaValue {
   char value;

   public JavaChar(char var1) {
      this.value = var1;
   }

   public String toString() {
      return "" + this.value;
   }
}
