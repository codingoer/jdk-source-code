package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import org.relaxng.datatype.Datatype;

public class ValuePattern extends StringPattern {
   Object obj;
   Datatype dt;

   ValuePattern(Datatype dt, Object obj) {
      super(combineHashCode(27, obj.hashCode()));
      this.dt = dt;
      this.obj = obj;
   }

   boolean samePattern(Pattern other) {
      if (this.getClass() != other.getClass()) {
         return false;
      } else if (!(other instanceof ValuePattern)) {
         return false;
      } else {
         return this.dt.equals(((ValuePattern)other).dt) && this.dt.sameValue(this.obj, ((ValuePattern)other).obj);
      }
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitValue(this.dt, this.obj);
   }

   public Object apply(PatternFunction f) {
      return f.caseValue(this);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_value");
         default:
      }
   }

   Datatype getDatatype() {
      return this.dt;
   }

   Object getValue() {
      return this.obj;
   }
}
