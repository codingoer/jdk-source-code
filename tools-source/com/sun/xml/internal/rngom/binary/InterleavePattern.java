package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;

public class InterleavePattern extends BinaryPattern {
   InterleavePattern(Pattern p1, Pattern p2) {
      super(p1.isNullable() && p2.isNullable(), combineHashCode(17, p1.hashCode(), p2.hashCode()), p1, p2);
   }

   Pattern expand(SchemaPatternBuilder b) {
      Pattern ep1 = this.p1.expand(b);
      Pattern ep2 = this.p2.expand(b);
      return (Pattern)(ep1 == this.p1 && ep2 == this.p2 ? this : b.makeInterleave(ep1, ep2));
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_interleave");
         case 6:
            throw new RestrictionViolationException("list_contains_interleave");
         case 7:
            throw new RestrictionViolationException("data_except_contains_interleave");
         default:
            if (context == 2) {
               context = 4;
            }

            Alphabet a1;
            if (alpha != null && alpha.isEmpty()) {
               a1 = alpha;
            } else {
               a1 = new Alphabet();
            }

            this.p1.checkRestrictions(context, dad, a1);
            if (a1.isEmpty()) {
               this.p2.checkRestrictions(context, dad, a1);
            } else {
               Alphabet a2 = new Alphabet();
               this.p2.checkRestrictions(context, dad, a2);
               a1.checkOverlap(a2);
               if (alpha != null) {
                  if (alpha != a1) {
                     alpha.addAlphabet(a1);
                  }

                  alpha.addAlphabet(a2);
               }
            }

            if (context != 6 && !contentTypeGroupable(this.p1.getContentType(), this.p2.getContentType())) {
               throw new RestrictionViolationException("interleave_string");
            } else if (this.p1.getContentType() == 2 && this.p2.getContentType() == 2) {
               throw new RestrictionViolationException("interleave_text_overlap");
            }
      }
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitInterleave(this.p1, this.p2);
   }

   public Object apply(PatternFunction f) {
      return f.caseInterleave(this);
   }
}
