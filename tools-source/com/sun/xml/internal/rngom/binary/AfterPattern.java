package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;

public class AfterPattern extends BinaryPattern {
   AfterPattern(Pattern p1, Pattern p2) {
      super(false, combineHashCode(41, p1.hashCode(), p2.hashCode()), p1, p2);
   }

   boolean isNotAllowed() {
      return this.p1.isNotAllowed();
   }

   public Object apply(PatternFunction f) {
      return f.caseAfter(this);
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitAfter(this.p1, this.p2);
   }
}
