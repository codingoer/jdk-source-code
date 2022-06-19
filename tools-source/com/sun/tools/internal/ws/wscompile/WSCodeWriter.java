package com.sun.tools.internal.ws.wscompile;

import com.sun.codemodel.internal.JPackage;
import com.sun.codemodel.internal.writer.FileCodeWriter;
import java.io.File;
import java.io.IOException;

public class WSCodeWriter extends FileCodeWriter {
   private final Options options;

   public WSCodeWriter(File outDir, Options options) throws IOException {
      super(outDir, options.encoding);
      this.options = options;
   }

   protected File getFile(JPackage pkg, String fileName) throws IOException {
      File f = super.getFile(pkg, fileName);
      this.options.addGeneratedFile(f);
      return f;
   }
}
