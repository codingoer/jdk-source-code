package com.sun.xml.internal.rngom.digested;

public class DChoicePattern extends DContainerPattern {
   public boolean isNullable() {
      for(DPattern p = this.firstChild(); p != null; p = p.next) {
         if (p.isNullable()) {
            return true;
         }
      }

      return false;
   }

   public Object accept(DPatternVisitor visitor) {
      return visitor.onChoice(this);
   }
}
