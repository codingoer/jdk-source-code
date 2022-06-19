package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

public class NsNameExceptNameClass extends NameClass {
   private final NameClass nameClass;
   private final String namespaceURI;

   public NsNameExceptNameClass(String namespaceURI, NameClass nameClass) {
      this.namespaceURI = namespaceURI;
      this.nameClass = nameClass;
   }

   public boolean contains(QName name) {
      return this.namespaceURI.equals(name.getNamespaceURI()) && !this.nameClass.contains(name);
   }

   public int containsSpecificity(QName name) {
      return this.contains(name) ? 1 : -1;
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof NsNameExceptNameClass) {
         NsNameExceptNameClass other = (NsNameExceptNameClass)obj;
         return this.namespaceURI.equals(other.namespaceURI) && this.nameClass.equals(other.nameClass);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.namespaceURI.hashCode() ^ this.nameClass.hashCode();
   }

   public Object accept(NameClassVisitor visitor) {
      return visitor.visitNsNameExcept(this.namespaceURI, this.nameClass);
   }

   public boolean isOpen() {
      return true;
   }
}
