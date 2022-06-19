package com.sun.codemodel.internal.writer;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JPackage;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCodeWriter extends CodeWriter {
   private final ZipOutputStream zip;
   private final OutputStream filter;

   public ZipCodeWriter(OutputStream target) {
      this.zip = new ZipOutputStream(target);
      this.filter = new FilterOutputStream(this.zip) {
         public void close() {
         }
      };
   }

   public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
      String name = fileName;
      if (!pkg.isUnnamed()) {
         name = toDirName(pkg) + fileName;
      }

      this.zip.putNextEntry(new ZipEntry(name));
      return this.filter;
   }

   private static String toDirName(JPackage pkg) {
      return pkg.name().replace('.', '/') + '/';
   }

   public void close() throws IOException {
      this.zip.close();
   }
}
