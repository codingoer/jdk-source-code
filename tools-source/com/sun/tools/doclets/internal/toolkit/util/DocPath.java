package com.sun.tools.doclets.internal.toolkit.util;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;

public class DocPath {
   private final String path;
   public static final DocPath empty = new DocPath("");
   public static final DocPath parent = new DocPath("..");

   public static DocPath create(String var0) {
      return var0 != null && !var0.isEmpty() ? new DocPath(var0) : empty;
   }

   public static DocPath forClass(ClassDoc var0) {
      return var0 == null ? empty : forPackage(var0.containingPackage()).resolve(forName(var0));
   }

   public static DocPath forName(ClassDoc var0) {
      return var0 == null ? empty : new DocPath(var0.name() + ".html");
   }

   public static DocPath forPackage(ClassDoc var0) {
      return var0 == null ? empty : forPackage(var0.containingPackage());
   }

   public static DocPath forPackage(PackageDoc var0) {
      return var0 == null ? empty : create(var0.name().replace('.', '/'));
   }

   public static DocPath forRoot(PackageDoc var0) {
      String var1 = var0 == null ? "" : var0.name();
      return var1.isEmpty() ? empty : new DocPath(var1.replace('.', '/').replaceAll("[^/]+", ".."));
   }

   public static DocPath relativePath(PackageDoc var0, PackageDoc var1) {
      return forRoot(var0).resolve(forPackage(var1));
   }

   protected DocPath(String var1) {
      this.path = var1.endsWith("/") ? var1.substring(0, var1.length() - 1) : var1;
   }

   public boolean equals(Object var1) {
      return var1 instanceof DocPath && this.path.equals(((DocPath)var1).path);
   }

   public int hashCode() {
      return this.path.hashCode();
   }

   public DocPath basename() {
      int var1 = this.path.lastIndexOf("/");
      return var1 == -1 ? this : new DocPath(this.path.substring(var1 + 1));
   }

   public DocPath parent() {
      int var1 = this.path.lastIndexOf("/");
      return var1 == -1 ? empty : new DocPath(this.path.substring(0, var1));
   }

   public DocPath resolve(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         return this.path.isEmpty() ? new DocPath(var1) : new DocPath(this.path + "/" + var1);
      } else {
         return this;
      }
   }

   public DocPath resolve(DocPath var1) {
      if (var1 != null && !var1.isEmpty()) {
         return this.path.isEmpty() ? var1 : new DocPath(this.path + "/" + var1.getPath());
      } else {
         return this;
      }
   }

   public DocPath invert() {
      return new DocPath(this.path.replaceAll("[^/]+", ".."));
   }

   public boolean isEmpty() {
      return this.path.isEmpty();
   }

   public DocLink fragment(String var1) {
      return new DocLink(this.path, (String)null, var1);
   }

   public DocLink query(String var1) {
      return new DocLink(this.path, var1, (String)null);
   }

   public String getPath() {
      return this.path;
   }
}
