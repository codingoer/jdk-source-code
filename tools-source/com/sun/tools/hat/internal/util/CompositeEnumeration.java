package com.sun.tools.hat.internal.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class CompositeEnumeration implements Enumeration {
   Enumeration e1;
   Enumeration e2;

   public CompositeEnumeration(Enumeration var1, Enumeration var2) {
      this.e1 = var1;
      this.e2 = var2;
   }

   public boolean hasMoreElements() {
      return this.e1.hasMoreElements() || this.e2.hasMoreElements();
   }

   public Object nextElement() {
      if (this.e1.hasMoreElements()) {
         return this.e1.nextElement();
      } else if (this.e2.hasMoreElements()) {
         return this.e2.nextElement();
      } else {
         throw new NoSuchElementException();
      }
   }
}
