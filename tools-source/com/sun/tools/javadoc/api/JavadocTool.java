package com.sun.tools.javadoc.api;

import com.sun.tools.javac.api.ClientCodeWrapper;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javadoc.Main;
import com.sun.tools.javadoc.ToolOption;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.DocumentationTool;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileObject.Kind;

public class JavadocTool implements DocumentationTool {
   public DocumentationTool.DocumentationTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Class var4, Iterable var5, Iterable var6) {
      Context var7 = new Context();
      return this.getTask(var1, var2, var3, var4, var5, var6, var7);
   }

   public DocumentationTool.DocumentationTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Class var4, Iterable var5, Iterable var6, Context var7) {
      try {
         ClientCodeWrapper var8 = ClientCodeWrapper.instance(var7);
         Iterator var9;
         if (var5 != null) {
            var9 = var5.iterator();

            while(var9.hasNext()) {
               String var10 = (String)var9.next();
               var10.getClass();
            }
         }

         if (var6 != null) {
            var6 = var8.wrapJavaFileObjects(var6);
            var9 = var6.iterator();

            while(var9.hasNext()) {
               JavaFileObject var13 = (JavaFileObject)var9.next();
               if (var13.getKind() != Kind.SOURCE) {
                  throw new IllegalArgumentException("All compilation units must be of SOURCE kind");
               }
            }
         }

         if (var3 != null) {
            var7.put((Class)DiagnosticListener.class, (Object)var8.wrap(var3));
         }

         if (var1 == null) {
            var7.put((Context.Key)Log.outKey, (Object)(new PrintWriter(System.err, true)));
         } else if (var1 instanceof PrintWriter) {
            var7.put((Context.Key)Log.outKey, (Object)((PrintWriter)var1));
         } else {
            var7.put((Context.Key)Log.outKey, (Object)(new PrintWriter(var1, true)));
         }

         if (var2 == null) {
            var2 = this.getStandardFileManager(var3, (Locale)null, (Charset)null);
         }

         JavaFileManager var14 = var8.wrap((JavaFileManager)var2);
         var7.put((Class)JavaFileManager.class, (Object)var14);
         return new JavadocTaskImpl(var7, var4, var5, var6);
      } catch (ClientCodeException var12) {
         throw new RuntimeException(var12.getCause());
      }
   }

   public StandardJavaFileManager getStandardFileManager(DiagnosticListener var1, Locale var2, Charset var3) {
      Context var4 = new Context();
      var4.put((Class)Locale.class, (Object)var2);
      if (var1 != null) {
         var4.put((Class)DiagnosticListener.class, (Object)var1);
      }

      PrintWriter var5 = var3 == null ? new PrintWriter(System.err, true) : new PrintWriter(new OutputStreamWriter(System.err, var3), true);
      var4.put((Context.Key)Log.outKey, (Object)var5);
      return new JavacFileManager(var4, true, var3);
   }

   public int run(InputStream var1, OutputStream var2, OutputStream var3, String... var4) {
      PrintWriter var5 = new PrintWriter((OutputStream)(var3 == null ? System.err : var3), true);
      PrintWriter var6 = new PrintWriter((OutputStream)(var2 == null ? System.out : var2));

      int var9;
      try {
         String var7 = "com.sun.tools.doclets.standard.Standard";
         ClassLoader var8 = this.getClass().getClassLoader();
         var9 = Main.execute("javadoc", var5, var5, var6, var7, var8, var4);
      } finally {
         var5.flush();
         var6.flush();
      }

      return var9;
   }

   public Set getSourceVersions() {
      return Collections.unmodifiableSet(EnumSet.range(SourceVersion.RELEASE_3, SourceVersion.latest()));
   }

   public int isSupportedOption(String var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         ToolOption[] var2 = ToolOption.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ToolOption var5 = var2[var4];
            if (var5.opt.equals(var1)) {
               return var5.hasArg ? 1 : 0;
            }
         }

         return -1;
      }
   }
}
