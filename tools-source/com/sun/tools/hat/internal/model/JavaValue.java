package com.sun.tools.hat.internal.model;

public abstract class JavaValue extends JavaThing {
   protected JavaValue() {
   }

   public boolean isHeapAllocated() {
      return false;
   }

   public abstract String toString();

   public int getSize() {
      return 0;
   }
}
