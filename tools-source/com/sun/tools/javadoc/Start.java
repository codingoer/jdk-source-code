package com.sun.tools.javadoc;

import com.sun.javadoc.LanguageVersion;
import com.sun.tools.javac.main.CommandLine;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.tools.JavaFileManager;

public class Start extends ToolOption.Helper {
   private final Context context;
   private final String defaultDocletClassName;
   private final ClassLoader docletParentClassLoader;
   private static final String javadocName = "javadoc";
   private static final String standardDocletClassName = "com.sun.tools.doclets.standard.Standard";
   private long defaultFilter;
   private final Messager messager;
   private DocletInvoker docletInvoker;
   private boolean apiMode;

   Start(String var1, PrintWriter var2, PrintWriter var3, PrintWriter var4, String var5) {
      this(var1, var2, var3, var4, var5, (ClassLoader)null);
   }

   Start(String var1, PrintWriter var2, PrintWriter var3, PrintWriter var4, String var5, ClassLoader var6) {
      this.defaultFilter = 5L;
      this.context = new Context();
      this.messager = new Messager(this.context, var1, var2, var3, var4);
      this.defaultDocletClassName = var5;
      this.docletParentClassLoader = var6;
   }

   Start(String var1, String var2) {
      this(var1, var2, (ClassLoader)null);
   }

   Start(String var1, String var2, ClassLoader var3) {
      this.defaultFilter = 5L;
      this.context = new Context();
      this.messager = new Messager(this.context, var1);
      this.defaultDocletClassName = var2;
      this.docletParentClassLoader = var3;
   }

   Start(String var1, ClassLoader var2) {
      this(var1, "com.sun.tools.doclets.standard.Standard", var2);
   }

   Start(String var1) {
      this(var1, "com.sun.tools.doclets.standard.Standard");
   }

   Start(ClassLoader var1) {
      this("javadoc", var1);
   }

   Start() {
      this("javadoc");
   }

   public Start(Context var1) {
      this.defaultFilter = 5L;
      var1.getClass();
      this.context = var1;
      this.apiMode = true;
      this.defaultDocletClassName = "com.sun.tools.doclets.standard.Standard";
      this.docletParentClassLoader = null;
      Log var2 = (Log)var1.get(Log.logKey);
      if (var2 instanceof Messager) {
         this.messager = (Messager)var2;
      } else {
         PrintWriter var3 = (PrintWriter)var1.get(Log.outKey);
         this.messager = var3 == null ? new Messager(var1, "javadoc") : new Messager(var1, "javadoc", var3, var3, var3);
      }

   }

   void usage() {
      this.usage(true);
   }

   void usage(boolean var1) {
      this.usage("main.usage", "-help", (String)null, var1);
   }

   void Xusage() {
      this.Xusage(true);
   }

   void Xusage(boolean var1) {
      this.usage("main.Xusage", "-X", "main.Xusage.foot", var1);
   }

   private void usage(String var1, String var2, String var3, boolean var4) {
      this.messager.notice(var1);
      if (this.docletInvoker != null) {
         this.docletInvoker.optionLength(var2);
      }

      if (var3 != null) {
         this.messager.notice(var3);
      }

      if (var4) {
         this.exit();
      }

   }

   private void exit() {
      this.messager.exit();
   }

   int begin(String... var1) {
      boolean var2 = this.begin((Class)null, (String[])var1, Collections.emptySet());
      return var2 ? 0 : 1;
   }

   public boolean begin(Class var1, Iterable var2, Iterable var3) {
      ArrayList var4 = new ArrayList();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         var4.add(var6);
      }

      return this.begin(var1, (String[])var4.toArray(new String[var4.size()]), var3);
   }

   private boolean begin(Class var1, String[] var2, Iterable var3) {
      boolean var4 = false;

      try {
         var4 = !this.parseAndExecute(var1, var2, var3);
      } catch (Messager.ExitJavadoc var13) {
      } catch (OutOfMemoryError var14) {
         this.messager.error(Messager.NOPOS, "main.out.of.memory");
         var4 = true;
      } catch (ClientCodeException var15) {
         throw var15;
      } catch (Error var16) {
         var16.printStackTrace(System.err);
         this.messager.error(Messager.NOPOS, "main.fatal.error");
         var4 = true;
      } catch (Exception var17) {
         var17.printStackTrace(System.err);
         this.messager.error(Messager.NOPOS, "main.fatal.exception");
         var4 = true;
      } finally {
         this.messager.exitNotice();
         this.messager.flush();
      }

      var4 |= this.messager.nerrors() > 0;
      var4 |= this.rejectWarnings && this.messager.nwarnings() > 0;
      return !var4;
   }

   private boolean parseAndExecute(Class var1, String[] var2, Iterable var3) throws IOException {
      long var4 = System.currentTimeMillis();
      ListBuffer var6 = new ListBuffer();

      try {
         var2 = CommandLine.parse(var2);
      } catch (FileNotFoundException var15) {
         this.messager.error(Messager.NOPOS, "main.cant.read", var15.getMessage());
         this.exit();
      } catch (IOException var16) {
         var16.printStackTrace(System.err);
         this.exit();
      }

      JavaFileManager var7 = (JavaFileManager)this.context.get(JavaFileManager.class);
      this.setDocletInvoker(var1, var7, var2);
      this.compOpts = Options.instance(this.context);
      this.compOpts.put("-Xlint:-options", "-Xlint:-options");

      for(int var8 = 0; var8 < var2.length; ++var8) {
         String var9 = var2[var8];
         ToolOption var10 = ToolOption.get(var9);
         if (var10 != null) {
            if (var10 == ToolOption.LOCALE && var8 > 0) {
               this.usageError("main.locale_first");
            }

            if (var10.hasArg) {
               this.oneArg(var2, var8++);
               var10.process(this, var2[var8]);
            } else {
               this.setOption(var9);
               var10.process(this);
            }
         } else if (var9.startsWith("-XD")) {
            String var20 = var9.substring("-XD".length());
            int var22 = var20.indexOf(61);
            String var23 = var22 < 0 ? var20 : var20.substring(0, var22);
            String var14 = var22 < 0 ? var20 : var20.substring(var22 + 1);
            this.compOpts.put(var23, var14);
         } else if (!var9.startsWith("-")) {
            var6.append(var9);
         } else {
            int var11 = this.docletInvoker.optionLength(var9);
            if (var11 < 0) {
               this.exit();
            } else if (var11 == 0) {
               this.usageError("main.invalid_flag", var9);
            } else {
               if (var8 + var11 > var2.length) {
                  this.usageError("main.requires_argument", var9);
               }

               ListBuffer var12 = new ListBuffer();

               for(int var13 = 0; var13 < var11 - 1; ++var13) {
                  ++var8;
                  var12.append(var2[var8]);
               }

               this.setOption(var9, var12.toList());
            }
         }
      }

      this.compOpts.notifyListeners();
      if (var6.isEmpty() && this.subPackages.isEmpty() && this.isEmpty(var3)) {
         this.usageError("main.No_packages_or_classes_specified");
      }

      if (!this.docletInvoker.validOptions(this.options.toList())) {
         this.exit();
      }

      JavadocTool var18 = JavadocTool.make0(this.context);
      if (var18 == null) {
         return false;
      } else {
         if (this.showAccess == null) {
            this.setFilter(this.defaultFilter);
         }

         LanguageVersion var17 = this.docletInvoker.languageVersion();
         RootDocImpl var19 = var18.getRootDocImpl(this.docLocale, this.encoding, this.showAccess, var6.toList(), this.options.toList(), var3, this.breakiterator, this.subPackages.toList(), this.excludedPackages.toList(), this.docClasses, var17 == null || var17 == LanguageVersion.JAVA_1_1, this.quiet);
         var18 = null;
         boolean var21 = var19 != null;
         if (var21) {
            var21 = this.docletInvoker.start(var19);
         }

         if (this.compOpts.get("-verbose") != null) {
            var4 = System.currentTimeMillis() - var4;
            this.messager.notice("main.done_in", Long.toString(var4));
         }

         return var21;
      }
   }

   private boolean isEmpty(Iterable var1) {
      return !var1.iterator().hasNext();
   }

   private void setDocletInvoker(Class var1, JavaFileManager var2, String[] var3) {
      if (var1 != null) {
         this.docletInvoker = new DocletInvoker(this.messager, var1, this.apiMode);
      } else {
         String var4 = null;
         String var5 = null;

         for(int var6 = 0; var6 < var3.length; ++var6) {
            String var7 = var3[var6];
            if (var7.equals(ToolOption.DOCLET.opt)) {
               this.oneArg(var3, var6++);
               if (var4 != null) {
                  this.usageError("main.more_than_one_doclet_specified_0_and_1", var4, var3[var6]);
               }

               var4 = var3[var6];
            } else if (var7.equals(ToolOption.DOCLETPATH.opt)) {
               this.oneArg(var3, var6++);
               if (var5 == null) {
                  var5 = var3[var6];
               } else {
                  var5 = var5 + File.pathSeparator + var3[var6];
               }
            }
         }

         if (var4 == null) {
            var4 = this.defaultDocletClassName;
         }

         this.docletInvoker = new DocletInvoker(this.messager, var2, var4, var5, this.docletParentClassLoader, this.apiMode);
      }
   }

   private void oneArg(String[] var1, int var2) {
      if (var2 + 1 < var1.length) {
         this.setOption(var1[var2], var1[var2 + 1]);
      } else {
         this.usageError("main.requires_argument", var1[var2]);
      }

   }

   void usageError(String var1, Object... var2) {
      this.messager.error(Messager.NOPOS, var1, var2);
      this.usage(true);
   }

   private void setOption(String var1) {
      String[] var2 = new String[]{var1};
      this.options.append(var2);
   }

   private void setOption(String var1, String var2) {
      String[] var3 = new String[]{var1, var2};
      this.options.append(var3);
   }

   private void setOption(String var1, List var2) {
      String[] var3 = new String[var2.length() + 1];
      int var4 = 0;
      var3[var4++] = var1;

      for(List var5 = var2; var5.nonEmpty(); var5 = var5.tail) {
         var3[var4++] = (String)var5.head;
      }

      this.options.append(var3);
   }
}
