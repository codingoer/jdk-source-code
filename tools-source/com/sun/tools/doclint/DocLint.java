package com.sun.tools.doclint;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.util.Context;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.lang.model.element.Name;
import javax.tools.DiagnosticListener;
import javax.tools.StandardLocation;

public class DocLint implements Plugin {
   public static final String XMSGS_OPTION = "-Xmsgs";
   public static final String XMSGS_CUSTOM_PREFIX = "-Xmsgs:";
   private static final String STATS = "-stats";
   public static final String XIMPLICIT_HEADERS = "-XimplicitHeaders:";
   public static final String XCUSTOM_TAGS_PREFIX = "-XcustomTags:";
   public static final String TAGS_SEPARATOR = ",";
   List javacBootClassPath;
   List javacClassPath;
   List javacSourcePath;
   List javacOpts;
   List javacFiles;
   boolean needHelp = false;
   Env env;
   Checker checker;

   public static void main(String... var0) {
      DocLint var1 = new DocLint();

      try {
         var1.run(var0);
      } catch (BadArgs var3) {
         System.err.println(var3.getMessage());
         System.exit(1);
      } catch (IOException var4) {
         System.err.println(var1.localize("dc.main.ioerror", var4.getLocalizedMessage()));
         System.exit(2);
      }

   }

   public void run(String... var1) throws BadArgs, IOException {
      PrintWriter var2 = new PrintWriter(System.out);

      try {
         this.run(var2, var1);
      } finally {
         var2.flush();
      }

   }

   public void run(PrintWriter var1, String... var2) throws BadArgs, IOException {
      this.env = new Env();
      this.processArgs(var2);
      if (this.needHelp) {
         this.showHelp(var1);
      }

      if (this.javacFiles.isEmpty() && !this.needHelp) {
         var1.println(this.localize("dc.main.no.files.given"));
      }

      JavacTool var3 = JavacTool.create();
      JavacFileManager var4 = new JavacFileManager(new Context(), false, (Charset)null);
      var4.setSymbolFileEnabled(false);
      var4.setLocation(StandardLocation.PLATFORM_CLASS_PATH, this.javacBootClassPath);
      var4.setLocation(StandardLocation.CLASS_PATH, this.javacClassPath);
      var4.setLocation(StandardLocation.SOURCE_PATH, this.javacSourcePath);
      JavacTask var5 = var3.getTask(var1, var4, (DiagnosticListener)null, this.javacOpts, (Iterable)null, var4.getJavaFileObjectsFromFiles(this.javacFiles));
      Iterable var6 = var5.parse();
      ((JavacTaskImpl)var5).enter();
      this.env.init(var5);
      this.checker = new Checker(this.env);
      DeclScanner var7 = new DeclScanner() {
         void visitDecl(Tree var1, Name var2) {
            TreePath var3 = this.getCurrentPath();
            DocCommentTree var4 = DocLint.this.env.trees.getDocCommentTree(var3);
            DocLint.this.checker.scan(var4, var3);
         }
      };
      var7.scan(var6, (Object)null);
      this.reportStats(var1);
      Context var8 = ((JavacTaskImpl)var5).getContext();
      JavaCompiler var9 = JavaCompiler.instance(var8);
      var9.printCount("error", var9.errorCount());
      var9.printCount("warn", var9.warningCount());
   }

   void processArgs(String... var1) throws BadArgs {
      this.javacOpts = new ArrayList();
      this.javacFiles = new ArrayList();
      if (var1.length == 0) {
         this.needHelp = true;
      }

      for(int var2 = 0; var2 < var1.length; ++var2) {
         String var3 = var1[var2];
         if (var3.matches("-Xmax(errs|warns)") && var2 + 1 < var1.length) {
            ++var2;
            if (!var1[var2].matches("[0-9]+")) {
               throw new BadArgs("dc.bad.value.for.option", new Object[]{var3, var1[var2]});
            }

            this.javacOpts.add(var3);
            this.javacOpts.add(var1[var2]);
         } else if (var3.equals("-stats")) {
            this.env.messages.setStatsEnabled(true);
         } else if (var3.equals("-bootclasspath") && var2 + 1 < var1.length) {
            ++var2;
            this.javacBootClassPath = this.splitPath(var1[var2]);
         } else if (var3.equals("-classpath") && var2 + 1 < var1.length) {
            ++var2;
            this.javacClassPath = this.splitPath(var1[var2]);
         } else if (var3.equals("-cp") && var2 + 1 < var1.length) {
            ++var2;
            this.javacClassPath = this.splitPath(var1[var2]);
         } else if (var3.equals("-sourcepath") && var2 + 1 < var1.length) {
            ++var2;
            this.javacSourcePath = this.splitPath(var1[var2]);
         } else if (var3.equals("-Xmsgs")) {
            this.env.messages.setOptions((String)null);
         } else if (var3.startsWith("-Xmsgs:")) {
            this.env.messages.setOptions(var3.substring(var3.indexOf(":") + 1));
         } else if (var3.startsWith("-XcustomTags:")) {
            this.env.setCustomTags(var3.substring(var3.indexOf(":") + 1));
         } else if (!var3.equals("-h") && !var3.equals("-help") && !var3.equals("--help") && !var3.equals("-?") && !var3.equals("-usage")) {
            if (var3.startsWith("-")) {
               throw new BadArgs("dc.bad.option", new Object[]{var3});
            }

            while(var2 < var1.length) {
               this.javacFiles.add(new File(var1[var2++]));
            }
         } else {
            this.needHelp = true;
         }
      }

   }

   void showHelp(PrintWriter var1) {
      String var2 = this.localize("dc.main.usage");
      String[] var3 = var2.split("\n");
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var1.println(var6);
      }

   }

   List splitPath(String var1) {
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

   public String getName() {
      return "doclint";
   }

   public void init(JavacTask var1, String... var2) {
      this.init(var1, var2, true);
   }

   public void init(JavacTask var1, String[] var2, boolean var3) {
      this.env = new Env();

      for(int var4 = 0; var4 < var2.length; ++var4) {
         String var5 = var2[var4];
         if (var5.equals("-Xmsgs")) {
            this.env.messages.setOptions((String)null);
         } else if (var5.startsWith("-Xmsgs:")) {
            this.env.messages.setOptions(var5.substring(var5.indexOf(":") + 1));
         } else if (var5.matches("-XimplicitHeaders:[1-6]")) {
            char var6 = var5.charAt(var5.length() - 1);
            this.env.setImplicitHeaders(Character.digit(var6, 10));
         } else {
            if (!var5.startsWith("-XcustomTags:")) {
               throw new IllegalArgumentException(var5);
            }

            this.env.setCustomTags(var5.substring(var5.indexOf(":") + 1));
         }
      }

      this.env.init(var1);
      this.checker = new Checker(this.env);
      if (var3) {
         final DeclScanner var7 = new DeclScanner() {
            void visitDecl(Tree var1, Name var2) {
               TreePath var3 = this.getCurrentPath();
               DocCommentTree var4 = DocLint.this.env.trees.getDocCommentTree(var3);
               DocLint.this.checker.scan(var4, var3);
            }
         };
         TaskListener var8 = new TaskListener() {
            Queue todo = new LinkedList();

            public void started(TaskEvent var1) {
               CompilationUnitTree var2;
               switch (var1.getKind()) {
                  case ANALYZE:
                     while((var2 = (CompilationUnitTree)this.todo.poll()) != null) {
                        var7.scan(var2, (Object)null);
                     }
               }

            }

            public void finished(TaskEvent var1) {
               switch (var1.getKind()) {
                  case PARSE:
                     this.todo.add(var1.getCompilationUnit());
                  default:
               }
            }
         };
         var1.addTaskListener(var8);
      }

   }

   public void scan(TreePath var1) {
      DocCommentTree var2 = this.env.trees.getDocCommentTree(var1);
      this.checker.scan(var2, var1);
   }

   public void reportStats(PrintWriter var1) {
      this.env.messages.reportStats(var1);
   }

   public static boolean isValidOption(String var0) {
      if (var0.equals("-Xmsgs")) {
         return true;
      } else {
         return var0.startsWith("-Xmsgs:") ? Messages.Options.isValidOptions(var0.substring("-Xmsgs:".length())) : false;
      }
   }

   private String localize(String var1, Object... var2) {
      Messages var3 = this.env != null ? this.env.messages : new Messages((Env)null);
      return var3.localize(var1, var2);
   }

   abstract static class DeclScanner extends TreePathScanner {
      abstract void visitDecl(Tree var1, Name var2);

      public Void visitCompilationUnit(CompilationUnitTree var1, Void var2) {
         if (var1.getPackageName() != null) {
            this.visitDecl(var1, (Name)null);
         }

         return (Void)super.visitCompilationUnit(var1, var2);
      }

      public Void visitClass(ClassTree var1, Void var2) {
         this.visitDecl(var1, var1.getSimpleName());
         return (Void)super.visitClass(var1, var2);
      }

      public Void visitMethod(MethodTree var1, Void var2) {
         this.visitDecl(var1, var1.getName());
         return null;
      }

      public Void visitVariable(VariableTree var1, Void var2) {
         this.visitDecl(var1, var1.getName());
         return (Void)super.visitVariable(var1, var2);
      }
   }

   public class BadArgs extends Exception {
      private static final long serialVersionUID = 0L;
      final String code;
      final Object[] args;

      BadArgs(String var2, Object... var3) {
         super(DocLint.this.localize(var2, var3));
         this.code = var2;
         this.args = var3;
      }
   }
}
