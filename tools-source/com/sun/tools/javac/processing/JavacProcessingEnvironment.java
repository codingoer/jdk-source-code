package com.sun.tools.javac.processing;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.api.MultiTaskListener;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.comp.CompileStates;
import com.sun.tools.javac.file.FSInfo;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.parser.Tokens;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Abort;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Convert;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.JavacMessages;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.ServiceLoader;
import java.io.Closeable;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class JavacProcessingEnvironment implements ProcessingEnvironment, Closeable {
   private final Options options;
   private final boolean printProcessorInfo;
   private final boolean printRounds;
   private final boolean verbose;
   private final boolean lint;
   private final boolean fatalErrors;
   private final boolean werror;
   private final boolean showResolveErrors;
   private final JavacFiler filer;
   private final JavacMessager messager;
   private final JavacElements elementUtils;
   private final JavacTypes typeUtils;
   private DiscoveredProcessors discoveredProcs;
   private final Map processorOptions;
   private final Set unmatchedProcessorOptions;
   private final Set platformAnnotations;
   private Set specifiedPackages = Collections.emptySet();
   Log log;
   JCDiagnostic.Factory diags;
   Source source;
   private ClassLoader processorClassLoader;
   private SecurityException processorClassLoaderException;
   private JavacMessages messages;
   private MultiTaskListener taskListener;
   private Context context;
   private static final TreeScanner treeCleaner = new TreeScanner() {
      public void scan(JCTree var1) {
         super.scan(var1);
         if (var1 != null) {
            var1.type = null;
         }

      }

      public void visitTopLevel(JCTree.JCCompilationUnit var1) {
         var1.packge = null;
         super.visitTopLevel(var1);
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         var1.sym = null;
         super.visitClassDef(var1);
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         var1.sym = null;
         super.visitMethodDef(var1);
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         var1.sym = null;
         super.visitVarDef(var1);
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         var1.constructor = null;
         super.visitNewClass(var1);
      }

      public void visitAssignop(JCTree.JCAssignOp var1) {
         var1.operator = null;
         super.visitAssignop(var1);
      }

      public void visitUnary(JCTree.JCUnary var1) {
         var1.operator = null;
         super.visitUnary(var1);
      }

      public void visitBinary(JCTree.JCBinary var1) {
         var1.operator = null;
         super.visitBinary(var1);
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         var1.sym = null;
         super.visitSelect(var1);
      }

      public void visitIdent(JCTree.JCIdent var1) {
         var1.sym = null;
         super.visitIdent(var1);
      }

      public void visitAnnotation(JCTree.JCAnnotation var1) {
         var1.attribute = null;
         super.visitAnnotation(var1);
      }
   };
   private static final Pattern allMatches = Pattern.compile(".*");
   public static final Pattern noMatches = Pattern.compile("(\\P{all})+");

   public static JavacProcessingEnvironment instance(Context var0) {
      JavacProcessingEnvironment var1 = (JavacProcessingEnvironment)var0.get(JavacProcessingEnvironment.class);
      if (var1 == null) {
         var1 = new JavacProcessingEnvironment(var0);
      }

      return var1;
   }

   protected JavacProcessingEnvironment(Context var1) {
      this.context = var1;
      var1.put((Class)JavacProcessingEnvironment.class, (Object)this);
      this.log = Log.instance(var1);
      this.source = Source.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.options = Options.instance(var1);
      this.printProcessorInfo = this.options.isSet(Option.XPRINTPROCESSORINFO);
      this.printRounds = this.options.isSet(Option.XPRINTROUNDS);
      this.verbose = this.options.isSet(Option.VERBOSE);
      this.lint = Lint.instance(var1).isEnabled(Lint.LintCategory.PROCESSING);
      if (this.options.isSet(Option.PROC, "only") || this.options.isSet(Option.XPRINT)) {
         JavaCompiler var2 = JavaCompiler.instance(var1);
         var2.shouldStopPolicyIfNoError = CompileStates.CompileState.PROCESS;
      }

      this.fatalErrors = this.options.isSet("fatalEnterError");
      this.showResolveErrors = this.options.isSet("showResolveErrors");
      this.werror = this.options.isSet(Option.WERROR);
      this.platformAnnotations = this.initPlatformAnnotations();
      this.filer = new JavacFiler(var1);
      this.messager = new JavacMessager(var1, this);
      this.elementUtils = JavacElements.instance(var1);
      this.typeUtils = JavacTypes.instance(var1);
      this.processorOptions = this.initProcessorOptions(var1);
      this.unmatchedProcessorOptions = this.initUnmatchedProcessorOptions();
      this.messages = JavacMessages.instance(var1);
      this.taskListener = MultiTaskListener.instance(var1);
      this.initProcessorClassLoader();
   }

   public void setProcessors(Iterable var1) {
      Assert.checkNull(this.discoveredProcs);
      this.initProcessorIterator(this.context, var1);
   }

   private Set initPlatformAnnotations() {
      HashSet var1 = new HashSet();
      var1.add("java.lang.Deprecated");
      var1.add("java.lang.Override");
      var1.add("java.lang.SuppressWarnings");
      var1.add("java.lang.annotation.Documented");
      var1.add("java.lang.annotation.Inherited");
      var1.add("java.lang.annotation.Retention");
      var1.add("java.lang.annotation.Target");
      return Collections.unmodifiableSet(var1);
   }

   private void initProcessorClassLoader() {
      JavaFileManager var1 = (JavaFileManager)this.context.get(JavaFileManager.class);

      try {
         this.processorClassLoader = var1.hasLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH) ? var1.getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH) : var1.getClassLoader(StandardLocation.CLASS_PATH);
         if (this.processorClassLoader != null && this.processorClassLoader instanceof Closeable) {
            JavaCompiler var2 = JavaCompiler.instance(this.context);
            var2.closeables = var2.closeables.prepend((Closeable)this.processorClassLoader);
         }
      } catch (SecurityException var3) {
         this.processorClassLoaderException = var3;
      }

   }

   private void initProcessorIterator(Context var1, Iterable var2) {
      Log var3 = Log.instance(var1);
      Object var4;
      if (this.options.isSet(Option.XPRINT)) {
         try {
            Processor var5 = (Processor)PrintingProcessor.class.newInstance();
            var4 = List.of(var5).iterator();
         } catch (Throwable var7) {
            AssertionError var6 = new AssertionError("Problem instantiating PrintingProcessor.");
            var6.initCause(var7);
            throw var6;
         }
      } else if (var2 != null) {
         var4 = var2.iterator();
      } else {
         String var8 = this.options.get(Option.PROCESSOR);
         if (this.processorClassLoaderException == null) {
            if (var8 != null) {
               var4 = new NameProcessIterator(var8, this.processorClassLoader, var3);
            } else {
               var4 = new ServiceIterator(this.processorClassLoader, var3);
            }
         } else {
            var4 = this.handleServiceLoaderUnavailability("proc.cant.create.loader", this.processorClassLoaderException);
         }
      }

      this.discoveredProcs = new DiscoveredProcessors((Iterator)var4);
   }

   private Iterator handleServiceLoaderUnavailability(String var1, Exception var2) {
      JavaFileManager var3 = (JavaFileManager)this.context.get(JavaFileManager.class);
      if (var3 instanceof JavacFileManager) {
         JavacFileManager var4 = (JavacFileManager)var3;
         Iterable var5 = var3.hasLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH) ? var4.getLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH) : var4.getLocation(StandardLocation.CLASS_PATH);
         if (this.needClassLoader(this.options.get(Option.PROCESSOR), var5)) {
            this.handleException(var1, var2);
         }
      } else {
         this.handleException(var1, var2);
      }

      java.util.List var6 = Collections.emptyList();
      return var6.iterator();
   }

   private void handleException(String var1, Exception var2) {
      if (var2 != null) {
         this.log.error(var1, new Object[]{var2.getLocalizedMessage()});
         throw new Abort(var2);
      } else {
         this.log.error(var1, new Object[0]);
         throw new Abort();
      }
   }

   public boolean atLeastOneProcessor() {
      return this.discoveredProcs.iterator().hasNext();
   }

   private Map initProcessorOptions(Context var1) {
      Options var2 = Options.instance(var1);
      Set var3 = var2.keySet();
      LinkedHashMap var4 = new LinkedHashMap();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (var6.startsWith("-A") && var6.length() > 2) {
            int var7 = var6.indexOf(61);
            String var8 = null;
            String var9 = null;
            if (var7 == -1) {
               var8 = var6.substring(2);
            } else if (var7 >= 3) {
               var8 = var6.substring(2, var7);
               var9 = var7 < var6.length() - 1 ? var6.substring(var7 + 1) : null;
            }

            var4.put(var8, var9);
         }
      }

      return Collections.unmodifiableMap(var4);
   }

   private Set initUnmatchedProcessorOptions() {
      HashSet var1 = new HashSet();
      var1.addAll(this.processorOptions.keySet());
      return var1;
   }

   private void discoverAndRunProcs(Context var1, Set var2, List var3, List var4) {
      HashMap var5 = new HashMap(var2.size());
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         TypeElement var7 = (TypeElement)var6.next();
         var5.put(var7.getQualifiedName().toString(), var7);
      }

      if (var5.size() == 0) {
         var5.put("", (Object)null);
      }

      DiscoveredProcessors.ProcessorStateIterator var16 = this.discoveredProcs.iterator();
      LinkedHashSet var17 = new LinkedHashSet();
      var17.addAll(var3);
      var17.addAll(var4);
      Set var18 = Collections.unmodifiableSet(var17);
      JavacRoundEnvironment var8 = new JavacRoundEnvironment(false, false, var18, this);

      while(var5.size() > 0 && var16.hasNext()) {
         ProcessorState var9 = var16.next();
         HashSet var10 = new HashSet();
         LinkedHashSet var11 = new LinkedHashSet();
         Iterator var12 = var5.entrySet().iterator();

         while(var12.hasNext()) {
            Map.Entry var13 = (Map.Entry)var12.next();
            String var14 = (String)var13.getKey();
            if (var9.annotationSupported(var14)) {
               var10.add(var14);
               TypeElement var15 = (TypeElement)var13.getValue();
               if (var15 != null) {
                  var11.add(var15);
               }
            }
         }

         if (var10.size() > 0 || var9.contributed) {
            boolean var19 = this.callProcessor(var9.processor, var11, var8);
            var9.contributed = true;
            var9.removeSupportedOptions(this.unmatchedProcessorOptions);
            if (this.printProcessorInfo || this.verbose) {
               this.log.printLines("x.print.processor.info", var9.processor.getClass().getName(), var10.toString(), var19);
            }

            if (var19) {
               var5.keySet().removeAll(var10);
            }
         }
      }

      var5.remove("");
      if (this.lint && var5.size() > 0) {
         var5.keySet().removeAll(this.platformAnnotations);
         if (var5.size() > 0) {
            this.log = Log.instance(var1);
            this.log.warning("proc.annotations.without.processors", new Object[]{var5.keySet()});
         }
      }

      var16.runContributingProcs(var8);
      if (this.options.isSet("displayFilerState")) {
         this.filer.displayState();
      }

   }

   private boolean callProcessor(Processor var1, Set var2, RoundEnvironment var3) {
      try {
         return var1.process(var2, var3);
      } catch (ClassReader.BadClassFile var6) {
         this.log.error("proc.cant.access.1", new Object[]{var6.sym, var6.getDetailValue()});
         return false;
      } catch (Symbol.CompletionFailure var7) {
         StringWriter var5 = new StringWriter();
         var7.printStackTrace(new PrintWriter(var5));
         this.log.error("proc.cant.access", new Object[]{var7.sym, var7.getDetailValue(), var5.toString()});
         return false;
      } catch (ClientCodeException var8) {
         throw var8;
      } catch (Throwable var9) {
         throw new AnnotationProcessingError(var9);
      }
   }

   public JavaCompiler doProcessing(Context var1, List var2, List var3, Iterable var4, Log.DeferredDiagnosticHandler var5) {
      this.log = Log.instance(var1);
      LinkedHashSet var6 = new LinkedHashSet();
      Iterator var7 = var4.iterator();

      while(var7.hasNext()) {
         Symbol.PackageSymbol var8 = (Symbol.PackageSymbol)var7.next();
         var6.add(var8);
      }

      this.specifiedPackages = Collections.unmodifiableSet(var6);
      Round var12 = new Round(var1, var2, var3, var5);

      boolean var9;
      boolean var13;
      do {
         var12.run(false, false);
         var13 = var12.unrecoverableError();
         var9 = this.moreToDo();
         var12.showDiagnostics(var13 || this.showResolveErrors);
         var12 = var12.next(new LinkedHashSet(this.filer.getGeneratedSourceFileObjects()), new LinkedHashMap(this.filer.getGeneratedClasses()));
         if (var12.unrecoverableError()) {
            var13 = true;
         }
      } while(var9 && !var13);

      var12.run(true, var13);
      var12.showDiagnostics(true);
      this.filer.warnIfUnclosedFiles();
      this.warnIfUnmatchedOptions();
      if (this.messager.errorRaised() || this.werror && var12.warningCount() > 0 && var12.errorCount() > 0) {
         var13 = true;
      }

      LinkedHashSet var10 = new LinkedHashSet(this.filer.getGeneratedSourceFileObjects());
      var2 = cleanTrees(var12.roots);
      JavaCompiler var11 = var12.finalCompiler();
      if (var10.size() > 0) {
         var2 = var2.appendList(var11.parseFiles(var10));
      }

      var13 = var13 || var11.errorCount() > 0;
      this.close();
      if (!this.taskListener.isEmpty()) {
         this.taskListener.finished(new TaskEvent(TaskEvent.Kind.ANNOTATION_PROCESSING));
      }

      if (var13) {
         if (var11.errorCount() == 0) {
            ++var11.log.nerrors;
         }

         return var11;
      } else {
         var11.enterTreesIfNeeded(var2);
         return var11;
      }
   }

   private void warnIfUnmatchedOptions() {
      if (!this.unmatchedProcessorOptions.isEmpty()) {
         this.log.warning("proc.unmatched.processor.options", new Object[]{this.unmatchedProcessorOptions.toString()});
      }

   }

   public void close() {
      this.filer.close();
      if (this.discoveredProcs != null) {
         this.discoveredProcs.close();
      }

      this.discoveredProcs = null;
   }

   private List getTopLevelClasses(List var1) {
      List var2 = List.nil();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var3.next();
         Iterator var5 = var4.defs.iterator();

         while(var5.hasNext()) {
            JCTree var6 = (JCTree)var5.next();
            if (var6.hasTag(JCTree.Tag.CLASSDEF)) {
               Symbol.ClassSymbol var7 = ((JCTree.JCClassDecl)var6).sym;
               Assert.checkNonNull(var7);
               var2 = var2.prepend(var7);
            }
         }
      }

      return var2.reverse();
   }

   private List getTopLevelClassesFromClasses(List var1) {
      List var2 = List.nil();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)var3.next();
         if (!this.isPkgInfo(var4)) {
            var2 = var2.prepend(var4);
         }
      }

      return var2.reverse();
   }

   private List getPackageInfoFiles(List var1) {
      List var2 = List.nil();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JCTree.JCCompilationUnit var4 = (JCTree.JCCompilationUnit)var3.next();
         if (this.isPkgInfo(var4.sourcefile, Kind.SOURCE)) {
            var2 = var2.prepend(var4.packge);
         }
      }

      return var2.reverse();
   }

   private List getPackageInfoFilesFromClasses(List var1) {
      List var2 = List.nil();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)var3.next();
         if (this.isPkgInfo(var4)) {
            var2 = var2.prepend((Symbol.PackageSymbol)var4.owner);
         }
      }

      return var2.reverse();
   }

   private static List join(List var0, List var1) {
      return var0.appendList(var1);
   }

   private boolean isPkgInfo(JavaFileObject var1, JavaFileObject.Kind var2) {
      return var1.isNameCompatible("package-info", var2);
   }

   private boolean isPkgInfo(Symbol.ClassSymbol var1) {
      return this.isPkgInfo(var1.classfile, Kind.CLASS) && var1.packge().package_info == var1;
   }

   private boolean needClassLoader(String var1, Iterable var2) {
      if (var1 != null) {
         return true;
      } else {
         URL[] var3 = new URL[1];
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            File var5 = (File)var4.next();

            try {
               var3[0] = var5.toURI().toURL();
               if (ServiceProxy.hasService(Processor.class, var3)) {
                  return true;
               }
            } catch (MalformedURLException var7) {
               throw new AssertionError(var7);
            } catch (ServiceProxy.ServiceConfigurationError var8) {
               this.log.error("proc.bad.config.file", new Object[]{var8.getLocalizedMessage()});
               return true;
            }
         }

         return false;
      }
   }

   private static List cleanTrees(List var0) {
      Iterator var1 = var0.iterator();

      while(var1.hasNext()) {
         JCTree var2 = (JCTree)var1.next();
         treeCleaner.scan(var2);
      }

      return var0;
   }

   private boolean moreToDo() {
      return this.filer.newFiles();
   }

   public Map getOptions() {
      return this.processorOptions;
   }

   public Messager getMessager() {
      return this.messager;
   }

   public Filer getFiler() {
      return this.filer;
   }

   public JavacElements getElementUtils() {
      return this.elementUtils;
   }

   public JavacTypes getTypeUtils() {
      return this.typeUtils;
   }

   public SourceVersion getSourceVersion() {
      return Source.toSourceVersion(this.source);
   }

   public Locale getLocale() {
      return this.messages.getCurrentLocale();
   }

   public Set getSpecifiedPackages() {
      return this.specifiedPackages;
   }

   private static Pattern importStringToPattern(String var0, Processor var1, Log var2) {
      if (isValidImportString(var0)) {
         return validImportStringToPattern(var0);
      } else {
         var2.warning("proc.malformed.supported.string", new Object[]{var0, var1.getClass().getName()});
         return noMatches;
      }
   }

   public static boolean isValidImportString(String var0) {
      if (var0.equals("*")) {
         return true;
      } else {
         boolean var1 = true;
         String var2 = var0;
         int var3 = var0.indexOf(42);
         if (var3 != -1) {
            if (var3 != var0.length() - 1) {
               return false;
            }

            if (var3 - 1 >= 0) {
               var1 = var0.charAt(var3 - 1) == '.';
               var2 = var0.substring(0, var0.length() - 2);
            }
         }

         if (var1) {
            String[] var4 = var2.split("\\.", var2.length() + 2);
            String[] var5 = var4;
            int var6 = var4.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String var8 = var5[var7];
               var1 &= SourceVersion.isIdentifier(var8);
            }
         }

         return var1;
      }
   }

   public static Pattern validImportStringToPattern(String var0) {
      if (var0.equals("*")) {
         return allMatches;
      } else {
         String var1 = var0.replace(".", "\\.");
         if (var1.endsWith("*")) {
            var1 = var1.substring(0, var1.length() - 1) + ".+";
         }

         return Pattern.compile(var1);
      }
   }

   public Context getContext() {
      return this.context;
   }

   public ClassLoader getProcessorClassLoader() {
      return this.processorClassLoader;
   }

   public String toString() {
      return "javac ProcessingEnvironment";
   }

   public static boolean isValidOptionName(String var0) {
      String[] var1 = var0.split("\\.", -1);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (!SourceVersion.isIdentifier(var4)) {
            return false;
         }
      }

      return true;
   }

   class Round {
      final int number;
      final Context context;
      final JavaCompiler compiler;
      final Log log;
      final Log.DeferredDiagnosticHandler deferredDiagnosticHandler;
      List roots;
      Map genClassFiles;
      Set annotationsPresent;
      List topLevelClasses;
      List packageInfoFiles;

      private Round(Context var2, int var3, int var4, int var5, Log.DeferredDiagnosticHandler var6) {
         this.context = var2;
         this.number = var3;
         this.compiler = JavaCompiler.instance(var2);
         this.log = Log.instance(var2);
         this.log.nerrors = var4;
         this.log.nwarnings = var5;
         if (var3 == 1) {
            Assert.checkNonNull(var6);
            this.deferredDiagnosticHandler = var6;
         } else {
            this.deferredDiagnosticHandler = new Log.DeferredDiagnosticHandler(this.log);
         }

         JavacProcessingEnvironment.this.context = var2;
         this.topLevelClasses = List.nil();
         this.packageInfoFiles = List.nil();
      }

      Round(Context var2, List var3, List var4, Log.DeferredDiagnosticHandler var5) {
         this(var2, 1, 0, 0, var5);
         this.roots = var3;
         this.genClassFiles = new HashMap();
         this.compiler.todo.clear();
         this.topLevelClasses = JavacProcessingEnvironment.this.getTopLevelClasses(var3).prependList(var4.reverse());
         this.packageInfoFiles = JavacProcessingEnvironment.this.getPackageInfoFiles(var3);
         this.findAnnotationsPresent();
      }

      private Round(Round var2, Set var3, Map var4) {
         this(var2.nextContext(), var2.number + 1, var2.compiler.log.nerrors, var2.compiler.log.nwarnings, (Log.DeferredDiagnosticHandler)null);
         this.genClassFiles = var2.genClassFiles;
         List var5 = this.compiler.parseFiles(var3);
         this.roots = JavacProcessingEnvironment.cleanTrees(var2.roots).appendList(var5);
         if (!this.unrecoverableError()) {
            this.enterClassFiles(this.genClassFiles);
            List var6 = this.enterClassFiles(var4);
            this.genClassFiles.putAll(var4);
            this.enterTrees(this.roots);
            if (!this.unrecoverableError()) {
               this.topLevelClasses = JavacProcessingEnvironment.join(JavacProcessingEnvironment.this.getTopLevelClasses(var5), JavacProcessingEnvironment.this.getTopLevelClassesFromClasses(var6));
               this.packageInfoFiles = JavacProcessingEnvironment.join(JavacProcessingEnvironment.this.getPackageInfoFiles(var5), JavacProcessingEnvironment.this.getPackageInfoFilesFromClasses(var6));
               this.findAnnotationsPresent();
            }
         }
      }

      Round next(Set var1, Map var2) {
         Round var3;
         try {
            var3 = JavacProcessingEnvironment.this.new Round(this, var1, var2);
         } finally {
            this.compiler.close(false);
         }

         return var3;
      }

      JavaCompiler finalCompiler() {
         JavaCompiler var3;
         try {
            Context var1 = this.nextContext();
            JavacProcessingEnvironment.this.context = var1;
            JavaCompiler var2 = JavaCompiler.instance(var1);
            var2.log.initRound(this.compiler.log);
            var3 = var2;
         } finally {
            this.compiler.close(false);
         }

         return var3;
      }

      int errorCount() {
         return this.compiler.errorCount();
      }

      int warningCount() {
         return this.compiler.warningCount();
      }

      boolean unrecoverableError() {
         if (JavacProcessingEnvironment.this.messager.errorRaised()) {
            return true;
         } else {
            Iterator var1 = this.deferredDiagnosticHandler.getDiagnostics().iterator();

            JCDiagnostic var2;
            label33:
            do {
               while(var1.hasNext()) {
                  var2 = (JCDiagnostic)var1.next();
                  switch (var2.getKind()) {
                     case WARNING:
                        if (JavacProcessingEnvironment.this.werror) {
                           return true;
                        }
                        break;
                     case ERROR:
                        continue label33;
                  }
               }

               return false;
            } while(!JavacProcessingEnvironment.this.fatalErrors && var2.isFlagSet(JCDiagnostic.DiagnosticFlag.RECOVERABLE));

            return true;
         }
      }

      void findAnnotationsPresent() {
         ComputeAnnotationSet var1 = new ComputeAnnotationSet(JavacProcessingEnvironment.this.elementUtils);
         this.annotationsPresent = new LinkedHashSet();
         Iterator var2 = this.topLevelClasses.iterator();

         while(var2.hasNext()) {
            Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.next();
            var1.scan(var3, (Set)this.annotationsPresent);
         }

         var2 = this.packageInfoFiles.iterator();

         while(var2.hasNext()) {
            Symbol.PackageSymbol var4 = (Symbol.PackageSymbol)var2.next();
            var1.scan(var4, (Set)this.annotationsPresent);
         }

      }

      private List enterClassFiles(Map var1) {
         ClassReader var2 = ClassReader.instance(this.context);
         Names var3 = Names.instance(this.context);
         List var4 = List.nil();

         Symbol.ClassSymbol var9;
         for(Iterator var5 = var1.entrySet().iterator(); var5.hasNext(); var4 = var4.prepend(var9)) {
            Map.Entry var6 = (Map.Entry)var5.next();
            Name var7 = var3.fromString((String)var6.getKey());
            JavaFileObject var8 = (JavaFileObject)var6.getValue();
            if (var8.getKind() != Kind.CLASS) {
               throw new AssertionError(var8);
            }

            if (JavacProcessingEnvironment.this.isPkgInfo(var8, Kind.CLASS)) {
               Name var10 = Convert.packagePart(var7);
               Symbol.PackageSymbol var11 = var2.enterPackage(var10);
               if (var11.package_info == null) {
                  var11.package_info = var2.enterClass(Convert.shortName(var7), (Symbol.TypeSymbol)var11);
               }

               var9 = var11.package_info;
               if (var9.classfile == null) {
                  var9.classfile = var8;
               }
            } else {
               var9 = var2.enterClass(var7, var8);
            }
         }

         return var4.reverse();
      }

      private void enterTrees(List var1) {
         this.compiler.enterTrees(var1);
      }

      void run(boolean var1, boolean var2) {
         this.printRoundInfo(var1);
         if (!JavacProcessingEnvironment.this.taskListener.isEmpty()) {
            JavacProcessingEnvironment.this.taskListener.started(new TaskEvent(TaskEvent.Kind.ANNOTATION_PROCESSING_ROUND));
         }

         try {
            if (var1) {
               JavacProcessingEnvironment.this.filer.setLastRound(true);
               Set var3 = Collections.emptySet();
               JavacRoundEnvironment var4 = new JavacRoundEnvironment(true, var2, var3, JavacProcessingEnvironment.this);
               JavacProcessingEnvironment.this.discoveredProcs.iterator().runContributingProcs(var4);
            } else {
               JavacProcessingEnvironment.this.discoverAndRunProcs(this.context, this.annotationsPresent, this.topLevelClasses, this.packageInfoFiles);
            }
         } catch (Throwable var8) {
            this.deferredDiagnosticHandler.reportDeferredDiagnostics();
            this.log.popDiagnosticHandler(this.deferredDiagnosticHandler);
            throw var8;
         } finally {
            if (!JavacProcessingEnvironment.this.taskListener.isEmpty()) {
               JavacProcessingEnvironment.this.taskListener.finished(new TaskEvent(TaskEvent.Kind.ANNOTATION_PROCESSING_ROUND));
            }

         }

      }

      void showDiagnostics(boolean var1) {
         EnumSet var2 = EnumSet.allOf(Diagnostic.Kind.class);
         if (!var1) {
            var2.remove(javax.tools.Diagnostic.Kind.ERROR);
         }

         this.deferredDiagnosticHandler.reportDeferredDiagnostics(var2);
         this.log.popDiagnosticHandler(this.deferredDiagnosticHandler);
      }

      private void printRoundInfo(boolean var1) {
         if (JavacProcessingEnvironment.this.printRounds || JavacProcessingEnvironment.this.verbose) {
            List var2 = var1 ? List.nil() : this.topLevelClasses;
            Set var3 = var1 ? Collections.emptySet() : this.annotationsPresent;
            this.log.printLines("x.print.rounds", this.number, "{" + var2.toString(", ") + "}", var3, var1);
         }

      }

      private Context nextContext() {
         Context var1 = new Context(this.context);
         Options var2 = Options.instance(this.context);
         Assert.checkNonNull(var2);
         var1.put((Context.Key)Options.optionsKey, (Object)var2);
         Locale var3 = (Locale)this.context.get(Locale.class);
         if (var3 != null) {
            var1.put((Class)Locale.class, (Object)var3);
         }

         Assert.checkNonNull(JavacProcessingEnvironment.this.messages);
         var1.put((Context.Key)JavacMessages.messagesKey, (Object)JavacProcessingEnvironment.this.messages);
         Names var5 = Names.instance(this.context);
         Assert.checkNonNull(var5);
         var1.put((Context.Key)Names.namesKey, (Object)var5);
         DiagnosticListener var16 = (DiagnosticListener)this.context.get(DiagnosticListener.class);
         if (var16 != null) {
            var1.put((Class)DiagnosticListener.class, (Object)var16);
         }

         MultiTaskListener var6 = (MultiTaskListener)this.context.get(MultiTaskListener.taskListenerKey);
         if (var6 != null) {
            var1.put((Context.Key)MultiTaskListener.taskListenerKey, (Object)var6);
         }

         FSInfo var7 = (FSInfo)this.context.get(FSInfo.class);
         if (var7 != null) {
            var1.put((Class)FSInfo.class, (Object)var7);
         }

         JavaFileManager var8 = (JavaFileManager)this.context.get(JavaFileManager.class);
         Assert.checkNonNull(var8);
         var1.put((Class)JavaFileManager.class, (Object)var8);
         if (var8 instanceof JavacFileManager) {
            ((JavacFileManager)var8).setContext(var1);
         }

         Names var9 = Names.instance(this.context);
         Assert.checkNonNull(var9);
         var1.put((Context.Key)Names.namesKey, (Object)var9);
         Tokens var10 = Tokens.instance(this.context);
         Assert.checkNonNull(var10);
         var1.put((Context.Key)Tokens.tokensKey, (Object)var10);
         Log var11 = Log.instance(var1);
         var11.initRound(this.log);
         JavaCompiler var12 = JavaCompiler.instance(this.context);
         JavaCompiler var13 = JavaCompiler.instance(var1);
         var13.initRound(var12);
         JavacProcessingEnvironment.this.filer.newRound(var1);
         JavacProcessingEnvironment.this.messager.newRound(var1);
         JavacProcessingEnvironment.this.elementUtils.setContext(var1);
         JavacProcessingEnvironment.this.typeUtils.setContext(var1);
         JavacTask var14 = (JavacTask)this.context.get(JavacTask.class);
         if (var14 != null) {
            var1.put((Class)JavacTask.class, (Object)var14);
            if (var14 instanceof BasicJavacTask) {
               ((BasicJavacTask)var14).updateContext(var1);
            }
         }

         JavacTrees var15 = (JavacTrees)this.context.get(JavacTrees.class);
         if (var15 != null) {
            var1.put((Class)JavacTrees.class, (Object)var15);
            var15.updateContext(var1);
         }

         this.context.clear();
         return var1;
      }
   }

   public static class ComputeAnnotationSet extends ElementScanner8 {
      final Elements elements;

      public ComputeAnnotationSet(Elements var1) {
         this.elements = var1;
      }

      public Set visitPackage(PackageElement var1, Set var2) {
         return var2;
      }

      public Set visitType(TypeElement var1, Set var2) {
         this.scan((Iterable)var1.getTypeParameters(), (Object)var2);
         return (Set)super.visitType(var1, var2);
      }

      public Set visitExecutable(ExecutableElement var1, Set var2) {
         this.scan((Iterable)var1.getTypeParameters(), (Object)var2);
         return (Set)super.visitExecutable(var1, var2);
      }

      void addAnnotations(Element var1, Set var2) {
         Iterator var3 = this.elements.getAllAnnotationMirrors(var1).iterator();

         while(var3.hasNext()) {
            AnnotationMirror var4 = (AnnotationMirror)var3.next();
            Element var5 = var4.getAnnotationType().asElement();
            var2.add((TypeElement)var5);
         }

      }

      public Set scan(Element var1, Set var2) {
         this.addAnnotations(var1, var2);
         return (Set)super.scan(var1, var2);
      }
   }

   class DiscoveredProcessors implements Iterable {
      Iterator processorIterator;
      ArrayList procStateList;

      public ProcessorStateIterator iterator() {
         return new ProcessorStateIterator(this);
      }

      DiscoveredProcessors(Iterator var2) {
         this.processorIterator = var2;
         this.procStateList = new ArrayList();
      }

      public void close() {
         if (this.processorIterator != null && this.processorIterator instanceof ServiceIterator) {
            ((ServiceIterator)this.processorIterator).close();
         }

      }

      class ProcessorStateIterator implements Iterator {
         DiscoveredProcessors psi;
         Iterator innerIter;
         boolean onProcInterator;

         ProcessorStateIterator(DiscoveredProcessors var2) {
            this.psi = var2;
            this.innerIter = var2.procStateList.iterator();
            this.onProcInterator = false;
         }

         public ProcessorState next() {
            if (!this.onProcInterator) {
               if (this.innerIter.hasNext()) {
                  return (ProcessorState)this.innerIter.next();
               }

               this.onProcInterator = true;
            }

            if (this.psi.processorIterator.hasNext()) {
               ProcessorState var1 = new ProcessorState((Processor)this.psi.processorIterator.next(), JavacProcessingEnvironment.this.log, JavacProcessingEnvironment.this.source, JavacProcessingEnvironment.this);
               this.psi.procStateList.add(var1);
               return var1;
            } else {
               throw new NoSuchElementException();
            }
         }

         public boolean hasNext() {
            if (this.onProcInterator) {
               return this.psi.processorIterator.hasNext();
            } else {
               return this.innerIter.hasNext() || this.psi.processorIterator.hasNext();
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }

         public void runContributingProcs(RoundEnvironment var1) {
            if (!this.onProcInterator) {
               Set var2 = Collections.emptySet();

               while(this.innerIter.hasNext()) {
                  ProcessorState var3 = (ProcessorState)this.innerIter.next();
                  if (var3.contributed) {
                     JavacProcessingEnvironment.this.callProcessor(var3.processor, var2, var1);
                  }
               }
            }

         }
      }
   }

   static class ProcessorState {
      public Processor processor;
      public boolean contributed;
      private ArrayList supportedAnnotationPatterns;
      private ArrayList supportedOptionNames;

      ProcessorState(Processor var1, Log var2, Source var3, ProcessingEnvironment var4) {
         this.processor = var1;
         this.contributed = false;

         try {
            this.processor.init(var4);
            this.checkSourceVersionCompatibility(var3, var2);
            this.supportedAnnotationPatterns = new ArrayList();
            Iterator var5 = this.processor.getSupportedAnnotationTypes().iterator();

            String var6;
            while(var5.hasNext()) {
               var6 = (String)var5.next();
               this.supportedAnnotationPatterns.add(JavacProcessingEnvironment.importStringToPattern(var6, this.processor, var2));
            }

            this.supportedOptionNames = new ArrayList();
            var5 = this.processor.getSupportedOptions().iterator();

            while(var5.hasNext()) {
               var6 = (String)var5.next();
               if (this.checkOptionName(var6, var2)) {
                  this.supportedOptionNames.add(var6);
               }
            }

         } catch (ClientCodeException var7) {
            throw var7;
         } catch (Throwable var8) {
            throw new AnnotationProcessingError(var8);
         }
      }

      private void checkSourceVersionCompatibility(Source var1, Log var2) {
         SourceVersion var3 = this.processor.getSupportedSourceVersion();
         if (var3.compareTo(Source.toSourceVersion(var1)) < 0) {
            var2.warning("proc.processor.incompatible.source.version", new Object[]{var3, this.processor.getClass().getName(), var1.name});
         }

      }

      private boolean checkOptionName(String var1, Log var2) {
         boolean var3 = JavacProcessingEnvironment.isValidOptionName(var1);
         if (!var3) {
            var2.error("proc.processor.bad.option.name", new Object[]{var1, this.processor.getClass().getName()});
         }

         return var3;
      }

      public boolean annotationSupported(String var1) {
         Iterator var2 = this.supportedAnnotationPatterns.iterator();

         Pattern var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (Pattern)var2.next();
         } while(!var3.matcher(var1).matches());

         return true;
      }

      public void removeSupportedOptions(Set var1) {
         var1.removeAll(this.supportedOptionNames);
      }
   }

   private static class NameProcessIterator implements Iterator {
      Processor nextProc = null;
      Iterator names;
      ClassLoader processorCL;
      Log log;

      NameProcessIterator(String var1, ClassLoader var2, Log var3) {
         this.names = Arrays.asList(var1.split(",")).iterator();
         this.processorCL = var2;
         this.log = var3;
      }

      public boolean hasNext() {
         if (this.nextProc != null) {
            return true;
         } else if (!this.names.hasNext()) {
            return false;
         } else {
            String var1 = (String)this.names.next();

            Processor var2;
            try {
               try {
                  var2 = (Processor)((Processor)this.processorCL.loadClass(var1).newInstance());
               } catch (ClassNotFoundException var4) {
                  this.log.error("proc.processor.not.found", new Object[]{var1});
                  return false;
               } catch (ClassCastException var5) {
                  this.log.error("proc.processor.wrong.type", new Object[]{var1});
                  return false;
               } catch (Exception var6) {
                  this.log.error("proc.processor.cant.instantiate", new Object[]{var1});
                  return false;
               }
            } catch (ClientCodeException var7) {
               throw var7;
            } catch (Throwable var8) {
               throw new AnnotationProcessingError(var8);
            }

            this.nextProc = var2;
            return true;
         }
      }

      public Processor next() {
         if (this.hasNext()) {
            Processor var1 = this.nextProc;
            this.nextProc = null;
            return var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private class ServiceIterator implements Iterator {
      private Iterator iterator;
      private Log log;
      private ServiceLoader loader;

      ServiceIterator(ClassLoader var2, Log var3) {
         this.log = var3;

         try {
            try {
               this.loader = ServiceLoader.load(Processor.class, var2);
               this.iterator = this.loader.iterator();
            } catch (Exception var5) {
               this.iterator = JavacProcessingEnvironment.this.handleServiceLoaderUnavailability("proc.no.service", (Exception)null);
            }

         } catch (Throwable var6) {
            var3.error("proc.service.problem", new Object[0]);
            throw new Abort(var6);
         }
      }

      public boolean hasNext() {
         try {
            return this.iterator.hasNext();
         } catch (ServiceConfigurationError var2) {
            this.log.error("proc.bad.config.file", new Object[]{var2.getLocalizedMessage()});
            throw new Abort(var2);
         } catch (Throwable var3) {
            throw new Abort(var3);
         }
      }

      public Processor next() {
         try {
            return (Processor)this.iterator.next();
         } catch (ServiceConfigurationError var2) {
            this.log.error("proc.bad.config.file", new Object[]{var2.getLocalizedMessage()});
            throw new Abort(var2);
         } catch (Throwable var3) {
            throw new Abort(var3);
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      public void close() {
         if (this.loader != null) {
            try {
               this.loader.reload();
            } catch (Exception var2) {
            }
         }

      }
   }
}
