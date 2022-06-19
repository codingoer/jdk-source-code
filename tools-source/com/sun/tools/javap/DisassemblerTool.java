package com.sun.tools.javap;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.OptionChecker;
import javax.tools.StandardJavaFileManager;
import javax.tools.Tool;

public interface DisassemblerTool extends Tool, OptionChecker {
   DisassemblerTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5);

   StandardJavaFileManager getStandardFileManager(DiagnosticListener var1, Locale var2, Charset var3);

   public interface DisassemblerTask extends Callable {
      void setLocale(Locale var1);

      Boolean call();
   }
}
