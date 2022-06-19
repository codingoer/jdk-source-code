package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.DeferredLintHandler;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeAnnotations;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.FatalError;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

public class MemberEnter extends JCTree.Visitor implements Symbol.Completer {
   protected static final Context.Key memberEnterKey = new Context.Key();
   static final boolean checkClash = true;
   private final Names names;
   private final Enter enter;
   private final Log log;
   private final Check chk;
   private final Attr attr;
   private final Symtab syms;
   private final TreeMaker make;
   private final ClassReader reader;
   private final Todo todo;
   private final Annotate annotate;
   private final TypeAnnotations typeAnnotations;
   private final Types types;
   private final JCDiagnostic.Factory diags;
   private final Source source;
   private final Target target;
   private final DeferredLintHandler deferredLintHandler;
   private final Lint lint;
   private final TypeEnvs typeEnvs;
   boolean allowTypeAnnos;
   boolean allowRepeatedAnnos;
   ListBuffer halfcompleted = new ListBuffer();
   boolean isFirst = true;
   boolean completionEnabled = true;
   protected Env env;

   public static MemberEnter instance(Context var0) {
      MemberEnter var1 = (MemberEnter)var0.get(memberEnterKey);
      if (var1 == null) {
         var1 = new MemberEnter(var0);
      }

      return var1;
   }

   protected MemberEnter(Context var1) {
      var1.put((Context.Key)memberEnterKey, (Object)this);
      this.names = Names.instance(var1);
      this.enter = Enter.instance(var1);
      this.log = Log.instance(var1);
      this.chk = Check.instance(var1);
      this.attr = Attr.instance(var1);
      this.syms = Symtab.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.reader = ClassReader.instance(var1);
      this.todo = Todo.instance(var1);
      this.annotate = Annotate.instance(var1);
      this.typeAnnotations = TypeAnnotations.instance(var1);
      this.types = Types.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.source = Source.instance(var1);
      this.target = Target.instance(var1);
      this.deferredLintHandler = DeferredLintHandler.instance(var1);
      this.lint = Lint.instance(var1);
      this.typeEnvs = TypeEnvs.instance(var1);
      this.allowTypeAnnos = this.source.allowTypeAnnotations();
      this.allowRepeatedAnnos = this.source.allowRepeatedAnnotations();
   }

   private void importAll(int var1, Symbol.TypeSymbol var2, Env var3) {
      if (var2.kind == 1 && var2.members().elems == null && !var2.exists()) {
         if (((Symbol.PackageSymbol)var2).fullname.equals(this.names.java_lang)) {
            JCDiagnostic var4 = this.diags.fragment("fatal.err.no.java.lang");
            throw new FatalError(var4);
         }

         this.log.error(JCDiagnostic.DiagnosticFlag.RESOLVE_ERROR, var1, "doesnt.exist", new Object[]{var2});
      }

      var3.toplevel.starImportScope.importAll(var2.members());
   }

   private void importStaticAll(int var1, final Symbol.TypeSymbol var2, Env var3) {
      final JavaFileObject var4 = var3.toplevel.sourcefile;
      final Scope.StarImportScope var5 = var3.toplevel.starImportScope;
      final Symbol.PackageSymbol var6 = var3.toplevel.packge;
      ((<undefinedtype>)(new Object() {
         Set processed = new HashSet();

         void importFrom(Symbol.TypeSymbol var1) {
            if (var1 != null && this.processed.add(var1)) {
               this.importFrom(MemberEnter.this.types.supertype(var1.type).tsym);
               Iterator var2x = MemberEnter.this.types.interfaces(var1.type).iterator();

               while(var2x.hasNext()) {
                  Type var3 = (Type)var2x.next();
                  this.importFrom(var3.tsym);
               }

               Scope var5x = var1.members();

               for(Scope.Entry var6x = var5x.elems; var6x != null; var6x = var6x.sibling) {
                  Symbol var4 = var6x.sym;
                  if (var4.kind == 2 && (var4.flags() & 8L) != 0L && MemberEnter.this.staticImportAccessible(var4, var6) && var4.isMemberOf(var2, MemberEnter.this.types) && !var5.includes(var4)) {
                     var5.enter(var4, var5x, var2.members(), true);
                  }
               }

            }
         }
      })).importFrom(var2);
      this.annotate.earlier(new Annotate.Worker() {
         Set processed = new HashSet();

         public String toString() {
            return "import static " + var2 + ".* in " + var4;
         }

         void importFrom(Symbol.TypeSymbol var1) {
            if (var1 != null && this.processed.add(var1)) {
               this.importFrom(MemberEnter.this.types.supertype(var1.type).tsym);
               Iterator var2x = MemberEnter.this.types.interfaces(var1.type).iterator();

               while(var2x.hasNext()) {
                  Type var3 = (Type)var2x.next();
                  this.importFrom(var3.tsym);
               }

               Scope var5x = var1.members();

               for(Scope.Entry var6x = var5x.elems; var6x != null; var6x = var6x.sibling) {
                  Symbol var4x = var6x.sym;
                  if (var4x.isStatic() && var4x.kind != 2 && MemberEnter.this.staticImportAccessible(var4x, var6) && !var5.includes(var4x) && var4x.isMemberOf(var2, MemberEnter.this.types)) {
                     var5.enter(var4x, var5x, var2.members(), true);
                  }
               }

            }
         }

         public void run() {
            this.importFrom(var2);
         }
      });
   }

   boolean staticImportAccessible(Symbol var1, Symbol.PackageSymbol var2) {
      int var3 = (int)(var1.flags() & 7L);
      switch (var3) {
         case 0:
         case 4:
            return var1.packge() == var2;
         case 1:
         case 3:
         default:
            return true;
         case 2:
            return false;
      }
   }

   private void importNamedStatic(final JCDiagnostic.DiagnosticPosition var1, final Symbol.TypeSymbol var2, final Name var3, final Env var4) {
      if (var2.kind != 2) {
         this.log.error(JCDiagnostic.DiagnosticFlag.RECOVERABLE, var1, "static.imp.only.classes.and.interfaces", new Object[0]);
      } else {
         final Scope.ImportScope var5 = var4.toplevel.namedImportScope;
         final Symbol.PackageSymbol var6 = var4.toplevel.packge;
         ((<undefinedtype>)(new Object() {
            Set processed = new HashSet();

            void importFrom(Symbol.TypeSymbol var1x) {
               if (var1x != null && this.processed.add(var1x)) {
                  this.importFrom(MemberEnter.this.types.supertype(var1x.type).tsym);
                  Iterator var2x = MemberEnter.this.types.interfaces(var1x.type).iterator();

                  while(var2x.hasNext()) {
                     Type var3x = (Type)var2x.next();
                     this.importFrom(var3x.tsym);
                  }

                  for(Scope.Entry var4 = var1x.members().lookup(var3); var4.scope != null; var4 = var4.next()) {
                     Symbol var5x = var4.sym;
                     if (var5x.isStatic() && var5x.kind == 2 && MemberEnter.this.staticImportAccessible(var5x, var6) && var5x.isMemberOf(var2, MemberEnter.this.types) && MemberEnter.this.chk.checkUniqueStaticImport(var1, var5x, var5)) {
                        var5.enter(var5x, var5x.owner.members(), var2.members(), true);
                     }
                  }

               }
            }
         })).importFrom(var2);
         this.annotate.earlier(new Annotate.Worker() {
            Set processed = new HashSet();
            boolean found = false;

            public String toString() {
               return "import static " + var2 + "." + var3;
            }

            void importFrom(Symbol.TypeSymbol var1x) {
               if (var1x != null && this.processed.add(var1x)) {
                  this.importFrom(MemberEnter.this.types.supertype(var1x.type).tsym);
                  Iterator var2x = MemberEnter.this.types.interfaces(var1x.type).iterator();

                  while(var2x.hasNext()) {
                     Type var3x = (Type)var2x.next();
                     this.importFrom(var3x.tsym);
                  }

                  for(Scope.Entry var4x = var1x.members().lookup(var3); var4x.scope != null; var4x = var4x.next()) {
                     Symbol var5x = var4x.sym;
                     if (var5x.isStatic() && MemberEnter.this.staticImportAccessible(var5x, var6) && var5x.isMemberOf(var2, MemberEnter.this.types)) {
                        this.found = true;
                        if (var5x.kind != 2) {
                           var5.enter(var5x, var5x.owner.members(), var2.members(), true);
                        }
                     }
                  }

               }
            }

            public void run() {
               JavaFileObject var1x = MemberEnter.this.log.useSource(var4.toplevel.sourcefile);

               try {
                  this.importFrom(var2);
                  if (!this.found) {
                     MemberEnter.this.log.error(var1, "cant.resolve.location", new Object[]{Kinds.KindName.STATIC, var3, List.nil(), List.nil(), Kinds.typeKindName(var2.type), var2.type});
                  }
               } finally {
                  MemberEnter.this.log.useSource(var1x);
               }

            }
         });
      }
   }

   private void importNamed(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Env var3) {
      if (var2.kind == 2 && this.chk.checkUniqueImport(var1, var2, var3.toplevel.namedImportScope)) {
         var3.toplevel.namedImportScope.enter(var2, var2.owner.members());
      }

   }

   Type signature(Symbol.MethodSymbol var1, List var2, List var3, JCTree var4, JCTree.JCVariableDecl var5, List var6, Env var7) {
      List var8 = this.enter.classEnter(var2, var7);
      this.attr.attribTypeVariables(var2, var7);
      ListBuffer var9 = new ListBuffer();

      for(List var10 = var3; var10.nonEmpty(); var10 = var10.tail) {
         this.memberEnter((JCTree)var10.head, var7);
         var9.append(((JCTree.JCVariableDecl)var10.head).vartype.type);
      }

      Object var15 = var4 == null ? this.syms.voidType : this.attr.attribType(var4, var7);
      Type var11;
      if (var5 != null) {
         this.memberEnter((JCTree)var5, var7);
         var11 = var5.vartype.type;
      } else {
         var11 = null;
      }

      ListBuffer var12 = new ListBuffer();

      for(List var13 = var6; var13.nonEmpty(); var13 = var13.tail) {
         Type var14 = this.attr.attribType((JCTree)var13.head, var7);
         if (!var14.hasTag(TypeTag.TYPEVAR)) {
            var14 = this.chk.checkClassType(((JCTree.JCExpression)var13.head).pos(), var14);
         } else if (var14.tsym.owner == var1) {
            Symbol.TypeSymbol var10000 = var14.tsym;
            var10000.flags_field |= 140737488355328L;
         }

         var12.append(var14);
      }

      Type.MethodType var16 = new Type.MethodType(var9.toList(), (Type)var15, var12.toList(), this.syms.methodClass);
      var16.recvtype = var11;
      return (Type)(var8.isEmpty() ? var16 : new Type.ForAll(var8, var16));
   }

   protected void memberEnter(JCTree var1, Env var2) {
      Env var3 = this.env;

      try {
         this.env = var2;
         var1.accept(this);
      } catch (Symbol.CompletionFailure var8) {
         this.chk.completionError(var1.pos(), var8);
      } finally {
         this.env = var3;
      }

   }

   void memberEnter(List var1, Env var2) {
      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         this.memberEnter((JCTree)var3.head, var2);
      }

   }

   void finishClass(JCTree.JCClassDecl var1, Env var2) {
      if ((var1.mods.flags & 16384L) != 0L && (this.types.supertype(var1.sym.type).tsym.flags() & 16384L) == 0L) {
         this.addEnumMembers(var1, var2);
      }

      this.memberEnter(var1.defs, var2);
   }

   private void addEnumMembers(JCTree.JCClassDecl var1, Env var2) {
      JCTree.JCExpression var3 = this.make.Type(new Type.ArrayType(var1.sym.type, this.syms.arrayClass));
      JCTree.JCMethodDecl var4 = this.make.MethodDef(this.make.Modifiers(9L), this.names.values, var3, List.nil(), List.nil(), List.nil(), (JCTree.JCBlock)null, (JCTree.JCExpression)null);
      this.memberEnter((JCTree)var4, var2);
      JCTree.JCMethodDecl var5 = this.make.MethodDef(this.make.Modifiers(9L), this.names.valueOf, this.make.Type(var1.sym.type), List.nil(), List.of(this.make.VarDef(this.make.Modifiers(8589967360L), this.names.fromString("name"), this.make.Type(this.syms.stringType), (JCTree.JCExpression)null)), List.nil(), (JCTree.JCBlock)null, (JCTree.JCExpression)null);
      this.memberEnter((JCTree)var5, var2);
   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      if (var1.starImportScope.elems == null) {
         if (var1.pid != null) {
            for(Object var2 = var1.packge; ((Symbol)var2).owner != this.syms.rootPackage; var2 = ((Symbol)var2).owner) {
               ((Symbol)var2).owner.complete();
               if (this.syms.classes.get(((Symbol)var2).getQualifiedName()) != null) {
                  this.log.error(var1.pos, "pkg.clashes.with.class.of.same.name", new Object[]{var2});
               }
            }
         }

         this.annotateLater(var1.packageAnnotations, this.env, var1.packge, (JCDiagnostic.DiagnosticPosition)null);
         JCDiagnostic.DiagnosticPosition var7 = this.deferredLintHandler.immediate();
         Lint var3 = this.chk.setLint(this.lint);

         try {
            this.importAll(var1.pos, this.reader.enterPackage(this.names.java_lang), this.env);
            this.memberEnter(var1.defs, this.env);
         } finally {
            this.chk.setLint(var3);
            this.deferredLintHandler.setPos(var7);
         }

      }
   }

   public void visitImport(JCTree.JCImport var1) {
      JCTree.JCFieldAccess var2 = (JCTree.JCFieldAccess)var1.qualid;
      Name var3 = TreeInfo.name(var2);
      Env var4 = this.env.dup(var1);
      Symbol.TypeSymbol var5 = this.attr.attribImportQualifier(var1, var4).tsym;
      if (var3 == this.names.asterisk) {
         this.chk.checkCanonical(var2.selected);
         if (var1.staticImport) {
            this.importStaticAll(var1.pos, var5, this.env);
         } else {
            this.importAll(var1.pos, var5, this.env);
         }
      } else if (var1.staticImport) {
         this.importNamedStatic(var1.pos(), var5, var3, var4);
         this.chk.checkCanonical(var2.selected);
      } else {
         Symbol.TypeSymbol var6 = this.attribImportType(var2, var4).tsym;
         this.chk.checkCanonical(var2);
         this.importNamed(var1.pos(), var6, this.env);
      }

   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      Scope var2 = this.enter.enterScope(this.env);
      Symbol.MethodSymbol var3 = new Symbol.MethodSymbol(0L, var1.name, (Type)null, var2.owner);
      var3.flags_field = this.chk.checkFlags(var1.pos(), var1.mods.flags, var3, var1);
      var1.sym = var3;
      if ((var1.mods.flags & 8796093022208L) != 0L) {
         Symbol.ClassSymbol var10000 = var3.enclClass();
         var10000.flags_field |= 8796093022208L;
      }

      Env var4 = this.methodEnv(var1, this.env);
      JCDiagnostic.DiagnosticPosition var5 = this.deferredLintHandler.setPos(var1.pos());

      try {
         var3.type = this.signature(var3, var1.typarams, var1.params, var1.restype, var1.recvparam, var1.thrown, var4);
      } finally {
         this.deferredLintHandler.setPos(var5);
      }

      if (this.types.isSignaturePolymorphic(var3)) {
         var3.flags_field |= 70368744177664L;
      }

      ListBuffer var6 = new ListBuffer();
      JCTree.JCVariableDecl var7 = null;

      for(List var8 = var1.params; var8.nonEmpty(); var8 = var8.tail) {
         JCTree.JCVariableDecl var9 = var7 = (JCTree.JCVariableDecl)var8.head;
         var6.append(Assert.checkNonNull(var9.sym));
      }

      var3.params = var6.toList();
      if (var7 != null && (var7.mods.flags & 17179869184L) != 0L) {
         var3.flags_field |= 17179869184L;
      }

      ((AttrContext)var4.info).scope.leave();
      if (this.chk.checkUnique(var1.pos(), var3, var2)) {
         var2.enter(var3);
      }

      this.annotateLater(var1.mods.annotations, var4, var3, var1.pos());
      this.typeAnnotate(var1, var4, var3, var1.pos());
      if (var1.defaultValue != null) {
         this.annotateDefaultValueLater(var1.defaultValue, var4, var3);
      }

   }

   Env methodEnv(JCTree.JCMethodDecl var1, Env var2) {
      Env var3 = var2.dup(var1, ((AttrContext)var2.info).dup(((AttrContext)var2.info).scope.dupUnshared()));
      var3.enclMethod = var1;
      ((AttrContext)var3.info).scope.owner = var1.sym;
      if (var1.sym.type != null) {
         ((AttrContext)var3.info).returnResult = this.attr.new ResultInfo(12, var1.sym.type.getReturnType());
      }

      if ((var1.mods.flags & 8L) != 0L) {
         ++((AttrContext)var3.info).staticLevel;
      }

      return var3;
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      Env var2 = this.env;
      if ((var1.mods.flags & 8L) != 0L || (((AttrContext)this.env.info).scope.owner.flags() & 512L) != 0L) {
         var2 = this.env.dup(var1, ((AttrContext)this.env.info).dup());
         ++((AttrContext)var2.info).staticLevel;
      }

      JCDiagnostic.DiagnosticPosition var3 = this.deferredLintHandler.setPos(var1.pos());

      try {
         if (TreeInfo.isEnumInit(var1)) {
            this.attr.attribIdentAsEnumType(var2, (JCTree.JCIdent)var1.vartype);
         } else {
            this.attr.attribType(var1.vartype, (Env)var2);
            if (TreeInfo.isReceiverParam(var1)) {
               this.checkReceiver(var1, var2);
            }
         }
      } finally {
         this.deferredLintHandler.setPos(var3);
      }

      if ((var1.mods.flags & 17179869184L) != 0L) {
         Type.ArrayType var4 = (Type.ArrayType)var1.vartype.type.unannotatedType();
         var1.vartype.type = var4.makeVarargs();
      }

      Scope var9 = this.enter.enterScope(this.env);
      Symbol.VarSymbol var5 = new Symbol.VarSymbol(0L, var1.name, var1.vartype.type, var9.owner);
      var5.flags_field = this.chk.checkFlags(var1.pos(), var1.mods.flags, var5, var1);
      var1.sym = var5;
      if (var1.init != null) {
         var5.flags_field |= 262144L;
         if ((var5.flags_field & 16L) != 0L && this.needsLazyConstValue(var1.init)) {
            Env var6 = this.getInitEnv(var1, this.env);
            ((AttrContext)var6.info).enclVar = var5;
            var5.setLazyConstValue(this.initEnv(var1, var6), this.attr, var1);
         }
      }

      if (this.chk.checkUnique(var1.pos(), var5, var9)) {
         this.chk.checkTransparentVar(var1.pos(), var5, var9);
         var9.enter(var5);
      }

      this.annotateLater(var1.mods.annotations, var2, var5, var1.pos());
      this.typeAnnotate(var1.vartype, this.env, var5, var1.pos());
      var5.pos = var1.pos;
   }

   void checkType(JCTree var1, Type var2, String var3) {
      if (!var1.type.isErroneous() && !this.types.isSameType(var1.type, var2)) {
         this.log.error(var1, var3, new Object[]{var2, var1.type});
      }

   }

   void checkReceiver(JCTree.JCVariableDecl var1, Env var2) {
      this.attr.attribExpr(var1.nameexpr, var2);
      Symbol.MethodSymbol var3 = var2.enclMethod.sym;
      if (var3.isConstructor()) {
         Type var4 = var3.owner.owner.type;
         if (var4.hasTag(TypeTag.METHOD)) {
            var4 = var3.owner.owner.owner.type;
         }

         if (var4.hasTag(TypeTag.CLASS)) {
            this.checkType(var1.vartype, var4, "incorrect.constructor.receiver.type");
            this.checkType(var1.nameexpr, var4, "incorrect.constructor.receiver.name");
         } else {
            this.log.error(var1, "receiver.parameter.not.applicable.constructor.toplevel.class", new Object[0]);
         }
      } else {
         this.checkType(var1.vartype, var3.owner.type, "incorrect.receiver.type");
         this.checkType(var1.nameexpr, var3.owner.type, "incorrect.receiver.name");
      }

   }

   public boolean needsLazyConstValue(JCTree var1) {
      InitTreeVisitor var2 = new InitTreeVisitor();
      var1.accept(var2);
      return var2.result;
   }

   Env initEnv(JCTree.JCVariableDecl var1, Env var2) {
      Env var3 = var2.dupto(new AttrContextEnv(var1, ((AttrContext)var2.info).dup()));
      if (var1.sym.owner.kind == 2) {
         ((AttrContext)var3.info).scope = ((AttrContext)var2.info).scope.dupUnshared();
         ((AttrContext)var3.info).scope.owner = var1.sym;
      }

      if ((var1.mods.flags & 8L) != 0L || (var2.enclClass.sym.flags() & 512L) != 0L && var2.enclMethod == null) {
         ++((AttrContext)var3.info).staticLevel;
      }

      return var3;
   }

   public void visitTree(JCTree var1) {
   }

   public void visitErroneous(JCTree.JCErroneous var1) {
      if (var1.errs != null) {
         this.memberEnter(var1.errs, this.env);
      }

   }

   public Env getMethodEnv(JCTree.JCMethodDecl var1, Env var2) {
      Env var3 = this.methodEnv(var1, var2);
      ((AttrContext)var3.info).lint = ((AttrContext)var3.info).lint.augment((Symbol)var1.sym);

      List var4;
      for(var4 = var1.typarams; var4.nonEmpty(); var4 = var4.tail) {
         ((AttrContext)var3.info).scope.enterIfAbsent(((JCTree.JCTypeParameter)var4.head).type.tsym);
      }

      for(var4 = var1.params; var4.nonEmpty(); var4 = var4.tail) {
         ((AttrContext)var3.info).scope.enterIfAbsent(((JCTree.JCVariableDecl)var4.head).sym);
      }

      return var3;
   }

   public Env getInitEnv(JCTree.JCVariableDecl var1, Env var2) {
      Env var3 = this.initEnv(var1, var2);
      return var3;
   }

   Type attribImportType(JCTree var1, Env var2) {
      Assert.check(this.completionEnabled);

      Type var3;
      try {
         this.completionEnabled = false;
         var3 = this.attr.attribType(var1, var2);
      } finally {
         this.completionEnabled = true;
      }

      return var3;
   }

   void annotateLater(final List var1, final Env var2, final Symbol var3, final JCDiagnostic.DiagnosticPosition var4) {
      if (!var1.isEmpty()) {
         if (var3.kind != 1) {
            var3.resetAnnotations();
         }

         this.annotate.normal(new Annotate.Worker() {
            public String toString() {
               return "annotate " + var1 + " onto " + var3 + " in " + var3.owner;
            }

            public void run() {
               Assert.check(var3.kind == 1 || var3.annotationsPendingCompletion());
               JavaFileObject var1x = MemberEnter.this.log.useSource(var2.toplevel.sourcefile);
               JCDiagnostic.DiagnosticPosition var2x = var4 != null ? MemberEnter.this.deferredLintHandler.setPos(var4) : MemberEnter.this.deferredLintHandler.immediate();
               Lint var3x = var4 != null ? null : MemberEnter.this.chk.setLint(MemberEnter.this.lint);

               try {
                  if (var3.hasAnnotations() && var1.nonEmpty()) {
                     MemberEnter.this.log.error(((JCTree.JCAnnotation)var1.head).pos, "already.annotated", new Object[]{Kinds.kindName(var3), var3});
                  }

                  MemberEnter.this.actualEnterAnnotations(var1, var2, var3);
               } finally {
                  if (var3x != null) {
                     MemberEnter.this.chk.setLint(var3x);
                  }

                  MemberEnter.this.deferredLintHandler.setPos(var2x);
                  MemberEnter.this.log.useSource(var1x);
               }

            }
         });
         this.annotate.validate(new Annotate.Worker() {
            public void run() {
               JavaFileObject var1x = MemberEnter.this.log.useSource(var2.toplevel.sourcefile);

               try {
                  MemberEnter.this.chk.validateAnnotations(var1, var3);
               } finally {
                  MemberEnter.this.log.useSource(var1x);
               }

            }
         });
      }
   }

   private boolean hasDeprecatedAnnotation(List var1) {
      for(List var2 = var1; !var2.isEmpty(); var2 = var2.tail) {
         JCTree.JCAnnotation var3 = (JCTree.JCAnnotation)var2.head;
         if (var3.annotationType.type == this.syms.deprecatedType && var3.args.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private void actualEnterAnnotations(List var1, Env var2, Symbol var3) {
      LinkedHashMap var4 = new LinkedHashMap();
      HashMap var5 = new HashMap();

      for(List var6 = var1; !var6.isEmpty(); var6 = var6.tail) {
         JCTree.JCAnnotation var7 = (JCTree.JCAnnotation)var6.head;
         Attribute.Compound var8 = this.annotate.enterAnnotation(var7, this.syms.annotationType, var2);
         if (var8 != null) {
            if (var4.containsKey(var7.type.tsym)) {
               if (!this.allowRepeatedAnnos) {
                  this.log.error(var7.pos(), "repeatable.annotations.not.supported.in.source", new Object[0]);
                  this.allowRepeatedAnnos = true;
               }

               ListBuffer var9 = (ListBuffer)var4.get(var7.type.tsym);
               var9 = var9.append(var8);
               var4.put(var7.type.tsym, var9);
               var5.put(var8, var7.pos());
            } else {
               var4.put(var7.type.tsym, ListBuffer.of(var8));
               var5.put(var8, var7.pos());
            }

            if (!var8.type.isErroneous() && var3.owner.kind != 16 && this.types.isSameType(var8.type, this.syms.deprecatedType)) {
               var3.flags_field |= 131072L;
            }
         }
      }

      var3.setDeclarationAttributesWithCompletion(this.annotate.new AnnotateRepeatedContext(var2, var4, var5, this.log, false));
   }

   void annotateDefaultValueLater(final JCTree.JCExpression var1, final Env var2, final Symbol.MethodSymbol var3) {
      this.annotate.normal(new Annotate.Worker() {
         public String toString() {
            return "annotate " + var3.owner + "." + var3 + " default " + var1;
         }

         public void run() {
            JavaFileObject var1x = MemberEnter.this.log.useSource(var2.toplevel.sourcefile);

            try {
               MemberEnter.this.enterDefaultValue(var1, var2, var3);
            } finally {
               MemberEnter.this.log.useSource(var1x);
            }

         }
      });
      this.annotate.validate(new Annotate.Worker() {
         public void run() {
            JavaFileObject var1x = MemberEnter.this.log.useSource(var2.toplevel.sourcefile);

            try {
               MemberEnter.this.chk.validateAnnotationTree(var1);
            } finally {
               MemberEnter.this.log.useSource(var1x);
            }

         }
      });
   }

   private void enterDefaultValue(JCTree.JCExpression var1, Env var2, Symbol.MethodSymbol var3) {
      var3.defaultValue = this.annotate.enterAttributeValue(var3.type.getReturnType(), var1, var2);
   }

   public void complete(Symbol var1) throws Symbol.CompletionFailure {
      if (!this.completionEnabled) {
         Assert.check((var1.flags() & 16777216L) == 0L);
         var1.completer = this;
      } else {
         Symbol.ClassSymbol var2 = (Symbol.ClassSymbol)var1;
         Type.ClassType var3 = (Type.ClassType)var2.type;
         Env var4 = this.typeEnvs.get(var2);
         JCTree.JCClassDecl var5 = (JCTree.JCClassDecl)var4.tree;
         boolean var6 = this.isFirst;
         this.isFirst = false;

         try {
            this.annotate.enterStart();
            JavaFileObject var7 = this.log.useSource(var4.toplevel.sourcefile);
            JCDiagnostic.DiagnosticPosition var8 = this.deferredLintHandler.setPos(var5.pos());

            Env var9;
            try {
               this.halfcompleted.append(var4);
               var2.flags_field |= 268435456L;
               if (var2.owner.kind == 1) {
                  this.memberEnter((JCTree)var4.toplevel, var4.enclosing(JCTree.Tag.TOPLEVEL));
                  this.todo.append(var4);
               }

               if (var2.owner.kind == 2) {
                  var2.owner.complete();
               }

               var9 = this.baseEnv(var5, var4);
               if (var5.extending != null) {
                  this.typeAnnotate(var5.extending, var9, var1, var5.pos());
               }

               Iterator var10 = var5.implementing.iterator();

               while(var10.hasNext()) {
                  JCTree.JCExpression var11 = (JCTree.JCExpression)var10.next();
                  this.typeAnnotate(var11, var9, var1, var5.pos());
               }

               this.annotate.flush();
               Object var44 = var5.extending != null ? this.attr.attribBase(var5.extending, var9, true, false, true) : ((var5.mods.flags & 16384L) != 0L ? this.attr.attribBase(this.enumBase(var5.pos, var2), var9, true, false, false) : (var2.fullname == this.names.java_lang_Object ? Type.noType : this.syms.objectType));
               var3.supertype_field = this.modelMissingTypes((Type)var44, var5.extending, false);
               ListBuffer var45 = new ListBuffer();
               ListBuffer var12 = null;
               HashSet var13 = new HashSet();
               List var14 = var5.implementing;
               Iterator var15 = var14.iterator();

               while(var15.hasNext()) {
                  JCTree.JCExpression var16 = (JCTree.JCExpression)var15.next();
                  Type var17 = this.attr.attribBase(var16, var9, false, true, true);
                  if (var17.hasTag(TypeTag.CLASS)) {
                     var45.append(var17);
                     if (var12 != null) {
                        var12.append(var17);
                     }

                     this.chk.checkNotRepeated(var16.pos(), this.types.erasure(var17), var13);
                  } else {
                     if (var12 == null) {
                        var12 = (new ListBuffer()).appendList(var45);
                     }

                     var12.append(this.modelMissingTypes(var17, var16, true));
                  }
               }

               if ((var2.flags_field & 8192L) != 0L) {
                  var3.interfaces_field = List.of(this.syms.annotationType);
                  var3.all_interfaces_field = var3.interfaces_field;
               } else {
                  var3.interfaces_field = var45.toList();
                  var3.all_interfaces_field = var12 == null ? var3.interfaces_field : var12.toList();
               }

               if (var2.fullname == this.names.java_lang_Object) {
                  if (var5.extending != null) {
                     this.chk.checkNonCyclic(var5.extending.pos(), (Type)var44);
                     var3.supertype_field = Type.noType;
                  } else if (var5.implementing.nonEmpty()) {
                     this.chk.checkNonCyclic(((JCTree.JCExpression)var5.implementing.head).pos(), (Type)var3.interfaces_field.head);
                     var3.interfaces_field = List.nil();
                  }
               }

               this.attr.attribAnnotationTypes(var5.mods.annotations, var9);
               if (this.hasDeprecatedAnnotation(var5.mods.annotations)) {
                  var2.flags_field |= 131072L;
               }

               this.annotateLater(var5.mods.annotations, var9, var2, var5.pos());
               this.chk.checkNonCyclicDecl(var5);
               this.attr.attribTypeVariables(var5.typarams, var9);
               var15 = var5.typarams.iterator();

               while(var15.hasNext()) {
                  JCTree.JCTypeParameter var47 = (JCTree.JCTypeParameter)var15.next();
                  this.typeAnnotate(var47, var9, var1, var5.pos());
               }

               if ((var2.flags() & 512L) == 0L && !TreeInfo.hasConstructors(var5.defs)) {
                  List var46 = List.nil();
                  List var48 = List.nil();
                  List var50 = List.nil();
                  long var18 = 0L;
                  boolean var20 = false;
                  boolean var21 = true;
                  JCTree.JCNewClass var22 = null;
                  if (var2.name.isEmpty()) {
                     var22 = (JCTree.JCNewClass)var4.next.tree;
                     if (var22.constructor != null) {
                        var21 = var22.constructor.kind != 63;
                        Type var23 = this.types.memberType(var2.type, var22.constructor);
                        var46 = var23.getParameterTypes();
                        var48 = var23.getTypeArguments();
                        var18 = var22.constructor.flags() & 17179869184L;
                        if (var22.encl != null) {
                           var46 = var46.prepend(var22.encl.type);
                           var20 = true;
                        }

                        var50 = var23.getThrownTypes();
                     }
                  }

                  if (var21) {
                     Symbol.MethodSymbol var52 = var22 != null ? (Symbol.MethodSymbol)var22.constructor : null;
                     JCTree var24 = this.DefaultConstructor(this.make.at(var5.pos), var2, var52, var48, var46, var50, var18, var20);
                     var5.defs = var5.defs.prepend(var24);
                  }
               }

               Symbol.VarSymbol var49 = new Symbol.VarSymbol(262160L, this.names._this, var2.type, var2);
               var49.pos = 0;
               ((AttrContext)var4.info).scope.enter(var49);
               if ((var2.flags_field & 512L) == 0L && var3.supertype_field.hasTag(TypeTag.CLASS)) {
                  Symbol.VarSymbol var51 = new Symbol.VarSymbol(262160L, this.names._super, var3.supertype_field, var2);
                  var51.pos = 0;
                  ((AttrContext)var4.info).scope.enter(var51);
               }

               if (var2.owner.kind == 1 && var2.owner != this.syms.unnamedPackage && this.reader.packageExists(var2.fullname)) {
                  this.log.error(var5.pos, "clash.with.pkg.of.same.name", new Object[]{Kinds.kindName(var1), var2});
               }

               if (var2.owner.kind == 1 && (var2.flags_field & 1L) == 0L && !var4.toplevel.sourcefile.isNameCompatible(var2.name.toString(), Kind.SOURCE)) {
                  var2.flags_field |= 17592186044416L;
               }
            } catch (Symbol.CompletionFailure var41) {
               this.chk.completionError(var5.pos(), var41);
            } finally {
               this.deferredLintHandler.setPos(var8);
               this.log.useSource(var7);
            }

            if (var6) {
               try {
                  while(this.halfcompleted.nonEmpty()) {
                     var9 = (Env)this.halfcompleted.next();
                     this.finish(var9);
                     if (this.allowTypeAnnos) {
                        this.typeAnnotations.organizeTypeAnnotationsSignatures(var9, (JCTree.JCClassDecl)var9.tree);
                        this.typeAnnotations.validateTypeAnnotationsSignatures(var9, (JCTree.JCClassDecl)var9.tree);
                     }
                  }
               } finally {
                  this.isFirst = true;
               }
            }
         } finally {
            this.annotate.enterDone();
         }

      }
   }

   private void actualEnterTypeAnnotations(List var1, Env var2, Symbol var3) {
      LinkedHashMap var4 = new LinkedHashMap();
      HashMap var5 = new HashMap();

      for(List var6 = var1; !var6.isEmpty(); var6 = var6.tail) {
         JCTree.JCAnnotation var7 = (JCTree.JCAnnotation)var6.head;
         Attribute.TypeCompound var8 = this.annotate.enterTypeAnnotation(var7, this.syms.annotationType, var2);
         if (var8 != null) {
            if (var4.containsKey(var7.type.tsym)) {
               if (this.source.allowRepeatedAnnotations()) {
                  ListBuffer var9 = (ListBuffer)var4.get(var7.type.tsym);
                  var9 = var9.append(var8);
                  var4.put(var7.type.tsym, var9);
                  var5.put(var8, var7.pos());
               } else {
                  this.log.error(var7.pos(), "repeatable.annotations.not.supported.in.source", new Object[0]);
               }
            } else {
               var4.put(var7.type.tsym, ListBuffer.of(var8));
               var5.put(var8, var7.pos());
            }
         }
      }

      if (var3 != null) {
         var3.appendTypeAttributesWithCompletion(this.annotate.new AnnotateRepeatedContext(var2, var4, var5, this.log, true));
      }

   }

   public void typeAnnotate(JCTree var1, Env var2, Symbol var3, JCDiagnostic.DiagnosticPosition var4) {
      if (this.allowTypeAnnos) {
         var1.accept(new TypeAnnotate(var2, var3, var4));
      }

   }

   private Env baseEnv(JCTree.JCClassDecl var1, Env var2) {
      Scope var3 = new Scope(var1.sym);

      for(Scope.Entry var4 = ((AttrContext)var2.outer.info).scope.elems; var4 != null; var4 = var4.sibling) {
         if (var4.sym.isLocal()) {
            var3.enter(var4.sym);
         }
      }

      if (var1.typarams != null) {
         for(List var7 = var1.typarams; var7.nonEmpty(); var7 = var7.tail) {
            var3.enter(((JCTree.JCTypeParameter)var7.head).type.tsym);
         }
      }

      Env var6 = var2.outer;
      Env var5 = var6.dup(var1, ((AttrContext)var6.info).dup(var3));
      var5.baseClause = true;
      var5.outer = var6;
      ((AttrContext)var5.info).isSelfCall = false;
      return var5;
   }

   private void finish(Env var1) {
      JavaFileObject var2 = this.log.useSource(var1.toplevel.sourcefile);

      try {
         JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)var1.tree;
         this.finishClass(var3, var1);
      } finally {
         this.log.useSource(var2);
      }

   }

   private JCTree.JCExpression enumBase(int var1, Symbol.ClassSymbol var2) {
      JCTree.JCTypeApply var3 = this.make.at(var1).TypeApply(this.make.QualIdent(this.syms.enumSym), List.of(this.make.Type(var2.type)));
      return var3;
   }

   Type modelMissingTypes(Type var1, final JCTree.JCExpression var2, final boolean var3) {
      return (Type)(!var1.hasTag(TypeTag.ERROR) ? var1 : new Type.ErrorType(var1.getOriginalType(), var1.tsym) {
         private Type modelType;

         public Type getModelType() {
            if (this.modelType == null) {
               this.modelType = (MemberEnter.this.new Synthesizer(this.getOriginalType(), var3)).visit((JCTree)var2);
            }

            return this.modelType;
         }
      });
   }

   JCTree DefaultConstructor(TreeMaker var1, Symbol.ClassSymbol var2, Symbol.MethodSymbol var3, List var4, List var5, List var6, long var7, boolean var9) {
      if ((var2.flags() & 16384L) != 0L && this.types.supertype(var2.type).tsym == this.syms.enumSym) {
         var7 = var7 & -8L | 2L | 68719476736L;
      } else {
         var7 |= var2.flags() & 7L | 68719476736L;
      }

      if (var2.name.isEmpty()) {
         var7 |= 536870912L;
      }

      Type.MethodType var11 = new Type.MethodType(var5, (Type)null, var6, var2);
      Object var12 = var4.nonEmpty() ? new Type.ForAll(var4, var11) : var11;
      Symbol.MethodSymbol var13 = new Symbol.MethodSymbol(var7, this.names.init, (Type)var12, var2);
      var13.params = this.createDefaultConstructorParams(var1, var3, var13, var5, var9);
      List var14 = var1.Params(var5, var13);
      List var15 = List.nil();
      if (var2.type != this.syms.objectType) {
         var15 = var15.prepend(this.SuperCall(var1, var4, var14, var9));
      }

      JCTree.JCMethodDecl var10 = var1.MethodDef(var13, var1.Block(0L, var15));
      return var10;
   }

   private List createDefaultConstructorParams(TreeMaker var1, Symbol.MethodSymbol var2, Symbol.MethodSymbol var3, List var4, boolean var5) {
      List var6 = null;
      List var7 = var4;
      if (var5) {
         var6 = List.nil();
         Symbol.VarSymbol var8 = new Symbol.VarSymbol(8589934592L, var1.paramName(0), (Type)var4.head, var3);
         var6 = var6.append(var8);
         var7 = var4.tail;
      }

      if (var2 != null && var2.params != null && var2.params.nonEmpty() && var7.nonEmpty()) {
         var6 = var6 == null ? List.nil() : var6;

         for(List var10 = var2.params; var10.nonEmpty() && var7.nonEmpty(); var7 = var7.tail) {
            Symbol.VarSymbol var9 = new Symbol.VarSymbol(((Symbol.VarSymbol)var10.head).flags() | 8589934592L, ((Symbol.VarSymbol)var10.head).name, (Type)var7.head, var3);
            var6 = var6.append(var9);
            var10 = var10.tail;
         }
      }

      return var6;
   }

   JCTree.JCExpressionStatement SuperCall(TreeMaker var1, List var2, List var3, boolean var4) {
      Object var5;
      if (var4) {
         var5 = var1.Select(var1.Ident((JCTree.JCVariableDecl)var3.head), this.names._super);
         var3 = var3.tail;
      } else {
         var5 = var1.Ident(this.names._super);
      }

      List var6 = var2.nonEmpty() ? var1.Types(var2) : null;
      return var1.Exec(var1.Apply(var6, (JCTree.JCExpression)var5, var1.Idents(var3)));
   }

   private class Synthesizer extends JCTree.Visitor {
      Type originalType;
      boolean interfaceExpected;
      List synthesizedSymbols = List.nil();
      Type result;

      Synthesizer(Type var2, boolean var3) {
         this.originalType = var2;
         this.interfaceExpected = var3;
      }

      Type visit(JCTree var1) {
         var1.accept(this);
         return this.result;
      }

      List visit(List var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            JCTree var4 = (JCTree)var3.next();
            var2.append(this.visit(var4));
         }

         return var2.toList();
      }

      public void visitTree(JCTree var1) {
         this.result = MemberEnter.this.syms.errType;
      }

      public void visitIdent(JCTree.JCIdent var1) {
         if (!var1.type.hasTag(TypeTag.ERROR)) {
            this.result = var1.type;
         } else {
            this.result = this.synthesizeClass(var1.name, MemberEnter.this.syms.unnamedPackage).type;
         }

      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         if (!var1.type.hasTag(TypeTag.ERROR)) {
            this.result = var1.type;
         } else {
            boolean var3 = this.interfaceExpected;

            Type var2;
            try {
               this.interfaceExpected = false;
               var2 = this.visit((JCTree)var1.selected);
            } finally {
               this.interfaceExpected = var3;
            }

            Symbol.ClassSymbol var4 = this.synthesizeClass(var1.name, var2.tsym);
            this.result = var4.type;
         }

      }

      public void visitTypeApply(JCTree.JCTypeApply var1) {
         if (!var1.type.hasTag(TypeTag.ERROR)) {
            this.result = var1.type;
         } else {
            Type.ClassType var2 = (Type.ClassType)this.visit((JCTree)var1.clazz);
            if (this.synthesizedSymbols.contains(var2.tsym)) {
               this.synthesizeTyparams((Symbol.ClassSymbol)var2.tsym, var1.arguments.size());
            }

            final List var3 = this.visit(var1.arguments);
            this.result = new Type.ErrorType(var1.type, var2.tsym) {
               public List getTypeArguments() {
                  return var3;
               }
            };
         }

      }

      Symbol.ClassSymbol synthesizeClass(Name var1, Symbol var2) {
         int var3 = this.interfaceExpected ? 512 : 0;
         Symbol.ClassSymbol var4 = new Symbol.ClassSymbol((long)var3, var1, var2);
         var4.members_field = new Scope.ErrorScope(var4);
         var4.type = new Type.ErrorType(this.originalType, var4) {
            public List getTypeArguments() {
               return this.typarams_field;
            }
         };
         this.synthesizedSymbols = this.synthesizedSymbols.prepend(var4);
         return var4;
      }

      void synthesizeTyparams(Symbol.ClassSymbol var1, int var2) {
         Type.ClassType var3 = (Type.ClassType)var1.type;
         Assert.check(var3.typarams_field.isEmpty());
         if (var2 == 1) {
            Type.TypeVar var4 = new Type.TypeVar(MemberEnter.this.names.fromString("T"), var1, MemberEnter.this.syms.botType);
            var3.typarams_field = var3.typarams_field.prepend(var4);
         } else {
            for(int var6 = var2; var6 > 0; --var6) {
               Type.TypeVar var5 = new Type.TypeVar(MemberEnter.this.names.fromString("T" + var6), var1, MemberEnter.this.syms.botType);
               var3.typarams_field = var3.typarams_field.prepend(var5);
            }
         }

      }
   }

   private class TypeAnnotate extends TreeScanner {
      private Env env;
      private Symbol sym;
      private JCDiagnostic.DiagnosticPosition deferPos;

      public TypeAnnotate(Env var2, Symbol var3, JCDiagnostic.DiagnosticPosition var4) {
         this.env = var2;
         this.sym = var3;
         this.deferPos = var4;
      }

      void annotateTypeLater(final List var1) {
         if (!var1.isEmpty()) {
            final JCDiagnostic.DiagnosticPosition var2 = this.deferPos;
            MemberEnter.this.annotate.normal(new Annotate.Worker() {
               public String toString() {
                  return "type annotate " + var1 + " onto " + TypeAnnotate.this.sym + " in " + TypeAnnotate.this.sym.owner;
               }

               public void run() {
                  JavaFileObject var1x = MemberEnter.this.log.useSource(TypeAnnotate.this.env.toplevel.sourcefile);
                  JCDiagnostic.DiagnosticPosition var2x = null;
                  if (var2 != null) {
                     var2x = MemberEnter.this.deferredLintHandler.setPos(var2);
                  }

                  try {
                     MemberEnter.this.actualEnterTypeAnnotations(var1, TypeAnnotate.this.env, TypeAnnotate.this.sym);
                  } finally {
                     if (var2x != null) {
                        MemberEnter.this.deferredLintHandler.setPos(var2x);
                     }

                     MemberEnter.this.log.useSource(var1x);
                  }

               }
            });
         }
      }

      public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
         this.annotateTypeLater(var1.annotations);
         super.visitAnnotatedType(var1);
      }

      public void visitTypeParameter(JCTree.JCTypeParameter var1) {
         this.annotateTypeLater(var1.annotations);
         super.visitTypeParameter(var1);
      }

      public void visitNewArray(JCTree.JCNewArray var1) {
         this.annotateTypeLater(var1.annotations);
         Iterator var2 = var1.dimAnnotations.iterator();

         while(var2.hasNext()) {
            List var3 = (List)var2.next();
            this.annotateTypeLater(var3);
         }

         super.visitNewArray(var1);
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         this.scan(var1.mods);
         this.scan(var1.restype);
         this.scan(var1.typarams);
         this.scan(var1.recvparam);
         this.scan(var1.params);
         this.scan(var1.thrown);
         this.scan(var1.defaultValue);
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         JCDiagnostic.DiagnosticPosition var2 = this.deferPos;
         this.deferPos = var1.pos();

         try {
            if (this.sym != null && this.sym.kind == 4) {
               this.scan(var1.mods);
               this.scan(var1.vartype);
            }

            this.scan(var1.init);
         } finally {
            this.deferPos = var2;
         }

      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         if (var1.def == null) {
            super.visitNewClass(var1);
         }

      }
   }

   static class InitTreeVisitor extends JCTree.Visitor {
      private boolean result = true;

      public void visitTree(JCTree var1) {
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         this.result = false;
      }

      public void visitNewArray(JCTree.JCNewArray var1) {
         this.result = false;
      }

      public void visitLambda(JCTree.JCLambda var1) {
         this.result = false;
      }

      public void visitReference(JCTree.JCMemberReference var1) {
         this.result = false;
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         this.result = false;
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         var1.selected.accept(this);
      }

      public void visitConditional(JCTree.JCConditional var1) {
         var1.cond.accept(this);
         var1.truepart.accept(this);
         var1.falsepart.accept(this);
      }

      public void visitParens(JCTree.JCParens var1) {
         var1.expr.accept(this);
      }

      public void visitTypeCast(JCTree.JCTypeCast var1) {
         var1.expr.accept(this);
      }
   }
}
