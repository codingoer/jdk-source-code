package com.sun.tools.doclets.internal.toolkit.util;

public class DocLink {
   final String path;
   final String query;
   final String fragment;

   public static DocLink fragment(String var0) {
      return new DocLink((String)null, (String)null, var0);
   }

   public DocLink(DocPath var1) {
      this((String)var1.getPath(), (String)null, (String)null);
   }

   public DocLink(DocPath var1, String var2, String var3) {
      this(var1.getPath(), var2, var3);
   }

   public DocLink(String var1, String var2, String var3) {
      this.path = var1;
      this.query = var2;
      this.fragment = var3;
   }

   public String toString() {
      if (this.path != null && isEmpty(this.query) && isEmpty(this.fragment)) {
         return this.path;
      } else {
         StringBuilder var1 = new StringBuilder();
         if (this.path != null) {
            var1.append(this.path);
         }

         if (!isEmpty(this.query)) {
            var1.append("?").append(this.query);
         }

         if (!isEmpty(this.fragment)) {
            var1.append("#").append(this.fragment);
         }

         return var1.toString();
      }
   }

   private static boolean isEmpty(String var0) {
      return var0 == null || var0.isEmpty();
   }
}
