package com.sun.tools.javah;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import javax.tools.DiagnosticListener;

class JavahFileManager extends JavacFileManager {
   private JavahFileManager(Context var1, Charset var2) {
      super(var1, true, var2);
      this.setSymbolFileEnabled(false);
   }

   static JavahFileManager create(DiagnosticListener var0, PrintWriter var1) {
      Context var2 = new Context();
      if (var0 != null) {
         var2.put((Class)DiagnosticListener.class, (Object)var0);
      }

      var2.put((Context.Key)Log.outKey, (Object)var1);
      return new JavahFileManager(var2, (Charset)null);
   }
}
