package com.sun.xml.internal.rngom.digested;

public class DRefPattern extends DPattern {
   private final DDefine target;

   public DRefPattern(DDefine target) {
      this.target = target;
   }

   public boolean isNullable() {
      return this.target.isNullable();
   }

   public DDefine getTarget() {
      return this.target;
   }

   public String getName() {
      return this.target.getName();
   }

   public Object accept(DPatternVisitor visitor) {
      return visitor.onRef(this);
   }
}
