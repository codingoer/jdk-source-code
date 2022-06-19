package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeprecatedAPIListBuilder {
   public static final int NUM_TYPES = 12;
   public static final int PACKAGE = 0;
   public static final int INTERFACE = 1;
   public static final int CLASS = 2;
   public static final int ENUM = 3;
   public static final int EXCEPTION = 4;
   public static final int ERROR = 5;
   public static final int ANNOTATION_TYPE = 6;
   public static final int FIELD = 7;
   public static final int METHOD = 8;
   public static final int CONSTRUCTOR = 9;
   public static final int ENUM_CONSTANT = 10;
   public static final int ANNOTATION_TYPE_MEMBER = 11;
   private List deprecatedLists = new ArrayList();

   public DeprecatedAPIListBuilder(Configuration var1) {
      for(int var2 = 0; var2 < 12; ++var2) {
         this.deprecatedLists.add(var2, new ArrayList());
      }

      this.buildDeprecatedAPIInfo(var1);
   }

   private void buildDeprecatedAPIInfo(Configuration var1) {
      PackageDoc[] var2 = var1.packages;

      for(int var4 = 0; var4 < var2.length; ++var4) {
         PackageDoc var3 = var2[var4];
         if (Util.isDeprecated(var3)) {
            this.getList(0).add(var3);
         }
      }

      ClassDoc[] var7 = var1.root.classes();

      for(int var5 = 0; var5 < var7.length; ++var5) {
         ClassDoc var6 = var7[var5];
         if (Util.isDeprecated(var6)) {
            if (var6.isOrdinaryClass()) {
               this.getList(2).add(var6);
            } else if (var6.isInterface()) {
               this.getList(1).add(var6);
            } else if (var6.isException()) {
               this.getList(4).add(var6);
            } else if (var6.isEnum()) {
               this.getList(3).add(var6);
            } else if (var6.isError()) {
               this.getList(5).add(var6);
            } else if (var6.isAnnotationType()) {
               this.getList(6).add(var6);
            }
         }

         this.composeDeprecatedList(this.getList(7), var6.fields());
         this.composeDeprecatedList(this.getList(8), var6.methods());
         this.composeDeprecatedList(this.getList(9), var6.constructors());
         if (var6.isEnum()) {
            this.composeDeprecatedList(this.getList(10), var6.enumConstants());
         }

         if (var6.isAnnotationType()) {
            this.composeDeprecatedList(this.getList(11), ((AnnotationTypeDoc)var6).elements());
         }
      }

      this.sortDeprecatedLists();
   }

   private void composeDeprecatedList(List var1, MemberDoc[] var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (Util.isDeprecated(var2[var3])) {
            var1.add(var2[var3]);
         }
      }

   }

   private void sortDeprecatedLists() {
      for(int var1 = 0; var1 < 12; ++var1) {
         Collections.sort(this.getList(var1));
      }

   }

   public List getList(int var1) {
      return (List)this.deprecatedLists.get(var1);
   }

   public boolean hasDocumentation(int var1) {
      return ((List)this.deprecatedLists.get(var1)).size() > 0;
   }
}
