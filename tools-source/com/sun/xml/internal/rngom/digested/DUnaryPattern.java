package com.sun.xml.internal.rngom.digested;

public abstract class DUnaryPattern extends DPattern {
   private DPattern child;

   public DPattern getChild() {
      return this.child;
   }

   public void setChild(DPattern child) {
      this.child = child;
   }
}
