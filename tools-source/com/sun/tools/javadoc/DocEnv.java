package com.sun.tools.javadoc;

import com.sun.javadoc.SourcePosition;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.tools.doclint.DocLint;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Check;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.Names;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.tools.JavaFileManager;

public class DocEnv {
   protected static final Context.Key docEnvKey = new Context.Key();
   private Messager messager;
   DocLocale doclocale;
   Symtab syms;
   JavadocClassReader reader;
   JavadocEnter enter;
   private final Names names;
   private String encoding;
   final Symbol externalizableSym;
   protected ModifierFilter showAccess;
   boolean breakiterator;
   boolean quiet = false;
   Check chk;
   Types types;
   JavaFileManager fileManager;
   Context context;
   DocLint doclint;
   JavaScriptScanner javaScriptScanner;
   WeakHashMap treePaths = new WeakHashMap();
   boolean docClasses = false;
   protected boolean legacyDoclet = true;
   private boolean silent = false;
   protected Source source;
   protected Map packageMap = new HashMap();
   protected Map classMap = new HashMap();
   protected Map fieldMap = new HashMap();
   protected Map methodMap = new HashMap();

   public static DocEnv instance(Context var0) {
      DocEnv var1 = (DocEnv)var0.get(docEnvKey);
      if (var1 == null) {
         var1 = new DocEnv(var0);
      }

      return var1;
   }

   protected DocEnv(Context var1) {
      var1.put((Context.Key)docEnvKey, (Object)this);
      this.context = var1;
      this.messager = Messager.instance0(var1);
      this.syms = Symtab.instance(var1);
      this.reader = JavadocClassReader.instance0(var1);
      this.enter = JavadocEnter.instance0(var1);
      this.names = Names.instance(var1);
      this.externalizableSym = this.reader.enterClass(this.names.fromString("java.io.Externalizable"));
      this.chk = Check.instance(var1);
      this.types = Types.instance(var1);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      if (this.fileManager instanceof JavacFileManager) {
         ((JavacFileManager)this.fileManager).setSymbolFileEnabled(false);
      }

      this.doclocale = new DocLocale(this, "", this.breakiterator);
      this.source = Source.instance(var1);
   }

   public void setSilent(boolean var1) {
      this.silent = var1;
   }

   public ClassDocImpl lookupClass(String var1) {
      Symbol.ClassSymbol var2 = this.getClassSymbol(var1);
      return var2 != null ? this.getClassDoc(var2) : null;
   }

   public ClassDocImpl loadClass(String var1) {
      try {
         Symbol.ClassSymbol var2 = this.reader.loadClass(this.names.fromString(var1));
         return this.getClassDoc(var2);
      } catch (Symbol.CompletionFailure var3) {
         this.chk.completionError((JCDiagnostic.DiagnosticPosition)null, var3);
         return null;
      }
   }

   public PackageDocImpl lookupPackage(String var1) {
      Symbol.PackageSymbol var2 = (Symbol.PackageSymbol)this.syms.packages.get(this.names.fromString(var1));
      Symbol.ClassSymbol var3 = this.getClassSymbol(var1);
      return var2 != null && var3 == null ? this.getPackageDoc(var2) : null;
   }

   Symbol.ClassSymbol getClassSymbol(String var1) {
      int var2 = var1.length();
      char[] var3 = var1.toCharArray();
      int var4 = var1.length();

      while(true) {
         Symbol.ClassSymbol var5 = (Symbol.ClassSymbol)this.syms.classes.get(this.names.fromChars(var3, 0, var2));
         if (var5 != null) {
            return var5;
         }

         var4 = var1.substring(0, var4).lastIndexOf(46);
         if (var4 < 0) {
            return null;
         }

         var3[var4] = '$';
      }
   }

   public void setLocale(String var1) {
      this.doclocale = new DocLocale(this, var1, this.breakiterator);
      this.messager.setLocale(this.doclocale.locale);
   }

   public boolean shouldDocument(Symbol.VarSymbol var1) {
      long var2 = var1.flags();
      return (var2 & 4096L) != 0L ? false : this.showAccess.checkModifier(translateModifiers(var2));
   }

   public boolean shouldDocument(Symbol.MethodSymbol var1) {
      long var2 = var1.flags();
      return (var2 & 4096L) != 0L ? false : this.showAccess.checkModifier(translateModifiers(var2));
   }

   public boolean shouldDocument(Symbol.ClassSymbol var1) {
      return (var1.flags_field & 4096L) == 0L && (this.docClasses || this.getClassDoc(var1).tree != null) && this.isVisible(var1);
   }

   protected boolean isVisible(Symbol.ClassSymbol var1) {
      long var2 = var1.flags_field;
      if (!this.showAccess.checkModifier(translateModifiers(var2))) {
         return false;
      } else {
         Symbol.ClassSymbol var4 = var1.owner.enclClass();
         return var4 == null || (var2 & 8L) != 0L || this.isVisible(var4);
      }
   }

   public void printError(String var1) {
      if (!this.silent) {
         this.messager.printError(var1);
      }
   }

   public void error(DocImpl var1, String var2) {
      if (!this.silent) {
         this.messager.error(var1 == null ? null : var1.position(), var2);
      }
   }

   public void error(SourcePosition var1, String var2) {
      if (!this.silent) {
         this.messager.error(var1, var2);
      }
   }

   public void printError(SourcePosition var1, String var2) {
      if (!this.silent) {
         this.messager.printError(var1, var2);
      }
   }

   public void error(DocImpl var1, String var2, String var3) {
      if (!this.silent) {
         this.messager.error(var1 == null ? null : var1.position(), var2, var3);
      }
   }

   public void error(DocImpl var1, String var2, String var3, String var4) {
      if (!this.silent) {
         this.messager.error(var1 == null ? null : var1.position(), var2, var3, var4);
      }
   }

   public void error(DocImpl var1, String var2, String var3, String var4, String var5) {
      if (!this.silent) {
         this.messager.error(var1 == null ? null : var1.position(), var2, var3, var4, var5);
      }
   }

   public void printWarning(String var1) {
      if (!this.silent) {
         this.messager.printWarning(var1);
      }
   }

   public void warning(DocImpl var1, String var2) {
      if (!this.silent) {
         this.messager.warning(var1 == null ? null : var1.position(), var2);
      }
   }

   public void printWarning(SourcePosition var1, String var2) {
      if (!this.silent) {
         this.messager.printWarning(var1, var2);
      }
   }

   public void warning(DocImpl var1, String var2, String var3) {
      if (!this.silent) {
         if (this.doclint == null || var1 == null || !var2.startsWith("tag")) {
            this.messager.warning(var1 == null ? null : var1.position(), var2, var3);
         }
      }
   }

   public void warning(DocImpl var1, String var2, String var3, String var4) {
      if (!this.silent) {
         this.messager.warning(var1 == null ? null : var1.position(), var2, var3, var4);
      }
   }

   public void warning(DocImpl var1, String var2, String var3, String var4, String var5) {
      if (!this.silent) {
         this.messager.warning(var1 == null ? null : var1.position(), var2, var3, var4, var5);
      }
   }

   public void warning(DocImpl var1, String var2, String var3, String var4, String var5, String var6) {
      if (!this.silent) {
         this.messager.warning(var1 == null ? null : var1.position(), var2, var3, var4, var5, var6);
      }
   }

   public void printNotice(String var1) {
      if (!this.silent && !this.quiet) {
         this.messager.printNotice(var1);
      }
   }

   public void notice(String var1) {
      if (!this.silent && !this.quiet) {
         this.messager.notice(var1);
      }
   }

   public void printNotice(SourcePosition var1, String var2) {
      if (!this.silent && !this.quiet) {
         this.messager.printNotice(var1, var2);
      }
   }

   public void notice(String var1, String var2) {
      if (!this.silent && !this.quiet) {
         this.messager.notice(var1, var2);
      }
   }

   public void notice(String var1, String var2, String var3) {
      if (!this.silent && !this.quiet) {
         this.messager.notice(var1, var2, var3);
      }
   }

   public void notice(String var1, String var2, String var3, String var4) {
      if (!this.silent && !this.quiet) {
         this.messager.notice(var1, var2, var3, var4);
      }
   }

   public void exit() {
      this.messager.exit();
   }

   public PackageDocImpl getPackageDoc(Symbol.PackageSymbol var1) {
      PackageDocImpl var2 = (PackageDocImpl)this.packageMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = new PackageDocImpl(this, var1);
         this.packageMap.put(var1, var2);
         return var2;
      }
   }

   void makePackageDoc(Symbol.PackageSymbol var1, TreePath var2) {
      PackageDocImpl var3 = (PackageDocImpl)this.packageMap.get(var1);
      if (var3 != null) {
         if (var2 != null) {
            var3.setTreePath(var2);
         }
      } else {
         var3 = new PackageDocImpl(this, var1, var2);
         this.packageMap.put(var1, var3);
      }

   }

   public ClassDocImpl getClassDoc(Symbol.ClassSymbol var1) {
      ClassDocImpl var2 = (ClassDocImpl)this.classMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Object var3;
         if (isAnnotationType(var1)) {
            var3 = new AnnotationTypeDocImpl(this, var1);
         } else {
            var3 = new ClassDocImpl(this, var1);
         }

         this.classMap.put(var1, var3);
         return (ClassDocImpl)var3;
      }
   }

   protected void makeClassDoc(Symbol.ClassSymbol var1, TreePath var2) {
      ClassDocImpl var3 = (ClassDocImpl)this.classMap.get(var1);
      if (var3 != null) {
         if (var2 != null) {
            var3.setTreePath(var2);
         }

      } else {
         Object var4;
         if (isAnnotationType((JCTree.JCClassDecl)var2.getLeaf())) {
            var4 = new AnnotationTypeDocImpl(this, var1, var2);
         } else {
            var4 = new ClassDocImpl(this, var1, var2);
         }

         this.classMap.put(var1, var4);
      }
   }

   protected static boolean isAnnotationType(Symbol.ClassSymbol var0) {
      return ClassDocImpl.isAnnotationType(var0);
   }

   protected static boolean isAnnotationType(JCTree.JCClassDecl var0) {
      return (var0.mods.flags & 8192L) != 0L;
   }

   public FieldDocImpl getFieldDoc(Symbol.VarSymbol var1) {
      FieldDocImpl var2 = (FieldDocImpl)this.fieldMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = new FieldDocImpl(this, var1);
         this.fieldMap.put(var1, var2);
         return var2;
      }
   }

   protected void makeFieldDoc(Symbol.VarSymbol var1, TreePath var2) {
      FieldDocImpl var3 = (FieldDocImpl)this.fieldMap.get(var1);
      if (var3 != null) {
         if (var2 != null) {
            var3.setTreePath(var2);
         }
      } else {
         var3 = new FieldDocImpl(this, var1, var2);
         this.fieldMap.put(var1, var3);
      }

   }

   protected void makeMethodDoc(Symbol.MethodSymbol var1, TreePath var2) {
      MethodDocImpl var3 = (MethodDocImpl)this.methodMap.get(var1);
      if (var3 != null) {
         if (var2 != null) {
            var3.setTreePath(var2);
         }
      } else {
         var3 = new MethodDocImpl(this, var1, var2);
         this.methodMap.put(var1, var3);
      }

   }

   public MethodDocImpl getMethodDoc(Symbol.MethodSymbol var1) {
      assert !var1.isConstructor() : "not expecting a constructor symbol";

      MethodDocImpl var2 = (MethodDocImpl)this.methodMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = new MethodDocImpl(this, var1);
         this.methodMap.put(var1, var2);
         return var2;
      }
   }

   protected void makeConstructorDoc(Symbol.MethodSymbol var1, TreePath var2) {
      ConstructorDocImpl var3 = (ConstructorDocImpl)this.methodMap.get(var1);
      if (var3 != null) {
         if (var2 != null) {
            var3.setTreePath(var2);
         }
      } else {
         var3 = new ConstructorDocImpl(this, var1, var2);
         this.methodMap.put(var1, var3);
      }

   }

   public ConstructorDocImpl getConstructorDoc(Symbol.MethodSymbol var1) {
      assert var1.isConstructor() : "expecting a constructor symbol";

      ConstructorDocImpl var2 = (ConstructorDocImpl)this.methodMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = new ConstructorDocImpl(this, var1);
         this.methodMap.put(var1, var2);
         return var2;
      }
   }

   protected void makeAnnotationTypeElementDoc(Symbol.MethodSymbol var1, TreePath var2) {
      AnnotationTypeElementDocImpl var3 = (AnnotationTypeElementDocImpl)this.methodMap.get(var1);
      if (var3 != null) {
         if (var2 != null) {
            var3.setTreePath(var2);
         }
      } else {
         var3 = new AnnotationTypeElementDocImpl(this, var1, var2);
         this.methodMap.put(var1, var3);
      }

   }

   public AnnotationTypeElementDocImpl getAnnotationTypeElementDoc(Symbol.MethodSymbol var1) {
      AnnotationTypeElementDocImpl var2 = (AnnotationTypeElementDocImpl)this.methodMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         var2 = new AnnotationTypeElementDocImpl(this, var1);
         this.methodMap.put(var1, var2);
         return var2;
      }
   }

   ParameterizedTypeImpl getParameterizedType(Type.ClassType var1) {
      return new ParameterizedTypeImpl(this, var1);
   }

   TreePath getTreePath(JCTree.JCCompilationUnit var1) {
      TreePath var2 = (TreePath)this.treePaths.get(var1);
      if (var2 == null) {
         this.treePaths.put(var1, var2 = new TreePath(var1));
      }

      return var2;
   }

   TreePath getTreePath(JCTree.JCCompilationUnit var1, JCTree.JCClassDecl var2) {
      TreePath var3 = (TreePath)this.treePaths.get(var2);
      if (var3 == null) {
         this.treePaths.put(var2, var3 = new TreePath(this.getTreePath(var1), var2));
      }

      return var3;
   }

   TreePath getTreePath(JCTree.JCCompilationUnit var1, JCTree.JCClassDecl var2, JCTree var3) {
      return new TreePath(this.getTreePath(var1, var2), var3);
   }

   public void setEncoding(String var1) {
      this.encoding = var1;
   }

   public String getEncoding() {
      return this.encoding;
   }

   static int translateModifiers(long var0) {
      int var2 = 0;
      if ((var0 & 1024L) != 0L) {
         var2 |= 1024;
      }

      if ((var0 & 16L) != 0L) {
         var2 |= 16;
      }

      if ((var0 & 512L) != 0L) {
         var2 |= 512;
      }

      if ((var0 & 256L) != 0L) {
         var2 |= 256;
      }

      if ((var0 & 2L) != 0L) {
         var2 |= 2;
      }

      if ((var0 & 4L) != 0L) {
         var2 |= 4;
      }

      if ((var0 & 1L) != 0L) {
         var2 |= 1;
      }

      if ((var0 & 8L) != 0L) {
         var2 |= 8;
      }

      if ((var0 & 32L) != 0L) {
         var2 |= 32;
      }

      if ((var0 & 128L) != 0L) {
         var2 |= 128;
      }

      if ((var0 & 64L) != 0L) {
         var2 |= 64;
      }

      return var2;
   }

   void initDoclint(Collection var1, Collection var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.add(var5 == null ? "-Xmsgs" : "-Xmsgs:" + var5);
      }

      if (var3.isEmpty()) {
         var3.add("-Xmsgs");
      } else if (var3.size() == 1 && ((String)var3.get(0)).equals("-Xmsgs:none")) {
         return;
      }

      String var8 = "";
      StringBuilder var9 = new StringBuilder();

      for(Iterator var6 = var2.iterator(); var6.hasNext(); var8 = ",") {
         String var7 = (String)var6.next();
         var9.append(var8);
         var9.append(var7);
      }

      var3.add("-XcustomTags:" + var9.toString());
      JavacTask var10 = BasicJavacTask.instance(this.context);
      this.doclint = new DocLint();
      var3.add("-XimplicitHeaders:2");
      this.doclint.init(var10, (String[])var3.toArray(new String[var3.size()]), false);
   }

   JavaScriptScanner initJavaScriptScanner(boolean var1) {
      if (var1) {
         this.javaScriptScanner = null;
      } else {
         this.javaScriptScanner = new JavaScriptScanner();
      }

      return this.javaScriptScanner;
   }

   boolean showTagMessages() {
      return this.doclint == null;
   }
}
