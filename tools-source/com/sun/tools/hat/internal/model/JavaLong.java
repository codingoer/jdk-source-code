package com.sun.tools.hat.internal.model;

public class JavaLong extends JavaValue {
   long value;

   public JavaLong(long var1) {
      this.value = var1;
   }

   public String toString() {
      return Long.toString(this.value);
   }
}
