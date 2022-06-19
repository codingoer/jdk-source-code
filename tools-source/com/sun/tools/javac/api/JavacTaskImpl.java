package com.sun.tools.javac.api;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.CommandLine;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Main;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

public class JavacTaskImpl extends BasicJavacTask {
   private Main compilerMain;
   private JavaCompiler compiler;
   private Locale locale;
   private String[] args;
   private String[] classNames;
   private List fileObjects;
   private Map notYetEntered;
   private ListBuffer genList;
   private final AtomicBoolean used;
   private Iterable processors;
   private Main.Result result;
   private boolean parsed;

   JavacTaskImpl(Main var1, String[] var2, String[] var3, Context var4, List var5) {
      super((Context)null, false);
      this.used = new AtomicBoolean();
      this.result = null;
      this.parsed = false;
      this.compilerMain = var1;
      this.args = var2;
      this.classNames = var3;
      this.context = var4;
      this.fileObjects = var5;
      this.setLocale(Locale.getDefault());
      var1.getClass();
      var2.getClass();
      var5.getClass();
   }

   JavacTaskImpl(Main var1, Iterable var2, Context var3, Iterable var4, Iterable var5) {
      this(var1, toArray(var2), toArray(var4), var3, toList(var5));
   }

   private static String[] toArray(Iterable var0) {
      ListBuffer var1 = new ListBuffer();
      if (var0 != null) {
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            var1.append(var3);
         }
      }

      return (String[])var1.toArray(new String[var1.length()]);
   }

   private static List toList(Iterable var0) {
      if (var0 == null) {
         return List.nil();
      } else {
         ListBuffer var1 = new ListBuffer();
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            JavaFileObject var3 = (JavaFileObject)var2.next();
            var1.append(var3);
         }

         return var1.toList();
      }
   }

   public Main.Result doCall() {
      if (!this.used.getAndSet(true)) {
         this.initContext();
         this.notYetEntered = new HashMap();
         this.compilerMain.setAPIMode(true);
         this.result = this.compilerMain.compile(this.args, this.classNames, this.context, this.fileObjects, this.processors);
         this.cleanup();
         return this.result;
      } else {
         throw new IllegalStateException("multiple calls to method 'call'");
      }
   }

   public Boolean call() {
      return this.doCall().isOK();
   }

   public void setProcessors(Iterable var1) {
      var1.getClass();
      if (this.used.get()) {
         throw new IllegalStateException();
      } else {
         this.processors = var1;
      }
   }

   public void setLocale(Locale var1) {
      if (this.used.get()) {
         throw new IllegalStateException();
      } else {
         this.locale = var1;
      }
   }

   private void prepareCompiler() throws IOException {
      if (this.used.getAndSet(true)) {
         if (this.compiler == null) {
            throw new IllegalStateException();
         }
      } else {
         this.initContext();
         this.compilerMain.log = Log.instance(this.context);
         this.compilerMain.setOptions(Options.instance(this.context));
         this.compilerMain.filenames = new LinkedHashSet();
         Collection var1 = this.compilerMain.processArgs(CommandLine.parse(this.args), this.classNames);
         if (var1 != null && !var1.isEmpty()) {
            throw new IllegalArgumentException("Malformed arguments " + this.toString(var1, " "));
         }

         this.compiler = JavaCompiler.instance(this.context);
         this.compiler.keepComments = true;
         this.compiler.genEndPos = true;
         this.compiler.initProcessAnnotations(this.processors);
         this.notYetEntered = new HashMap();
         Iterator var2 = this.fileObjects.iterator();

         while(var2.hasNext()) {
            JavaFileObject var3 = (JavaFileObject)var2.next();
            this.notYetEntered.put(var3, (Object)null);
         }

         this.genList = new ListBuffer();
         this.args = null;
         this.classNames = null;
      }

   }

   String toString(Iterable var1, String var2) {
      String var3 = "";
      StringBuilder var4 = new StringBuilder();

      for(Iterator var5 = var1.iterator(); var5.hasNext(); var3 = var2) {
         Object var6 = var5.next();
         var4.append(var3);
         var4.append(var6.toString());
      }

      return var4.toString();
   }

   private void initContext() {
      this.context.put((Class)JavacTask.class, (Object)this);
      this.context.put((Class)Locale.class, (Object)this.locale);
   }

   void cleanup() {
      if (this.compiler != null) {
         this.compiler.close();
      }

      this.compiler = null;
      this.compilerMain = null;
      this.args = null;
      this.classNames = null;
      this.context = null;
      this.fileObjects = null;
      this.notYetEntered = null;
   }

   public JavaFileObject asJavaFileObject(File var1) {
      JavacFileManager var2 = (JavacFileManager)this.context.get(JavaFileManager.class);
      return var2.getRegularFile(var1);
   }

   public Iterable parse() throws IOException {
      List var8;
      try {
         this.prepareCompiler();
         List var1 = this.compiler.parseFiles(this.fileObjects);
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            JCTree.JCCompilationUnit var3 = (JCTree.JCCompilationUnit)var2.next();
            JavaFileObject var4 = var3.getSourceFile();
            if (this.notYetEntered.containsKey(var4)) {
               this.notYetEntered.put(var4, var3);
            }
         }

         var8 = var1;
      } finally {
         this.parsed = true;
         if (this.compiler != null && this.compiler.log != null) {
            this.compiler.log.flush();
         }

      }

      return var8;
   }

   public Iterable enter() throws IOException {
      return this.enter((Iterable)null);
   }

   public Iterable enter(Iterable var1) throws IOException {
      if (var1 == null && this.notYetEntered != null && this.notYetEntered.isEmpty()) {
         return List.nil();
      } else {
         this.prepareCompiler();
         ListBuffer var2 = null;
         Iterator var3;
         if (var1 == null) {
            if (this.notYetEntered.size() > 0) {
               if (!this.parsed) {
                  this.parse();
               }

               var3 = this.fileObjects.iterator();

               while(var3.hasNext()) {
                  JavaFileObject var4 = (JavaFileObject)var3.next();
                  JCTree.JCCompilationUnit var5 = (JCTree.JCCompilationUnit)this.notYetEntered.remove(var4);
                  if (var5 != null) {
                     if (var2 == null) {
                        var2 = new ListBuffer();
                     }

                     var2.append(var5);
                  }
               }

               this.notYetEntered.clear();
            }
         } else {
            var3 = var1.iterator();

            while(var3.hasNext()) {
               CompilationUnitTree var14 = (CompilationUnitTree)var3.next();
               if (!(var14 instanceof JCTree.JCCompilationUnit)) {
                  throw new IllegalArgumentException(var14.toString());
               }

               if (var2 == null) {
                  var2 = new ListBuffer();
               }

               var2.append((JCTree.JCCompilationUnit)var14);
               this.notYetEntered.remove(var14.getSourceFile());
            }
         }

         if (var2 == null) {
            return List.nil();
         } else {
            List var17;
            try {
               List var13 = this.compiler.enterTrees(var2.toList());
               if (this.notYetEntered.isEmpty()) {
                  this.compiler = this.compiler.processAnnotations(var13);
               }

               ListBuffer var15 = new ListBuffer();
               Iterator var16 = var13.iterator();

               while(var16.hasNext()) {
                  JCTree.JCCompilationUnit var6 = (JCTree.JCCompilationUnit)var16.next();
                  Iterator var7 = var6.defs.iterator();

                  while(var7.hasNext()) {
                     JCTree var8 = (JCTree)var7.next();
                     if (var8.hasTag(JCTree.Tag.CLASSDEF)) {
                        JCTree.JCClassDecl var9 = (JCTree.JCClassDecl)var8;
                        if (var9.sym != null) {
                           var15.append(var9.sym);
                        }
                     }
                  }
               }

               var17 = var15.toList();
            } finally {
               this.compiler.log.flush();
            }

            return var17;
         }
      }
   }

   public Iterable analyze() throws IOException {
      return this.analyze((Iterable)null);
   }

   public Iterable analyze(Iterable var1) throws IOException {
      this.enter((Iterable)null);
      final ListBuffer var2 = new ListBuffer();

      try {
         if (var1 == null) {
            this.handleFlowResults(this.compiler.flow(this.compiler.attribute((Queue)this.compiler.todo)), var2);
         } else {
            Filter var3 = new Filter() {
               public void process(Env var1) {
                  JavacTaskImpl.this.handleFlowResults(JavacTaskImpl.this.compiler.flow(JavacTaskImpl.this.compiler.attribute(var1)), var2);
               }
            };
            var3.run(this.compiler.todo, var1);
         }
      } finally {
         this.compiler.log.flush();
      }

      return var2;
   }

   private void handleFlowResults(Queue var1, ListBuffer var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Env var4 = (Env)var3.next();
         switch (var4.tree.getTag()) {
            case CLASSDEF:
               JCTree.JCClassDecl var5 = (JCTree.JCClassDecl)var4.tree;
               if (var5.sym != null) {
                  var2.append(var5.sym);
               }
               break;
            case TOPLEVEL:
               JCTree.JCCompilationUnit var6 = (JCTree.JCCompilationUnit)var4.tree;
               if (var6.packge != null) {
                  var2.append(var6.packge);
               }
         }
      }

      this.genList.addAll(var1);
   }

   public Iterable generate() throws IOException {
      return this.generate((Iterable)null);
   }

   public Iterable generate(Iterable var1) throws IOException {
      final ListBuffer var2 = new ListBuffer();

      try {
         this.analyze((Iterable)null);
         if (var1 == null) {
            this.compiler.generate(this.compiler.desugar(this.genList), var2);
            this.genList.clear();
         } else {
            Filter var3 = new Filter() {
               public void process(Env var1) {
                  JavacTaskImpl.this.compiler.generate(JavacTaskImpl.this.compiler.desugar(ListBuffer.of(var1)), var2);
               }
            };
            var3.run(this.genList, var1);
         }

         if (this.genList.isEmpty()) {
            this.compiler.reportDeferredDiagnostics();
            this.cleanup();
         }
      } finally {
         if (this.compiler != null) {
            this.compiler.log.flush();
         }

      }

      return var2;
   }

   public TypeMirror getTypeMirror(Iterable var1) {
      Tree var2 = null;

      Tree var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var2 = var4) {
         var4 = (Tree)var3.next();
      }

      return ((JCTree)var2).type;
   }

   public JavacElements getElements() {
      if (this.context == null) {
         throw new IllegalStateException();
      } else {
         return JavacElements.instance(this.context);
      }
   }

   public JavacTypes getTypes() {
      if (this.context == null) {
         throw new IllegalStateException();
      } else {
         return JavacTypes.instance(this.context);
      }
   }

   public Iterable pathFor(CompilationUnitTree var1, Tree var2) {
      return TreeInfo.pathFor((JCTree)var2, (JCTree.JCCompilationUnit)var1).reverse();
   }

   public Type parseType(String var1, TypeElement var2) {
      if (var1 != null && !var1.equals("")) {
         this.compiler = JavaCompiler.instance(this.context);
         JavaFileObject var3 = this.compiler.log.useSource((JavaFileObject)null);
         ParserFactory var4 = ParserFactory.instance(this.context);
         Attr var5 = Attr.instance(this.context);

         Type var9;
         try {
            CharBuffer var6 = CharBuffer.wrap((var1 + "\u0000").toCharArray(), 0, var1.length());
            JavacParser var7 = var4.newParser(var6, false, false, false);
            JCTree.JCExpression var8 = var7.parseType();
            var9 = var5.attribType(var8, (Symbol.TypeSymbol)((Symbol.TypeSymbol)var2));
         } finally {
            this.compiler.log.useSource(var3);
         }

         return var9;
      } else {
         throw new IllegalArgumentException();
      }
   }

   abstract class Filter {
      void run(Queue var1, Iterable var2) {
         HashSet var3 = new HashSet();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            TypeElement var5 = (TypeElement)var4.next();
            var3.add(var5);
         }

         ListBuffer var8 = new ListBuffer();

         while(true) {
            while(var1.peek() != null) {
               Env var7 = (Env)var1.remove();
               Symbol.ClassSymbol var6 = var7.enclClass.sym;
               if (var6 != null && var3.contains(var6.outermostClass())) {
                  this.process(var7);
               } else {
                  var8 = var8.append(var7);
               }
            }

            var1.addAll(var8);
            return;
         }
      }

      abstract void process(Env var1);
   }
}
