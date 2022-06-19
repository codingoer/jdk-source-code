package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;

public class TextPattern extends Pattern {
   TextPattern() {
      super(true, 2, 1);
   }

   boolean samePattern(Pattern other) {
      return other instanceof TextPattern;
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitText();
   }

   public Object apply(PatternFunction f) {
      return f.caseText(this);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_text");
         case 6:
            throw new RestrictionViolationException("list_contains_text");
         case 7:
            throw new RestrictionViolationException("data_except_contains_text");
         default:
      }
   }
}
