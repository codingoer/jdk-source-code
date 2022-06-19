package com.sun.xml.internal.xsom.util;

import com.sun.xml.internal.xsom.XSType;

public abstract class TypeSet {
   public abstract boolean contains(XSType var1);

   public static TypeSet intersection(final TypeSet a, final TypeSet b) {
      return new TypeSet() {
         public boolean contains(XSType type) {
            return a.contains(type) && b.contains(type);
         }
      };
   }

   public static TypeSet union(final TypeSet a, final TypeSet b) {
      return new TypeSet() {
         public boolean contains(XSType type) {
            return a.contains(type) || b.contains(type);
         }
      };
   }
}
