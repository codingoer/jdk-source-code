package com.sun.tools.hat.internal.model;

public class JavaByte extends JavaValue {
   byte value;

   public JavaByte(byte var1) {
      this.value = var1;
   }

   public String toString() {
      return "0x" + Integer.toString(this.value & 255, 16);
   }
}
