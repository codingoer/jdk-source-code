package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

public class SimpleNameClass extends NameClass {
   public final QName name;

   public SimpleNameClass(QName name) {
      this.name = name;
   }

   public SimpleNameClass(String nsUri, String localPart) {
      this(new QName(nsUri, localPart));
   }

   public SimpleNameClass(String nsUri, String localPart, String prefix) {
      this(new QName(nsUri, localPart, prefix));
   }

   public boolean contains(QName name) {
      return this.name.equals(name);
   }

   public int containsSpecificity(QName name) {
      return this.contains(name) ? 2 : -1;
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof SimpleNameClass) {
         SimpleNameClass other = (SimpleNameClass)obj;
         return this.name.equals(other.name);
      } else {
         return false;
      }
   }

   public Object accept(NameClassVisitor visitor) {
      return visitor.visitName(this.name);
   }

   public boolean isOpen() {
      return false;
   }
}
