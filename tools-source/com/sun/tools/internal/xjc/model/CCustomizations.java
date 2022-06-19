package com.sun.tools.internal.xjc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class CCustomizations extends ArrayList {
   CCustomizations next;
   private CCustomizable owner;
   public static final CCustomizations EMPTY = new CCustomizations();

   public CCustomizations() {
   }

   public CCustomizations(Collection cPluginCustomizations) {
      super(cPluginCustomizations);
   }

   void setParent(Model model, CCustomizable owner) {
      if (this.owner == null) {
         this.next = model.customizations;
         model.customizations = this;

         assert owner != null;

         this.owner = owner;
      }
   }

   public CCustomizable getOwner() {
      assert this.owner != null;

      return this.owner;
   }

   public CPluginCustomization find(String nsUri) {
      Iterator var2 = this.iterator();

      CPluginCustomization p;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         p = (CPluginCustomization)var2.next();
      } while(!this.fixNull(p.element.getNamespaceURI()).equals(nsUri));

      return p;
   }

   public CPluginCustomization find(String nsUri, String localName) {
      Iterator var3 = this.iterator();

      CPluginCustomization p;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         p = (CPluginCustomization)var3.next();
      } while(!this.fixNull(p.element.getNamespaceURI()).equals(nsUri) || !this.fixNull(p.element.getLocalName()).equals(localName));

      return p;
   }

   private String fixNull(String s) {
      return s == null ? "" : s;
   }

   public static CCustomizations merge(CCustomizations lhs, CCustomizations rhs) {
      if (lhs != null && !lhs.isEmpty()) {
         if (rhs != null && !rhs.isEmpty()) {
            CCustomizations r = new CCustomizations(lhs);
            r.addAll(rhs);
            return r;
         } else {
            return lhs;
         }
      } else {
         return rhs;
      }
   }

   public boolean equals(Object o) {
      return this == o;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }
}
