package com.sun.tools.internal.xjc.model;

import com.sun.codemodel.internal.JPackage;
import com.sun.tools.internal.xjc.api.ClassNameAllocator;

final class ClassNameAllocatorWrapper implements ClassNameAllocator {
   private final ClassNameAllocator core;

   ClassNameAllocatorWrapper(ClassNameAllocator core) {
      if (core == null) {
         core = new ClassNameAllocator() {
            public String assignClassName(String packageName, String className) {
               return className;
            }
         };
      }

      this.core = core;
   }

   public String assignClassName(String packageName, String className) {
      return this.core.assignClassName(packageName, className);
   }

   public String assignClassName(JPackage pkg, String className) {
      return this.core.assignClassName(pkg.name(), className);
   }

   public String assignClassName(CClassInfoParent parent, String className) {
      if (parent instanceof CClassInfoParent.Package) {
         CClassInfoParent.Package p = (CClassInfoParent.Package)parent;
         return this.assignClassName(p.pkg, className);
      } else {
         return className;
      }
   }
}
