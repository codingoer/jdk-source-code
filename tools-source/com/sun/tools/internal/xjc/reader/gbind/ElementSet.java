package com.sun.tools.internal.xjc.reader.gbind;

import java.util.Collections;
import java.util.Iterator;

interface ElementSet extends Iterable {
   ElementSet EMPTY_SET = new ElementSet() {
      public void addNext(Element element) {
      }

      public boolean contains(ElementSet element) {
         return this == element;
      }

      public Iterator iterator() {
         return Collections.emptySet().iterator();
      }
   };

   void addNext(Element var1);

   boolean contains(ElementSet var1);
}
