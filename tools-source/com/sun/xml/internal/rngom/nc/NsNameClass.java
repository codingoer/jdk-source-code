package com.sun.xml.internal.rngom.nc;

import javax.xml.namespace.QName;

public final class NsNameClass extends NameClass {
   private final String namespaceUri;

   public NsNameClass(String namespaceUri) {
      this.namespaceUri = namespaceUri;
   }

   public boolean contains(QName name) {
      return this.namespaceUri.equals(name.getNamespaceURI());
   }

   public int containsSpecificity(QName name) {
      return this.contains(name) ? 1 : -1;
   }

   public int hashCode() {
      return this.namespaceUri.hashCode();
   }

   public boolean equals(Object obj) {
      return obj != null && obj instanceof NsNameClass ? this.namespaceUri.equals(((NsNameClass)obj).namespaceUri) : false;
   }

   public Object accept(NameClassVisitor visitor) {
      return visitor.visitNsName(this.namespaceUri);
   }

   public boolean isOpen() {
      return true;
   }
}
