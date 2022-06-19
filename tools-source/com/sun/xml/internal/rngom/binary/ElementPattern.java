package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import com.sun.xml.internal.rngom.nc.NameClass;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class ElementPattern extends Pattern {
   private Pattern p;
   private NameClass origNameClass;
   private NameClass nameClass;
   private boolean expanded = false;
   private boolean checkedRestrictions = false;
   private Locator loc;

   ElementPattern(NameClass nameClass, Pattern p, Locator loc) {
      super(false, 1, combineHashCode(23, nameClass.hashCode(), p.hashCode()));
      this.nameClass = nameClass;
      this.origNameClass = nameClass;
      this.p = p;
      this.loc = loc;
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      if (alpha != null) {
         alpha.addElement(this.origNameClass);
      }

      if (!this.checkedRestrictions) {
         switch (context) {
            case 5:
               throw new RestrictionViolationException("attribute_contains_element");
            case 6:
               throw new RestrictionViolationException("list_contains_element");
            case 7:
               throw new RestrictionViolationException("data_except_contains_element");
            default:
               this.checkedRestrictions = true;

               try {
                  this.p.checkRestrictions(1, new DuplicateAttributeDetector(), (Alphabet)null);
               } catch (RestrictionViolationException var5) {
                  this.checkedRestrictions = false;
                  var5.maybeSetLocator(this.loc);
                  throw var5;
               }
         }
      }
   }

   Pattern expand(SchemaPatternBuilder b) {
      if (!this.expanded) {
         this.expanded = true;
         this.p = this.p.expand(b);
         if (this.p.isNotAllowed()) {
            this.nameClass = NameClass.NULL;
         }
      }

      return this;
   }

   boolean samePattern(Pattern other) {
      if (!(other instanceof ElementPattern)) {
         return false;
      } else {
         ElementPattern ep = (ElementPattern)other;
         return this.nameClass.equals(ep.nameClass) && this.p == ep.p;
      }
   }

   void checkRecursion(int depth) throws SAXException {
      this.p.checkRecursion(depth + 1);
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitElement(this.nameClass, this.p);
   }

   public Object apply(PatternFunction f) {
      return f.caseElement(this);
   }

   void setContent(Pattern p) {
      this.p = p;
   }

   public Pattern getContent() {
      return this.p;
   }

   public NameClass getNameClass() {
      return this.nameClass;
   }

   public Locator getLocator() {
      return this.loc;
   }
}
