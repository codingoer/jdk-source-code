package com.sun.tools.javah;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;

public class JavahTool implements NativeHeaderTool {
   public NativeHeaderTool.NativeHeaderTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5) {
      return new JavahTask(var1, var2, var3, var4, var5);
   }

   public StandardJavaFileManager getStandardFileManager(DiagnosticListener var1, Locale var2, Charset var3) {
      return JavahTask.getDefaultFileManager(var1, (PrintWriter)null);
   }

   public int run(InputStream var1, OutputStream var2, OutputStream var3, String... var4) {
      JavahTask var5 = new JavahTask(JavahTask.getPrintWriterForStream(var2), (JavaFileManager)null, (DiagnosticListener)null, Arrays.asList(var4), (Iterable)null);
      return var5.run() ? 0 : 1;
   }

   public Set getSourceVersions() {
      return EnumSet.allOf(SourceVersion.class);
   }

   public int isSupportedOption(String var1) {
      JavahTask.Option[] var2 = JavahTask.recognizedOptions;

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3].matches(var1)) {
            return var2[var3].hasArg ? 1 : 0;
         }
      }

      return -1;
   }
}
