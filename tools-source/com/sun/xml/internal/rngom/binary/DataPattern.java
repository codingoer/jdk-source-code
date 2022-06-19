package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import org.relaxng.datatype.Datatype;

public class DataPattern extends StringPattern {
   private Datatype dt;

   DataPattern(Datatype dt) {
      super(combineHashCode(31, dt.hashCode()));
      this.dt = dt;
   }

   boolean samePattern(Pattern other) {
      return other.getClass() != this.getClass() ? false : this.dt.equals(((DataPattern)other).dt);
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitData(this.dt);
   }

   public Object apply(PatternFunction f) {
      return f.caseData(this);
   }

   Datatype getDatatype() {
      return this.dt;
   }

   boolean allowsAnyString() {
      return false;
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_data");
         default:
      }
   }
}
