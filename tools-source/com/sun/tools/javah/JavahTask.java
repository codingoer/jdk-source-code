package com.sun.tools.javah;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.main.CommandLine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleTypeVisitor8;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.tools.Diagnostic.Kind;

public class JavahTask implements NativeHeaderTool.NativeHeaderTask {
   static final Option[] recognizedOptions = new Option[]{new Option(true, new String[]{"-o"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.ofile = new File(var3);
      }
   }, new Option(true, new String[]{"-d"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.odir = new File(var3);
      }
   }, new HiddenOption(true, new String[]{"-td"}) {
      void process(JavahTask var1, String var2, String var3) {
      }
   }, new HiddenOption(false, new String[]{"-stubs"}) {
      void process(JavahTask var1, String var2, String var3) {
      }
   }, new Option(false, new String[]{"-v", "-verbose"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.verbose = true;
      }
   }, new Option(false, new String[]{"-h", "-help", "--help", "-?"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.help = true;
      }
   }, new HiddenOption(false, new String[]{"-trace"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.trace = true;
      }
   }, new Option(false, new String[]{"-version"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.version = true;
      }
   }, new HiddenOption(false, new String[]{"-fullversion"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.fullVersion = true;
      }
   }, new Option(false, new String[]{"-jni"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.jni = true;
      }
   }, new Option(false, new String[]{"-force"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.force = true;
      }
   }, new HiddenOption(false, new String[]{"-Xnew"}) {
      void process(JavahTask var1, String var2, String var3) {
      }
   }, new HiddenOption(false, new String[]{"-llni", "-Xllni"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.llni = true;
      }
   }, new HiddenOption(false, new String[]{"-llnidouble"}) {
      void process(JavahTask var1, String var2, String var3) {
         var1.llni = true;
         var1.doubleAlign = true;
      }
   }, new HiddenOption(false, new String[0]) {
      boolean matches(String var1) {
         return var1.startsWith("-XD");
      }

      void process(JavahTask var1, String var2, String var3) {
         var1.javac_extras.add(var2);
      }
   }};
   private static final String versionRBName = "com.sun.tools.javah.resources.version";
   private static ResourceBundle versionRB;
   File ofile;
   File odir;
   String bootcp;
   String usercp;
   List classes;
   boolean verbose;
   boolean noArgs;
   boolean help;
   boolean trace;
   boolean version;
   boolean fullVersion;
   boolean jni;
   boolean llni;
   boolean doubleAlign;
   boolean force;
   Set javac_extras;
   PrintWriter log;
   JavaFileManager fileManager;
   DiagnosticListener diagnosticListener;
   Locale task_locale;
   Map bundles;
   private static final String progname = "javah";

   JavahTask() {
      this.javac_extras = new LinkedHashSet();
   }

   JavahTask(Writer var1, JavaFileManager var2, DiagnosticListener var3, Iterable var4, Iterable var5) {
      this();
      this.log = getPrintWriterForWriter(var1);
      this.fileManager = var2;
      this.diagnosticListener = var3;

      try {
         this.handleOptions(var4, false);
      } catch (BadArgs var8) {
         throw new IllegalArgumentException(var8.getMessage());
      }

      this.classes = new ArrayList();
      if (var5 != null) {
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            String var7 = (String)var6.next();
            var7.getClass();
            this.classes.add(var7);
         }
      }

   }

   public void setLocale(Locale var1) {
      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      this.task_locale = var1;
   }

   public void setLog(PrintWriter var1) {
      this.log = var1;
   }

   public void setLog(OutputStream var1) {
      this.setLog(getPrintWriterForStream(var1));
   }

   static PrintWriter getPrintWriterForStream(OutputStream var0) {
      return new PrintWriter(var0, true);
   }

   static PrintWriter getPrintWriterForWriter(Writer var0) {
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
            if (var1.getKind() == Kind.ERROR) {
               var2.print(JavahTask.this.getMessage("err.prefix"));
               var2.print(" ");
            }

            var2.println(var1.getMessage((Locale)null));
         }
      };
   }

   int run(String[] var1) {
      byte var13;
      try {
         int var3;
         try {
            this.handleOptions(var1);
            boolean var2 = this.run();
            var3 = var2 ? 0 : 1;
            return var3;
         } catch (BadArgs var9) {
            this.diagnosticListener.report(this.createDiagnostic(var9.key, var9.args));
            var13 = 1;
            return var13;
         } catch (InternalError var10) {
            this.diagnosticListener.report(this.createDiagnostic("err.internal.error", var10.getMessage()));
            var13 = 1;
         } catch (Util.Exit var11) {
            var3 = var11.exitValue;
            return var3;
         }
      } finally {
         this.log.flush();
      }

      return var13;
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
         this.fileManager = getDefaultFileManager(this.diagnosticListener, this.log);
      }

      Iterator var3 = this.expandAtArgs(var1).iterator();
      this.noArgs = !var3.hasNext();

      while(true) {
         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            if (var4.startsWith("-")) {
               this.handleOption(var4, var3);
            } else {
               if (!var2) {
                  throw (new BadArgs("err.unknown.option", new Object[]{var4})).showUsage(true);
               }

               if (this.classes == null) {
                  this.classes = new ArrayList();
               }

               this.classes.add(var4);

               while(var3.hasNext()) {
                  this.classes.add(var3.next());
               }
            }
         }

         if ((this.classes == null || this.classes.size() == 0) && !this.noArgs && !this.help && !this.version && !this.fullVersion) {
            throw new BadArgs("err.no.classes.specified", new Object[0]);
         }

         if (this.jni && this.llni) {
            throw new BadArgs("jni.llni.mixed", new Object[0]);
         }

         if (this.odir != null && this.ofile != null) {
            throw new BadArgs("dir.file.mixed", new Object[0]);
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

      if (!this.fileManager.handleOption(var1, var2)) {
         throw (new BadArgs("err.unknown.option", new Object[]{var1})).showUsage(true);
      }
   }

   private Iterable expandAtArgs(Iterable var1) throws BadArgs {
      try {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            var2.add(var4);
         }

         return Arrays.asList(CommandLine.parse((String[])var2.toArray(new String[var2.size()])));
      } catch (FileNotFoundException var5) {
         throw new BadArgs("at.args.file.not.found", new Object[]{var5.getLocalizedMessage()});
      } catch (IOException var6) {
         throw new BadArgs("at.args.io.exception", new Object[]{var6.getLocalizedMessage()});
      }
   }

   public Boolean call() {
      return this.run();
   }

   public boolean run() throws Util.Exit {
      Util var1 = new Util(this.log, this.diagnosticListener);
      if (!this.noArgs && !this.help) {
         if (!this.version && !this.fullVersion) {
            var1.verbose = this.verbose;
            Object var2;
            if (this.llni) {
               var2 = new LLNI(this.doubleAlign, var1);
            } else {
               var2 = new JNI(var1);
            }

            if (this.ofile != null) {
               if (!(this.fileManager instanceof StandardJavaFileManager)) {
                  this.diagnosticListener.report(this.createDiagnostic("err.cant.use.option.for.fm", "-o"));
                  return false;
               }

               Iterable var3 = ((StandardJavaFileManager)this.fileManager).getJavaFileObjectsFromFiles(Collections.singleton(this.ofile));
               JavaFileObject var4 = (JavaFileObject)var3.iterator().next();
               ((Gen)var2).setOutFile(var4);
            } else {
               if (this.odir != null) {
                  if (!(this.fileManager instanceof StandardJavaFileManager)) {
                     this.diagnosticListener.report(this.createDiagnostic("err.cant.use.option.for.fm", "-d"));
                     return false;
                  }

                  if (!this.odir.exists() && !this.odir.mkdirs()) {
                     var1.error("cant.create.dir", this.odir.toString());
                  }

                  try {
                     ((StandardJavaFileManager)this.fileManager).setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(this.odir));
                  } catch (IOException var8) {
                     Object var10 = var8.getLocalizedMessage();
                     if (var10 == null) {
                        var10 = var8;
                     }

                     this.diagnosticListener.report(this.createDiagnostic("err.ioerror", this.odir, var10));
                     return false;
                  }
               }

               ((Gen)var2).setFileManager(this.fileManager);
            }

            ((Gen)var2).setForce(this.force);
            if (this.fileManager instanceof JavahFileManager) {
               ((JavahFileManager)this.fileManager).setSymbolFileEnabled(false);
            }

            JavaCompiler var9 = ToolProvider.getSystemJavaCompiler();
            ArrayList var11 = new ArrayList();
            var11.add("-proc:only");
            var11.addAll(this.javac_extras);
            JavaCompiler.CompilationTask var5 = var9.getTask(this.log, this.fileManager, this.diagnosticListener, var11, this.classes, (Iterable)null);
            JavahProcessor var6 = new JavahProcessor((Gen)var2);
            var5.setProcessors(Collections.singleton(var6));
            boolean var7 = var5.call();
            if (var6.exit != null) {
               throw new Util.Exit(var6.exit);
            } else {
               return var7;
            }
         } else {
            this.showVersion(this.fullVersion);
            return true;
         }
      } else {
         this.showHelp();
         return this.help;
      }
   }

   private List pathToFiles(String var1) {
      ArrayList var2 = new ArrayList();
      String[] var3 = var1.split(File.pathSeparator);
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         if (var6.length() > 0) {
            var2.add(new File(var6));
         }
      }

      return var2;
   }

   static StandardJavaFileManager getDefaultFileManager(DiagnosticListener var0, PrintWriter var1) {
      return JavahFileManager.create(var0, var1);
   }

   private void showHelp() {
      this.log.println(this.getMessage("main.usage", "javah"));
      Option[] var1 = recognizedOptions;
      int var2 = var1.length;

      int var3;
      String var5;
      for(var3 = 0; var3 < var2; ++var3) {
         Option var4 = var1[var3];
         if (!var4.isHidden()) {
            var5 = var4.aliases[0].substring(1);
            this.log.println(this.getMessage("main.opt." + var5));
         }
      }

      String[] var7 = new String[]{"-classpath", "-cp", "-bootclasspath"};
      String[] var8 = var7;
      var3 = var7.length;

      for(int var9 = 0; var9 < var3; ++var9) {
         var5 = var8[var9];
         if (this.fileManager.isSupportedOption(var5) != -1) {
            String var6 = var5.substring(1);
            this.log.println(this.getMessage("main.opt." + var6));
         }
      }

      this.log.println(this.getMessage("main.usage.foot"));
   }

   private void showVersion(boolean var1) {
      this.log.println(this.version(var1));
   }

   private String version(boolean var1) {
      String var2 = var1 ? "javah.fullVersion" : "javah.version";
      String var3 = var1 ? "full" : "release";
      if (versionRB == null) {
         try {
            versionRB = ResourceBundle.getBundle("com.sun.tools.javah.resources.version");
         } catch (MissingResourceException var6) {
            return this.getMessage("version.resource.missing", System.getProperty("java.version"));
         }
      }

      try {
         return this.getMessage(var2, "javah", versionRB.getString(var3));
      } catch (MissingResourceException var5) {
         return this.getMessage("version.unknown", System.getProperty("java.version"));
      }
   }

   private Diagnostic createDiagnostic(final String var1, final Object... var2) {
      return new Diagnostic() {
         public Diagnostic.Kind getKind() {
            return Kind.ERROR;
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
            return var1;
         }

         public String getMessage(Locale var1x) {
            return JavahTask.this.getMessage(var1x, var1, var2);
         }
      };
   }

   private String getMessage(String var1, Object... var2) {
      return this.getMessage(this.task_locale, var1, var2);
   }

   private String getMessage(Locale var1, String var2, Object... var3) {
      if (this.bundles == null) {
         this.bundles = new HashMap();
      }

      if (var1 == null) {
         var1 = Locale.getDefault();
      }

      ResourceBundle var4 = (ResourceBundle)this.bundles.get(var1);
      if (var4 == null) {
         try {
            var4 = ResourceBundle.getBundle("com.sun.tools.javah.resources.l10n", var1);
            this.bundles.put(var1, var4);
         } catch (MissingResourceException var7) {
            throw new InternalError("Cannot find javah resource bundle for locale " + var1, var7);
         }
      }

      try {
         return MessageFormat.format(var4.getString(var2), var3);
      } catch (MissingResourceException var6) {
         return var2;
      }
   }

   @SupportedAnnotationTypes({"*"})
   class JavahProcessor extends AbstractProcessor {
      private Messager messager;
      private TypeVisitor checkMethodParametersVisitor = new SimpleTypeVisitor8() {
         public Void visitArray(ArrayType var1, Types var2) {
            this.visit(var1.getComponentType(), var2);
            return null;
         }

         public Void visitDeclared(DeclaredType var1, Types var2) {
            var1.asElement().getKind();
            Iterator var3 = var2.directSupertypes(var1).iterator();

            while(var3.hasNext()) {
               TypeMirror var4 = (TypeMirror)var3.next();
               this.visit(var4, var2);
            }

            return null;
         }
      };
      private Gen g;
      private Util.Exit exit;

      JavahProcessor(Gen var2) {
         this.g = var2;
      }

      public SourceVersion getSupportedSourceVersion() {
         return SourceVersion.latest();
      }

      public void init(ProcessingEnvironment var1) {
         super.init(var1);
         this.messager = this.processingEnv.getMessager();
      }

      public boolean process(Set var1, RoundEnvironment var2) {
         try {
            Set var3 = this.getAllClasses(ElementFilter.typesIn(var2.getRootElements()));
            if (var3.size() > 0) {
               this.checkMethodParameters(var3);
               this.g.setProcessingEnvironment(this.processingEnv);
               this.g.setClasses(var3);
               this.g.run();
            }
         } catch (Symbol.CompletionFailure var4) {
            this.messager.printMessage(Kind.ERROR, JavahTask.this.getMessage("class.not.found", var4.sym.getQualifiedName().toString()));
         } catch (ClassNotFoundException var5) {
            this.messager.printMessage(Kind.ERROR, JavahTask.this.getMessage("class.not.found", var5.getMessage()));
         } catch (IOException var6) {
            this.messager.printMessage(Kind.ERROR, JavahTask.this.getMessage("io.exception", var6.getMessage()));
         } catch (Util.Exit var7) {
            this.exit = var7;
         }

         return true;
      }

      private Set getAllClasses(Set var1) {
         LinkedHashSet var2 = new LinkedHashSet();
         this.getAllClasses0(var1, var2);
         return var2;
      }

      private void getAllClasses0(Iterable var1, Set var2) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            TypeElement var4 = (TypeElement)var3.next();
            var2.add(var4);
            this.getAllClasses0(ElementFilter.typesIn(var4.getEnclosedElements()), var2);
         }

      }

      private void checkMethodParameters(Set var1) {
         Types var2 = this.processingEnv.getTypeUtils();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            TypeElement var4 = (TypeElement)var3.next();
            Iterator var5 = ElementFilter.methodsIn(var4.getEnclosedElements()).iterator();

            while(var5.hasNext()) {
               ExecutableElement var6 = (ExecutableElement)var5.next();
               Iterator var7 = var6.getParameters().iterator();

               while(var7.hasNext()) {
                  VariableElement var8 = (VariableElement)var7.next();
                  TypeMirror var9 = var8.asType();
                  this.checkMethodParametersVisitor.visit(var9, var2);
               }
            }
         }

      }
   }

   abstract static class HiddenOption extends Option {
      HiddenOption(boolean var1, String... var2) {
         super(var1, var2);
      }

      boolean isHidden() {
         return true;
      }
   }

   abstract static class Option {
      final boolean hasArg;
      final String[] aliases;

      Option(boolean var1, String... var2) {
         this.hasArg = var1;
         this.aliases = var2;
      }

      boolean isHidden() {
         return false;
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

      abstract void process(JavahTask var1, String var2, String var3) throws BadArgs;
   }

   public class BadArgs extends Exception {
      private static final long serialVersionUID = 1479361270874789045L;
      final String key;
      final Object[] args;
      boolean showUsage;

      BadArgs(String var2, Object... var3) {
         super(JavahTask.this.getMessage(var2, var3));
         this.key = var2;
         this.args = var3;
      }

      BadArgs showUsage(boolean var1) {
         this.showUsage = var1;
         return this;
      }
   }
}
