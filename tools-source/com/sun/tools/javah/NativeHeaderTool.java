package com.sun.tools.javah;

import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.OptionChecker;
import javax.tools.StandardJavaFileManager;
import javax.tools.Tool;

public interface NativeHeaderTool extends Tool, OptionChecker {
   NativeHeaderTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5);

   StandardJavaFileManager getStandardFileManager(DiagnosticListener var1, Locale var2, Charset var3);

   public interface NativeHeaderTask extends Callable {
      void setLocale(Locale var1);

      Boolean call();
   }
}
