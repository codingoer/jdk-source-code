package com.sun.xml.internal.rngom.binary;

import com.sun.xml.internal.rngom.nc.ChoiceNameClass;
import com.sun.xml.internal.rngom.nc.NameClass;

class Alphabet {
   private NameClass nameClass;

   boolean isEmpty() {
      return this.nameClass == null;
   }

   void addElement(NameClass nc) {
      if (this.nameClass == null) {
         this.nameClass = nc;
      } else if (nc != null) {
         this.nameClass = new ChoiceNameClass(this.nameClass, nc);
      }

   }

   void addAlphabet(Alphabet a) {
      this.addElement(a.nameClass);
   }

   void checkOverlap(Alphabet a) throws RestrictionViolationException {
      if (this.nameClass != null && a.nameClass != null && this.nameClass.hasOverlapWith(a.nameClass)) {
         throw new RestrictionViolationException("interleave_element_overlap");
      }
   }
}
