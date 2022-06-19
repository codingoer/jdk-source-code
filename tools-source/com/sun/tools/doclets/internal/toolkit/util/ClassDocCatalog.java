package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassDocCatalog {
   private Set packageSet;
   private Map allClasses;
   private Map ordinaryClasses;
   private Map exceptions;
   private Map enums;
   private Map annotationTypes;
   private Map errors;
   private Map interfaces;
   private Configuration configuration;

   public ClassDocCatalog(ClassDoc[] var1, Configuration var2) {
      this.init();
      this.configuration = var2;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         this.addClassDoc(var1[var3]);
      }

   }

   public ClassDocCatalog() {
      this.init();
   }

   private void init() {
      this.allClasses = new HashMap();
      this.ordinaryClasses = new HashMap();
      this.exceptions = new HashMap();
      this.enums = new HashMap();
      this.annotationTypes = new HashMap();
      this.errors = new HashMap();
      this.interfaces = new HashMap();
      this.packageSet = new HashSet();
   }

   public void addClassDoc(ClassDoc var1) {
      if (var1 != null) {
         this.addClass(var1, this.allClasses);
         if (var1.isOrdinaryClass()) {
            this.addClass(var1, this.ordinaryClasses);
         } else if (var1.isException()) {
            this.addClass(var1, this.exceptions);
         } else if (var1.isEnum()) {
            this.addClass(var1, this.enums);
         } else if (var1.isAnnotationType()) {
            this.addClass(var1, this.annotationTypes);
         } else if (var1.isError()) {
            this.addClass(var1, this.errors);
         } else if (var1.isInterface()) {
            this.addClass(var1, this.interfaces);
         }

      }
   }

   private void addClass(ClassDoc var1, Map var2) {
      PackageDoc var3 = var1.containingPackage();
      if (!var3.isIncluded() && (!this.configuration.nodeprecated || !Util.isDeprecated(var3))) {
         String var4 = Util.getPackageName(var3);
         Object var5 = (Set)var2.get(var4);
         if (var5 == null) {
            this.packageSet.add(var4);
            var5 = new HashSet();
         }

         ((Set)var5).add(var1);
         var2.put(var4, var5);
      }
   }

   private ClassDoc[] getArray(Map var1, String var2) {
      Set var3 = (Set)var1.get(var2);
      return var3 == null ? new ClassDoc[0] : (ClassDoc[])var3.toArray(new ClassDoc[0]);
   }

   public ClassDoc[] allClasses(PackageDoc var1) {
      return var1.isIncluded() ? var1.allClasses() : this.getArray(this.allClasses, Util.getPackageName(var1));
   }

   public ClassDoc[] allClasses(String var1) {
      return this.getArray(this.allClasses, var1);
   }

   public String[] packageNames() {
      return (String[])this.packageSet.toArray(new String[0]);
   }

   public boolean isKnownPackage(String var1) {
      return this.packageSet.contains(var1);
   }

   public ClassDoc[] errors(String var1) {
      return this.getArray(this.errors, var1);
   }

   public ClassDoc[] exceptions(String var1) {
      return this.getArray(this.exceptions, var1);
   }

   public ClassDoc[] enums(String var1) {
      return this.getArray(this.enums, var1);
   }

   public ClassDoc[] annotationTypes(String var1) {
      return this.getArray(this.annotationTypes, var1);
   }

   public ClassDoc[] interfaces(String var1) {
      return this.getArray(this.interfaces, var1);
   }

   public ClassDoc[] ordinaryClasses(String var1) {
      return this.getArray(this.ordinaryClasses, var1);
   }
}
