package com.sun.tools.javap;

import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Log;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import javax.tools.DiagnosticListener;

public class JavapFileManager extends JavacFileManager {
   private JavapFileManager(com.sun.tools.javac.util.Context var1, Charset var2) {
      super(var1, true, var2);
      this.setSymbolFileEnabled(false);
   }

   public static JavapFileManager create(DiagnosticListener var0, PrintWriter var1) {
      com.sun.tools.javac.util.Context var2 = new com.sun.tools.javac.util.Context();
      if (var0 != null) {
         var2.put((Class)DiagnosticListener.class, (Object)var0);
      }

      var2.put((com.sun.tools.javac.util.Context.Key)Log.outKey, (Object)var1);
      return new JavapFileManager(var2, (Charset)null);
   }
}
