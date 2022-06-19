package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

public class AnyNameExceptNameClass extends NameClass {
   private final NameClass nameClass;

   public AnyNameExceptNameClass(NameClass nameClass) {
      this.nameClass = nameClass;
   }

   public boolean contains(QName name) {
      return !this.nameClass.contains(name);
   }

   public int containsSpecificity(QName name) {
      return this.contains(name) ? 0 : -1;
   }

   public boolean equals(Object obj) {
      return obj != null && obj instanceof AnyNameExceptNameClass ? this.nameClass.equals(((AnyNameExceptNameClass)obj).nameClass) : false;
   }

   public int hashCode() {
      return ~this.nameClass.hashCode();
   }

   public Object accept(NameClassVisitor visitor) {
      return visitor.visitAnyNameExcept(this.nameClass);
   }

   public boolean isOpen() {
      return true;
   }
}
