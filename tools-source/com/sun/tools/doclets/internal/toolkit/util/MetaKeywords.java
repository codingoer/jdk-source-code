package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.javac.jvm.Profile;
import java.util.ArrayList;

public class MetaKeywords {
   private final Configuration configuration;

   public MetaKeywords(Configuration var1) {
      this.configuration = var1;
   }

   public String[] getMetaKeywords(ClassDoc var1) {
      ArrayList var2 = new ArrayList();
      if (this.configuration.keywords) {
         var2.addAll(this.getClassKeyword(var1));
         var2.addAll(this.getMemberKeywords(var1.fields()));
         var2.addAll(this.getMemberKeywords(var1.methods()));
      }

      return (String[])var2.toArray(new String[0]);
   }

   protected ArrayList getClassKeyword(ClassDoc var1) {
      String var2 = var1.isInterface() ? "interface" : "class";
      ArrayList var3 = new ArrayList(1);
      var3.add(var1.qualifiedName() + " " + var2);
      return var3;
   }

   public String[] getMetaKeywords(PackageDoc var1) {
      if (this.configuration.keywords) {
         String var2 = Util.getPackageName(var1);
         return new String[]{var2 + " package"};
      } else {
         return new String[0];
      }
   }

   public String[] getMetaKeywords(Profile var1) {
      if (this.configuration.keywords) {
         String var2 = var1.name;
         return new String[]{var2 + " profile"};
      } else {
         return new String[0];
      }
   }

   public String[] getOverviewMetaKeywords(String var1, String var2) {
      if (this.configuration.keywords) {
         String var3 = this.configuration.getText(var1);
         String[] var4 = new String[]{var3};
         if (var2.length() > 0) {
            var4[0] = var4[0] + ", " + var2;
         }

         return var4;
      } else {
         return new String[0];
      }
   }

   protected ArrayList getMemberKeywords(MemberDoc[] var1) {
      ArrayList var2 = new ArrayList();

      for(int var4 = 0; var4 < var1.length; ++var4) {
         String var3 = var1[var4].name() + (var1[var4].isMethod() ? "()" : "");
         if (!var2.contains(var3)) {
            var2.add(var3);
         }
      }

      return var2;
   }
}
