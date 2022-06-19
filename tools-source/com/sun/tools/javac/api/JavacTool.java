package com.sun.tools.javac.api;

import com.sun.source.util.JavacTask;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.Main;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.main.OptionHelper;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
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
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public final class JavacTool implements JavaCompiler {
   public static JavacTool create() {
      return new JavacTool();
   }

   public JavacFileManager getStandardFileManager(DiagnosticListener var1, Locale var2, Charset var3) {
      Context var4 = new Context();
      var4.put((Class)Locale.class, (Object)var2);
      if (var1 != null) {
         var4.put((Class)DiagnosticListener.class, (Object)var1);
      }

      PrintWriter var5 = var3 == null ? new PrintWriter(System.err, true) : new PrintWriter(new OutputStreamWriter(System.err, var3), true);
      var4.put((Context.Key)Log.outKey, (Object)var5);
      return new JavacFileManager(var4, true, var3);
   }

   public JavacTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5, Iterable var6) {
      Context var7 = new Context();
      return this.getTask(var1, var2, var3, var4, var5, var6, var7);
   }

   public JavacTask getTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5, Iterable var6, Context var7) {
      try {
         ClientCodeWrapper var8 = ClientCodeWrapper.instance(var7);
         Iterator var9;
         String var10;
         if (var4 != null) {
            var9 = var4.iterator();

            while(var9.hasNext()) {
               var10 = (String)var9.next();
               var10.getClass();
            }
         }

         if (var5 != null) {
            var9 = var5.iterator();

            while(var9.hasNext()) {
               var10 = (String)var9.next();
               if (!SourceVersion.isName(var10)) {
                  throw new IllegalArgumentException("Not a valid class name: " + var10);
               }
            }
         }

         if (var6 != null) {
            var6 = var8.wrapJavaFileObjects(var6);
            var9 = var6.iterator();

            while(var9.hasNext()) {
               JavaFileObject var15 = (JavaFileObject)var9.next();
               if (var15.getKind() != Kind.SOURCE) {
                  String var11 = "Compilation unit is not of SOURCE kind: \"" + var15.getName() + "\"";
                  throw new IllegalArgumentException(var11);
               }
            }
         }

         if (var3 != null) {
            var7.put((Class)DiagnosticListener.class, (Object)var8.wrap(var3));
         }

         if (var1 == null) {
            var7.put((Context.Key)Log.outKey, (Object)(new PrintWriter(System.err, true)));
         } else {
            var7.put((Context.Key)Log.outKey, (Object)(new PrintWriter(var1, true)));
         }

         if (var2 == null) {
            var2 = this.getStandardFileManager(var3, (Locale)null, (Charset)null);
         }

         JavaFileManager var13 = var8.wrap((JavaFileManager)var2);
         var7.put((Class)JavaFileManager.class, (Object)var13);
         processOptions(var7, var13, var4);
         Main var14 = new Main("javacTask", (PrintWriter)var7.get(Log.outKey));
         return new JavacTaskImpl(var14, var4, var7, var5, var6);
      } catch (ClientCodeException var12) {
         throw new RuntimeException(var12.getCause());
      }
   }

   public static void processOptions(Context var0, JavaFileManager var1, Iterable var2) {
      if (var2 != null) {
         final Options var3 = Options.instance(var0);
         Log var4 = Log.instance(var0);
         Option[] var5 = (Option[])Option.getJavacToolOptions().toArray(new Option[0]);
         OptionHelper.GrumpyHelper var6 = new OptionHelper.GrumpyHelper(var4) {
            public String get(Option var1) {
               return var3.get(var1.getText());
            }

            public void put(String var1, String var2) {
               var3.put(var1, var2);
            }

            public void remove(String var1) {
               var3.remove(var1);
            }
         };
         Iterator var7 = var2.iterator();

         while(var7.hasNext()) {
            String var8 = (String)var7.next();

            int var9;
            for(var9 = 0; var9 < var5.length && !var5[var9].matches(var8); ++var9) {
            }

            if (var9 == var5.length) {
               if (!var1.handleOption(var8, var7)) {
                  String var10 = var4.localize(Log.PrefixKind.JAVAC, "err.invalid.flag", var8);
                  throw new IllegalArgumentException(var10);
               }
            } else {
               Option var12 = var5[var9];
               if (var12.hasArg()) {
                  String var11;
                  if (!var7.hasNext()) {
                     var11 = var4.localize(Log.PrefixKind.JAVAC, "err.req.arg", var8);
                     throw new IllegalArgumentException(var11);
                  }

                  var11 = (String)var7.next();
                  if (var12.process(var6, var8, var11)) {
                     throw new IllegalArgumentException(var8 + " " + var11);
                  }
               } else if (var12.process(var6, var8)) {
                  throw new IllegalArgumentException(var8);
               }
            }
         }

         var3.notifyListeners();
      }
   }

   public int run(InputStream var1, OutputStream var2, OutputStream var3, String... var4) {
      if (var3 == null) {
         var3 = System.err;
      }

      String[] var5 = var4;
      int var6 = var4.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String var8 = var5[var7];
         var8.getClass();
      }

      return com.sun.tools.javac.Main.compile(var4, new PrintWriter((OutputStream)var3, true));
   }

   public Set getSourceVersions() {
      return Collections.unmodifiableSet(EnumSet.range(SourceVersion.RELEASE_3, SourceVersion.latest()));
   }

   public int isSupportedOption(String var1) {
      Set var2 = Option.getJavacToolOptions();
      Iterator var3 = var2.iterator();

      Option var4;
      do {
         if (!var3.hasNext()) {
            return -1;
         }

         var4 = (Option)var3.next();
      } while(!var4.matches(var1));

      return var4.hasArg() ? 1 : 0;
   }
}
