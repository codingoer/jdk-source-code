package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import java.util.Iterator;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class Enter extends JCTree.Visitor {
   protected static final Context.Key enterKey = new Context.Key();
   Log log;
   Symtab syms;
   Check chk;
   TreeMaker make;
   ClassReader reader;
   Annotate annotate;
   MemberEnter memberEnter;
   Types types;
   Lint lint;
   Names names;
   JavaFileManager fileManager;
   Option.PkgInfo pkginfoOpt;
   TypeEnvs typeEnvs;
   private final Todo todo;
   ListBuffer uncompleted;
   private JCTree.JCClassDecl predefClassDef;
   protected Env env;
   Type result;

   public static Enter instance(Context var0) {
      Enter var1 = (Enter)var0.get(enterKey);
      if (var1 == null) {
         var1 = new Enter(var0);
      }

      return var1;
   }

   protected Enter(Context var1) {
      var1.put((Context.Key)enterKey, (Object)this);
      this.log = Log.instance(var1);
      this.reader = ClassReader.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.syms = Symtab.instance(var1);
      this.chk = Check.instance(var1);
      this.memberEnter = MemberEnter.instance(var1);
      this.types = Types.instance(var1);
      this.annotate = Annotate.instance(var1);
      this.lint = Lint.instance(var1);
      this.names = Names.instance(var1);
      this.predefClassDef = this.make.ClassDef(this.make.Modifiers(1L), this.syms.predefClass.name, List.nil(), (JCTree.JCExpression)null, List.nil(), List.nil());
      this.predefClassDef.sym = this.syms.predefClass;
      this.todo = Todo.instance(var1);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      Options var2 = Options.instance(var1);
      this.pkginfoOpt = Option.PkgInfo.get(var2);
      this.typeEnvs = TypeEnvs.instance(var1);
   }

   public Env getEnv(Symbol.TypeSymbol var1) {
      return this.typeEnvs.get(var1);
   }

   public Env getClassEnv(Symbol.TypeSymbol var1) {
      Env var2 = this.getEnv(var1);

      Env var3;
      for(var3 = var2; ((AttrContext)var3.info).lint == null; var3 = var3.next) {
      }

      ((AttrContext)var2.info).lint = ((AttrContext)var3.info).lint.augment((Symbol)var1);
      return var2;
   }

   public Env classEnv(JCTree.JCClassDecl var1, Env var2) {
      Env var3 = var2.dup(var1, ((AttrContext)var2.info).dup(new Scope(var1.sym)));
      var3.enclClass = var1;
      var3.outer = var2;
      ((AttrContext)var3.info).isSelfCall = false;
      ((AttrContext)var3.info).lint = null;
      return var3;
   }

   Env topLevelEnv(JCTree.JCCompilationUnit var1) {
      Env var2 = new Env(var1, new AttrContext());
      var2.toplevel = var1;
      var2.enclClass = this.predefClassDef;
      var1.namedImportScope = new Scope.ImportScope(var1.packge);
      var1.starImportScope = new Scope.StarImportScope(var1.packge);
      ((AttrContext)var2.info).scope = var1.namedImportScope;
      ((AttrContext)var2.info).lint = this.lint;
      return var2;
   }

   public Env getTopLevelEnv(JCTree.JCCompilationUnit var1) {
      Env var2 = new Env(var1, new AttrContext());
      var2.toplevel = var1;
      var2.enclClass = this.predefClassDef;
      ((AttrContext)var2.info).scope = var1.namedImportScope;
      ((AttrContext)var2.info).lint = this.lint;
      return var2;
   }

   Scope enterScope(Env var1) {
      return var1.tree.hasTag(JCTree.Tag.CLASSDEF) ? ((JCTree.JCClassDecl)var1.tree).sym.members_field : ((AttrContext)var1.info).scope;
   }

   Type classEnter(JCTree var1, Env var2) {
      Env var3 = this.env;

      Type var5;
      try {
         this.env = var2;
         var1.accept(this);
         Type var4 = this.result;
         return var4;
      } catch (Symbol.CompletionFailure var9) {
         var5 = this.chk.completionError(var1.pos(), var9);
      } finally {
         this.env = var3;
      }

      return var5;
   }

   List classEnter(List var1, Env var2) {
      ListBuffer var3 = new ListBuffer();

      for(List var4 = var1; var4.nonEmpty(); var4 = var4.tail) {
         Type var5 = this.classEnter((JCTree)var4.head, var2);
         if (var5 != null) {
            var3.append(var5);
         }
      }

      return var3.toList();
   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      JavaFileObject var2 = this.log.useSource(var1.sourcefile);
      boolean var3 = false;
      boolean var4 = var1.sourcefile.isNameCompatible("package-info", Kind.SOURCE);
      if (var1.pid != null) {
         var1.packge = this.reader.enterPackage(TreeInfo.fullName(var1.pid));
         if (var1.packageAnnotations.nonEmpty() || this.pkginfoOpt == Option.PkgInfo.ALWAYS || var1.docComments != null) {
            if (var4) {
               var3 = true;
            } else if (var1.packageAnnotations.nonEmpty()) {
               this.log.error(((JCTree.JCAnnotation)var1.packageAnnotations.head).pos(), "pkg.annotations.sb.in.package-info.java", new Object[0]);
            }
         }
      } else {
         var1.packge = this.syms.unnamedPackage;
      }

      var1.packge.complete();
      Env var5 = this.topLevelEnv(var1);
      if (var4) {
         Env var6 = this.typeEnvs.get(var1.packge);
         if (var6 == null) {
            this.typeEnvs.put(var1.packge, var5);
         } else {
            JCTree.JCCompilationUnit var7 = var6.toplevel;
            if (!this.fileManager.isSameFile(var1.sourcefile, var7.sourcefile)) {
               this.log.warning(var1.pid != null ? var1.pid.pos() : null, "pkg-info.already.seen", new Object[]{var1.packge});
               if (var3 || var7.packageAnnotations.isEmpty() && var1.docComments != null && var1.docComments.hasComment(var1)) {
                  this.typeEnvs.put(var1.packge, var5);
               }
            }
         }

         for(Object var9 = var1.packge; var9 != null && ((Symbol)var9).kind == 1; var9 = ((Symbol)var9).owner) {
            ((Symbol)var9).flags_field |= 8388608L;
         }

         Name var10 = this.names.package_info;
         Symbol.ClassSymbol var8 = this.reader.enterClass(var10, (Symbol.TypeSymbol)var1.packge);
         var8.flatname = this.names.fromString(var1.packge + "." + var10);
         var8.sourcefile = var1.sourcefile;
         var8.completer = null;
         var8.members_field = new Scope(var8);
         var1.packge.package_info = var8;
      }

      this.classEnter(var1.defs, var5);
      if (var3) {
         this.todo.append(var5);
      }

      this.log.useSource(var2);
      this.result = null;
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      Symbol var2 = ((AttrContext)this.env.info).scope.owner;
      Scope var3 = this.enterScope(this.env);
      Symbol.ClassSymbol var4;
      if (var2.kind == 1) {
         Symbol.PackageSymbol var5 = (Symbol.PackageSymbol)var2;

         for(Object var6 = var5; var6 != null && ((Symbol)var6).kind == 1; var6 = ((Symbol)var6).owner) {
            ((Symbol)var6).flags_field |= 8388608L;
         }

         var4 = this.reader.enterClass(var1.name, (Symbol.TypeSymbol)var5);
         var5.members().enterIfAbsent(var4);
         if ((var1.mods.flags & 1L) != 0L && !classNameMatchesFileName(var4, this.env)) {
            this.log.error(var1.pos(), "class.public.should.be.in.file", new Object[]{var1.name});
         }
      } else {
         if (!var1.name.isEmpty() && !this.chk.checkUniqueClassName(var1.pos(), var1.name, var3)) {
            this.result = null;
            return;
         }

         if (var2.kind == 2) {
            var4 = this.reader.enterClass(var1.name, (Symbol.TypeSymbol)var2);
            if ((var2.flags_field & 512L) != 0L) {
               JCTree.JCModifiers var10000 = var1.mods;
               var10000.flags |= 9L;
            }
         } else {
            var4 = this.reader.defineClass(var1.name, var2);
            var4.flatname = this.chk.localClassName(var4);
            if (!var4.name.isEmpty()) {
               this.chk.checkTransparentClass(var1.pos(), var4, ((AttrContext)this.env.info).scope);
            }
         }
      }

      var1.sym = var4;
      if (this.chk.compiled.get(var4.flatname) != null) {
         this.duplicateClass(var1.pos(), var4);
         this.result = this.types.createErrorType(var1.name, (Symbol.TypeSymbol)var2, Type.noType);
         var1.sym = (Symbol.ClassSymbol)this.result.tsym;
      } else {
         this.chk.compiled.put(var4.flatname, var4);
         var3.enter(var4);
         Env var8 = this.classEnv(var1, this.env);
         this.typeEnvs.put(var4, var8);
         var4.completer = this.memberEnter;
         var4.flags_field = this.chk.checkFlags(var1.pos(), var1.mods.flags, var4, var1);
         var4.sourcefile = this.env.toplevel.sourcefile;
         var4.members_field = new Scope(var4);
         Type.ClassType var9 = (Type.ClassType)var4.type;
         if (var2.kind != 1 && (var4.flags_field & 8L) == 0L) {
            Symbol var7;
            for(var7 = var2; (var7.kind & 20) != 0 && (var7.flags_field & 8L) == 0L; var7 = var7.owner) {
            }

            if (var7.kind == 2) {
               var9.setEnclosingType(var7.type);
            }
         }

         var9.typarams_field = this.classEnter(var1.typarams, var8);
         if (!var4.isLocal() && this.uncompleted != null) {
            this.uncompleted.append(var4);
         }

         this.classEnter(var1.defs, var8);
         this.result = var4.type;
      }
   }

   private static boolean classNameMatchesFileName(Symbol.ClassSymbol var0, Env var1) {
      return var1.toplevel.sourcefile.isNameCompatible(var0.name.toString(), Kind.SOURCE);
   }

   protected void duplicateClass(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      this.log.error(var1, "duplicate.class", new Object[]{var2.fullname});
   }

   public void visitTypeParameter(JCTree.JCTypeParameter var1) {
      Type.TypeVar var2 = var1.type != null ? (Type.TypeVar)var1.type : new Type.TypeVar(var1.name, ((AttrContext)this.env.info).scope.owner, this.syms.botType);
      var1.type = var2;
      if (this.chk.checkUnique(var1.pos(), var2.tsym, ((AttrContext)this.env.info).scope)) {
         ((AttrContext)this.env.info).scope.enter(var2.tsym);
      }

      this.result = var2;
   }

   public void visitTree(JCTree var1) {
      this.result = null;
   }

   public void main(List var1) {
      this.complete(var1, (Symbol.ClassSymbol)null);
   }

   public void complete(List var1, Symbol.ClassSymbol var2) {
      this.annotate.enterStart();
      ListBuffer var3 = this.uncompleted;
      if (this.memberEnter.completionEnabled) {
         this.uncompleted = new ListBuffer();
      }

      try {
         this.classEnter((List)var1, (Env)null);
         if (this.memberEnter.completionEnabled) {
            while(true) {
               while(this.uncompleted.nonEmpty()) {
                  Symbol.ClassSymbol var4 = (Symbol.ClassSymbol)this.uncompleted.next();
                  if (var2 != null && var2 != var4 && var3 != null) {
                     var3.append(var4);
                  } else {
                     var4.complete();
                  }
               }

               Iterator var11 = var1.iterator();

               while(var11.hasNext()) {
                  JCTree.JCCompilationUnit var5 = (JCTree.JCCompilationUnit)var11.next();
                  if (var5.starImportScope.elems == null) {
                     JavaFileObject var6 = this.log.useSource(var5.sourcefile);
                     Env var7 = this.topLevelEnv(var5);
                     this.memberEnter.memberEnter((JCTree)var5, var7);
                     this.log.useSource(var6);
                  }
               }

               return;
            }
         }
      } finally {
         this.uncompleted = var3;
         this.annotate.enterDone();
      }

   }
}
