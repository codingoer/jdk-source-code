package com.sun.tools.hat.internal.model;

public abstract class JavaThing {
   protected JavaThing() {
   }

   public JavaThing dereference(Snapshot var1, JavaField var2) {
      return this;
   }

   public boolean isSameTypeAs(JavaThing var1) {
      return this.getClass() == var1.getClass();
   }

   public abstract boolean isHeapAllocated();

   public abstract int getSize();

   public abstract String toString();

   public int compareTo(JavaThing var1) {
      return this.toString().compareTo(var1.toString());
   }
}
