package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ListPattern extends Pattern {
   Pattern p;
   Locator locator;

   ListPattern(Pattern p, Locator locator) {
      super(false, 3, combineHashCode(37, p.hashCode()));
      this.p = p;
      this.locator = locator;
   }

   Pattern expand(SchemaPatternBuilder b) {
      Pattern ep = this.p.expand(b);
      return (Pattern)(ep != this.p ? b.makeList(ep, this.locator) : this);
   }

   void checkRecursion(int depth) throws SAXException {
      this.p.checkRecursion(depth);
   }

   boolean samePattern(Pattern other) {
      return other instanceof ListPattern && this.p == ((ListPattern)other).p;
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitList(this.p);
   }

   public Object apply(PatternFunction f) {
      return f.caseList(this);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_list");
         case 6:
            throw new RestrictionViolationException("list_contains_list");
         case 7:
            throw new RestrictionViolationException("data_except_contains_list");
         default:
            try {
               this.p.checkRestrictions(6, dad, (Alphabet)null);
            } catch (RestrictionViolationException var5) {
               var5.maybeSetLocator(this.locator);
               throw var5;
            }
      }
   }

   Pattern getOperand() {
      return this.p;
   }
}
