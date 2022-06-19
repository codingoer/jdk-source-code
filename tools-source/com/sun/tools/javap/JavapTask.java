package com.sun.tools.javap;

import com.sun.tools.classfile.Attribute;
import com.sun.tools.classfile.Attributes;
import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;
import com.sun.tools.classfile.ConstantPoolException;
import com.sun.tools.classfile.Field;
import com.sun.tools.classfile.InnerClasses_attribute;
import com.sun.tools.classfile.Method;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class JavapTask implements DisassemblerTool.DisassemblerTask, Messages {
   static final Option[] recognizedOptions = new Option[]{new Option(false, new String[]{"-help", "--help", "-?"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.help = true;
      }
   }, new Option(false, new String[]{"-version"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.version = true;
      }
   }, new Option(false, new String[]{"-fullversion"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.fullVersion = true;
      }
   }, new Option(false, new String[]{"-v", "-verbose", "-all"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.verbose = true;
         var1.options.showDescriptors = true;
         var1.options.showFlags = true;
         var1.options.showAllAttrs = true;
      }
   }, new Option(false, new String[]{"-l"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.showLineAndLocalVariableTables = true;
      }
   }, new Option(false, new String[]{"-public"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.accessOptions.add(var2);
         var1.options.showAccess = 1;
      }
   }, new Option(false, new String[]{"-protected"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.accessOptions.add(var2);
         var1.options.showAccess = 4;
      }
   }, new Option(false, new String[]{"-package"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.accessOptions.add(var2);
         var1.options.showAccess = 0;
      }
   }, new Option(false, new String[]{"-p", "-private"}) {
      void process(JavapTask var1, String var2, String var3) {
         if (!var1.options.accessOptions.contains("-p") && !var1.options.accessOptions.contains("-private")) {
            var1.options.accessOptions.add(var2);
         }

         var1.options.showAccess = 2;
      }
   }, new Option(false, new String[]{"-c"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.showDisassembled = true;
      }
   }, new Option(false, new String[]{"-s"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.showDescriptors = true;
      }
   }, new Option(false, new String[]{"-sysinfo"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.sysInfo = true;
      }
   }, new Option(false, new String[]{"-XDdetails"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.details = EnumSet.allOf(InstructionDetailWriter.Kind.class);
      }
   }, new Option(false, new String[]{"-XDdetails:"}) {
      boolean matches(String var1) {
         int var2 = var1.indexOf(":");
         return var2 != -1 && super.matches(var1.substring(0, var2 + 1));
      }

      void process(JavapTask var1, String var2, String var3) throws BadArgs {
         int var4 = var2.indexOf(":");
         String[] var5 = var2.substring(var4 + 1).split("[,: ]+");
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (!this.handleArg(var1, var8)) {
               throw var1.new BadArgs("err.invalid.arg.for.option", new Object[]{var8});
            }
         }

      }

      boolean handleArg(JavapTask var1, String var2) {
         if (var2.length() == 0) {
            return true;
         } else if (var2.equals("all")) {
            var1.options.details = EnumSet.allOf(InstructionDetailWriter.Kind.class);
            return true;
         } else {
            boolean var3 = true;
            if (var2.startsWith("-")) {
               var3 = false;
               var2 = var2.substring(1);
            }

            InstructionDetailWriter.Kind[] var4 = InstructionDetailWriter.Kind.values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               InstructionDetailWriter.Kind var7 = var4[var6];
               if (var2.equalsIgnoreCase(var7.option)) {
                  if (var3) {
                     var1.options.details.add(var7);
                  } else {
                     var1.options.details.remove(var7);
                  }

                  return true;
               }
            }

            return false;
         }
      }
   }, new Option(false, new String[]{"-constants"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.showConstants = true;
      }
   }, new Option(false, new String[]{"-XDinner"}) {
      void process(JavapTask var1, String var2, String var3) {
         var1.options.showInnerClasses = true;
      }
   }, new Option(false, new String[]{"-XDindent:"}) {
      boolean matches(String var1) {
         int var2 = var1.indexOf(":");
         return var2 != -1 && super.matches(var1.substring(0, var2 + 1));
      }

      void process(JavapTask var1, String var2, String var3) throws BadArgs {
         int var4 = var2.indexOf(":");

         try {
            int var5 = Integer.valueOf(var2.substring(var4 + 1));
            if (var5 > 0) {
               var1.options.indentWidth = var5;
            }
         } catch (NumberFormatException var6) {
         }

      }
   }, new Option(false, new String[]{"-XDtab:"}) {
      boolean matches(String var1) {
         int var2 = var1.indexOf(":");
         return var2 != -1 && super.matches(var1.substring(0, var2 + 1));
      }

      void process(JavapTask var1, String var2, String var3) throws BadArgs {
         int var4 = var2.indexOf(":");

         try {
            int var5 = Integer.valueOf(var2.substring(var4 + 1));
            if (var5 > 0) {
               var1.options.tabColumn = var5;
            }
         } catch (NumberFormatException var6) {
         }

      }
   }};
   static final int EXIT_OK = 0;
   static final int EXIT_ERROR = 1;
   static final int EXIT_CMDERR = 2;
   static final int EXIT_SYSERR = 3;
   static final int EXIT_ABNORMAL = 4;
   private static final String nl = System.getProperty("line.separator");
   private static final String versionRBName = "com.sun.tools.javap.resources.version";
   private static ResourceBundle versionRB;
   protected Context context;
   JavaFileManager fileManager;
   JavaFileManager defaultFileManager;
   PrintWriter log;
   DiagnosticListener diagnosticListener;
   List classes;
   Options options;
   Locale task_locale;
   Map bundles;
   protected Attribute.Factory attributeFactory;
   private static final String progname = "javap";

   public JavapTask() {
      this.context = new Context();
      this.context.put(Messages.class, this);
      this.options = Options.instance(this.context);
      this.attributeFactory = new Attribute.Factory();
   }

   public JavapTask(Writer var1, JavaFileManager var2, DiagnosticListener var3) {
      this();
      this.log = getPrintWriterForWriter(var1);
      this.fileManager = var2;
      this.diagnosticListener = var3;
   }

   public JavapTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5) {
      this(var1, var2, var3);
      this.classes = new ArrayList();
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
         var7.getClass();
         this.classes.add(var7);
      }

      try {
         if (var4 != null) {
            this.handleOptions(var4, false);
         }

      } catch (BadArgs var8) {
         throw new IllegalArgumentException(var8.getMessage());
      }
   }

   public void setLocale(Locale var1) {
      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      this.task_locale = var1;
   }

   public void setLog(Writer var1) {
      this.log = getPrintWriterForWriter(var1);
   }

   public void setLog(OutputStream var1) {
      this.setLog((Writer)getPrintWriterForStream(var1));
   }

   private static PrintWriter getPrintWriterForStream(OutputStream var0) {
      return new PrintWriter((OutputStream)(var0 == null ? System.err : var0), true);
   }

   private static PrintWriter getPrintWriterForWriter(Writer var0) {
      if (var0 == null) {
         return getPrintWriterForStream((OutputStream)null);
      } else {
         return var0 instanceof PrintWriter ? (PrintWriter)var0 : new PrintWriter(var0, true);
      }
   }

   public void setDiagnosticListener(DiagnosticListener var1) {
      this.diagnosticListener = var1;
   }

   public void setDiagnosticListener(OutputStream var1) {
      this.setDiagnosticListener(this.getDiagnosticListenerForStream(var1));
   }

   private DiagnosticListener getDiagnosticListenerForStream(OutputStream var1) {
      return this.getDiagnosticListenerForWriter(getPrintWriterForStream(var1));
   }

   private DiagnosticListener getDiagnosticListenerForWriter(Writer var1) {
      final PrintWriter var2 = getPrintWriterForWriter(var1);
      return new DiagnosticListener() {
         public void report(Diagnostic var1) {
            switch (var1.getKind()) {
               case ERROR:
                  var2.print(JavapTask.this.getMessage("err.prefix"));
                  break;
               case WARNING:
                  var2.print(JavapTask.this.getMessage("warn.prefix"));
                  break;
               case NOTE:
                  var2.print(JavapTask.this.getMessage("note.prefix"));
            }

            var2.print(" ");
            var2.println(var1.getMessage((Locale)null));
         }
      };
   }

   int run(String[] var1) {
      int var2;
      try {
         this.handleOptions(var1);
         if (this.classes == null || this.classes.size() == 0) {
            byte var24;
            if (!this.options.help && !this.options.version && !this.options.fullVersion) {
               var24 = 2;
               return var24;
            }

            var24 = 0;
            return var24;
         }

         try {
            var2 = this.run();
         } finally {
            if (this.defaultFileManager != null) {
               try {
                  this.defaultFileManager.close();
                  this.defaultFileManager = null;
               } catch (IOException var19) {
                  throw new InternalError(var19, new Object[0]);
               }
            }

         }
      } catch (BadArgs var21) {
         this.reportError(var21.key, var21.args);
         if (var21.showUsage) {
            this.printLines(this.getMessage("main.usage.summary", "javap"));
         }

         byte var25 = 2;
         return var25;
      } catch (InternalError var22) {
         Object[] var3;
         if (var22.getCause() == null) {
            var3 = var22.args;
         } else {
            var3 = new Object[var22.args.length + 1];
            var3[0] = var22.getCause();
            System.arraycopy(var22.args, 0, var3, 1, var22.args.length);
         }

         this.reportError("err.internal.error", var3);
         byte var4 = 4;
         return var4;
      } finally {
         this.log.flush();
      }

      return var2;
   }

   public void handleOptions(String[] var1) throws BadArgs {
      this.handleOptions(Arrays.asList(var1), true);
   }

   private void handleOptions(Iterable var1, boolean var2) throws BadArgs {
      if (this.log == null) {
         this.log = getPrintWriterForStream(System.out);
         if (this.diagnosticListener == null) {
            this.diagnosticListener = this.getDiagnosticListenerForStream(System.err);
         }
      } else if (this.diagnosticListener == null) {
         this.diagnosticListener = this.getDiagnosticListenerForWriter(this.log);
      }

      if (this.fileManager == null) {
         this.fileManager = this.getDefaultFileManager(this.diagnosticListener, this.log);
      }

      Iterator var3 = var1.iterator();
      boolean var4 = !var3.hasNext();

      while(true) {
         while(var3.hasNext()) {
            String var5 = (String)var3.next();
            if (var5.startsWith("-")) {
               this.handleOption(var5, var3);
            } else {
               if (!var2) {
                  throw (new BadArgs("err.unknown.option", new Object[]{var5})).showUsage(true);
               }

               if (this.classes == null) {
                  this.classes = new ArrayList();
               }

               this.classes.add(var5);

               while(var3.hasNext()) {
                  this.classes.add(var3.next());
               }
            }
         }

         if (this.options.accessOptions.size() > 1) {
            StringBuilder var8 = new StringBuilder();

            String var7;
            for(Iterator var6 = this.options.accessOptions.iterator(); var6.hasNext(); var8.append(var7)) {
               var7 = (String)var6.next();
               if (var8.length() > 0) {
                  var8.append(" ");
               }
            }

            throw new BadArgs("err.incompatible.options", new Object[]{var8});
         }

         if ((this.classes == null || this.classes.size() == 0) && !var4 && !this.options.help && !this.options.version && !this.options.fullVersion) {
            throw new BadArgs("err.no.classes.specified", new Object[0]);
         }

         if (var4 || this.options.help) {
            this.showHelp();
         }

         if (this.options.version || this.options.fullVersion) {
            this.showVersion(this.options.fullVersion);
         }

         return;
      }
   }

   private void handleOption(String var1, Iterator var2) throws BadArgs {
      Option[] var3 = recognizedOptions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Option var6 = var3[var5];
         if (var6.matches(var1)) {
            if (var6.hasArg) {
               if (!var2.hasNext()) {
                  throw (new BadArgs("err.missing.arg", new Object[]{var1})).showUsage(true);
               }

               var6.process(this, var1, (String)var2.next());
            } else {
               var6.process(this, var1, (String)null);
            }

            if (var6.ignoreRest()) {
               while(var2.hasNext()) {
                  var2.next();
               }
            }

            return;
         }
      }

      try {
         if (this.fileManager.handleOption(var1, var2)) {
            return;
         }
      } catch (IllegalArgumentException var7) {
         throw (new BadArgs("err.invalid.use.of.option", new Object[]{var1})).showUsage(true);
      }

      throw (new BadArgs("err.unknown.option", new Object[]{var1})).showUsage(true);
   }

   public Boolean call() {
      return this.run() == 0;
   }

   public int run() {
      if (this.classes != null && !this.classes.isEmpty()) {
         this.context.put(PrintWriter.class, this.log);
         ClassWriter var1 = ClassWriter.instance(this.context);
         SourceWriter var2 = SourceWriter.instance(this.context);
         var2.setFileManager(this.fileManager);
         int var3 = 0;
         Iterator var4 = this.classes.iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();

            try {
               var3 = this.writeClass(var1, var5);
            } catch (ConstantPoolException var9) {
               this.reportError("err.bad.constant.pool", var5, var9.getLocalizedMessage());
               var3 = 1;
            } catch (EOFException var10) {
               this.reportError("err.end.of.file", var5);
               var3 = 1;
            } catch (FileNotFoundException var11) {
               this.reportError("err.file.not.found", var11.getLocalizedMessage());
               var3 = 1;
            } catch (IOException var12) {
               Object var14 = var12.getLocalizedMessage();
               if (var14 == null) {
                  var14 = var12;
               }

               this.reportError("err.ioerror", var5, var14);
               var3 = 1;
            } catch (Throwable var13) {
               StringWriter var7 = new StringWriter();
               PrintWriter var8 = new PrintWriter(var7);
               var13.printStackTrace(var8);
               var8.close();
               this.reportError("err.crash", var13.toString(), var7.toString());
               var3 = 4;
            }
         }

         return var3;
      } else {
         return 1;
      }
   }

   protected int writeClass(ClassWriter var1, String var2) throws IOException, ConstantPoolException {
      JavaFileObject var3 = this.open(var2);
      if (var3 == null) {
         this.reportError("err.class.not.found", var2);
         return 1;
      } else {
         ClassFileInfo var4 = this.read(var3);
         if (!var2.endsWith(".class")) {
            String var5 = var4.cf.getName();
            if (!var5.replaceAll("[/$]", ".").equals(var2.replaceAll("[/$]", "."))) {
               this.reportWarning("warn.unexpected.class", var2, var5.replace('/', '.'));
            }
         }

         this.write(var4);
         if (this.options.showInnerClasses) {
            ClassFile var17 = var4.cf;
            Attribute var6 = var17.getAttribute("InnerClasses");
            if (var6 instanceof InnerClasses_attribute) {
               InnerClasses_attribute var7 = (InnerClasses_attribute)var6;

               try {
                  int var8 = 0;

                  for(int var9 = 0; var9 < var7.classes.length; ++var9) {
                     int var10 = var7.classes[var9].outer_class_info_index;
                     ConstantPool.CONSTANT_Class_info var11 = var17.constant_pool.getClassInfo(var10);
                     String var12 = var11.getName();
                     if (var12.equals(var17.getName())) {
                        int var13 = var7.classes[var9].inner_class_info_index;
                        ConstantPool.CONSTANT_Class_info var14 = var17.constant_pool.getClassInfo(var13);
                        String var15 = var14.getName();
                        var1.println("// inner class " + var15.replaceAll("[/$]", "."));
                        var1.println();
                        var8 = this.writeClass(var1, var15);
                        if (var8 != 0) {
                           return var8;
                        }
                     }
                  }

                  return var8;
               } catch (ConstantPoolException var16) {
                  this.reportError("err.bad.innerclasses.attribute", var2);
                  return 1;
               }
            }

            if (var6 != null) {
               this.reportError("err.bad.innerclasses.attribute", var2);
               return 1;
            }
         }

         return 0;
      }
   }

   protected JavaFileObject open(String var1) throws IOException {
      JavaFileObject var2 = this.getClassFileObject(var1);
      if (var2 != null) {
         return var2;
      } else {
         String var3 = var1;

         do {
            int var4;
            if ((var4 = var3.lastIndexOf(".")) == -1) {
               if (!var1.endsWith(".class")) {
                  return null;
               }

               if (this.fileManager instanceof StandardJavaFileManager) {
                  StandardJavaFileManager var5 = (StandardJavaFileManager)this.fileManager;
                  var2 = (JavaFileObject)var5.getJavaFileObjects(new String[]{var1}).iterator().next();
                  if (var2 != null && var2.getLastModified() != 0L) {
                     return var2;
                  }
               }

               if (var1.matches("^[A-Za-z]+:.*")) {
                  try {
                     final URI var10 = new URI(var1);
                     final URL var6 = var10.toURL();
                     final URLConnection var7 = var6.openConnection();
                     return new JavaFileObject() {
                        public JavaFileObject.Kind getKind() {
                           return Kind.CLASS;
                        }

                        public boolean isNameCompatible(String var1, JavaFileObject.Kind var2) {
                           throw new UnsupportedOperationException();
                        }

                        public NestingKind getNestingKind() {
                           throw new UnsupportedOperationException();
                        }

                        public Modifier getAccessLevel() {
                           throw new UnsupportedOperationException();
                        }

                        public URI toUri() {
                           return var10;
                        }

                        public String getName() {
                           return var6.toString();
                        }

                        public InputStream openInputStream() throws IOException {
                           return var7.getInputStream();
                        }

                        public OutputStream openOutputStream() throws IOException {
                           throw new UnsupportedOperationException();
                        }

                        public Reader openReader(boolean var1) throws IOException {
                           throw new UnsupportedOperationException();
                        }

                        public CharSequence getCharContent(boolean var1) throws IOException {
                           throw new UnsupportedOperationException();
                        }

                        public Writer openWriter() throws IOException {
                           throw new UnsupportedOperationException();
                        }

                        public long getLastModified() {
                           return var7.getLastModified();
                        }

                        public boolean delete() {
                           throw new UnsupportedOperationException();
                        }
                     };
                  } catch (URISyntaxException var8) {
                  } catch (IOException var9) {
                  }
               }

               return null;
            }

            var3 = var3.substring(0, var4) + "$" + var3.substring(var4 + 1);
            var2 = this.getClassFileObject(var3);
         } while(var2 == null);

         return var2;
      }
   }

   public ClassFileInfo read(JavaFileObject var1) throws IOException, ConstantPoolException {
      Object var2 = var1.openInputStream();

      ClassFileInfo var8;
      try {
         SizeInputStream var3 = null;
         MessageDigest var4 = null;
         if (this.options.sysInfo || this.options.verbose) {
            try {
               var4 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var12) {
            }

            DigestInputStream var14 = new DigestInputStream((InputStream)var2, var4);
            var2 = var3 = new SizeInputStream(var14);
         }

         ClassFile var5 = ClassFile.read((InputStream)var2, this.attributeFactory);
         byte[] var6 = var4 == null ? null : var4.digest();
         int var7 = var3 == null ? -1 : var3.size();
         var8 = new ClassFileInfo(var1, var5, var6, var7);
      } finally {
         ((InputStream)var2).close();
      }

      return var8;
   }

   public void write(ClassFileInfo var1) {
      ClassWriter var2 = ClassWriter.instance(this.context);
      if (this.options.sysInfo || this.options.verbose) {
         var2.setFile(var1.fo.toUri());
         var2.setLastModified(var1.fo.getLastModified());
         var2.setDigest("MD5", var1.digest);
         var2.setFileSize(var1.size);
      }

      var2.write(var1.cf);
   }

   protected void setClassFile(ClassFile var1) {
      ClassWriter var2 = ClassWriter.instance(this.context);
      var2.setClassFile(var1);
   }

   protected void setMethod(Method var1) {
      ClassWriter var2 = ClassWriter.instance(this.context);
      var2.setMethod(var1);
   }

   protected void write(Attribute var1) {
      AttributeWriter var2 = AttributeWriter.instance(this.context);
      ClassWriter var3 = ClassWriter.instance(this.context);
      ClassFile var4 = var3.getClassFile();
      var2.write(var4, (Attribute)var1, var4.constant_pool);
   }

   protected void write(Attributes var1) {
      AttributeWriter var2 = AttributeWriter.instance(this.context);
      ClassWriter var3 = ClassWriter.instance(this.context);
      ClassFile var4 = var3.getClassFile();
      var2.write(var4, (Attributes)var1, var4.constant_pool);
   }

   protected void write(ConstantPool var1) {
      ConstantWriter var2 = ConstantWriter.instance(this.context);
      var2.writeConstantPool(var1);
   }

   protected void write(ConstantPool var1, int var2) {
      ConstantWriter var3 = ConstantWriter.instance(this.context);
      var3.write(var2);
   }

   protected void write(ConstantPool.CPInfo var1) {
      ConstantWriter var2 = ConstantWriter.instance(this.context);
      var2.println(var1);
   }

   protected void write(Field var1) {
      ClassWriter var2 = ClassWriter.instance(this.context);
      var2.writeField(var1);
   }

   protected void write(Method var1) {
      ClassWriter var2 = ClassWriter.instance(this.context);
      var2.writeMethod(var1);
   }

   private JavaFileManager getDefaultFileManager(DiagnosticListener var1, PrintWriter var2) {
      if (this.defaultFileManager == null) {
         this.defaultFileManager = JavapFileManager.create(var1, var2);
      }

      return this.defaultFileManager;
   }

   private JavaFileObject getClassFileObject(String var1) throws IOException {
      JavaFileObject var2 = this.fileManager.getJavaFileForInput(StandardLocation.PLATFORM_CLASS_PATH, var1, Kind.CLASS);
      if (var2 == null) {
         var2 = this.fileManager.getJavaFileForInput(StandardLocation.CLASS_PATH, var1, Kind.CLASS);
      }

      return var2;
   }

   private void showHelp() {
      this.printLines(this.getMessage("main.usage", "javap"));
      Option[] var1 = recognizedOptions;
      int var2 = var1.length;

      int var3;
      String var5;
      for(var3 = 0; var3 < var2; ++var3) {
         Option var4 = var1[var3];
         var5 = var4.aliases[0].substring(1);
         if (!var5.startsWith("X") && !var5.equals("fullversion") && !var5.equals("h") && !var5.equals("verify")) {
            this.printLines(this.getMessage("main.opt." + var5));
         }
      }

      String[] var7 = new String[]{"-classpath", "-cp", "-bootclasspath"};
      String[] var8 = var7;
      var3 = var7.length;

      for(int var9 = 0; var9 < var3; ++var9) {
         var5 = var8[var9];
         if (this.fileManager.isSupportedOption(var5) != -1) {
            String var6 = var5.substring(1);
            this.printLines(this.getMessage("main.opt." + var6));
         }
      }

   }

   private void showVersion(boolean var1) {
      this.printLines(this.version(var1 ? "full" : "release"));
   }

   private void printLines(String var1) {
      this.log.println(var1.replace("\n", nl));
   }

   private String version(String var1) {
      if (versionRB == null) {
         try {
            versionRB = ResourceBundle.getBundle("com.sun.tools.javap.resources.version");
         } catch (MissingResourceException var4) {
            return this.getMessage("version.resource.missing", System.getProperty("java.version"));
         }
      }

      try {
         return versionRB.getString(var1);
      } catch (MissingResourceException var3) {
         return this.getMessage("version.unknown", System.getProperty("java.version"));
      }
   }

   private void reportError(String var1, Object... var2) {
      this.diagnosticListener.report(this.createDiagnostic(javax.tools.Diagnostic.Kind.ERROR, var1, var2));
   }

   private void reportNote(String var1, Object... var2) {
      this.diagnosticListener.report(this.createDiagnostic(javax.tools.Diagnostic.Kind.NOTE, var1, var2));
   }

   private void reportWarning(String var1, Object... var2) {
      this.diagnosticListener.report(this.createDiagnostic(javax.tools.Diagnostic.Kind.WARNING, var1, var2));
   }

   private Diagnostic createDiagnostic(final Diagnostic.Kind var1, final String var2, final Object... var3) {
      return new Diagnostic() {
         public Diagnostic.Kind getKind() {
            return var1;
         }

         public JavaFileObject getSource() {
            return null;
         }

         public long getPosition() {
            return -1L;
         }

         public long getStartPosition() {
            return -1L;
         }

         public long getEndPosition() {
            return -1L;
         }

         public long getLineNumber() {
            return -1L;
         }

         public long getColumnNumber() {
            return -1L;
         }

         public String getCode() {
            return var2;
         }

         public String getMessage(Locale var1x) {
            return JavapTask.this.getMessage(var1x, var2, var3);
         }

         public String toString() {
            return this.getClass().getName() + "[key=" + var2 + ",args=" + Arrays.asList(var3) + "]";
         }
      };
   }

   public String getMessage(String var1, Object... var2) {
      return this.getMessage(this.task_locale, var1, var2);
   }

   public String getMessage(Locale var1, String var2, Object... var3) {
      if (this.bundles == null) {
         this.bundles = new HashMap();
      }

      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      ResourceBundle var4 = (ResourceBundle)this.bundles.get(var1);
      if (var4 == null) {
         try {
            var4 = ResourceBundle.getBundle("com.sun.tools.javap.resources.javap", var1);
            this.bundles.put(var1, var4);
         } catch (MissingResourceException var7) {
            throw new InternalError(new Object[]{"Cannot find javap resource bundle for locale " + var1});
         }
      }

      try {
         return MessageFormat.format(var4.getString(var2), var3);
      } catch (MissingResourceException var6) {
         throw new InternalError(var6, new Object[]{var2});
      }
   }

   private static class SizeInputStream extends FilterInputStream {
      private int size;

      SizeInputStream(InputStream var1) {
         super(var1);
      }

      int size() {
         return this.size;
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         int var4 = super.read(var1, var2, var3);
         if (var4 > 0) {
            this.size += var4;
         }

         return var4;
      }

      public int read() throws IOException {
         int var1 = super.read();
         ++this.size;
         return var1;
      }
   }

   public static class ClassFileInfo {
      public final JavaFileObject fo;
      public final ClassFile cf;
      public final byte[] digest;
      public final int size;

      ClassFileInfo(JavaFileObject var1, ClassFile var2, byte[] var3, int var4) {
         this.fo = var1;
         this.cf = var2;
         this.digest = var3;
         this.size = var4;
      }
   }

   abstract static class Option {
      final boolean hasArg;
      final String[] aliases;

      Option(boolean var1, String... var2) {
         this.hasArg = var1;
         this.aliases = var2;
      }

      boolean matches(String var1) {
         String[] var2 = this.aliases;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if (var5.equals(var1)) {
               return true;
            }
         }

         return false;
      }

      boolean ignoreRest() {
         return false;
      }

      abstract void process(JavapTask var1, String var2, String var3) throws BadArgs;
   }

   public class BadArgs extends Exception {
      static final long serialVersionUID = 8765093759964640721L;
      final String key;
      final Object[] args;
      boolean showUsage;

      BadArgs(String var2, Object... var3) {
         super(JavapTask.this.getMessage(var2, var3));
         this.key = var2;
         this.args = var3;
      }

      BadArgs showUsage(boolean var1) {
         this.showUsage = var1;
         return this;
      }
   }
}
