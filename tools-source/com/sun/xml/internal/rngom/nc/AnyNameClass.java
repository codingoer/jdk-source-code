package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

final class AnyNameClass extends NameClass {
   protected AnyNameClass() {
   }

   public boolean contains(QName name) {
      return true;
   }

   public int containsSpecificity(QName name) {
      return 0;
   }

   public boolean equals(Object obj) {
      return obj == this;
   }

   public int hashCode() {
      return AnyNameClass.class.hashCode();
   }

   public Object accept(NameClassVisitor visitor) {
      return visitor.visitAnyName();
   }

   public boolean isOpen() {
      return true;
   }
}
