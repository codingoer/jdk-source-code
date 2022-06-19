package com.sun.codemodel.internal.writer;

import com.sun.codemodel.internal.CodeWriter;
import com.sun.codemodel.internal.JPackage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class PrologCodeWriter extends FilterCodeWriter {
   private final String prolog;

   public PrologCodeWriter(CodeWriter core, String prolog) {
      super(core);
      this.prolog = prolog;
   }

   public Writer openSource(JPackage pkg, String fileName) throws IOException {
      Writer w = super.openSource(pkg, fileName);
      PrintWriter out = new PrintWriter(w);
      if (this.prolog != null) {
         out.println("//");

         int idx;
         for(String s = this.prolog; (idx = s.indexOf(10)) != -1; s = s.substring(idx + 1)) {
            out.println("// " + s.substring(0, idx));
         }

         out.println("//");
         out.println();
      }

      out.flush();
      return w;
   }
}
