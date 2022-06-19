package com.sun.tools.javac.main;

import com.sun.source.util.TaskEvent;
import com.sun.tools.javac.api.MultiTaskListener;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Annotate;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Check;
import com.sun.tools.javac.comp.CompileStates;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.Flow;
import com.sun.tools.javac.comp.LambdaToMethod;
import com.sun.tools.javac.comp.Lower;
import com.sun.tools.javac.comp.Todo;
import com.sun.tools.javac.comp.TransTypes;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.jvm.ClassWriter;
import com.sun.tools.javac.jvm.Gen;
import com.sun.tools.javac.jvm.JNIWriter;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.Pretty;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Abort;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.BaseFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.FatalError;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Pair;
import com.sun.tools.javac.util.RichDiagnosticFormatter;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.JavaFileObject.Kind;

public class JavaCompiler {
   protected static final Context.Key compilerKey = new Context.Key();
   private static final String versionRBName = "com.sun.tools.javac.resources.version";
   private static ResourceBundle versionRB;
   private static final CompilePolicy DEFAULT_COMPILE_POLICY;
   public Log log;
   JCDiagnostic.Factory diagFactory;
   protected TreeMaker make;
   protected ClassReader reader;
   protected ClassWriter writer;
   protected JNIWriter jniWriter;
   protected Enter enter;
   protected Symtab syms;
   protected Source source;
   protected Gen gen;
   protected Names names;
   protected Attr attr;
   protected Check chk;
   protected Flow flow;
   protected TransTypes transTypes;
   protected Lower lower;
   protected Annotate annotate;
   protected final Name completionFailureName;
   protected Types types;
   protected JavaFileManager fileManager;
   protected ParserFactory parserFactory;
   protected MultiTaskListener taskListener;
   protected JavaCompiler delegateCompiler;
   protected final ClassReader.SourceCompleter thisCompleter = new ClassReader.SourceCompleter() {
      public void complete(Symbol.ClassSymbol var1) throws Symbol.CompletionFailure {
         JavaCompiler.this.complete(var1);
      }
   };
   protected Options options;
   protected Context context;
   protected boolean annotationProcessingOccurred;
   protected boolean implicitSourceFilesRead;
   protected CompileStates compileStates;
   public boolean verbose;
   public boolean sourceOutput;
   public boolean stubOutput;
   public boolean attrParseOnly;
   boolean relax;
   public boolean printFlat;
   public String encoding;
   public boolean lineDebugInfo;
   public boolean genEndPos;
   protected boolean devVerbose;
   protected boolean processPcks;
   protected boolean werror;
   protected boolean explicitAnnotationProcessingRequested = false;
   protected CompilePolicy compilePolicy;
   protected ImplicitSourcePolicy implicitSourcePolicy;
   public boolean verboseCompilePolicy;
   public CompileStates.CompileState shouldStopPolicyIfError;
   public CompileStates.CompileState shouldStopPolicyIfNoError;
   public Todo todo;
   public List closeables = List.nil();
   protected Set inputFiles = new HashSet();
   public boolean keepComments = false;
   private boolean hasBeenUsed = false;
   private long start_msec = 0L;
   public long elapsed_msec = 0L;
   protected boolean needRootClasses = false;
   private List rootClasses;
   boolean processAnnotations = false;
   Log.DeferredDiagnosticHandler deferredDiagnosticHandler;
   private JavacProcessingEnvironment procEnvImpl = null;
   HashMap desugaredEnvs = new HashMap();

   public static JavaCompiler instance(Context var0) {
      JavaCompiler var1 = (JavaCompiler)var0.get(compilerKey);
      if (var1 == null) {
         var1 = new JavaCompiler(var0);
      }

      return var1;
   }

   public static String version() {
      return version("release");
   }

   public static String fullVersion() {
      return version("full");
   }

   private static String version(String var0) {
      if (versionRB == null) {
         try {
            versionRB = ResourceBundle.getBundle("com.sun.tools.javac.resources.version");
         } catch (MissingResourceException var3) {
            return Log.getLocalizedString("version.not.available");
         }
      }

      try {
         return versionRB.getString(var0);
      } catch (MissingResourceException var2) {
         return Log.getLocalizedString("version.not.available");
      }
   }

   public JavaCompiler(Context var1) {
      this.context = var1;
      var1.put((Context.Key)compilerKey, (Object)this);
      if (var1.get(JavaFileManager.class) == null) {
         JavacFileManager.preRegister(var1);
      }

      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.diagFactory = JCDiagnostic.Factory.instance(var1);
      this.reader = ClassReader.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.writer = ClassWriter.instance(var1);
      this.jniWriter = JNIWriter.instance(var1);
      this.enter = Enter.instance(var1);
      this.todo = Todo.instance(var1);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      this.parserFactory = ParserFactory.instance(var1);
      this.compileStates = CompileStates.instance(var1);

      try {
         this.syms = Symtab.instance(var1);
      } catch (Symbol.CompletionFailure var3) {
         this.log.error("cant.access", new Object[]{var3.sym, var3.getDetailValue()});
         if (var3 instanceof ClassReader.BadClassFile) {
            throw new Abort();
         }
      }

      this.source = Source.instance(var1);
      Target var2 = Target.instance(var1);
      this.attr = Attr.instance(var1);
      this.chk = Check.instance(var1);
      this.gen = Gen.instance(var1);
      this.flow = Flow.instance(var1);
      this.transTypes = TransTypes.instance(var1);
      this.lower = Lower.instance(var1);
      this.annotate = Annotate.instance(var1);
      this.types = Types.instance(var1);
      this.taskListener = MultiTaskListener.instance(var1);
      this.reader.sourceCompleter = this.thisCompleter;
      this.options = Options.instance(var1);
      this.verbose = this.options.isSet(Option.VERBOSE);
      this.sourceOutput = this.options.isSet(Option.PRINTSOURCE);
      this.stubOutput = this.options.isSet("-stubs");
      this.relax = this.options.isSet("-relax");
      this.printFlat = this.options.isSet("-printflat");
      this.attrParseOnly = this.options.isSet("-attrparseonly");
      this.encoding = this.options.get(Option.ENCODING);
      this.lineDebugInfo = this.options.isUnset(Option.G_CUSTOM) || this.options.isSet(Option.G_CUSTOM, "lines");
      this.genEndPos = this.options.isSet(Option.XJCOV) || var1.get(DiagnosticListener.class) != null;
      this.devVerbose = this.options.isSet("dev");
      this.processPcks = this.options.isSet("process.packages");
      this.werror = this.options.isSet(Option.WERROR);
      if (this.source.compareTo(Source.DEFAULT) < 0 && this.options.isUnset(Option.XLINT_CUSTOM, "-" + Lint.LintCategory.OPTIONS.option) && this.fileManager instanceof BaseFileManager && ((BaseFileManager)this.fileManager).isDefaultBootClassPath()) {
         this.log.warning(Lint.LintCategory.OPTIONS, "source.no.bootclasspath", new Object[]{this.source.name});
      }

      this.checkForObsoleteOptions(var2);
      this.verboseCompilePolicy = this.options.isSet("verboseCompilePolicy");
      if (this.attrParseOnly) {
         this.compilePolicy = JavaCompiler.CompilePolicy.ATTR_ONLY;
      } else {
         this.compilePolicy = JavaCompiler.CompilePolicy.decode(this.options.get("compilePolicy"));
      }

      this.implicitSourcePolicy = JavaCompiler.ImplicitSourcePolicy.decode(this.options.get("-implicit"));
      this.completionFailureName = this.options.isSet("failcomplete") ? this.names.fromString(this.options.get("failcomplete")) : null;
      this.shouldStopPolicyIfError = this.options.isSet("shouldStopPolicy") ? CompileStates.CompileState.valueOf(this.options.get("shouldStopPolicy")) : (this.options.isSet("shouldStopPolicyIfError") ? CompileStates.CompileState.valueOf(this.options.get("shouldStopPolicyIfError")) : CompileStates.CompileState.INIT);
      this.shouldStopPolicyIfNoError = this.options.isSet("shouldStopPolicyIfNoError") ? CompileStates.CompileState.valueOf(this.options.get("shouldStopPolicyIfNoError")) : CompileStates.CompileState.GENERATE;
      if (this.options.isUnset("oldDiags")) {
         this.log.setDiagnosticFormatter(RichDiagnosticFormatter.instance(var1));
      }

   }

   private void checkForObsoleteOptions(Target var1) {
      boolean var2 = false;
      if (this.options.isUnset(Option.XLINT_CUSTOM, "-" + Lint.LintCategory.OPTIONS.option)) {
         if (this.source.compareTo(Source.JDK1_5) <= 0) {
            this.log.warning(Lint.LintCategory.OPTIONS, "option.obsolete.source", new Object[]{this.source.name});
            var2 = true;
         }

         if (var1.compareTo(Target.JDK1_5) <= 0) {
            this.log.warning(Lint.LintCategory.OPTIONS, "option.obsolete.target", new Object[]{var1.name});
            var2 = true;
         }

         if (var2) {
            this.log.warning(Lint.LintCategory.OPTIONS, "option.obsolete.suppression", new Object[0]);
         }
      }

   }

   protected boolean shouldStop(CompileStates.CompileState var1) {
      CompileStates.CompileState var2 = this.errorCount() <= 0 && !this.unrecoverableError() ? this.shouldStopPolicyIfNoError : this.shouldStopPolicyIfError;
      return var1.isAfter(var2);
   }

   public int errorCount() {
      if (this.delegateCompiler != null && this.delegateCompiler != this) {
         return this.delegateCompiler.errorCount();
      } else {
         if (this.werror && this.log.nerrors == 0 && this.log.nwarnings > 0) {
            this.log.error("warnings.and.werror", new Object[0]);
         }

         return this.log.nerrors;
      }
   }

   protected final Queue stopIfError(CompileStates.CompileState var1, Queue var2) {
      return (Queue)(this.shouldStop(var1) ? new ListBuffer() : var2);
   }

   protected final List stopIfError(CompileStates.CompileState var1, List var2) {
      return this.shouldStop(var1) ? List.nil() : var2;
   }

   public int warningCount() {
      return this.delegateCompiler != null && this.delegateCompiler != this ? this.delegateCompiler.warningCount() : this.log.nwarnings;
   }

   public CharSequence readSource(JavaFileObject var1) {
      try {
         this.inputFiles.add(var1);
         return var1.getCharContent(false);
      } catch (IOException var3) {
         this.log.error("error.reading.file", new Object[]{var1, JavacFileManager.getMessage(var3)});
         return null;
      }
   }

   protected JCTree.JCCompilationUnit parse(JavaFileObject var1, CharSequence var2) {
      long var3 = now();
      JCTree.JCCompilationUnit var5 = this.make.TopLevel(List.nil(), (JCTree.JCExpression)null, List.nil());
      TaskEvent var6;
      if (var2 != null) {
         if (this.verbose) {
            this.log.printVerbose("parsing.started", var1);
         }

         if (!this.taskListener.isEmpty()) {
            var6 = new TaskEvent(TaskEvent.Kind.PARSE, var1);
            this.taskListener.started(var6);
            this.keepComments = true;
            this.genEndPos = true;
         }

         JavacParser var7 = this.parserFactory.newParser(var2, this.keepComments(), this.genEndPos, this.lineDebugInfo);
         var5 = var7.parseCompilationUnit();
         if (this.verbose) {
            this.log.printVerbose("parsing.done", Long.toString(elapsed(var3)));
         }
      }

      var5.sourcefile = var1;
      if (var2 != null && !this.taskListener.isEmpty()) {
         var6 = new TaskEvent(TaskEvent.Kind.PARSE, var5);
         this.taskListener.finished(var6);
      }

      return var5;
   }

   protected boolean keepComments() {
      return this.keepComments || this.sourceOutput || this.stubOutput;
   }

   /** @deprecated */
   @Deprecated
   public JCTree.JCCompilationUnit parse(String var1) {
      JavacFileManager var2 = (JavacFileManager)this.fileManager;
      return this.parse((JavaFileObject)var2.getJavaFileObjectsFromStrings(List.of(var1)).iterator().next());
   }

   public JCTree.JCCompilationUnit parse(JavaFileObject var1) {
      JavaFileObject var2 = this.log.useSource(var1);

      JCTree.JCCompilationUnit var4;
      try {
         JCTree.JCCompilationUnit var3 = this.parse(var1, this.readSource(var1));
         if (var3.endPositions != null) {
            this.log.setEndPosTable(var1, var3.endPositions);
         }

         var4 = var3;
      } finally {
         this.log.useSource(var2);
      }

      return var4;
   }

   public Symbol resolveBinaryNameOrIdent(String var1) {
      try {
         Name var2 = this.names.fromString(var1.replace("/", "."));
         return this.reader.loadClass(var2);
      } catch (Symbol.CompletionFailure var3) {
         return this.resolveIdent(var1);
      }
   }

   public Symbol resolveIdent(String var1) {
      if (var1.equals("")) {
         return this.syms.errSymbol;
      } else {
         JavaFileObject var2 = this.log.useSource((JavaFileObject)null);

         try {
            Object var3 = null;
            String[] var4 = var1.split("\\.", -1);
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String var7 = var4[var6];
               if (!SourceVersion.isIdentifier(var7)) {
                  Symbol.ClassSymbol var8 = this.syms.errSymbol;
                  return var8;
               }

               var3 = var3 == null ? this.make.Ident(this.names.fromString(var7)) : this.make.Select((JCTree.JCExpression)var3, (Name)this.names.fromString(var7));
            }

            JCTree.JCCompilationUnit var12 = this.make.TopLevel(List.nil(), (JCTree.JCExpression)null, List.nil());
            var12.packge = this.syms.unnamedPackage;
            Symbol var13 = this.attr.attribIdent((JCTree)var3, var12);
            return var13;
         } finally {
            this.log.useSource(var2);
         }
      }
   }

   JavaFileObject printSource(Env var1, JCTree.JCClassDecl var2) throws IOException {
      JavaFileObject var3 = this.fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, var2.sym.flatname.toString(), Kind.SOURCE, (FileObject)null);
      if (this.inputFiles.contains(var3)) {
         this.log.error(var2.pos(), "source.cant.overwrite.input.file", new Object[]{var3});
         return null;
      } else {
         BufferedWriter var4 = new BufferedWriter(var3.openWriter());

         try {
            (new Pretty(var4, true)).printUnit(var1.toplevel, var2);
            if (this.verbose) {
               this.log.printVerbose("wrote.file", var3);
            }
         } finally {
            var4.close();
         }

         return var3;
      }
   }

   JavaFileObject genCode(Env var1, JCTree.JCClassDecl var2) throws IOException {
      try {
         if (this.gen.genClass(var1, var2) && this.errorCount() == 0) {
            return this.writer.writeClass(var2.sym);
         }
      } catch (ClassWriter.PoolOverflow var4) {
         this.log.error(var2.pos(), "limit.pool", new Object[0]);
      } catch (ClassWriter.StringOverflow var5) {
         this.log.error(var2.pos(), "limit.string.overflow", new Object[]{var5.value.substring(0, 20)});
      } catch (Symbol.CompletionFailure var6) {
         this.chk.completionError(var2.pos(), var6);
      }

      return null;
   }

   public void complete(Symbol.ClassSymbol var1) throws Symbol.CompletionFailure {
      if (this.completionFailureName == var1.fullname) {
         throw new Symbol.CompletionFailure(var1, "user-selected completion failure by class name");
      } else {
         JavaFileObject var3 = var1.classfile;
         JavaFileObject var4 = this.log.useSource(var3);

         JCTree.JCCompilationUnit var2;
         try {
            var2 = this.parse(var3, var3.getCharContent(false));
         } catch (IOException var9) {
            this.log.error("error.reading.file", new Object[]{var3, JavacFileManager.getMessage(var9)});
            var2 = this.make.TopLevel(List.nil(), (JCTree.JCExpression)null, List.nil());
         } finally {
            this.log.useSource(var4);
         }

         TaskEvent var5;
         if (!this.taskListener.isEmpty()) {
            var5 = new TaskEvent(TaskEvent.Kind.ENTER, var2);
            this.taskListener.started(var5);
         }

         this.enter.complete(List.of(var2), var1);
         if (!this.taskListener.isEmpty()) {
            var5 = new TaskEvent(TaskEvent.Kind.ENTER, var2);
            this.taskListener.finished(var5);
         }

         if (this.enter.getEnv(var1) == null) {
            boolean var11 = var2.sourcefile.isNameCompatible("package-info", Kind.SOURCE);
            JCDiagnostic var6;
            if (!var11) {
               var6 = this.diagFactory.fragment("file.doesnt.contain.class", var1.getQualifiedName());
               throw this.reader.new BadClassFile(var1, var3, var6);
            }

            if (this.enter.getEnv(var2.packge) == null) {
               var6 = this.diagFactory.fragment("file.does.not.contain.package", var1.location());
               throw this.reader.new BadClassFile(var1, var3, var6);
            }
         }

         this.implicitSourceFilesRead = true;
      }
   }

   public void compile(List var1) throws Throwable {
      this.compile(var1, List.nil(), (Iterable)null);
   }

   public void compile(List var1, List var2, Iterable var3) {
      if (var3 != null && var3.iterator().hasNext()) {
         this.explicitAnnotationProcessingRequested = true;
      }

      if (this.hasBeenUsed) {
         throw new AssertionError("attempt to reuse JavaCompiler");
      } else {
         this.hasBeenUsed = true;
         this.options.put(Option.XLINT_CUSTOM.text + "-" + Lint.LintCategory.OPTIONS.option, "true");
         this.options.remove(Option.XLINT_CUSTOM.text + Lint.LintCategory.OPTIONS.option);
         this.start_msec = now();

         try {
            this.initProcessAnnotations(var3);
            this.delegateCompiler = this.processAnnotations(this.enterTrees(this.stopIfError(CompileStates.CompileState.PARSE, this.parseFiles(var1))), var2);
            this.delegateCompiler.compile2();
            this.delegateCompiler.close();
            this.elapsed_msec = this.delegateCompiler.elapsed_msec;
         } catch (Abort var8) {
            if (this.devVerbose) {
               var8.printStackTrace(System.err);
            }
         } finally {
            if (this.procEnvImpl != null) {
               this.procEnvImpl.close();
            }

         }

      }
   }

   private void compile2() {
      try {
         label44:
         switch (this.compilePolicy) {
            case ATTR_ONLY:
               this.attribute((Queue)this.todo);
               break;
            case CHECK_ONLY:
               this.flow(this.attribute((Queue)this.todo));
               break;
            case SIMPLE:
               this.generate(this.desugar(this.flow(this.attribute((Queue)this.todo))));
               break;
            case BY_FILE:
               Queue var1 = this.todo.groupByFile();

               while(true) {
                  if (var1.isEmpty() || this.shouldStop(CompileStates.CompileState.ATTR)) {
                     break label44;
                  }

                  this.generate(this.desugar(this.flow(this.attribute((Queue)var1.remove()))));
               }
            case BY_TODO:
               while(true) {
                  if (this.todo.isEmpty()) {
                     break label44;
                  }

                  this.generate(this.desugar(this.flow(this.attribute((Env)this.todo.remove()))));
               }
            default:
               Assert.error("unknown compile policy");
         }
      } catch (Abort var2) {
         if (this.devVerbose) {
            var2.printStackTrace(System.err);
         }
      }

      if (this.verbose) {
         this.elapsed_msec = elapsed(this.start_msec);
         this.log.printVerbose("total", Long.toString(this.elapsed_msec));
      }

      this.reportDeferredDiagnostics();
      if (!this.log.hasDiagnosticListener()) {
         this.printCount("error", this.errorCount());
         this.printCount("warn", this.warningCount());
      }

   }

   public List parseFiles(Iterable var1) {
      if (this.shouldStop(CompileStates.CompileState.PARSE)) {
         return List.nil();
      } else {
         ListBuffer var2 = new ListBuffer();
         HashSet var3 = new HashSet();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            JavaFileObject var5 = (JavaFileObject)var4.next();
            if (!var3.contains(var5)) {
               var3.add(var5);
               var2.append(this.parse(var5));
            }
         }

         return var2.toList();
      }
   }

   public List enterTreesIfNeeded(List var1) {
      return this.shouldStop(CompileStates.CompileState.ATTR) ? List.nil() : this.enterTrees(var1);
   }

   public List enterTrees(List var1) {
      Iterator var2;
      JCTree.JCCompilationUnit var3;
      TaskEvent var4;
      if (!this.taskListener.isEmpty()) {
         var2 = var1.iterator();

         while(var2.hasNext()) {
            var3 = (JCTree.JCCompilationUnit)var2.next();
            var4 = new TaskEvent(TaskEvent.Kind.ENTER, var3);
            this.taskListener.started(var4);
         }
      }

      this.enter.main(var1);
      if (!this.taskListener.isEmpty()) {
         var2 = var1.iterator();

         while(var2.hasNext()) {
            var3 = (JCTree.JCCompilationUnit)var2.next();
            var4 = new TaskEvent(TaskEvent.Kind.ENTER, var3);
            this.taskListener.finished(var4);
         }
      }

      if (this.needRootClasses || this.sourceOutput || this.stubOutput) {
         ListBuffer var6 = new ListBuffer();
         Iterator var7 = var1.iterator();

         while(var7.hasNext()) {
            JCTree.JCCompilationUnit var8 = (JCTree.JCCompilationUnit)var7.next();

            for(List var5 = var8.defs; var5.nonEmpty(); var5 = var5.tail) {
               if (var5.head instanceof JCTree.JCClassDecl) {
                  var6.append((JCTree.JCClassDecl)var5.head);
               }
            }
         }

         this.rootClasses = var6.toList();
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         var3 = (JCTree.JCCompilationUnit)var2.next();
         this.inputFiles.add(var3.sourcefile);
      }

      return var1;
   }

   public void initProcessAnnotations(Iterable var1) {
      if (this.options.isSet(Option.PROC, "none")) {
         this.processAnnotations = false;
      } else if (this.procEnvImpl == null) {
         this.procEnvImpl = JavacProcessingEnvironment.instance(this.context);
         this.procEnvImpl.setProcessors(var1);
         this.processAnnotations = this.procEnvImpl.atLeastOneProcessor();
         if (this.processAnnotations) {
            this.options.put("save-parameter-names", "save-parameter-names");
            this.reader.saveParameterNames = true;
            this.keepComments = true;
            this.genEndPos = true;
            if (!this.taskListener.isEmpty()) {
               this.taskListener.started(new TaskEvent(TaskEvent.Kind.ANNOTATION_PROCESSING));
            }

            this.deferredDiagnosticHandler = new Log.DeferredDiagnosticHandler(this.log);
         } else {
            this.procEnvImpl.close();
         }
      }

   }

   public JavaCompiler processAnnotations(List var1) {
      return this.processAnnotations(var1, List.nil());
   }

   public JavaCompiler processAnnotations(List var1, List var2) {
      if (this.shouldStop(CompileStates.CompileState.PROCESS) && this.unrecoverableError()) {
         this.deferredDiagnosticHandler.reportDeferredDiagnostics();
         this.log.popDiagnosticHandler(this.deferredDiagnosticHandler);
         return this;
      } else if (!this.processAnnotations) {
         if (this.options.isSet(Option.PROC, "only")) {
            this.log.warning("proc.proc-only.requested.no.procs", new Object[0]);
            this.todo.clear();
         }

         if (!var2.isEmpty()) {
            this.log.error("proc.no.explicit.annotation.processing.requested", new Object[]{var2});
         }

         Assert.checkNull(this.deferredDiagnosticHandler);
         return this;
      } else {
         Assert.checkNonNull(this.deferredDiagnosticHandler);

         try {
            List var3 = List.nil();
            List var4 = List.nil();
            if (!var2.isEmpty()) {
               if (!this.explicitAnnotationProcessingRequested()) {
                  this.log.error("proc.no.explicit.annotation.processing.requested", new Object[]{var2});
                  this.deferredDiagnosticHandler.reportDeferredDiagnostics();
                  this.log.popDiagnosticHandler(this.deferredDiagnosticHandler);
                  return this;
               }

               boolean var5 = false;
               Iterator var6 = var2.iterator();

               while(true) {
                  while(var6.hasNext()) {
                     String var7 = (String)var6.next();
                     Symbol var8 = this.resolveBinaryNameOrIdent(var7);
                     if (var8 != null && (var8.kind != 1 || this.processPcks) && var8.kind != 137) {
                        try {
                           if (var8.kind == 1) {
                              var8.complete();
                           }

                           if (var8.exists()) {
                              if (var8.kind == 1) {
                                 var4 = var4.prepend((Symbol.PackageSymbol)var8);
                              } else {
                                 var3 = var3.prepend((Symbol.ClassSymbol)var8);
                              }
                           } else {
                              Assert.check(var8.kind == 1);
                              this.log.warning("proc.package.does.not.exist", new Object[]{var7});
                              var4 = var4.prepend((Symbol.PackageSymbol)var8);
                           }
                        } catch (Symbol.CompletionFailure var14) {
                           this.log.error("proc.cant.find.class", new Object[]{var7});
                           var5 = true;
                        }
                     } else {
                        this.log.error("proc.cant.find.class", new Object[]{var7});
                        var5 = true;
                     }
                  }

                  if (var5) {
                     this.deferredDiagnosticHandler.reportDeferredDiagnostics();
                     this.log.popDiagnosticHandler(this.deferredDiagnosticHandler);
                     return this;
                  }
                  break;
               }
            }

            JavaCompiler var18;
            try {
               JavaCompiler var17 = this.procEnvImpl.doProcessing(this.context, var1, var3, var4, this.deferredDiagnosticHandler);
               if (var17 != this) {
                  this.annotationProcessingOccurred = var17.annotationProcessingOccurred = true;
               }

               var18 = var17;
            } finally {
               this.procEnvImpl.close();
            }

            return var18;
         } catch (Symbol.CompletionFailure var16) {
            this.log.error("cant.access", new Object[]{var16.sym, var16.getDetailValue()});
            this.deferredDiagnosticHandler.reportDeferredDiagnostics();
            this.log.popDiagnosticHandler(this.deferredDiagnosticHandler);
            return this;
         }
      }
   }

   private boolean unrecoverableError() {
      if (this.deferredDiagnosticHandler != null) {
         Iterator var1 = this.deferredDiagnosticHandler.getDiagnostics().iterator();

         while(var1.hasNext()) {
            JCDiagnostic var2 = (JCDiagnostic)var1.next();
            if (var2.getKind() == javax.tools.Diagnostic.Kind.ERROR && !var2.isFlagSet(JCDiagnostic.DiagnosticFlag.RECOVERABLE)) {
               return true;
            }
         }
      }

      return false;
   }

   boolean explicitAnnotationProcessingRequested() {
      return this.explicitAnnotationProcessingRequested || explicitAnnotationProcessingRequested(this.options);
   }

   static boolean explicitAnnotationProcessingRequested(Options var0) {
      return var0.isSet(Option.PROCESSOR) || var0.isSet(Option.PROCESSORPATH) || var0.isSet(Option.PROC, "only") || var0.isSet(Option.XPRINT);
   }

   public Queue attribute(Queue var1) {
      ListBuffer var2 = new ListBuffer();

      while(!var1.isEmpty()) {
         var2.append(this.attribute((Env)var1.remove()));
      }

      return this.stopIfError(CompileStates.CompileState.ATTR, (Queue)var2);
   }

   public Env attribute(Env var1) {
      if (this.compileStates.isDone(var1, CompileStates.CompileState.ATTR)) {
         return var1;
      } else {
         if (this.verboseCompilePolicy) {
            this.printNote("[attribute " + var1.enclClass.sym + "]");
         }

         if (this.verbose) {
            this.log.printVerbose("checking.attribution", var1.enclClass.sym);
         }

         if (!this.taskListener.isEmpty()) {
            TaskEvent var2 = new TaskEvent(TaskEvent.Kind.ANALYZE, var1.toplevel, var1.enclClass.sym);
            this.taskListener.started(var2);
         }

         JavaFileObject var6 = this.log.useSource(var1.enclClass.sym.sourcefile != null ? var1.enclClass.sym.sourcefile : var1.toplevel.sourcefile);

         try {
            this.attr.attrib(var1);
            if (this.errorCount() > 0 && !this.shouldStop(CompileStates.CompileState.ATTR)) {
               this.attr.postAttr(var1.tree);
            }

            this.compileStates.put(var1, CompileStates.CompileState.ATTR);
            if (this.rootClasses != null && this.rootClasses.contains(var1.enclClass)) {
               this.reportPublicApi(var1.enclClass.sym);
            }
         } finally {
            this.log.useSource(var6);
         }

         return var1;
      }
   }

   public void reportPublicApi(Symbol.ClassSymbol var1) {
   }

   public Queue flow(Queue var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Env var4 = (Env)var3.next();
         this.flow(var4, var2);
      }

      return this.stopIfError(CompileStates.CompileState.FLOW, (Queue)var2);
   }

   public Queue flow(Env var1) {
      ListBuffer var2 = new ListBuffer();
      this.flow(var1, var2);
      return this.stopIfError(CompileStates.CompileState.FLOW, (Queue)var2);
   }

   protected void flow(Env var1, Queue var2) {
      if (this.compileStates.isDone(var1, CompileStates.CompileState.FLOW)) {
         var2.add(var1);
      } else {
         boolean var11 = false;

         TaskEvent var16;
         label252: {
            label253: {
               label254: {
                  try {
                     var11 = true;
                     if (this.shouldStop(CompileStates.CompileState.FLOW)) {
                        var11 = false;
                        break label252;
                     }

                     if (this.relax) {
                        var2.add(var1);
                        var11 = false;
                        break label253;
                     }

                     if (this.verboseCompilePolicy) {
                        this.printNote("[flow " + var1.enclClass.sym + "]");
                     }

                     JavaFileObject var3 = this.log.useSource(var1.enclClass.sym.sourcefile != null ? var1.enclClass.sym.sourcefile : var1.toplevel.sourcefile);

                     label240: {
                        try {
                           this.make.at(0);
                           TreeMaker var4 = this.make.forToplevel(var1.toplevel);
                           this.flow.analyzeTree(var1, var4);
                           this.compileStates.put(var1, CompileStates.CompileState.FLOW);
                           if (!this.shouldStop(CompileStates.CompileState.FLOW)) {
                              var2.add(var1);
                              break label240;
                           }
                        } finally {
                           this.log.useSource(var3);
                        }

                        var11 = false;
                        break label254;
                     }

                     var11 = false;
                  } finally {
                     if (var11) {
                        if (!this.taskListener.isEmpty()) {
                           TaskEvent var8 = new TaskEvent(TaskEvent.Kind.ANALYZE, var1.toplevel, var1.enclClass.sym);
                           this.taskListener.finished(var8);
                        }

                     }
                  }

                  if (!this.taskListener.isEmpty()) {
                     var16 = new TaskEvent(TaskEvent.Kind.ANALYZE, var1.toplevel, var1.enclClass.sym);
                     this.taskListener.finished(var16);
                  }

                  return;
               }

               if (!this.taskListener.isEmpty()) {
                  TaskEvent var5 = new TaskEvent(TaskEvent.Kind.ANALYZE, var1.toplevel, var1.enclClass.sym);
                  this.taskListener.finished(var5);
               }

               return;
            }

            if (!this.taskListener.isEmpty()) {
               var16 = new TaskEvent(TaskEvent.Kind.ANALYZE, var1.toplevel, var1.enclClass.sym);
               this.taskListener.finished(var16);
            }

            return;
         }

         if (!this.taskListener.isEmpty()) {
            var16 = new TaskEvent(TaskEvent.Kind.ANALYZE, var1.toplevel, var1.enclClass.sym);
            this.taskListener.finished(var16);
         }

      }
   }

   public Queue desugar(Queue var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Env var4 = (Env)var3.next();
         this.desugar(var4, var2);
      }

      return this.stopIfError(CompileStates.CompileState.FLOW, (Queue)var2);
   }

   protected void desugar(final Env var1, Queue var2) {
      if (!this.shouldStop(CompileStates.CompileState.TRANSTYPES)) {
         if (this.implicitSourcePolicy != JavaCompiler.ImplicitSourcePolicy.NONE || this.inputFiles.contains(var1.toplevel.sourcefile)) {
            if (this.compileStates.isDone(var1, CompileStates.CompileState.LOWER)) {
               var2.addAll((Collection)this.desugaredEnvs.get(var1));
            } else {
               class ScanNested extends TreeScanner {
                  Set dependencies = new LinkedHashSet();
                  protected boolean hasLambdas;

                  public void visitClassDef(JCTree.JCClassDecl var1x) {
                     Type var2 = JavaCompiler.this.types.supertype(var1x.sym.type);

                     for(boolean var3 = false; !var3 && var2.hasTag(TypeTag.CLASS); var2 = JavaCompiler.this.types.supertype(var2)) {
                        Symbol.ClassSymbol var4 = var2.tsym.outermostClass();
                        Env var5 = JavaCompiler.this.enter.getEnv(var4);
                        if (var5 != null && var1 != var5) {
                           if (this.dependencies.add(var5)) {
                              boolean var6 = this.hasLambdas;

                              try {
                                 this.scan(var5.tree);
                              } finally {
                                 this.hasLambdas = var6;
                              }
                           }

                           var3 = true;
                        }
                     }

                     super.visitClassDef(var1x);
                  }

                  public void visitLambda(JCTree.JCLambda var1x) {
                     this.hasLambdas = true;
                     super.visitLambda(var1x);
                  }

                  public void visitReference(JCTree.JCMemberReference var1x) {
                     this.hasLambdas = true;
                     super.visitReference(var1x);
                  }
               }

               ScanNested var3 = new ScanNested();
               var3.scan(var1.tree);
               Iterator var4 = var3.dependencies.iterator();

               while(var4.hasNext()) {
                  Env var5 = (Env)var4.next();
                  if (!this.compileStates.isDone(var5, CompileStates.CompileState.FLOW)) {
                     this.desugaredEnvs.put(var5, this.desugar(this.flow(this.attribute(var5))));
                  }
               }

               if (!this.shouldStop(CompileStates.CompileState.TRANSTYPES)) {
                  if (this.verboseCompilePolicy) {
                     this.printNote("[desugar " + var1.enclClass.sym + "]");
                  }

                  JavaFileObject var13 = this.log.useSource(var1.enclClass.sym.sourcefile != null ? var1.enclClass.sym.sourcefile : var1.toplevel.sourcefile);

                  try {
                     JCTree var14 = var1.tree;
                     this.make.at(0);
                     TreeMaker var6 = this.make.forToplevel(var1.toplevel);
                     List var7;
                     if (!(var1.tree instanceof JCTree.JCCompilationUnit)) {
                        JCTree.JCClassDecl var15;
                        if (this.stubOutput) {
                           var15 = (JCTree.JCClassDecl)var1.tree;
                           if (var14 instanceof JCTree.JCClassDecl && this.rootClasses.contains((JCTree.JCClassDecl)var14) && ((var15.mods.flags & 5L) != 0L || var15.sym.packge().getQualifiedName() == this.names.java_lang)) {
                              var2.add(new Pair(var1, this.removeMethodBodies(var15)));
                           }

                           return;
                        }

                        if (this.shouldStop(CompileStates.CompileState.TRANSTYPES)) {
                           return;
                        }

                        var1.tree = this.transTypes.translateTopLevelClass(var1.tree, var6);
                        this.compileStates.put(var1, CompileStates.CompileState.TRANSTYPES);
                        if (this.source.allowLambda() && var3.hasLambdas) {
                           if (this.shouldStop(CompileStates.CompileState.UNLAMBDA)) {
                              return;
                           }

                           var1.tree = LambdaToMethod.instance(this.context).translateTopLevelClass(var1, var1.tree, var6);
                           this.compileStates.put(var1, CompileStates.CompileState.UNLAMBDA);
                        }

                        if (this.shouldStop(CompileStates.CompileState.LOWER)) {
                           return;
                        }

                        if (this.sourceOutput) {
                           var15 = (JCTree.JCClassDecl)var1.tree;
                           if (var14 instanceof JCTree.JCClassDecl && this.rootClasses.contains((JCTree.JCClassDecl)var14)) {
                              var2.add(new Pair(var1, var15));
                           }

                           return;
                        }

                        var7 = this.lower.translateTopLevelClass(var1, var1.tree, var6);
                        this.compileStates.put(var1, CompileStates.CompileState.LOWER);
                        if (this.shouldStop(CompileStates.CompileState.LOWER)) {
                           return;
                        }

                        for(List var8 = var7; var8.nonEmpty(); var8 = var8.tail) {
                           JCTree.JCClassDecl var9 = (JCTree.JCClassDecl)var8.head;
                           var2.add(new Pair(var1, var9));
                        }

                        return;
                     }

                     if (this.stubOutput || this.sourceOutput || this.printFlat) {
                        return;
                     }

                     if (!this.shouldStop(CompileStates.CompileState.LOWER)) {
                        var7 = this.lower.translateTopLevelClass(var1, var1.tree, var6);
                        if (var7.head != null) {
                           Assert.check(var7.tail.isEmpty());
                           var2.add(new Pair(var1, (JCTree.JCClassDecl)var7.head));
                        }

                        return;
                     }
                  } finally {
                     this.log.useSource(var13);
                  }

               }
            }
         }
      }
   }

   public void generate(Queue var1) {
      this.generate(var1, (Queue)null);
   }

   public void generate(Queue var1, Queue var2) {
      if (!this.shouldStop(CompileStates.CompileState.GENERATE)) {
         boolean var3 = this.stubOutput || this.sourceOutput || this.printFlat;
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            Pair var5 = (Pair)var4.next();
            Env var6 = (Env)var5.fst;
            JCTree.JCClassDecl var7 = (JCTree.JCClassDecl)var5.snd;
            if (this.verboseCompilePolicy) {
               this.printNote("[generate " + (var3 ? " source" : "code") + " " + var7.sym + "]");
            }

            if (!this.taskListener.isEmpty()) {
               TaskEvent var8 = new TaskEvent(TaskEvent.Kind.GENERATE, var6.toplevel, var7.sym);
               this.taskListener.started(var8);
            }

            JavaFileObject var15 = this.log.useSource(var6.enclClass.sym.sourcefile != null ? var6.enclClass.sym.sourcefile : var6.toplevel.sourcefile);

            label149: {
               try {
                  JavaFileObject var9;
                  if (var3) {
                     var9 = this.printSource(var6, var7);
                  } else {
                     if (this.fileManager.hasLocation(StandardLocation.NATIVE_HEADER_OUTPUT) && this.jniWriter.needsHeader(var7.sym)) {
                        this.jniWriter.write(var7.sym);
                     }

                     var9 = this.genCode(var6, var7);
                  }

                  if (var2 != null && var9 != null) {
                     var2.add(var9);
                  }
                  break label149;
               } catch (IOException var13) {
                  this.log.error(var7.pos(), "class.cant.write", new Object[]{var7.sym, var13.getMessage()});
               } finally {
                  this.log.useSource(var15);
               }

               return;
            }

            if (!this.taskListener.isEmpty()) {
               TaskEvent var16 = new TaskEvent(TaskEvent.Kind.GENERATE, var6.toplevel, var7.sym);
               this.taskListener.finished(var16);
            }
         }

      }
   }

   Map groupByFile(Queue var1) {
      LinkedHashMap var2 = new LinkedHashMap();

      Env var4;
      Object var5;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); ((Queue)var5).add(var4)) {
         var4 = (Env)var3.next();
         var5 = (Queue)var2.get(var4.toplevel);
         if (var5 == null) {
            var5 = new ListBuffer();
            var2.put(var4.toplevel, var5);
         }
      }

      return var2;
   }

   JCTree.JCClassDecl removeMethodBodies(JCTree.JCClassDecl var1) {
      final boolean var2 = (var1.mods.flags & 512L) != 0L;

      class MethodBodyRemover extends TreeTranslator {
         public void visitMethodDef(JCTree.JCMethodDecl var1) {
            JCTree.JCModifiers var10000 = var1.mods;
            var10000.flags &= -33L;

            for(Iterator var2x = var1.params.iterator(); var2x.hasNext(); var10000.flags &= -17L) {
               JCTree.JCVariableDecl var3 = (JCTree.JCVariableDecl)var2x.next();
               var10000 = var3.mods;
            }

            var1.body = null;
            super.visitMethodDef(var1);
         }

         public void visitVarDef(JCTree.JCVariableDecl var1) {
            if (var1.init != null && var1.init.type.constValue() == null) {
               var1.init = null;
            }

            super.visitVarDef(var1);
         }

         public void visitClassDef(JCTree.JCClassDecl var1) {
            ListBuffer var2x = new ListBuffer();

            for(List var3 = var1.defs; var3.tail != null; var3 = var3.tail) {
               JCTree var4 = (JCTree)var3.head;
               switch (var4.getTag()) {
                  case CLASSDEF:
                     if (var2 || (((JCTree.JCClassDecl)var4).mods.flags & 5L) != 0L || (((JCTree.JCClassDecl)var4).mods.flags & 2L) == 0L && ((JCTree.JCClassDecl)var4).sym.packge().getQualifiedName() == JavaCompiler.this.names.java_lang) {
                        var2x.append(var4);
                     }
                     break;
                  case METHODDEF:
                     if (var2 || (((JCTree.JCMethodDecl)var4).mods.flags & 5L) != 0L || ((JCTree.JCMethodDecl)var4).sym.name == JavaCompiler.this.names.init || (((JCTree.JCMethodDecl)var4).mods.flags & 2L) == 0L && ((JCTree.JCMethodDecl)var4).sym.packge().getQualifiedName() == JavaCompiler.this.names.java_lang) {
                        var2x.append(var4);
                     }
                     break;
                  case VARDEF:
                     if (var2 || (((JCTree.JCVariableDecl)var4).mods.flags & 5L) != 0L || (((JCTree.JCVariableDecl)var4).mods.flags & 2L) == 0L && ((JCTree.JCVariableDecl)var4).sym.packge().getQualifiedName() == JavaCompiler.this.names.java_lang) {
                        var2x.append(var4);
                     }
               }
            }

            var1.defs = var2x.toList();
            super.visitClassDef(var1);
         }
      }

      MethodBodyRemover var3 = new MethodBodyRemover();
      return (JCTree.JCClassDecl)var3.translate(var1);
   }

   public void reportDeferredDiagnostics() {
      if (this.errorCount() == 0 && this.annotationProcessingOccurred && this.implicitSourceFilesRead && this.implicitSourcePolicy == JavaCompiler.ImplicitSourcePolicy.UNSET) {
         if (this.explicitAnnotationProcessingRequested()) {
            this.log.warning("proc.use.implicit", new Object[0]);
         } else {
            this.log.warning("proc.use.proc.or.implicit", new Object[0]);
         }
      }

      this.chk.reportDeferredDiagnostics();
      if (this.log.compressedOutput) {
         this.log.mandatoryNote((JavaFileObject)null, "compressed.diags", new Object[0]);
      }

   }

   public void close() {
      this.close(true);
   }

   public void close(boolean var1) {
      this.rootClasses = null;
      this.reader = null;
      this.make = null;
      this.writer = null;
      this.enter = null;
      if (this.todo != null) {
         this.todo.clear();
      }

      this.todo = null;
      this.parserFactory = null;
      this.syms = null;
      this.source = null;
      this.attr = null;
      this.chk = null;
      this.gen = null;
      this.flow = null;
      this.transTypes = null;
      this.lower = null;
      this.annotate = null;
      this.types = null;
      this.log.flush();
      boolean var15 = false;

      try {
         var15 = true;
         this.fileManager.flush();
         var15 = false;
      } catch (IOException var18) {
         throw new Abort(var18);
      } finally {
         if (var15) {
            if (this.names != null && var1) {
               this.names.dispose();
            }

            this.names = null;
            Iterator var7 = this.closeables.iterator();

            while(var7.hasNext()) {
               Closeable var8 = (Closeable)var7.next();

               try {
                  var8.close();
               } catch (IOException var16) {
                  JCDiagnostic var10 = this.diagFactory.fragment("fatal.err.cant.close");
                  throw new FatalError(var10, var16);
               }
            }

            this.closeables = List.nil();
         }
      }

      if (this.names != null && var1) {
         this.names.dispose();
      }

      this.names = null;
      Iterator var2 = this.closeables.iterator();

      while(var2.hasNext()) {
         Closeable var3 = (Closeable)var2.next();

         try {
            var3.close();
         } catch (IOException var17) {
            JCDiagnostic var5 = this.diagFactory.fragment("fatal.err.cant.close");
            throw new FatalError(var5, var17);
         }
      }

      this.closeables = List.nil();
   }

   protected void printNote(String var1) {
      this.log.printRawLines(Log.WriterKind.NOTICE, var1);
   }

   public void printCount(String var1, int var2) {
      if (var2 != 0) {
         String var3;
         if (var2 == 1) {
            var3 = "count." + var1;
         } else {
            var3 = "count." + var1 + ".plural";
         }

         this.log.printLines(Log.WriterKind.ERROR, var3, String.valueOf(var2));
         this.log.flush(Log.WriterKind.ERROR);
      }

   }

   private static long now() {
      return System.currentTimeMillis();
   }

   private static long elapsed(long var0) {
      return now() - var0;
   }

   public void initRound(JavaCompiler var1) {
      this.genEndPos = var1.genEndPos;
      this.keepComments = var1.keepComments;
      this.start_msec = var1.start_msec;
      this.hasBeenUsed = true;
      this.closeables = var1.closeables;
      var1.closeables = List.nil();
      this.shouldStopPolicyIfError = var1.shouldStopPolicyIfError;
      this.shouldStopPolicyIfNoError = var1.shouldStopPolicyIfNoError;
   }

   static {
      DEFAULT_COMPILE_POLICY = JavaCompiler.CompilePolicy.BY_TODO;
   }

   protected static enum ImplicitSourcePolicy {
      NONE,
      CLASS,
      UNSET;

      static ImplicitSourcePolicy decode(String var0) {
         if (var0 == null) {
            return UNSET;
         } else if (var0.equals("none")) {
            return NONE;
         } else {
            return var0.equals("class") ? CLASS : UNSET;
         }
      }
   }

   protected static enum CompilePolicy {
      ATTR_ONLY,
      CHECK_ONLY,
      SIMPLE,
      BY_FILE,
      BY_TODO;

      static CompilePolicy decode(String var0) {
         if (var0 == null) {
            return JavaCompiler.DEFAULT_COMPILE_POLICY;
         } else if (var0.equals("attr")) {
            return ATTR_ONLY;
         } else if (var0.equals("check")) {
            return CHECK_ONLY;
         } else if (var0.equals("simple")) {
            return SIMPLE;
         } else if (var0.equals("byfile")) {
            return BY_FILE;
         } else {
            return var0.equals("bytodo") ? BY_TODO : JavaCompiler.DEFAULT_COMPILE_POLICY;
         }
      }
   }
}
