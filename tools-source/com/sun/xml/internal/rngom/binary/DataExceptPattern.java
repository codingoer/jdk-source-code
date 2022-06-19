package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

public class DataExceptPattern extends DataPattern {
   private Pattern except;
   private Locator loc;

   DataExceptPattern(Datatype dt, Pattern except, Locator loc) {
      super(dt);
      this.except = except;
      this.loc = loc;
   }

   boolean samePattern(Pattern other) {
      return !super.samePattern(other) ? false : this.except.samePattern(((DataExceptPattern)other).except);
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitDataExcept(this.getDatatype(), this.except);
   }

   public Object apply(PatternFunction f) {
      return f.caseDataExcept(this);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      super.checkRestrictions(context, dad, alpha);

      try {
         this.except.checkRestrictions(7, (DuplicateAttributeDetector)null, (Alphabet)null);
      } catch (RestrictionViolationException var5) {
         var5.maybeSetLocator(this.loc);
         throw var5;
      }
   }

   Pattern getExcept() {
      return this.except;
   }
}
