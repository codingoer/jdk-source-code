package com.sun.xml.internal.rngom.digested;

public class DGroupPattern extends DContainerPattern {
   public boolean isNullable() {
      for(DPattern p = this.firstChild(); p != null; p = p.next) {
         if (!p.isNullable()) {
            return false;
         }
      }

      return true;
   }

   public Object accept(DPatternVisitor visitor) {
      return visitor.onGroup(this);
   }
}
