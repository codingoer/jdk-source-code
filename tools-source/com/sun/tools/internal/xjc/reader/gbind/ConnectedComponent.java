package com.sun.tools.internal.xjc.reader.gbind;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ConnectedComponent implements Iterable {
   private final List elements = new ArrayList();
   boolean isRequired;

   public final boolean isCollection() {
      assert !this.elements.isEmpty();

      if (this.elements.size() > 1) {
         return true;
      } else {
         Element n = (Element)this.elements.get(0);
         return n.hasSelfLoop();
      }
   }

   public final boolean isRequired() {
      return this.isRequired;
   }

   void add(Element e) {
      assert !this.elements.contains(e);

      this.elements.add(e);
   }

   public Iterator iterator() {
      return this.elements.iterator();
   }

   public String toString() {
      String s = this.elements.toString();
      if (this.isRequired()) {
         s = s + '!';
      }

      if (this.isCollection()) {
         s = s + '*';
      }

      return s;
   }
}
