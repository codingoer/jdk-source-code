package sun.rmi.rmic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.ClassDefinition;
import sun.tools.java.ClassFile;
import sun.tools.java.ClassNotFound;
import sun.tools.java.ClassPath;
import sun.tools.java.Identifier;
import sun.tools.javac.SourceClass;
import sun.tools.util.CommandLine;

public class Main implements Constants {
   String sourcePathArg;
   String sysClassPathArg;
   String extDirsArg;
   String classPathString;
   File destDir;
   int flags;
   long tm;
   Vector classes;
   boolean nowrite;
   boolean nocompile;
   boolean keepGenerated;
   boolean status;
   String[] generatorArgs;
   Vector generators;
   Class environmentClass = BatchEnvironment.class;
   boolean iiopGeneration = false;
   String program;
   OutputStream out;
   private static boolean resourcesInitialized = false;
   private static ResourceBundle resources;
   private static ResourceBundle resourcesExt = null;

   public Main(OutputStream var1, String var2) {
      this.out = var1;
      this.program = var2;
   }

   public void output(String var1) {
      PrintStream var2 = this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out, true);
      var2.println(var1);
   }

   public void error(String var1) {
      this.output(getText(var1));
   }

   public void error(String var1, String var2) {
      this.output(getText(var1, var2));
   }

   public void error(String var1, String var2, String var3) {
      this.output(getText(var1, var2, var3));
   }

   public void usage() {
      this.error("rmic.usage", this.program);
   }

   public synchronized boolean compile(String[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var1[var2].equals("-Xnew")) {
            return (new sun.rmi.rmic.newrmic.Main(this.out, this.program)).compile(var1);
         }
      }

      if (!this.parseArgs(var1)) {
         return false;
      } else if (this.classes.size() == 0) {
         this.usage();
         return false;
      } else {
         if ((this.flags & 4) != 0) {
            Iterator var4 = this.generators.iterator();

            while(var4.hasNext()) {
               Generator var3 = (Generator)var4.next();
               if (var3 instanceof RMIGenerator) {
                  this.output(getText("rmic.jrmp.stubs.deprecated", this.program));
                  break;
               }
            }
         }

         return this.doCompile();
      }
   }

   public File getDestinationDir() {
      return this.destDir;
   }

   public boolean parseArgs(String[] var1) {
      this.sourcePathArg = null;
      this.sysClassPathArg = null;
      this.extDirsArg = null;
      this.classPathString = null;
      this.destDir = null;
      this.flags = 4;
      this.tm = System.currentTimeMillis();
      this.classes = new Vector();
      this.nowrite = false;
      this.nocompile = false;
      this.keepGenerated = false;
      this.generatorArgs = this.getArray("generator.args", true);
      if (this.generatorArgs == null) {
         return false;
      } else {
         this.generators = new Vector();

         try {
            var1 = CommandLine.parse(var1);
         } catch (FileNotFoundException var3) {
            this.error("rmic.cant.read", var3.getMessage());
            return false;
         } catch (IOException var4) {
            var4.printStackTrace(this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out, true));
            return false;
         }

         int var2;
         for(var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] != null) {
               if (var1[var2].equals("-g")) {
                  this.flags &= -16385;
                  this.flags |= 12288;
                  var1[var2] = null;
               } else if (var1[var2].equals("-O")) {
                  this.flags &= -4097;
                  this.flags &= -8193;
                  this.flags |= 16416;
                  var1[var2] = null;
               } else if (var1[var2].equals("-nowarn")) {
                  this.flags &= -5;
                  var1[var2] = null;
               } else if (var1[var2].equals("-debug")) {
                  this.flags |= 2;
                  var1[var2] = null;
               } else if (var1[var2].equals("-depend")) {
                  this.flags |= 32;
                  var1[var2] = null;
               } else if (var1[var2].equals("-verbose")) {
                  this.flags |= 1;
                  var1[var2] = null;
               } else if (var1[var2].equals("-nowrite")) {
                  this.nowrite = true;
                  var1[var2] = null;
               } else if (var1[var2].equals("-Xnocompile")) {
                  this.nocompile = true;
                  this.keepGenerated = true;
                  var1[var2] = null;
               } else if (!var1[var2].equals("-keep") && !var1[var2].equals("-keepgenerated")) {
                  if (var1[var2].equals("-show")) {
                     this.error("rmic.option.unsupported", "-show");
                     this.usage();
                     return false;
                  }

                  if (var1[var2].equals("-classpath")) {
                     if (var2 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", "-classpath");
                        this.usage();
                        return false;
                     }

                     if (this.classPathString != null) {
                        this.error("rmic.option.already.seen", "-classpath");
                        this.usage();
                        return false;
                     }

                     var1[var2] = null;
                     ++var2;
                     this.classPathString = var1[var2];
                     var1[var2] = null;
                  } else if (var1[var2].equals("-sourcepath")) {
                     if (var2 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", "-sourcepath");
                        this.usage();
                        return false;
                     }

                     if (this.sourcePathArg != null) {
                        this.error("rmic.option.already.seen", "-sourcepath");
                        this.usage();
                        return false;
                     }

                     var1[var2] = null;
                     ++var2;
                     this.sourcePathArg = var1[var2];
                     var1[var2] = null;
                  } else if (var1[var2].equals("-bootclasspath")) {
                     if (var2 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", "-bootclasspath");
                        this.usage();
                        return false;
                     }

                     if (this.sysClassPathArg != null) {
                        this.error("rmic.option.already.seen", "-bootclasspath");
                        this.usage();
                        return false;
                     }

                     var1[var2] = null;
                     ++var2;
                     this.sysClassPathArg = var1[var2];
                     var1[var2] = null;
                  } else if (var1[var2].equals("-extdirs")) {
                     if (var2 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", "-extdirs");
                        this.usage();
                        return false;
                     }

                     if (this.extDirsArg != null) {
                        this.error("rmic.option.already.seen", "-extdirs");
                        this.usage();
                        return false;
                     }

                     var1[var2] = null;
                     ++var2;
                     this.extDirsArg = var1[var2];
                     var1[var2] = null;
                  } else if (var1[var2].equals("-d")) {
                     if (var2 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", "-d");
                        this.usage();
                        return false;
                     }

                     if (this.destDir != null) {
                        this.error("rmic.option.already.seen", "-d");
                        this.usage();
                        return false;
                     }

                     var1[var2] = null;
                     ++var2;
                     this.destDir = new File(var1[var2]);
                     var1[var2] = null;
                     if (!this.destDir.exists()) {
                        this.error("rmic.no.such.directory", this.destDir.getPath());
                        this.usage();
                        return false;
                     }
                  } else if (!this.checkGeneratorArg(var1, var2)) {
                     this.usage();
                     return false;
                  }
               } else {
                  this.keepGenerated = true;
                  var1[var2] = null;
               }
            }
         }

         for(var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] != null) {
               if (var1[var2].startsWith("-")) {
                  this.error("rmic.no.such.option", var1[var2]);
                  this.usage();
                  return false;
               }

               this.classes.addElement(var1[var2]);
            }
         }

         if (this.generators.size() == 0) {
            this.addGenerator("default");
         }

         return true;
      }
   }

   protected boolean checkGeneratorArg(String[] var1, int var2) {
      boolean var3 = true;
      if (var1[var2].startsWith("-")) {
         String var4 = var1[var2].substring(1).toLowerCase();

         for(int var5 = 0; var5 < this.generatorArgs.length; ++var5) {
            if (var4.equalsIgnoreCase(this.generatorArgs[var5])) {
               Generator var6 = this.addGenerator(var4);
               if (var6 == null) {
                  return false;
               }

               var3 = var6.parseArgs(var1, this);
               break;
            }
         }
      }

      return var3;
   }

   protected Generator addGenerator(String var1) {
      String var3 = getString("generator.class." + var1);
      if (var3 == null) {
         this.error("rmic.missing.property", var1);
         return null;
      } else {
         Generator var2;
         try {
            var2 = (Generator)Class.forName(var3).newInstance();
         } catch (Exception var8) {
            this.error("rmic.cannot.instantiate", var3);
            return null;
         }

         this.generators.addElement(var2);
         Class var4 = BatchEnvironment.class;
         String var5 = getString("generator.env." + var1);
         if (var5 != null) {
            try {
               var4 = Class.forName(var5);
               if (this.environmentClass.isAssignableFrom(var4)) {
                  this.environmentClass = var4.asSubclass(BatchEnvironment.class);
               } else if (!var4.isAssignableFrom(this.environmentClass)) {
                  this.error("rmic.cannot.use.both", this.environmentClass.getName(), var4.getName());
                  return null;
               }
            } catch (ClassNotFoundException var7) {
               this.error("rmic.class.not.found", var5);
               return null;
            }
         }

         if (var1.equals("iiop")) {
            this.iiopGeneration = true;
         }

         return var2;
      }
   }

   protected String[] getArray(String var1, boolean var2) {
      String[] var3 = null;
      String var4 = getString(var1);
      if (var4 == null) {
         if (var2) {
            this.error("rmic.resource.not.found", var1);
            return null;
         } else {
            return new String[0];
         }
      } else {
         StringTokenizer var5 = new StringTokenizer(var4, ", \t\n\r", false);
         int var6 = var5.countTokens();
         var3 = new String[var6];

         for(int var7 = 0; var7 < var6; ++var7) {
            var3[var7] = var5.nextToken();
         }

         return var3;
      }
   }

   public BatchEnvironment getEnv() {
      ClassPath var1 = BatchEnvironment.createClassPath(this.classPathString, this.sysClassPathArg, this.extDirsArg);
      BatchEnvironment var2 = null;

      try {
         Class[] var3 = new Class[]{OutputStream.class, ClassPath.class, Main.class};
         Object[] var4 = new Object[]{this.out, var1, this};
         Constructor var5 = this.environmentClass.getConstructor(var3);
         var2 = (BatchEnvironment)var5.newInstance(var4);
         var2.reset();
      } catch (Exception var6) {
         this.error("rmic.cannot.instantiate", this.environmentClass.getName());
      }

      return var2;
   }

   public boolean doCompile() {
      BatchEnvironment var1 = this.getEnv();
      var1.flags |= this.flags;
      var1.majorVersion = 45;
      var1.minorVersion = 3;
      String var2 = getText("rmic.no.memory");
      String var3 = getText("rmic.stack.overflow");

      try {
         for(int var4 = this.classes.size() - 1; var4 >= 0; --var4) {
            Identifier var5 = Identifier.lookup((String)this.classes.elementAt(var4));
            var5 = var1.resolvePackageQualifiedName(var5);
            var5 = Names.mangleClass(var5);
            ClassDeclaration var6 = var1.getClassDeclaration(var5);

            try {
               ClassDefinition var7 = var6.getClassDefinition(var1);

               for(int var8 = 0; var8 < this.generators.size(); ++var8) {
                  Generator var9 = (Generator)this.generators.elementAt(var8);
                  var9.generate(var1, var7, this.destDir);
               }
            } catch (ClassNotFound var10) {
               var1.error(0L, "rmic.class.not.found", var5);
            }
         }

         if (!this.nocompile) {
            this.compileAllClasses(var1);
         }
      } catch (OutOfMemoryError var11) {
         var1.output(var2);
         return false;
      } catch (StackOverflowError var12) {
         var1.output(var3);
         return false;
      } catch (Error var13) {
         if (var1.nerrors == 0 || var1.dump()) {
            var1.error(0L, "fatal.error");
            var13.printStackTrace(this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out, true));
         }
      } catch (Exception var14) {
         if (var1.nerrors == 0 || var1.dump()) {
            var1.error(0L, "fatal.exception");
            var14.printStackTrace(this.out instanceof PrintStream ? (PrintStream)this.out : new PrintStream(this.out, true));
         }
      }

      var1.flushErrors();
      boolean var15 = true;
      if (var1.nerrors > 0) {
         String var16 = "";
         if (var1.nerrors > 1) {
            var16 = getText("rmic.errors", var1.nerrors);
         } else {
            var16 = getText("rmic.1error");
         }

         if (var1.nwarnings > 0) {
            if (var1.nwarnings > 1) {
               var16 = var16 + ", " + getText("rmic.warnings", var1.nwarnings);
            } else {
               var16 = var16 + ", " + getText("rmic.1warning");
            }
         }

         this.output(var16);
         var15 = false;
      } else if (var1.nwarnings > 0) {
         if (var1.nwarnings > 1) {
            this.output(getText("rmic.warnings", var1.nwarnings));
         } else {
            this.output(getText("rmic.1warning"));
         }
      }

      if (!this.keepGenerated) {
         var1.deleteGeneratedFiles();
      }

      if (var1.verbose()) {
         this.tm = System.currentTimeMillis() - this.tm;
         this.output(getText("rmic.done_in", Long.toString(this.tm)));
      }

      var1.shutdown();
      this.sourcePathArg = null;
      this.sysClassPathArg = null;
      this.extDirsArg = null;
      this.classPathString = null;
      this.destDir = null;
      this.classes = null;
      this.generatorArgs = null;
      this.generators = null;
      this.environmentClass = null;
      this.program = null;
      this.out = null;
      return var15;
   }

   public void compileAllClasses(BatchEnvironment var1) throws ClassNotFound, IOException, InterruptedException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(4096);

      boolean var3;
      do {
         var3 = true;

         ClassDeclaration var5;
         for(Enumeration var4 = var1.getClasses(); var4.hasMoreElements(); var3 = this.compileClass(var5, var2, var1)) {
            var5 = (ClassDeclaration)var4.nextElement();
         }
      } while(!var3);

   }

   public boolean compileClass(ClassDeclaration var1, ByteArrayOutputStream var2, BatchEnvironment var3) throws ClassNotFound, IOException, InterruptedException {
      boolean var4 = true;
      var3.flushErrors();
      SourceClass var5;
      switch (var1.getStatus()) {
         case 0:
            if (!var3.dependencies()) {
               break;
            }
         case 3:
            var4 = false;
            var3.loadDefinition(var1);
            if (var1.getStatus() != 4) {
               break;
            }
         case 4:
            if (var1.getClassDefinition().isInsideLocal()) {
               break;
            }

            if (this.nocompile) {
               throw new IOException("Compilation required, but -Xnocompile option in effect");
            }

            var4 = false;
            var5 = (SourceClass)var1.getClassDefinition(var3);
            var5.check(var3);
            var1.setDefinition(var5, 5);
         case 5:
            var5 = (SourceClass)var1.getClassDefinition(var3);
            if (var5.getError()) {
               var1.setDefinition(var5, 6);
            } else {
               var4 = false;
               var2.reset();
               var5.compile(var2);
               var1.setDefinition(var5, 6);
               var5.cleanup(var3);
               if (!var5.getError() && !this.nowrite) {
                  String var6 = var1.getName().getQualifier().toString().replace('.', File.separatorChar);
                  String var7 = var1.getName().getFlatName().toString().replace('.', '$') + ".class";
                  File var8;
                  if (this.destDir != null) {
                     if (var6.length() > 0) {
                        var8 = new File(this.destDir, var6);
                        if (!var8.exists()) {
                           var8.mkdirs();
                        }

                        var8 = new File(var8, var7);
                     } else {
                        var8 = new File(this.destDir, var7);
                     }
                  } else {
                     ClassFile var9 = (ClassFile)var5.getSource();
                     if (var9.isZipped()) {
                        var3.error(0L, "cant.write", var9.getPath());
                        return var4;
                     }

                     var8 = new File(var9.getPath());
                     var8 = new File(var8.getParent(), var7);
                  }

                  try {
                     FileOutputStream var11 = new FileOutputStream(var8.getPath());
                     var2.writeTo(var11);
                     var11.close();
                     if (var3.verbose()) {
                        this.output(getText("rmic.wrote", var8.getPath()));
                     }
                  } catch (IOException var10) {
                     var3.error(0L, "cant.write", var8.getPath());
                  }
               }
            }
         case 1:
         case 2:
      }

      return var4;
   }

   public static void main(String[] var0) {
      Main var1 = new Main(System.out, "rmic");
      System.exit(var1.compile(var0) ? 0 : 1);
   }

   public static String getString(String var0) {
      if (!resourcesInitialized) {
         initResources();
      }

      if (resourcesExt != null) {
         try {
            return resourcesExt.getString(var0);
         } catch (MissingResourceException var3) {
         }
      }

      try {
         return resources.getString(var0);
      } catch (MissingResourceException var2) {
         return null;
      }
   }

   private static void initResources() {
      try {
         resources = ResourceBundle.getBundle("sun.rmi.rmic.resources.rmic");
         resourcesInitialized = true;

         try {
            resourcesExt = ResourceBundle.getBundle("sun.rmi.rmic.resources.rmicext");
         } catch (MissingResourceException var1) {
         }

      } catch (MissingResourceException var2) {
         throw new Error("fatal: missing resource bundle: " + var2.getClassName());
      }
   }

   public static String getText(String var0) {
      String var1 = getString(var0);
      if (var1 == null) {
         var1 = "no text found: \"" + var0 + "\"";
      }

      return var1;
   }

   public static String getText(String var0, int var1) {
      return getText(var0, Integer.toString(var1), (String)null, (String)null);
   }

   public static String getText(String var0, String var1) {
      return getText(var0, var1, (String)null, (String)null);
   }

   public static String getText(String var0, String var1, String var2) {
      return getText(var0, var1, var2, (String)null);
   }

   public static String getText(String var0, String var1, String var2, String var3) {
      String var4 = getString(var0);
      if (var4 == null) {
         var4 = "no text found: key = \"" + var0 + "\", arguments = \"{0}\", \"{1}\", \"{2}\"";
      }

      String[] var5 = new String[]{var1 != null ? var1 : "null", var2 != null ? var2 : "null", var3 != null ? var3 : "null"};
      return MessageFormat.format(var4, (Object[])var5);
   }
}
