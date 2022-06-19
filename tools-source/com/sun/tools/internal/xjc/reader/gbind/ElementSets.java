package com.sun.tools.internal.xjc.reader.gbind;

import java.util.Iterator;
import java.util.LinkedHashSet;

public final class ElementSets {
   public static ElementSet union(ElementSet lhs, ElementSet rhs) {
      if (lhs.contains(rhs)) {
         return lhs;
      } else if (lhs == ElementSet.EMPTY_SET) {
         return rhs;
      } else {
         return (ElementSet)(rhs == ElementSet.EMPTY_SET ? lhs : new MultiValueSet(lhs, rhs));
      }
   }

   private static final class MultiValueSet extends LinkedHashSet implements ElementSet {
      public MultiValueSet(ElementSet lhs, ElementSet rhs) {
         this.addAll(lhs);
         this.addAll(rhs);

         assert this.size() > 1;

      }

      private void addAll(ElementSet lhs) {
         if (lhs instanceof MultiValueSet) {
            super.addAll((MultiValueSet)lhs);
         } else {
            Iterator var2 = lhs.iterator();

            while(var2.hasNext()) {
               Element e = (Element)var2.next();
               this.add(e);
            }
         }

      }

      public boolean contains(ElementSet rhs) {
         return super.contains(rhs) || rhs == ElementSet.EMPTY_SET;
      }

      public void addNext(Element element) {
         Iterator var2 = this.iterator();

         while(var2.hasNext()) {
            Element e = (Element)var2.next();
            e.addNext(element);
         }

      }
   }
}
