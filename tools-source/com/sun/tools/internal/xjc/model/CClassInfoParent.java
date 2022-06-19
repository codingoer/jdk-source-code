package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JPackage;

public interface CClassInfoParent {
   String fullName();

   Object accept(Visitor var1);

   JPackage getOwnerPackage();

   public static final class Package implements CClassInfoParent {
      public final JPackage pkg;

      public Package(JPackage pkg) {
         this.pkg = pkg;
      }

      public String fullName() {
         return this.pkg.name();
      }

      public Object accept(Visitor visitor) {
         return visitor.onPackage(this.pkg);
      }

      public JPackage getOwnerPackage() {
         return this.pkg;
      }
   }

   public interface Visitor {
      Object onBean(CClassInfo var1);

      Object onPackage(JPackage var1);

      Object onElement(CElementInfo var1);
   }
}
