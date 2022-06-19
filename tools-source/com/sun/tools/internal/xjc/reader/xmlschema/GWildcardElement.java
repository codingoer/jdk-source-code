package com.sun.tools.internal.xjc.reader.xmlschema;

import com.sun.xml.internal.xsom.XSWildcard;

final class GWildcardElement extends GElement {
   private boolean strict = true;

   public String toString() {
      return "#any";
   }

   String getPropertyNameSeed() {
      return "any";
   }

   public void merge(XSWildcard wc) {
      switch (wc.getMode()) {
         case 1:
         case 3:
            this.strict = false;
         default:
      }
   }

   public boolean isStrict() {
      return this.strict;
   }
}
