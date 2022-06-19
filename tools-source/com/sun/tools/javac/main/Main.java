package com.sun.tools.javac.main;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.tools.doclint.DocLint;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.file.CacheFSInfo;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.processing.AnnotationProcessingError;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.FatalError;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.PropagatedException;
import com.sun.tools.javac.util.ServiceLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class Main {
   String ownName;
   PrintWriter out;
   public Log log;
   boolean apiMode;
   private Option[] recognizedOptions;
   private OptionHelper optionHelper;
   private Options options;
   public Set filenames;
   public ListBuffer classnames;
   private JavaFileManager fileManager;
   public static final String javacBundleName = "com.sun.tools.javac.resources.javac";

   public Main(String var1) {
      this(var1, new PrintWriter(System.err, true));
   }

   public Main(String var1, PrintWriter var2) {
      this.recognizedOptions = (Option[])Option.getJavaCompilerOptions().toArray(new Option[0]);
      this.optionHelper = new OptionHelper() {
         public String get(Option var1) {
            return Main.this.options.get(var1);
         }

         public void put(String var1, String var2) {
            Main.this.options.put(var1, var2);
         }

         public void remove(String var1) {
            Main.this.options.remove(var1);
         }

         public Log getLog() {
            return Main.this.log;
         }

         public String getOwnName() {
            return Main.this.ownName;
         }

         public void error(String var1, Object... var2) {
            Main.this.error(var1, var2);
         }

         public void addFile(File var1) {
            Main.this.filenames.add(var1);
         }

         public void addClassName(String var1) {
            Main.this.classnames.append(var1);
         }
      };
      this.options = null;
      this.filenames = null;
      this.classnames = null;
      this.ownName = var1;
      this.out = var2;
   }

   void error(String var1, Object... var2) {
      if (this.apiMode) {
         String var3 = this.log.localize(Log.PrefixKind.JAVAC, var1, var2);
         throw new PropagatedException(new IllegalStateException(var3));
      } else {
         this.warning(var1, var2);
         this.log.printLines(Log.PrefixKind.JAVAC, "msg.usage", this.ownName);
      }
   }

   void warning(String var1, Object... var2) {
      this.log.printRawLines(this.ownName + ": " + this.log.localize(Log.PrefixKind.JAVAC, var1, var2));
   }

   public Option getOption(String var1) {
      Option[] var2 = this.recognizedOptions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Option var5 = var2[var4];
         if (var5.matches(var1)) {
            return var5;
         }
      }

      return null;
   }

   public void setOptions(Options var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.options = var1;
      }
   }

   public void setAPIMode(boolean var1) {
      this.apiMode = var1;
   }

   public Collection processArgs(String[] var1) {
      return this.processArgs(var1, (String[])null);
   }

   public Collection processArgs(String[] var1, String[] var2) {
      int var3 = 0;

      String var4;
      String var11;
      while(var3 < var1.length) {
         var4 = var1[var3];
         ++var3;
         Option var5 = null;
         if (var4.length() > 0) {
            int var6 = var4.charAt(0) == '-' ? 0 : this.recognizedOptions.length - 1;

            for(int var7 = var6; var7 < this.recognizedOptions.length; ++var7) {
               if (this.recognizedOptions[var7].matches(var4)) {
                  var5 = this.recognizedOptions[var7];
                  break;
               }
            }
         }

         if (var5 == null) {
            this.error("err.invalid.flag", var4);
            return null;
         }

         if (var5.hasArg()) {
            if (var3 == var1.length) {
               this.error("err.req.arg", var4);
               return null;
            }

            var11 = var1[var3];
            ++var3;
            if (var5.process(this.optionHelper, var4, var11)) {
               return null;
            }
         } else if (var5.process(this.optionHelper, var4)) {
            return null;
         }
      }

      if (this.options.get(Option.PROFILE) != null && this.options.get(Option.BOOTCLASSPATH) != null) {
         this.error("err.profile.bootclasspath.conflict");
         return null;
      } else {
         if (this.classnames != null && var2 != null) {
            this.classnames.addAll(Arrays.asList(var2));
         }

         if (!this.checkDirectory(Option.D)) {
            return null;
         } else if (!this.checkDirectory(Option.S)) {
            return null;
         } else {
            var4 = this.options.get(Option.SOURCE);
            Source var10 = var4 != null ? Source.lookup(var4) : Source.DEFAULT;
            var11 = this.options.get(Option.TARGET);
            Target var12 = var11 != null ? Target.lookup(var11) : Target.DEFAULT;
            if (Character.isDigit(var12.name.charAt(0))) {
               if (var12.compareTo(var10.requiredTarget()) < 0) {
                  if (var11 != null) {
                     if (var4 == null) {
                        this.warning("warn.target.default.source.conflict", var11, var10.requiredTarget().name);
                     } else {
                        this.warning("warn.source.target.conflict", var4, var10.requiredTarget().name);
                     }

                     return null;
                  }

                  var12 = var10.requiredTarget();
                  this.options.put("-target", var12.name);
               } else if (var11 == null && !var10.allowGenerics()) {
                  var12 = Target.JDK1_4;
                  this.options.put("-target", var12.name);
               }
            }

            String var8 = this.options.get(Option.PROFILE);
            if (var8 != null) {
               Profile var9 = Profile.lookup(var8);
               if (!var9.isValid(var12)) {
                  this.warning("warn.profile.target.conflict", var8, var12.name);
                  return null;
               }
            }

            String var13 = this.options.get("showClass");
            if (var13 != null) {
               if (var13.equals("showClass")) {
                  var13 = "com.sun.tools.javac.Main";
               }

               this.showClass(var13);
            }

            this.options.notifyListeners();
            return this.filenames;
         }
      }
   }

   private boolean checkDirectory(Option var1) {
      String var2 = this.options.get(var1);
      if (var2 == null) {
         return true;
      } else {
         File var3 = new File(var2);
         if (!var3.exists()) {
            this.error("err.dir.not.found", var2);
            return false;
         } else if (!var3.isDirectory()) {
            this.error("err.file.not.directory", var2);
            return false;
         } else {
            return true;
         }
      }
   }

   public Result compile(String[] var1) {
      Context var2 = new Context();
      JavacFileManager.preRegister(var2);
      Result var3 = this.compile(var1, var2);
      if (this.fileManager instanceof JavacFileManager) {
         ((JavacFileManager)this.fileManager).close();
      }

      return var3;
   }

   public Result compile(String[] var1, Context var2) {
      return this.compile(var1, var2, List.nil(), (Iterable)null);
   }

   public Result compile(String[] var1, Context var2, List var3, Iterable var4) {
      return this.compile(var1, (String[])null, var2, var3, var4);
   }

   public Result compile(String[] var1, String[] var2, Context var3, List var4, Iterable var5) {
      var3.put((Context.Key)Log.outKey, (Object)this.out);
      this.log = Log.instance(var3);
      if (this.options == null) {
         this.options = Options.instance(var3);
      }

      this.filenames = new LinkedHashSet();
      this.classnames = new ListBuffer();
      JavaCompiler var6 = null;

      try {
         Result var8;
         try {
            if (var1.length == 0 && (var2 == null || var2.length == 0) && var4.isEmpty()) {
               Option.HELP.process(this.optionHelper, "-help");
               Result var65 = Main.Result.CMDERR;
               return var65;
            } else {
               Collection var7;
               try {
                  var7 = this.processArgs(CommandLine.parse(var1), var2);
                  if (var7 == null) {
                     var8 = Main.Result.CMDERR;
                     return var8;
                  }

                  if (var7.isEmpty() && var4.isEmpty() && this.classnames.isEmpty()) {
                     if (!this.options.isSet(Option.HELP) && !this.options.isSet(Option.X) && !this.options.isSet(Option.VERSION) && !this.options.isSet(Option.FULLVERSION)) {
                        if (JavaCompiler.explicitAnnotationProcessingRequested(this.options)) {
                           this.error("err.no.source.files.classes");
                        } else {
                           this.error("err.no.source.files");
                        }

                        var8 = Main.Result.CMDERR;
                        return var8;
                     }

                     var8 = Main.Result.OK;
                     return var8;
                  }
               } catch (FileNotFoundException var55) {
                  this.warning("err.file.not.found", var55.getMessage());
                  Result var9 = Main.Result.SYSERR;
                  return var9;
               }

               boolean var67 = this.options.isSet("stdout");
               if (var67) {
                  this.log.flush();
                  this.log.setWriters(new PrintWriter(System.out, true));
               }

               boolean var66 = this.options.isUnset("nonBatchMode") && System.getProperty("nonBatchMode") == null;
               if (var66) {
                  CacheFSInfo.preRegister(var3);
               }

               String var10 = this.options.get(Option.PLUGIN);
               int var16;
               if (var10 != null) {
                  JavacProcessingEnvironment var11 = JavacProcessingEnvironment.instance(var3);
                  ClassLoader var12 = var11.getProcessorClassLoader();
                  ServiceLoader var13 = ServiceLoader.load(Plugin.class, var12);
                  LinkedHashSet var14 = new LinkedHashSet();
                  String[] var15 = var10.split("\\x00");
                  var16 = var15.length;

                  for(int var17 = 0; var17 < var16; ++var17) {
                     String var18 = var15[var17];
                     var14.add(List.from((Object[])var18.split("\\s+")));
                  }

                  JavacTask var76 = null;
                  Iterator var77 = var13.iterator();

                  label742:
                  while(true) {
                     if (!var77.hasNext()) {
                        Iterator var82 = var14.iterator();

                        while(true) {
                           if (!var82.hasNext()) {
                              break label742;
                           }

                           List var85 = (List)var82.next();
                           this.log.printLines(Log.PrefixKind.JAVAC, "msg.plugin.not.found", var85.head);
                        }
                     }

                     Plugin var81 = (Plugin)var77.next();
                     Iterator var83 = var14.iterator();

                     while(var83.hasNext()) {
                        List var19 = (List)var83.next();
                        if (var81.getName().equals(var19.head)) {
                           var14.remove(var19);

                           try {
                              if (var76 == null) {
                                 var76 = JavacTask.instance(var11);
                              }

                              var81.init(var76, (String[])var19.tail.toArray(new String[var19.tail.size()]));
                           } catch (Throwable var54) {
                              if (this.apiMode) {
                                 throw new RuntimeException(var54);
                              }

                              this.pluginMessage(var54);
                              Result var21 = Main.Result.SYSERR;
                              return var21;
                           }
                        }
                     }
                  }
               }

               var6 = JavaCompiler.instance(var3);
               String var68 = this.options.get(Option.XDOCLINT);
               String var69 = this.options.get(Option.XDOCLINT_CUSTOM);
               if (var68 != null || var69 != null) {
                  LinkedHashSet var70 = new LinkedHashSet();
                  if (var68 != null) {
                     var70.add("-Xmsgs");
                  }

                  if (var69 != null) {
                     String[] var73 = var69.split("\\s+");
                     int var78 = var73.length;

                     for(var16 = 0; var16 < var78; ++var16) {
                        String var84 = var73[var16];
                        if (!var84.isEmpty()) {
                           var70.add(var84.replace(Option.XDOCLINT_CUSTOM.text, "-Xmsgs:"));
                        }
                     }
                  }

                  if (var70.size() != 1 || !((String)var70.iterator().next()).equals("-Xmsgs:none")) {
                     JavacTask var74 = BasicJavacTask.instance(var3);
                     var70.add("-XimplicitHeaders:2");
                     (new DocLint()).init(var74, (String[])var70.toArray(new String[var70.size()]));
                     var6.keepComments = true;
                  }
               }

               this.fileManager = (JavaFileManager)var3.get(JavaFileManager.class);
               if (!var7.isEmpty()) {
                  var6 = JavaCompiler.instance(var3);
                  List var71 = List.nil();
                  JavacFileManager var75 = (JavacFileManager)this.fileManager;
                  Iterator var79 = var75.getJavaFileObjectsFromFiles(var7).iterator();

                  label708:
                  while(true) {
                     JavaFileObject var80;
                     if (!var79.hasNext()) {
                        var79 = var71.iterator();

                        while(true) {
                           if (!var79.hasNext()) {
                              break label708;
                           }

                           var80 = (JavaFileObject)var79.next();
                           var4 = var4.prepend(var80);
                        }
                     }

                     var80 = (JavaFileObject)var79.next();
                     var71 = var71.prepend(var80);
                  }
               }

               var6.compile(var4, this.classnames.toList(), var5);
               Result var72;
               if (this.log.expectDiagKeys != null) {
                  if (this.log.expectDiagKeys.isEmpty()) {
                     this.log.printRawLines("all expected diagnostics found");
                     var72 = Main.Result.OK;
                     return var72;
                  } else {
                     this.log.printRawLines("expected diagnostic keys not found: " + this.log.expectDiagKeys);
                     var72 = Main.Result.ERROR;
                     return var72;
                  }
               } else if (var6.errorCount() == 0) {
                  return Main.Result.OK;
               } else {
                  var72 = Main.Result.ERROR;
                  return var72;
               }
            }
         } catch (IOException var56) {
            this.ioMessage(var56);
            var8 = Main.Result.SYSERR;
            return var8;
         } catch (OutOfMemoryError var57) {
            this.resourceMessage(var57);
            var8 = Main.Result.SYSERR;
            return var8;
         } catch (StackOverflowError var58) {
            this.resourceMessage(var58);
            var8 = Main.Result.SYSERR;
            return var8;
         } catch (FatalError var59) {
            this.feMessage(var59);
            var8 = Main.Result.SYSERR;
            return var8;
         } catch (AnnotationProcessingError var60) {
            if (this.apiMode) {
               throw new RuntimeException(var60.getCause());
            } else {
               this.apMessage(var60);
               var8 = Main.Result.SYSERR;
               return var8;
            }
         } catch (ClientCodeException var61) {
            throw new RuntimeException(var61.getCause());
         } catch (PropagatedException var62) {
            throw var62.getCause();
         } catch (Throwable var63) {
            if (var6 == null || var6.errorCount() == 0 || this.options == null || this.options.isSet("dev")) {
               this.bugMessage(var63);
            }

            var8 = Main.Result.ABNORMAL;
            return var8;
         }
      } finally {
         if (var6 != null) {
            try {
               var6.close();
            } catch (ClientCodeException var53) {
               throw new RuntimeException(var53.getCause());
            }
         }

         this.filenames = null;
         this.options = null;
      }
   }

   void bugMessage(Throwable var1) {
      this.log.printLines(Log.PrefixKind.JAVAC, "msg.bug", JavaCompiler.version());
      var1.printStackTrace(this.log.getWriter(Log.WriterKind.NOTICE));
   }

   void feMessage(Throwable var1) {
      this.log.printRawLines(var1.getMessage());
      if (var1.getCause() != null && this.options.isSet("dev")) {
         var1.getCause().printStackTrace(this.log.getWriter(Log.WriterKind.NOTICE));
      }

   }

   void ioMessage(Throwable var1) {
      this.log.printLines(Log.PrefixKind.JAVAC, "msg.io");
      var1.printStackTrace(this.log.getWriter(Log.WriterKind.NOTICE));
   }

   void resourceMessage(Throwable var1) {
      this.log.printLines(Log.PrefixKind.JAVAC, "msg.resource");
      var1.printStackTrace(this.log.getWriter(Log.WriterKind.NOTICE));
   }

   void apMessage(AnnotationProcessingError var1) {
      this.log.printLines(Log.PrefixKind.JAVAC, "msg.proc.annotation.uncaught.exception");
      var1.getCause().printStackTrace(this.log.getWriter(Log.WriterKind.NOTICE));
   }

   void pluginMessage(Throwable var1) {
      this.log.printLines(Log.PrefixKind.JAVAC, "msg.plugin.uncaught.exception");
      var1.printStackTrace(this.log.getWriter(Log.WriterKind.NOTICE));
   }

   void showClass(String var1) {
      PrintWriter var2 = this.log.getWriter(Log.WriterKind.NOTICE);
      var2.println("javac: show class: " + var1);
      URL var3 = this.getClass().getResource('/' + var1.replace('.', '/') + ".class");
      if (var3 == null) {
         var2.println("  class not found");
      } else {
         var2.println("  " + var3);

         try {
            MessageDigest var6 = MessageDigest.getInstance("MD5");
            DigestInputStream var7 = new DigestInputStream(var3.openStream(), var6);

            byte[] var5;
            try {
               byte[] var8 = new byte[8192];

               while(true) {
                  int var9 = var7.read(var8);
                  if (var9 <= 0) {
                     var5 = var6.digest();
                     break;
                  }
               }
            } finally {
               var7.close();
            }

            StringBuilder var17 = new StringBuilder();
            byte[] var18 = var5;
            int var10 = var5.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               byte var12 = var18[var11];
               var17.append(String.format("%02x", var12));
            }

            var2.println("  MD5 checksum: " + var17);
         } catch (Exception var16) {
            var2.println("  cannot compute digest: " + var16);
         }
      }

   }

   public static enum Result {
      OK(0),
      ERROR(1),
      CMDERR(2),
      SYSERR(3),
      ABNORMAL(4);

      public final int exitCode;

      private Result(int var3) {
         this.exitCode = var3;
      }

      public boolean isOK() {
         return this.exitCode == 0;
      }
   }
}
