package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import org.xml.sax.SAXException;

public class OneOrMorePattern extends Pattern {
   Pattern p;

   OneOrMorePattern(Pattern p) {
      super(p.isNullable(), p.getContentType(), combineHashCode(19, p.hashCode()));
      this.p = p;
   }

   Pattern expand(SchemaPatternBuilder b) {
      Pattern ep = this.p.expand(b);
      return (Pattern)(ep != this.p ? b.makeOneOrMore(ep) : this);
   }

   void checkRecursion(int depth) throws SAXException {
      this.p.checkRecursion(depth);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_one_or_more");
         case 7:
            throw new RestrictionViolationException("data_except_contains_one_or_more");
         default:
            this.p.checkRestrictions(context == 1 ? 2 : context, dad, alpha);
            if (context != 6 && !contentTypeGroupable(this.p.getContentType(), this.p.getContentType())) {
               throw new RestrictionViolationException("one_or_more_string");
            }
      }
   }

   boolean samePattern(Pattern other) {
      return other instanceof OneOrMorePattern && this.p == ((OneOrMorePattern)other).p;
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitOneOrMore(this.p);
   }

   public Object apply(PatternFunction f) {
      return f.caseOneOrMore(this);
   }

   Pattern getOperand() {
      return this.p;
   }
}
