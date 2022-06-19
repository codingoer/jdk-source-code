package com.sun.tools.javac.file;

import com.sun.tools.javac.util.List;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.tools.JavaFileObject;

public class SymbolArchive extends ZipArchive {
   final File origFile;
   final RelativePath.RelativeDirectory prefix;

   public SymbolArchive(JavacFileManager var1, File var2, ZipFile var3, RelativePath.RelativeDirectory var4) throws IOException {
      super(var1, var3, false);
      this.origFile = var2;
      this.prefix = var4;
      this.initMap();
   }

   void addZipEntry(ZipEntry var1) {
      String var2 = var1.getName();
      if (var2.startsWith(this.prefix.path)) {
         var2 = var2.substring(this.prefix.path.length());
         int var3 = var2.lastIndexOf(47);
         RelativePath.RelativeDirectory var4 = new RelativePath.RelativeDirectory(var2.substring(0, var3 + 1));
         String var5 = var2.substring(var3 + 1);
         if (var5.length() != 0) {
            List var6 = (List)this.map.get(var4);
            if (var6 == null) {
               var6 = List.nil();
            }

            var6 = var6.prepend(var5);
            this.map.put(var4, var6);
         }
      }
   }

   public JavaFileObject getFileObject(RelativePath.RelativeDirectory var1, String var2) {
      RelativePath.RelativeDirectory var3 = new RelativePath.RelativeDirectory(this.prefix, var1.path);
      ZipEntry var4 = (new RelativePath.RelativeFile(var3, var2)).getZipEntry(this.zfile);
      return new SymbolFileObject(this, var2, var4);
   }

   public String toString() {
      return "SymbolArchive[" + this.zfile.getName() + "]";
   }

   public static class SymbolFileObject extends ZipArchive.ZipFileObject {
      protected SymbolFileObject(SymbolArchive var1, String var2, ZipEntry var3) {
         super(var1, var2, var3);
      }

      protected String inferBinaryName(Iterable var1) {
         String var2 = this.entry.getName();
         String var3 = ((SymbolArchive)this.zarch).prefix.path;
         if (var2.startsWith(var3)) {
            var2 = var2.substring(var3.length());
         }

         return removeExtension(var2).replace('/', '.');
      }
   }
}
