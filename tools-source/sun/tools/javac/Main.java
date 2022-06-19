package sun.tools.javac;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import sun.tools.asm.Assembler;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.Constants;
import sun.tools.util.CommandLine;

/** @deprecated */
@Deprecated
public class Main implements Constants {
   String program;
   OutputStream out;
   public static final int EXIT_OK = 0;
   public static final int EXIT_ERROR = 1;
   public static final int EXIT_CMDERR = 2;
   public static final int EXIT_SYSERR = 3;
   public static final int EXIT_ABNORMAL = 4;
   private int exitStatus;
   private static ResourceBundle messageRB;
   private static final String[] releases = new String[]{"1.1", "1.2", "1.3", "1.4"};
   private static final short[] majorVersions = new short[]{45, 46, 47, 48};
   private static final short[] minorVersions = new short[]{3, 0, 0, 0};

   public Main(OutputStream var1, String var2) {
      this.out = var1;
      this.program = var2;
   }

   public int getExitStatus() {
      return this.exitStatus;
   }

   public boolean compilationPerformedSuccessfully() {
      return this.exitStatus == 0 || this.exitStatus == 1;
   }

   public boolean compilationReportedErrors() {
      return this.exitStatus != 0;
   }

   private void output(String var1) {
      PrintStream var2 = this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out, true);
      var2.println(var1);
   }

   private void error(String var1) {
      this.exitStatus = 2;
      this.output(getText(var1));
   }

   private void error(String var1, String var2) {
      this.exitStatus = 2;
      this.output(getText(var1, var2));
   }

   private void error(String var1, String var2, String var3) {
      this.exitStatus = 2;
      this.output(getText(var1, var2, var3));
   }

   public void usage_error() {
      this.error("main.usage", this.program);
   }

   static void initResource() {
      try {
         messageRB = ResourceBundle.getBundle("sun.tools.javac.resources.javac");
      } catch (MissingResourceException var1) {
         throw new Error("Fatal: Resource for javac is missing");
      }
   }

   public static String getText(String var0) {
      return getText(var0, (String)null);
   }

   public static String getText(String var0, int var1) {
      return getText(var0, Integer.toString(var1));
   }

   public static String getText(String var0, String var1) {
      return getText(var0, var1, (String)null);
   }

   public static String getText(String var0, String var1, String var2) {
      return getText(var0, var1, var2, (String)null);
   }

   public static String getText(String var0, String var1, String var2, String var3) {
      if (messageRB == null) {
         initResource();
      }

      try {
         String var4 = messageRB.getString(var0);
         return MessageFormat.format(var4, var1, var2, var3);
      } catch (MissingResourceException var6) {
         if (var1 == null) {
            var1 = "null";
         }

         if (var2 == null) {
            var2 = "null";
         }

         if (var3 == null) {
            var3 = "null";
         }

         String var5 = "JAVAC MESSAGE FILE IS BROKEN: key={0}, arguments={1}, {2}, {3}";
         return MessageFormat.format(var5, var0, var1, var2, var3);
      }
   }

   public synchronized boolean compile(String[] var1) {
      String var2 = null;
      String var3 = null;
      String var4 = null;
      String var5 = null;
      boolean var6 = false;
      String var7 = null;
      short var8 = 45;
      short var9 = 3;
      File var10 = null;
      File var11 = null;
      String var12 = "-Xjcov";
      String var13 = "-Xjcov:file=";
      int var14 = 266244;
      long var15 = System.currentTimeMillis();
      Vector var17 = new Vector();
      boolean var18 = false;
      Object var19 = null;
      String var20 = null;
      String var21 = null;
      String var22 = null;
      this.exitStatus = 0;

      try {
         var1 = CommandLine.parse(var1);
      } catch (FileNotFoundException var38) {
         this.error("javac.err.cant.read", var38.getMessage());
         System.exit(1);
      } catch (IOException var39) {
         var39.printStackTrace();
         System.exit(1);
      }

      String var44;
      for(int var23 = 0; var23 < var1.length; ++var23) {
         if (var1[var23].equals("-g")) {
            if (var21 != null && !var21.equals("-g")) {
               this.error("main.conflicting.options", var21, "-g");
            }

            var21 = "-g";
            var14 |= 4096;
            var14 |= 8192;
            var14 |= 262144;
         } else if (var1[var23].equals("-g:none")) {
            if (var21 != null && !var21.equals("-g:none")) {
               this.error("main.conflicting.options", var21, "-g:none");
            }

            var21 = "-g:none";
            var14 &= -4097;
            var14 &= -8193;
            var14 &= -262145;
         } else if (var1[var23].startsWith("-g:")) {
            if (var21 != null && !var21.equals(var1[var23])) {
               this.error("main.conflicting.options", var21, var1[var23]);
            }

            var21 = var1[var23];
            var44 = var1[var23].substring("-g:".length());
            var14 &= -4097;
            var14 &= -8193;
            var14 &= -262145;

            while(true) {
               if (var44.startsWith("lines")) {
                  var14 |= 4096;
                  var44 = var44.substring("lines".length());
               } else if (var44.startsWith("vars")) {
                  var14 |= 8192;
                  var44 = var44.substring("vars".length());
               } else {
                  if (!var44.startsWith("source")) {
                     this.error("main.bad.debug.option", var1[var23]);
                     this.usage_error();
                     return false;
                  }

                  var14 |= 262144;
                  var44 = var44.substring("source".length());
               }

               if (var44.length() == 0) {
                  break;
               }

               if (var44.startsWith(",")) {
                  var44 = var44.substring(",".length());
               }
            }
         } else if (var1[var23].equals("-O")) {
            if (var22 != null && !var22.equals("-O")) {
               this.error("main.conflicting.options", var22, "-O");
            }

            var22 = "-O";
         } else if (var1[var23].equals("-nowarn")) {
            var14 &= -5;
         } else if (var1[var23].equals("-deprecation")) {
            var14 |= 512;
         } else if (var1[var23].equals("-verbose")) {
            var14 |= 1;
         } else if (var1[var23].equals("-nowrite")) {
            var18 = true;
         } else if (var1[var23].equals("-classpath")) {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-classpath");
               this.usage_error();
               return false;
            }

            if (var3 != null) {
               this.error("main.option.already.seen", "-classpath");
            }

            ++var23;
            var3 = var1[var23];
         } else if (var1[var23].equals("-sourcepath")) {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-sourcepath");
               this.usage_error();
               return false;
            }

            if (var2 != null) {
               this.error("main.option.already.seen", "-sourcepath");
            }

            ++var23;
            var2 = var1[var23];
         } else if (var1[var23].equals("-sysclasspath")) {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-sysclasspath");
               this.usage_error();
               return false;
            }

            if (var4 != null) {
               this.error("main.option.already.seen", "-sysclasspath");
            }

            ++var23;
            var4 = var1[var23];
         } else if (var1[var23].equals("-bootclasspath")) {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-bootclasspath");
               this.usage_error();
               return false;
            }

            if (var4 != null) {
               this.error("main.option.already.seen", "-bootclasspath");
            }

            ++var23;
            var4 = var1[var23];
         } else if (var1[var23].equals("-extdirs")) {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-extdirs");
               this.usage_error();
               return false;
            }

            if (var5 != null) {
               this.error("main.option.already.seen", "-extdirs");
            }

            ++var23;
            var5 = var1[var23];
         } else if (var1[var23].equals("-encoding")) {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-encoding");
               this.usage_error();
               return false;
            }

            if (var20 != null) {
               this.error("main.option.already.seen", "-encoding");
            }

            ++var23;
            var20 = var1[var23];
         } else if (!var1[var23].equals("-target")) {
            if (var1[var23].equals("-d")) {
               if (var23 + 1 >= var1.length) {
                  this.error("main.option.requires.argument", "-d");
                  this.usage_error();
                  return false;
               }

               if (var10 != null) {
                  this.error("main.option.already.seen", "-d");
               }

               ++var23;
               var10 = new File(var1[var23]);
               if (!var10.exists()) {
                  this.error("main.no.such.directory", var10.getPath());
                  this.usage_error();
                  return false;
               }
            } else if (var1[var23].equals(var12)) {
               var14 |= 64;
               var14 &= -16385;
               var14 &= -32769;
            } else if (var1[var23].startsWith(var13) && var1[var23].length() > var13.length()) {
               var11 = new File(var1[var23].substring(var13.length()));
               var14 &= -16385;
               var14 &= -32769;
               var14 |= 64;
               var14 |= 128;
            } else if (var1[var23].equals("-XO")) {
               if (var22 != null && !var22.equals("-XO")) {
                  this.error("main.conflicting.options", var22, "-XO");
               }

               var22 = "-XO";
               var14 |= 16384;
            } else if (var1[var23].equals("-Xinterclass")) {
               if (var22 != null && !var22.equals("-Xinterclass")) {
                  this.error("main.conflicting.options", var22, "-Xinterclass");
               }

               var22 = "-Xinterclass";
               var14 |= 16384;
               var14 |= 32768;
               var14 |= 32;
            } else if (var1[var23].equals("-Xdepend")) {
               var14 |= 32;
            } else if (var1[var23].equals("-Xdebug")) {
               var14 |= 2;
            } else if (!var1[var23].equals("-xdepend") && !var1[var23].equals("-Xjws")) {
               if (var1[var23].equals("-Xstrictdefault")) {
                  var14 |= 131072;
               } else if (var1[var23].equals("-Xverbosepath")) {
                  var6 = true;
               } else if (var1[var23].equals("-Xstdout")) {
                  this.out = System.out;
               } else {
                  if (var1[var23].equals("-X")) {
                     this.error("main.unsupported.usage");
                     return false;
                  }

                  if (var1[var23].equals("-Xversion1.2")) {
                     var14 |= 2048;
                  } else {
                     if (!var1[var23].endsWith(".java")) {
                        this.error("main.no.such.option", var1[var23]);
                        this.usage_error();
                        return false;
                     }

                     var17.addElement(var1[var23]);
                  }
               }
            } else {
               var14 |= 1024;
               if (this.out == System.err) {
                  this.out = System.out;
               }
            }
         } else {
            if (var23 + 1 >= var1.length) {
               this.error("main.option.requires.argument", "-target");
               this.usage_error();
               return false;
            }

            if (var7 != null) {
               this.error("main.option.already.seen", "-target");
            }

            ++var23;
            var7 = var1[var23];

            int var24;
            for(var24 = 0; var24 < releases.length; ++var24) {
               if (releases[var24].equals(var7)) {
                  var8 = majorVersions[var24];
                  var9 = minorVersions[var24];
                  break;
               }
            }

            if (var24 == releases.length) {
               this.error("main.unknown.release", var7);
               this.usage_error();
               return false;
            }
         }
      }

      if (var17.size() != 0 && this.exitStatus != 2) {
         BatchEnvironment var45 = BatchEnvironment.create(this.out, var2, var3, var4, var5);
         if (var6) {
            this.output(getText("main.path.msg", var45.sourcePath.toString(), var45.binaryPath.toString()));
         }

         var45.flags |= var14;
         var45.majorVersion = var8;
         var45.minorVersion = var9;
         var45.covFile = var11;
         var45.setCharacterEncoding(var20);
         var44 = getText("main.no.memory");
         String var25 = getText("main.stack.overflow");
         var45.error(0L, "warn.class.is.deprecated", "sun.tools.javac.Main");

         boolean var49;
         try {
            Enumeration var26 = var17.elements();

            while(var26.hasMoreElements()) {
               File var27 = new File((String)var26.nextElement());

               try {
                  var45.parseFile(new ClassFile(var27));
               } catch (FileNotFoundException var37) {
                  var45.error(0L, "cant.read", var27.getPath());
                  this.exitStatus = 2;
               }
            }

            var26 = var45.getClasses();

            while(var26.hasMoreElements()) {
               ClassDeclaration var48 = (ClassDeclaration)var26.nextElement();
               if (var48.getStatus() == 4 && !var48.getClassDefinition().isLocal()) {
                  try {
                     var48.getClassDefinition(var45);
                  } catch (ClassNotFound var36) {
                  }
               }
            }

            ByteArrayOutputStream var46 = new ByteArrayOutputStream(4096);

            label386:
            do {
               var49 = true;
               var45.flushErrors();
               Enumeration var28 = var45.getClasses();

               while(true) {
                  while(true) {
                     if (!var28.hasMoreElements()) {
                        continue label386;
                     }

                     ClassDeclaration var29 = (ClassDeclaration)var28.nextElement();
                     SourceClass var30;
                     switch (var29.getStatus()) {
                        case 0:
                           if (!var45.dependencies()) {
                              break;
                           }
                        case 3:
                           var45.dtEvent("Main.compile (SOURCE): loading, " + var29);
                           var49 = false;
                           var45.loadDefinition(var29);
                           if (var29.getStatus() != 4) {
                              var45.dtEvent("Main.compile (SOURCE): not parsed, " + var29);
                              break;
                           }
                        case 4:
                           if (var29.getClassDefinition().isInsideLocal()) {
                              var45.dtEvent("Main.compile (PARSED): skipping local class, " + var29);
                              break;
                           } else {
                              var49 = false;
                              var45.dtEvent("Main.compile (PARSED): checking, " + var29);
                              var30 = (SourceClass)var29.getClassDefinition(var45);
                              var30.check(var45);
                              var29.setDefinition(var30, 5);
                           }
                        case 5:
                           var30 = (SourceClass)var29.getClassDefinition(var45);
                           if (var30.getError()) {
                              var45.dtEvent("Main.compile (CHECKED): bailing out on error, " + var29);
                              var29.setDefinition(var30, 6);
                           } else {
                              var49 = false;
                              var46.reset();
                              var45.dtEvent("Main.compile (CHECKED): compiling, " + var29);
                              var30.compile(var46);
                              var29.setDefinition(var30, 6);
                              var30.cleanup(var45);
                              if (!var30.getNestError() && !var18) {
                                 String var31 = var29.getName().getQualifier().toString().replace('.', File.separatorChar);
                                 String var32 = var29.getName().getFlatName().toString().replace('.', '$') + ".class";
                                 File var33;
                                 if (var10 != null) {
                                    if (var31.length() > 0) {
                                       var33 = new File(var10, var31);
                                       if (!var33.exists()) {
                                          var33.mkdirs();
                                       }

                                       var33 = new File(var33, var32);
                                    } else {
                                       var33 = new File(var10, var32);
                                    }
                                 } else {
                                    ClassFile var34 = (ClassFile)var30.getSource();
                                    if (var34.isZipped()) {
                                       var45.error(0L, "cant.write", var34.getPath());
                                       this.exitStatus = 2;
                                       continue;
                                    }

                                    var33 = new File(var34.getPath());
                                    var33 = new File(var33.getParent(), var32);
                                 }

                                 try {
                                    FileOutputStream var54 = new FileOutputStream(var33.getPath());
                                    var46.writeTo(var54);
                                    var54.close();
                                    if (var45.verbose()) {
                                       this.output(getText("main.wrote", var33.getPath()));
                                    }
                                 } catch (IOException var35) {
                                    var45.error(0L, "cant.write", var33.getPath());
                                    this.exitStatus = 2;
                                 }

                                 if (var45.print_dependencies()) {
                                    var30.printClassDependencies(var45);
                                 }
                              }
                           }
                        case 1:
                        case 2:
                     }
                  }
               }
            } while(!var49);
         } catch (OutOfMemoryError var40) {
            var45.output(var44);
            this.exitStatus = 3;
            return false;
         } catch (StackOverflowError var41) {
            var45.output(var25);
            this.exitStatus = 3;
            return false;
         } catch (Error var42) {
            if (var45.nerrors == 0 || var45.dump()) {
               var42.printStackTrace();
               var45.error(0L, "fatal.error");
               this.exitStatus = 4;
            }
         } catch (Exception var43) {
            if (var45.nerrors == 0 || var45.dump()) {
               var43.printStackTrace();
               var45.error(0L, "fatal.exception");
               this.exitStatus = 4;
            }
         }

         int var47 = var45.deprecationFiles.size();
         if (var47 > 0 && var45.warnings()) {
            int var52 = var45.ndeprecations;
            Object var50 = var45.deprecationFiles.elementAt(0);
            if (var45.deprecation()) {
               if (var47 > 1) {
                  var45.error(0L, "warn.note.deprecations", new Integer(var47), new Integer(var52));
               } else {
                  var45.error(0L, "warn.note.1deprecation", var50, new Integer(var52));
               }
            } else if (var47 > 1) {
               var45.error(0L, "warn.note.deprecations.silent", new Integer(var47), new Integer(var52));
            } else {
               var45.error(0L, "warn.note.1deprecation.silent", var50, new Integer(var52));
            }
         }

         var45.flushErrors();
         var45.shutdown();
         var49 = true;
         if (var45.nerrors > 0) {
            String var51 = "";
            if (var45.nerrors > 1) {
               var51 = getText("main.errors", var45.nerrors);
            } else {
               var51 = getText("main.1error");
            }

            if (var45.nwarnings > 0) {
               if (var45.nwarnings > 1) {
                  var51 = var51 + ", " + getText("main.warnings", var45.nwarnings);
               } else {
                  var51 = var51 + ", " + getText("main.1warning");
               }
            }

            this.output(var51);
            if (this.exitStatus == 0) {
               this.exitStatus = 1;
            }

            var49 = false;
         } else if (var45.nwarnings > 0) {
            if (var45.nwarnings > 1) {
               this.output(getText("main.warnings", var45.nwarnings));
            } else {
               this.output(getText("main.1warning"));
            }
         }

         if (var45.covdata()) {
            Assembler var53 = new Assembler();
            var53.GenJCov(var45);
         }

         if (var45.verbose()) {
            var15 = System.currentTimeMillis() - var15;
            this.output(getText("main.done_in", Long.toString(var15)));
         }

         return var49;
      } else {
         this.usage_error();
         return false;
      }
   }

   public static void main(String[] var0) {
      PrintStream var1 = System.err;
      if (Boolean.getBoolean("javac.pipe.output")) {
         var1 = System.out;
      }

      Main var2 = new Main(var1, "javac");
      System.exit(var2.compile(var0) ? 0 : var2.exitStatus);
   }
}
