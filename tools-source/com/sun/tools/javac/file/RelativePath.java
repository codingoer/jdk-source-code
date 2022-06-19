package com.sun.tools.javac.file;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.JavaFileObject;

public abstract class RelativePath implements Comparable {
   protected final String path;

   protected RelativePath(String var1) {
      this.path = var1;
   }

   public abstract RelativeDirectory dirname();

   public abstract String basename();

   public File getFile(File var1) {
      return this.path.length() == 0 ? var1 : new File(var1, this.path.replace('/', File.separatorChar));
   }

   public int compareTo(RelativePath var1) {
      return this.path.compareTo(var1.path);
   }

   public boolean equals(Object var1) {
      return !(var1 instanceof RelativePath) ? false : this.path.equals(((RelativePath)var1).path);
   }

   public int hashCode() {
      return this.path.hashCode();
   }

   public String toString() {
      return "RelPath[" + this.path + "]";
   }

   public String getPath() {
      return this.path;
   }

   public static class RelativeFile extends RelativePath {
      static RelativeFile forClass(CharSequence var0, JavaFileObject.Kind var1) {
         return new RelativeFile(var0.toString().replace('.', '/') + var1.extension);
      }

      public RelativeFile(String var1) {
         super(var1);
         if (var1.endsWith("/")) {
            throw new IllegalArgumentException(var1);
         }
      }

      public RelativeFile(RelativeDirectory var1, String var2) {
         this(var1.path + var2);
      }

      RelativeFile(RelativeDirectory var1, RelativePath var2) {
         this(var1, var2.path);
      }

      public RelativeDirectory dirname() {
         int var1 = this.path.lastIndexOf(47);
         return new RelativeDirectory(this.path.substring(0, var1 + 1));
      }

      public String basename() {
         int var1 = this.path.lastIndexOf(47);
         return this.path.substring(var1 + 1);
      }

      ZipEntry getZipEntry(ZipFile var1) {
         return var1.getEntry(this.path);
      }

      public String toString() {
         return "RelativeFile[" + this.path + "]";
      }
   }

   public static class RelativeDirectory extends RelativePath {
      static RelativeDirectory forPackage(CharSequence var0) {
         return new RelativeDirectory(var0.toString().replace('.', '/'));
      }

      public RelativeDirectory(String var1) {
         super(var1.length() != 0 && !var1.endsWith("/") ? var1 + "/" : var1);
      }

      public RelativeDirectory(RelativeDirectory var1, String var2) {
         this(var1.path + var2);
      }

      public RelativeDirectory dirname() {
         int var1 = this.path.length();
         if (var1 == 0) {
            return this;
         } else {
            int var2 = this.path.lastIndexOf(47, var1 - 2);
            return new RelativeDirectory(this.path.substring(0, var2 + 1));
         }
      }

      public String basename() {
         int var1 = this.path.length();
         if (var1 == 0) {
            return this.path;
         } else {
            int var2 = this.path.lastIndexOf(47, var1 - 2);
            return this.path.substring(var2 + 1, var1 - 1);
         }
      }

      boolean contains(RelativePath var1) {
         return var1.path.length() > this.path.length() && var1.path.startsWith(this.path);
      }

      public String toString() {
         return "RelativeDirectory[" + this.path + "]";
      }
   }
}
