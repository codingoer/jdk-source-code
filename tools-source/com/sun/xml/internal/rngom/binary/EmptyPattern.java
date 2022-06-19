package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;

public class EmptyPattern extends Pattern {
   EmptyPattern() {
      super(true, 0, 5);
   }

   boolean samePattern(Pattern other) {
      return other instanceof EmptyPattern;
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitEmpty();
   }

   public Object apply(PatternFunction f) {
      return f.caseEmpty(this);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_empty");
         case 7:
            throw new RestrictionViolationException("data_except_contains_empty");
         default:
      }
   }
}
