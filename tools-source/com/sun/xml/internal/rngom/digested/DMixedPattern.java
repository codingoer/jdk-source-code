package com.sun.xml.internal.rngom.digested;

public class DMixedPattern extends DUnaryPattern {
   public boolean isNullable() {
      return this.getChild().isNullable();
   }

   public Object accept(DPatternVisitor visitor) {
      return visitor.onMixed(this);
   }
}
