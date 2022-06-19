package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.binary.visitor.PatternFunction;
import com.sun.xml.internal.rngom.binary.visitor.PatternVisitor;
import com.sun.xml.internal.rngom.nc.NameClass;
import com.sun.xml.internal.rngom.nc.SimpleNameClass;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class AttributePattern extends Pattern {
   private NameClass nameClass;
   private Pattern p;
   private Locator loc;

   AttributePattern(NameClass nameClass, Pattern value, Locator loc) {
      super(false, 0, combineHashCode(29, nameClass.hashCode(), value.hashCode()));
      this.nameClass = nameClass;
      this.p = value;
      this.loc = loc;
   }

   Pattern expand(SchemaPatternBuilder b) {
      Pattern ep = this.p.expand(b);
      return (Pattern)(ep != this.p ? b.makeAttribute(this.nameClass, ep, this.loc) : this);
   }

   void checkRestrictions(int context, DuplicateAttributeDetector dad, Alphabet alpha) throws RestrictionViolationException {
      switch (context) {
         case 0:
            throw new RestrictionViolationException("start_contains_attribute");
         case 1:
            if (this.nameClass.isOpen()) {
               throw new RestrictionViolationException("open_name_class_not_repeated");
            }
         case 2:
         default:
            if (!dad.addAttribute(this.nameClass)) {
               if (this.nameClass instanceof SimpleNameClass) {
                  throw new RestrictionViolationException("duplicate_attribute_detail", ((SimpleNameClass)this.nameClass).name);
               }

               throw new RestrictionViolationException("duplicate_attribute");
            } else {
               try {
                  this.p.checkRestrictions(5, (DuplicateAttributeDetector)null, (Alphabet)null);
                  return;
               } catch (RestrictionViolationException var5) {
                  var5.maybeSetLocator(this.loc);
                  throw var5;
               }
            }
         case 3:
            throw new RestrictionViolationException("one_or_more_contains_group_contains_attribute");
         case 4:
            throw new RestrictionViolationException("one_or_more_contains_interleave_contains_attribute");
         case 5:
            throw new RestrictionViolationException("attribute_contains_attribute");
         case 6:
            throw new RestrictionViolationException("list_contains_attribute");
         case 7:
            throw new RestrictionViolationException("data_except_contains_attribute");
      }
   }

   boolean samePattern(Pattern other) {
      if (!(other instanceof AttributePattern)) {
         return false;
      } else {
         AttributePattern ap = (AttributePattern)other;
         return this.nameClass.equals(ap.nameClass) && this.p == ap.p;
      }
   }

   void checkRecursion(int depth) throws SAXException {
      this.p.checkRecursion(depth);
   }

   public void accept(PatternVisitor visitor) {
      visitor.visitAttribute(this.nameClass, this.p);
   }

   public Object apply(PatternFunction f) {
      return f.caseAttribute(this);
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
