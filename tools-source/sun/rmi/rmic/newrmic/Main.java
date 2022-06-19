package sun.rmi.rmic.newrmic;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.rmi.rmic.newrmic.jrmp.JrmpGenerator;
import sun.tools.util.CommandLine;

public class Main {
   private static final Object batchCountLock = new Object();
   private static long batchCount = 0L;
   private static final Map batchTable = Collections.synchronizedMap(new HashMap());
   private final PrintStream out;
   private final String program;

   public static void main(String[] var0) {
      Main var1 = new Main(System.err, "rmic");
      System.exit(var1.compile(var0) ? 0 : 1);
   }

   public Main(OutputStream var1, String var2) {
      this.out = var1 instanceof PrintStream ? (PrintStream)var1 : new PrintStream(var1);
      this.program = var2;
   }

   public boolean compile(String[] var1) {
      long var2 = System.currentTimeMillis();
      long var4;
      synchronized(batchCountLock) {
         var4 = (long)(batchCount++);
      }

      Batch var6 = this.parseArgs(var1);
      if (var6 == null) {
         return false;
      } else {
         boolean var7;
         try {
            batchTable.put(var4, var6);
            var7 = this.invokeJavadoc(var6, var4);
         } finally {
            batchTable.remove(var4);
         }

         if (var6.verbose) {
            long var8 = System.currentTimeMillis() - var2;
            this.output(Resources.getText("rmic.done_in", Long.toString(var8)));
         }

         return var7;
      }
   }

   public void output(String var1) {
      this.out.println(var1);
   }

   public void error(String var1, String... var2) {
      this.output(Resources.getText(var1, var2));
   }

   public void usage() {
      this.error("rmic.usage", this.program);
   }

   private Batch parseArgs(String[] var1) {
      Batch var2 = new Batch();

      try {
         var1 = CommandLine.parse(var1);
      } catch (FileNotFoundException var5) {
         this.error("rmic.cant.read", var5.getMessage());
         return null;
      } catch (IOException var6) {
         var6.printStackTrace(this.out);
         return null;
      }

      int var3;
      for(var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null) {
            if (var1[var3].equals("-Xnew")) {
               var1[var3] = null;
            } else {
               if (var1[var3].equals("-show")) {
                  this.error("rmic.option.unsupported", var1[var3]);
                  this.usage();
                  return null;
               }

               if (var1[var3].equals("-O")) {
                  this.error("rmic.option.unsupported", var1[var3]);
                  var1[var3] = null;
               } else if (var1[var3].equals("-debug")) {
                  this.error("rmic.option.unsupported", var1[var3]);
                  var1[var3] = null;
               } else if (var1[var3].equals("-depend")) {
                  this.error("rmic.option.unsupported", var1[var3]);
                  var1[var3] = null;
               } else if (!var1[var3].equals("-keep") && !var1[var3].equals("-keepgenerated")) {
                  if (var1[var3].equals("-g")) {
                     var2.debug = true;
                     var1[var3] = null;
                  } else if (var1[var3].equals("-nowarn")) {
                     var2.noWarn = true;
                     var1[var3] = null;
                  } else if (var1[var3].equals("-nowrite")) {
                     var2.noWrite = true;
                     var1[var3] = null;
                  } else if (var1[var3].equals("-verbose")) {
                     var2.verbose = true;
                     var1[var3] = null;
                  } else if (var1[var3].equals("-Xnocompile")) {
                     var2.noCompile = true;
                     var2.keepGenerated = true;
                     var1[var3] = null;
                  } else if (var1[var3].equals("-bootclasspath")) {
                     if (var3 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", var1[var3]);
                        this.usage();
                        return null;
                     }

                     if (var2.bootClassPath != null) {
                        this.error("rmic.option.already.seen", var1[var3]);
                        this.usage();
                        return null;
                     }

                     var1[var3] = null;
                     ++var3;
                     var2.bootClassPath = var1[var3];

                     assert var2.bootClassPath != null;

                     var1[var3] = null;
                  } else if (var1[var3].equals("-extdirs")) {
                     if (var3 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", var1[var3]);
                        this.usage();
                        return null;
                     }

                     if (var2.extDirs != null) {
                        this.error("rmic.option.already.seen", var1[var3]);
                        this.usage();
                        return null;
                     }

                     var1[var3] = null;
                     ++var3;
                     var2.extDirs = var1[var3];

                     assert var2.extDirs != null;

                     var1[var3] = null;
                  } else if (var1[var3].equals("-classpath")) {
                     if (var3 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", var1[var3]);
                        this.usage();
                        return null;
                     }

                     if (var2.classPath != null) {
                        this.error("rmic.option.already.seen", var1[var3]);
                        this.usage();
                        return null;
                     }

                     var1[var3] = null;
                     ++var3;
                     var2.classPath = var1[var3];

                     assert var2.classPath != null;

                     var1[var3] = null;
                  } else if (var1[var3].equals("-d")) {
                     if (var3 + 1 >= var1.length) {
                        this.error("rmic.option.requires.argument", var1[var3]);
                        this.usage();
                        return null;
                     }

                     if (var2.destDir != null) {
                        this.error("rmic.option.already.seen", var1[var3]);
                        this.usage();
                        return null;
                     }

                     var1[var3] = null;
                     ++var3;
                     var2.destDir = new File(var1[var3]);

                     assert var2.destDir != null;

                     var1[var3] = null;
                     if (!var2.destDir.exists()) {
                        this.error("rmic.no.such.directory", var2.destDir.getPath());
                        this.usage();
                        return null;
                     }
                  } else if (!var1[var3].equals("-v1.1") && !var1[var3].equals("-vcompat") && !var1[var3].equals("-v1.2")) {
                     if (var1[var3].equalsIgnoreCase("-iiop")) {
                        this.error("rmic.option.unimplemented", var1[var3]);
                        return null;
                     }

                     if (var1[var3].equalsIgnoreCase("-idl")) {
                        this.error("rmic.option.unimplemented", var1[var3]);
                        return null;
                     }

                     if (var1[var3].equalsIgnoreCase("-xprint")) {
                        this.error("rmic.option.unimplemented", var1[var3]);
                        return null;
                     }
                  } else {
                     JrmpGenerator var4 = new JrmpGenerator();
                     var2.generators.add(var4);
                     if (!var4.parseArgs(var1, this)) {
                        return null;
                     }
                  }
               } else {
                  var2.keepGenerated = true;
                  var1[var3] = null;
               }
            }
         }
      }

      for(var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] != null) {
            if (var1[var3].startsWith("-")) {
               this.error("rmic.no.such.option", var1[var3]);
               this.usage();
               return null;
            }

            var2.classes.add(var1[var3]);
         }
      }

      if (var2.classes.isEmpty()) {
         this.usage();
         return null;
      } else {
         if (var2.generators.isEmpty()) {
            var2.generators.add(new JrmpGenerator());
         }

         return var2;
      }
   }

   public static boolean start(RootDoc var0) {
      long var1 = -1L;
      String[][] var3 = var0.options();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String[] var6 = var3[var5];
         if (var6[0].equals("-batchID")) {
            try {
               var1 = Long.parseLong(var6[1]);
            } catch (NumberFormatException var15) {
               throw new AssertionError(var15);
            }
         }
      }

      Batch var17 = (Batch)batchTable.get(var1);

      assert var17 != null;

      BatchEnvironment var18;
      try {
         Constructor var19 = var17.envClass.getConstructor(RootDoc.class);
         var18 = (BatchEnvironment)var19.newInstance(var0);
      } catch (NoSuchMethodException var11) {
         throw new AssertionError(var11);
      } catch (IllegalAccessException var12) {
         throw new AssertionError(var12);
      } catch (InstantiationException var13) {
         throw new AssertionError(var13);
      } catch (InvocationTargetException var14) {
         throw new AssertionError(var14);
      }

      var18.setVerbose(var17.verbose);
      File var20 = var17.destDir;
      if (var20 == null) {
         var20 = new File(System.getProperty("user.dir"));
      }

      Iterator var21 = var17.classes.iterator();

      while(var21.hasNext()) {
         String var7 = (String)var21.next();
         ClassDoc var8 = var0.classNamed(var7);

         try {
            Iterator var9 = var17.generators.iterator();

            while(var9.hasNext()) {
               Generator var10 = (Generator)var9.next();
               var10.generate(var18, var8, var20);
            }
         } catch (NullPointerException var16) {
         }
      }

      boolean var22 = true;
      List var23 = var18.generatedFiles();
      if (!var17.noCompile && !var17.noWrite && !var23.isEmpty()) {
         var22 = var17.enclosingMain().invokeJavac(var17, var23);
      }

      if (!var17.keepGenerated) {
         Iterator var24 = var23.iterator();

         while(var24.hasNext()) {
            File var25 = (File)var24.next();
            var25.delete();
         }
      }

      return var22;
   }

   public static int optionLength(String var0) {
      return var0.equals("-batchID") ? 2 : 0;
   }

   private boolean invokeJavadoc(Batch var1, long var2) {
      ArrayList var4 = new ArrayList();
      var4.add("-private");
      var4.add("-Xclasses");
      if (var1.verbose) {
         var4.add("-verbose");
      }

      if (var1.bootClassPath != null) {
         var4.add("-bootclasspath");
         var4.add(var1.bootClassPath);
      }

      if (var1.extDirs != null) {
         var4.add("-extdirs");
         var4.add(var1.extDirs);
      }

      if (var1.classPath != null) {
         var4.add("-classpath");
         var4.add(var1.classPath);
      }

      var4.add("-batchID");
      var4.add(Long.toString(var2));
      HashSet var5 = new HashSet();
      Iterator var6 = var1.generators.iterator();

      while(var6.hasNext()) {
         Generator var7 = (Generator)var6.next();
         var5.addAll(var7.bootstrapClassNames());
      }

      var5.addAll(var1.classes);
      var6 = var5.iterator();

      while(var6.hasNext()) {
         String var9 = (String)var6.next();
         var4.add(var9);
      }

      int var8 = com.sun.tools.javadoc.Main.execute(this.program, new PrintWriter(this.out, true), new PrintWriter(this.out, true), new PrintWriter(this.out, true), this.getClass().getName(), (String[])var4.toArray(new String[var4.size()]));
      return var8 == 0;
   }

   private boolean invokeJavac(Batch var1, List var2) {
      ArrayList var3 = new ArrayList();
      var3.add("-nowarn");
      if (var1.debug) {
         var3.add("-g");
      }

      if (var1.verbose) {
         var3.add("-verbose");
      }

      if (var1.bootClassPath != null) {
         var3.add("-bootclasspath");
         var3.add(var1.bootClassPath);
      }

      if (var1.extDirs != null) {
         var3.add("-extdirs");
         var3.add(var1.extDirs);
      }

      if (var1.classPath != null) {
         var3.add("-classpath");
         var3.add(var1.classPath);
      }

      var3.add("-source");
      var3.add("1.3");
      var3.add("-target");
      var3.add("1.1");
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         File var5 = (File)var4.next();
         var3.add(var5.getPath());
      }

      int var6 = com.sun.tools.javac.Main.compile((String[])var3.toArray(new String[var3.size()]), new PrintWriter(this.out, true));
      return var6 == 0;
   }

   private class Batch {
      boolean keepGenerated = false;
      boolean debug = false;
      boolean noWarn = false;
      boolean noWrite = false;
      boolean verbose = false;
      boolean noCompile = false;
      String bootClassPath = null;
      String extDirs = null;
      String classPath = null;
      File destDir = null;
      List generators = new ArrayList();
      Class envClass = BatchEnvironment.class;
      List classes = new ArrayList();

      Batch() {
      }

      Main enclosingMain() {
         return Main.this;
      }
   }
}
