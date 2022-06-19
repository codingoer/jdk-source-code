package com.sun.tools.internal.xjc;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.writer.FilterCodeWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

final class ProgressCodeWriter extends FilterCodeWriter {
   private int current;
   private final int totalFileCount;
   private final XJCListener progress;

   public ProgressCodeWriter(CodeWriter output, XJCListener progress, int totalFileCount) {
      super(output);
      this.progress = progress;
      this.totalFileCount = totalFileCount;
      if (progress == null) {
         throw new IllegalArgumentException();
      }
   }

   public Writer openSource(JPackage pkg, String fileName) throws IOException {
      this.report(pkg, fileName);
      return super.openSource(pkg, fileName);
   }

   public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
      this.report(pkg, fileName);
      return super.openBinary(pkg, fileName);
   }

   private void report(JPackage pkg, String fileName) {
      String name = pkg.name().replace('.', File.separatorChar);
      if (name.length() != 0) {
         name = name + File.separatorChar;
      }

      name = name + fileName;
      if (this.progress.isCanceled()) {
         throw new AbortException();
      } else {
         this.progress.generatedFile(name, this.current++, this.totalFileCount);
      }
   }
}
