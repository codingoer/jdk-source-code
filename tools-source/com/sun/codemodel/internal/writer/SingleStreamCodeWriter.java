package com.sun.codemodel.internal.writer;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JPackage;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SingleStreamCodeWriter extends CodeWriter {
   private final PrintStream out;

   public SingleStreamCodeWriter(OutputStream os) {
      this.out = new PrintStream(os);
   }

   public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
      String pkgName = pkg.name();
      if (pkgName.length() != 0) {
         pkgName = pkgName + '.';
      }

      this.out.println("-----------------------------------" + pkgName + fileName + "-----------------------------------");
      return new FilterOutputStream(this.out) {
         public void close() {
         }
      };
   }

   public void close() throws IOException {
      this.out.close();
   }
}
