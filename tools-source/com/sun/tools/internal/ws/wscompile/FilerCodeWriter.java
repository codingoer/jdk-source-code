package com.sun.tools.internal.ws.wscompile;

import com.sun.codemodel.internal.JPackage;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;

public class FilerCodeWriter extends WSCodeWriter {
   private final Filer filer;
   private Writer w;

   public FilerCodeWriter(File outDir, Options options) throws IOException {
      super(outDir, options);
      this.filer = options.filer;
   }

   public Writer openSource(JPackage pkg, String fileName) throws IOException {
      String tmp = fileName.substring(0, fileName.length() - 5);
      if (pkg.name() != null && !"".equals(pkg.name())) {
         this.w = this.filer.createSourceFile(pkg.name() + "." + tmp, new Element[0]).openWriter();
      } else {
         this.w = this.filer.createSourceFile(tmp, new Element[0]).openWriter();
      }

      return this.w;
   }

   public void close() throws IOException {
      super.close();
      if (this.w != null) {
         this.w.close();
      }

      this.w = null;
   }
}
