package com.sun.codemodel.internal.writer;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JPackage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class FilterCodeWriter extends CodeWriter {
   protected CodeWriter core;

   public FilterCodeWriter(CodeWriter core) {
      this.core = core;
   }

   public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
      return this.core.openBinary(pkg, fileName);
   }

   public Writer openSource(JPackage pkg, String fileName) throws IOException {
      return this.core.openSource(pkg, fileName);
   }

   public void close() throws IOException {
      this.core.close();
   }
}
