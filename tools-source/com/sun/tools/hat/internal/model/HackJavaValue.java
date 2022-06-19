package com.sun.tools.hat.internal.model;

public class HackJavaValue extends JavaValue {
   private String value;
   private int size;

   public HackJavaValue(String var1, int var2) {
      this.value = var1;
      this.size = var2;
   }

   public String toString() {
      return this.value;
   }

   public int getSize() {
      return this.size;
   }
}
