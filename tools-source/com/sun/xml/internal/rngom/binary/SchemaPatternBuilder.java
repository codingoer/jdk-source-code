package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.nc.NameClass;
import org.relaxng.datatype.Datatype;
import org.xml.sax.Locator;

public class SchemaPatternBuilder extends PatternBuilder {
   private boolean idTypes;
   private final Pattern unexpandedNotAllowed = new NotAllowedPattern() {
      boolean isNotAllowed() {
         return false;
      }

      Pattern expand(SchemaPatternBuilder b) {
         return b.makeNotAllowed();
      }
   };
   private final TextPattern text = new TextPattern();
   private final PatternInterner schemaInterner = new PatternInterner();

   public boolean hasIdTypes() {
      return this.idTypes;
   }

   Pattern makeElement(NameClass nameClass, Pattern content, Locator loc) {
      Pattern p = new ElementPattern(nameClass, content, loc);
      return this.schemaInterner.intern(p);
   }

   Pattern makeAttribute(NameClass nameClass, Pattern value, Locator loc) {
      if (value == this.notAllowed) {
         return value;
      } else {
         Pattern p = new AttributePattern(nameClass, value, loc);
         return this.schemaInterner.intern(p);
      }
   }

   Pattern makeData(Datatype dt) {
      this.noteDatatype(dt);
      Pattern p = new DataPattern(dt);
      return this.schemaInterner.intern(p);
   }

   Pattern makeDataExcept(Datatype dt, Pattern except, Locator loc) {
      this.noteDatatype(dt);
      Pattern p = new DataExceptPattern(dt, except, loc);
      return this.schemaInterner.intern(p);
   }

   Pattern makeValue(Datatype dt, Object obj) {
      this.noteDatatype(dt);
      Pattern p = new ValuePattern(dt, obj);
      return this.schemaInterner.intern(p);
   }

   Pattern makeText() {
      return this.text;
   }

   Pattern makeOneOrMore(Pattern p) {
      return p == this.text ? p : super.makeOneOrMore(p);
   }

   Pattern makeUnexpandedNotAllowed() {
      return this.unexpandedNotAllowed;
   }

   Pattern makeError() {
      Pattern p = new ErrorPattern();
      return this.schemaInterner.intern(p);
   }

   Pattern makeChoice(Pattern p1, Pattern p2) {
      if (p1 != this.notAllowed && p1 != p2) {
         return p2 == this.notAllowed ? p1 : super.makeChoice(p1, p2);
      } else {
         return p2;
      }
   }

   Pattern makeList(Pattern p, Locator loc) {
      if (p == this.notAllowed) {
         return p;
      } else {
         Pattern p1 = new ListPattern(p, loc);
         return this.schemaInterner.intern(p1);
      }
   }

   Pattern makeMixed(Pattern p) {
      return this.makeInterleave(this.text, p);
   }

   private void noteDatatype(Datatype dt) {
      if (dt.getIdType() != 0) {
         this.idTypes = true;
      }

   }
}
