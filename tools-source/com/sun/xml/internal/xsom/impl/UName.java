package com.sun.xml.internal.xsom.impl;

import com.sun.xml.internal.xsom.XSDeclaration;
import java.util.Comparator;

public final class UName {
   private final String nsUri;
   private final String localName;
   private final String qname;
   public static final Comparator comparator = new Comparator() {
      public int compare(Object o1, Object o2) {
         UName lhs = (UName)o1;
         UName rhs = (UName)o2;
         int r = lhs.nsUri.compareTo(rhs.nsUri);
         return r != 0 ? r : lhs.localName.compareTo(rhs.localName);
      }
   };

   public UName(String _nsUri, String _localName, String _qname) {
      if (_nsUri != null && _localName != null && _qname != null) {
         this.nsUri = _nsUri.intern();
         this.localName = _localName.intern();
         this.qname = _qname.intern();
      } else {
         throw new NullPointerException(_nsUri + " " + _localName + " " + _qname);
      }
   }

   public UName(String nsUri, String localName) {
      this(nsUri, localName, localName);
   }

   public UName(XSDeclaration decl) {
      this(decl.getTargetNamespace(), decl.getName());
   }

   public String getName() {
      return this.localName;
   }

   public String getNamespaceURI() {
      return this.nsUri;
   }

   public String getQualifiedName() {
      return this.qname;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof UName)) {
         return false;
      } else {
         UName u = (UName)obj;
         return this.getName().compareTo(u.getName()) == 0 && this.getNamespaceURI().compareTo(u.getNamespaceURI()) == 0 && this.getQualifiedName().compareTo(u.getQualifiedName()) == 0;
      }
   }

   public int hashCode() {
      int hash = 7;
      hash = 13 * hash + (this.nsUri != null ? this.nsUri.hashCode() : 0);
      hash = 13 * hash + (this.localName != null ? this.localName.hashCode() : 0);
      hash = 13 * hash + (this.qname != null ? this.qname.hashCode() : 0);
      return hash;
   }
}
