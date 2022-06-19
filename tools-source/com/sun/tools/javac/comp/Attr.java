package com.sun.tools.javac.comp;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.SimpleTreeVisitor;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.BoundKind;
import com.sun.tools.javac.code.DeferredLintHandler;
import com.sun.tools.javac.code.Flags;
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
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.tree.EndPosTable;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Pair;
import com.sun.tools.javac.util.Warner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.lang.model.element.ElementKind;
import javax.tools.JavaFileObject;

public class Attr extends JCTree.Visitor {
   protected static final Context.Key attrKey = new Context.Key();
   final Names names;
   final Log log;
   final Symtab syms;
   final Resolve rs;
   final Infer infer;
   final DeferredAttr deferredAttr;
   final Check chk;
   final Flow flow;
   final MemberEnter memberEnter;
   final TreeMaker make;
   final ConstFold cfolder;
   final Enter enter;
   final Target target;
   final Types types;
   final JCDiagnostic.Factory diags;
   final Annotate annotate;
   final TypeAnnotations typeAnnotations;
   final DeferredLintHandler deferredLintHandler;
   final TypeEnvs typeEnvs;
   boolean relax;
   boolean allowPoly;
   boolean allowTypeAnnos;
   boolean allowGenerics;
   boolean allowVarargs;
   boolean allowEnums;
   boolean allowBoxing;
   boolean allowCovariantReturns;
   boolean allowLambda;
   boolean allowDefaultMethods;
   boolean allowStaticInterfaceMethods;
   boolean allowAnonOuterThis;
   boolean findDiamonds;
   static final boolean allowDiamondFinder = true;
   boolean useBeforeDeclarationWarning;
   boolean identifyLambdaCandidate;
   boolean allowStringsInSwitch;
   String sourceName;
   private TreeVisitor identAttributer = new IdentAttributer();
   private JCTree breakTree = null;
   final ResultInfo statInfo;
   final ResultInfo varInfo;
   final ResultInfo unknownAnyPolyInfo;
   final ResultInfo unknownExprInfo;
   final ResultInfo unknownTypeInfo;
   final ResultInfo unknownTypeExprInfo;
   final ResultInfo recoveryInfo;
   Env env;
   ResultInfo resultInfo;
   Type result;
   JCTree noCheckTree;
   TreeTranslator removeClassParams = new TreeTranslator() {
      public void visitTypeApply(JCTree.JCTypeApply var1) {
         this.result = this.translate(var1.clazz);
      }
   };
   static final TypeTag[] primitiveTags;
   Types.MapVisitor targetChecker = new Types.MapVisitor() {
      public Type visitClassType(Type.ClassType var1, JCDiagnostic.DiagnosticPosition var2) {
         return (Type)(var1.isIntersection() ? this.visitIntersectionClassType((Type.IntersectionClassType)var1, var2) : var1);
      }

      public Type visitIntersectionClassType(Type.IntersectionClassType var1, JCDiagnostic.DiagnosticPosition var2) {
         Symbol var3 = Attr.this.types.findDescriptorSymbol(this.makeNotionalInterface(var1));
         Type var4 = null;
         Iterator var5 = var1.getExplicitComponents().iterator();

         while(true) {
            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               Symbol.TypeSymbol var7 = var6.tsym;
               if (Attr.this.types.isFunctionalInterface(var7) && Attr.this.types.findDescriptorSymbol(var7) == var3) {
                  var4 = var6;
               } else if (!var7.isInterface() || (var7.flags() & 8192L) != 0L) {
                  this.reportIntersectionError(var2, "not.an.intf.component", var7);
               }
            }

            return var4 != null ? var4 : (Type)var1.getExplicitComponents().head;
         }
      }

      private Symbol.TypeSymbol makeNotionalInterface(Type.IntersectionClassType var1) {
         ListBuffer var2 = new ListBuffer();
         ListBuffer var3 = new ListBuffer();

         Type var5;
         for(Iterator var4 = var1.interfaces_field.iterator(); var4.hasNext(); var3.append(var5.tsym.type)) {
            var5 = (Type)var4.next();
            if (var5.isParameterized()) {
               var2.appendList(var5.tsym.type.allparams());
            }
         }

         Type.IntersectionClassType var6 = Attr.this.types.makeIntersectionType(var3.toList());
         var6.allparams_field = var2.toList();
         Symbol.TypeSymbol var10000 = var6.tsym;
         var10000.flags_field |= 512L;
         return var6.tsym;
      }

      private void reportIntersectionError(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
         Attr.this.resultInfo.checkContext.report(var1, Attr.this.diags.fragment("bad.intersection.target.for.functional.expr", Attr.this.diags.fragment(var2, var3)));
      }
   };
   private Map clinits = new HashMap();
   Warner noteWarner = new Warner();
   public static final Filter anyNonAbstractOrDefaultMethod;

   public static Attr instance(Context var0) {
      Attr var1 = (Attr)var0.get(attrKey);
      if (var1 == null) {
         var1 = new Attr(var0);
      }

      return var1;
   }

   protected Attr(Context var1) {
      var1.put((Context.Key)attrKey, (Object)this);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.rs = Resolve.instance(var1);
      this.chk = Check.instance(var1);
      this.flow = Flow.instance(var1);
      this.memberEnter = MemberEnter.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.enter = Enter.instance(var1);
      this.infer = Infer.instance(var1);
      this.deferredAttr = DeferredAttr.instance(var1);
      this.cfolder = ConstFold.instance(var1);
      this.target = Target.instance(var1);
      this.types = Types.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.annotate = Annotate.instance(var1);
      this.typeAnnotations = TypeAnnotations.instance(var1);
      this.deferredLintHandler = DeferredLintHandler.instance(var1);
      this.typeEnvs = TypeEnvs.instance(var1);
      Options var2 = Options.instance(var1);
      Source var3 = Source.instance(var1);
      this.allowGenerics = var3.allowGenerics();
      this.allowVarargs = var3.allowVarargs();
      this.allowEnums = var3.allowEnums();
      this.allowBoxing = var3.allowBoxing();
      this.allowCovariantReturns = var3.allowCovariantReturns();
      this.allowAnonOuterThis = var3.allowAnonOuterThis();
      this.allowStringsInSwitch = var3.allowStringsInSwitch();
      this.allowPoly = var3.allowPoly();
      this.allowTypeAnnos = var3.allowTypeAnnotations();
      this.allowLambda = var3.allowLambda();
      this.allowDefaultMethods = var3.allowDefaultMethods();
      this.allowStaticInterfaceMethods = var3.allowStaticInterfaceMethods();
      this.sourceName = var3.name;
      this.relax = var2.isSet("-retrofit") || var2.isSet("-relax");
      this.findDiamonds = var2.get("findDiamond") != null && var3.allowDiamond();
      this.useBeforeDeclarationWarning = var2.isSet("useBeforeDeclarationWarning");
      this.identifyLambdaCandidate = var2.getBoolean("identifyLambdaCandidate", false);
      this.statInfo = new ResultInfo(0, Type.noType);
      this.varInfo = new ResultInfo(4, Type.noType);
      this.unknownExprInfo = new ResultInfo(12, Type.noType);
      this.unknownAnyPolyInfo = new ResultInfo(12, Infer.anyPoly);
      this.unknownTypeInfo = new ResultInfo(2, Type.noType);
      this.unknownTypeExprInfo = new ResultInfo(14, Type.noType);
      this.recoveryInfo = new RecoveryInfo(this.deferredAttr.emptyDeferredAttrContext);
      this.noCheckTree = this.make.at(-1).Skip();
   }

   Type check(final JCTree var1, final Type var2, final int var3, final ResultInfo var4) {
      Infer.InferenceContext var5 = var4.checkContext.inferenceContext();
      boolean var7 = !var2.hasTag(TypeTag.ERROR) && !var4.pt.hasTag(TypeTag.METHOD) && !var4.pt.hasTag(TypeTag.FORALL);
      Type var6;
      if (var7 && (var3 & ~var4.pkind) != 0) {
         this.log.error(var1.pos(), "unexpected.type", new Object[]{Kinds.kindNames(var4.pkind), Kinds.kindName(var3)});
         var6 = this.types.createErrorType(var2);
      } else if (this.allowPoly && var5.free(var2)) {
         var6 = var7 ? var4.pt : var2;
         var5.addFreeTypeListener(List.of(var2, var4.pt), new Infer.FreeTypeListener() {
            public void typesInferred(Infer.InferenceContext var1x) {
               ResultInfo var2x = var4.dup(var1x.asInstType(var4.pt));
               Attr.this.check(var1, var1x.asInstType(var2), var3, var2x);
            }
         });
      } else {
         var6 = var7 ? var4.check(var1, var2) : var2;
      }

      if (var1 != this.noCheckTree) {
         var1.type = var6;
      }

      return var6;
   }

   boolean isAssignableAsBlankFinal(Symbol.VarSymbol var1, Env var2) {
      Symbol var3 = ((AttrContext)var2.info).scope.owner;
      return var1.owner == var3 || (var3.name == this.names.init || var3.kind == 4 || (var3.flags() & 1048576L) != 0L) && var1.owner == var3.owner && (var1.flags() & 8L) != 0L == Resolve.isStatic(var2);
   }

   void checkAssignable(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2, JCTree var3, Env var4) {
      if ((var2.flags() & 16L) != 0L && ((var2.flags() & 262144L) != 0L || var3 != null && (!var3.hasTag(JCTree.Tag.IDENT) || TreeInfo.name(var3) != this.names._this) || !this.isAssignableAsBlankFinal(var2, var4))) {
         if (var2.isResourceVariable()) {
            this.log.error(var1, "try.resource.may.not.be.assigned", new Object[]{var2});
         } else {
            this.log.error(var1, "cant.assign.val.to.final.var", new Object[]{var2});
         }
      }

   }

   boolean isStaticReference(JCTree var1) {
      if (var1.hasTag(JCTree.Tag.SELECT)) {
         Symbol var2 = TreeInfo.symbol(((JCTree.JCFieldAccess)var1).selected);
         if (var2 == null || var2.kind != 2) {
            return false;
         }
      }

      return true;
   }

   static boolean isType(Symbol var0) {
      return var0 != null && var0.kind == 2;
   }

   Symbol thisSym(JCDiagnostic.DiagnosticPosition var1, Env var2) {
      return this.rs.resolveSelf(var1, var2, var2.enclClass.sym, this.names._this);
   }

   public Symbol attribIdent(JCTree var1, JCTree.JCCompilationUnit var2) {
      Env var3 = this.enter.topLevelEnv(var2);
      var3.enclClass = this.make.ClassDef(this.make.Modifiers(0L), this.syms.errSymbol.name, (List)null, (JCTree.JCExpression)null, (List)null, (List)null);
      var3.enclClass.sym = this.syms.errSymbol;
      return (Symbol)var1.accept(this.identAttributer, var3);
   }

   public Type coerce(Type var1, Type var2) {
      return this.cfolder.coerce(var1, var2);
   }

   public Type attribType(JCTree var1, Symbol.TypeSymbol var2) {
      Env var3 = this.typeEnvs.get(var2);
      Env var4 = var3.dup(var1, ((AttrContext)var3.info).dup());
      return this.attribTree(var1, var4, this.unknownTypeInfo);
   }

   public Type attribImportQualifier(JCTree.JCImport var1, Env var2) {
      JCTree.JCFieldAccess var3 = (JCTree.JCFieldAccess)var1.qualid;
      return this.attribTree(var3.selected, var2, new ResultInfo(var1.staticImport ? 2 : 3, Type.noType));
   }

   public Env attribExprToTree(JCTree var1, Env var2, JCTree var3) {
      this.breakTree = var3;
      JavaFileObject var4 = this.log.useSource(var2.toplevel.sourcefile);

      Env var6;
      try {
         this.attribExpr(var1, var2);
         return var2;
      } catch (BreakAttr var11) {
         var6 = var11.env;
      } catch (AssertionError var12) {
         if (var12.getCause() instanceof BreakAttr) {
            var6 = ((BreakAttr)((BreakAttr)var12.getCause())).env;
            return var6;
         }

         throw var12;
      } finally {
         this.breakTree = null;
         this.log.useSource(var4);
      }

      return var6;
   }

   public Env attribStatToTree(JCTree var1, Env var2, JCTree var3) {
      this.breakTree = var3;
      JavaFileObject var4 = this.log.useSource(var2.toplevel.sourcefile);

      Env var6;
      try {
         this.attribStat(var1, var2);
         return var2;
      } catch (BreakAttr var11) {
         var6 = var11.env;
         return var6;
      } catch (AssertionError var12) {
         if (!(var12.getCause() instanceof BreakAttr)) {
            throw var12;
         }

         var6 = ((BreakAttr)((BreakAttr)var12.getCause())).env;
      } finally {
         this.breakTree = null;
         this.log.useSource(var4);
      }

      return var6;
   }

   Type pt() {
      return this.resultInfo.pt;
   }

   int pkind() {
      return this.resultInfo.pkind;
   }

   Type attribTree(JCTree var1, Env var2, ResultInfo var3) {
      Env var4 = this.env;
      ResultInfo var5 = this.resultInfo;

      Type var7;
      try {
         this.env = var2;
         this.resultInfo = var3;
         var1.accept(this);
         if (var1 == this.breakTree && var3.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.CHECK) {
            throw new BreakAttr(this.copyEnv(var2));
         }

         Type var6 = this.result;
         return var6;
      } catch (Symbol.CompletionFailure var11) {
         var1.type = this.syms.errType;
         var7 = this.chk.completionError(var1.pos(), var11);
      } finally {
         this.env = var4;
         this.resultInfo = var5;
      }

      return var7;
   }

   Env copyEnv(Env var1) {
      Env var2 = var1.dup(var1.tree, ((AttrContext)var1.info).dup(this.copyScope(((AttrContext)var1.info).scope)));
      if (var2.outer != null) {
         var2.outer = this.copyEnv(var2.outer);
      }

      return var2;
   }

   Scope copyScope(Scope var1) {
      Scope var2 = new Scope(var1.owner);

      List var3;
      for(var3 = List.nil(); var1 != null; var1 = var1.next) {
         for(Scope.Entry var4 = var1.elems; var4 != null; var4 = var4.sibling) {
            var3 = var3.prepend(var4.sym);
         }
      }

      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         Symbol var5 = (Symbol)var6.next();
         var2.enter(var5);
      }

      return var2;
   }

   public Type attribExpr(JCTree var1, Env var2, Type var3) {
      return this.attribTree(var1, var2, new ResultInfo(12, (Type)(!var3.hasTag(TypeTag.ERROR) ? var3 : Type.noType)));
   }

   public Type attribExpr(JCTree var1, Env var2) {
      return this.attribTree(var1, var2, this.unknownExprInfo);
   }

   public Type attribType(JCTree var1, Env var2) {
      Type var3 = this.attribType(var1, var2, Type.noType);
      return var3;
   }

   Type attribType(JCTree var1, Env var2, Type var3) {
      Type var4 = this.attribTree(var1, var2, new ResultInfo(2, var3));
      return var4;
   }

   public Type attribStat(JCTree var1, Env var2) {
      return this.attribTree(var1, var2, this.statInfo);
   }

   List attribExprs(List var1, Env var2, Type var3) {
      ListBuffer var4 = new ListBuffer();

      for(List var5 = var1; var5.nonEmpty(); var5 = var5.tail) {
         var4.append(this.attribExpr((JCTree)var5.head, var2, var3));
      }

      return var4.toList();
   }

   void attribStats(List var1, Env var2) {
      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         this.attribStat((JCTree)var3.head, var2);
      }

   }

   int attribArgs(int var1, List var2, Env var3, ListBuffer var4) {
      int var5 = var1;

      Object var8;
      for(Iterator var6 = var2.iterator(); var6.hasNext(); var4.append(var8)) {
         JCTree.JCExpression var7 = (JCTree.JCExpression)var6.next();
         if (this.allowPoly && this.deferredAttr.isDeferred(var3, var7)) {
            var8 = this.deferredAttr.new DeferredType(var7, var3);
            var5 |= 32;
         } else {
            var8 = this.chk.checkNonVoid(var7, this.attribTree(var7, var3, this.unknownAnyPolyInfo));
         }
      }

      return var5;
   }

   List attribAnyTypes(List var1, Env var2) {
      ListBuffer var3 = new ListBuffer();

      for(List var4 = var1; var4.nonEmpty(); var4 = var4.tail) {
         var3.append(this.attribType((JCTree)var4.head, var2));
      }

      return var3.toList();
   }

   List attribTypes(List var1, Env var2) {
      List var3 = this.attribAnyTypes(var1, var2);
      return this.chk.checkRefTypes(var1, var3);
   }

   void attribTypeVariables(List var1, Env var2) {
      Symbol.TypeSymbol var10000;
      Iterator var3;
      JCTree.JCTypeParameter var4;
      for(var3 = var1.iterator(); var3.hasNext(); var10000.flags_field &= -268435457L) {
         var4 = (JCTree.JCTypeParameter)var3.next();
         Type.TypeVar var5 = (Type.TypeVar)var4.type;
         var10000 = var5.tsym;
         var10000.flags_field |= 268435456L;
         var5.bound = Type.noType;
         if (var4.bounds.isEmpty()) {
            this.types.setBounds(var5, List.of(this.syms.objectType));
         } else {
            List var6 = List.of(this.attribType((JCTree)var4.bounds.head, var2));

            JCTree.JCExpression var8;
            for(Iterator var7 = var4.bounds.tail.iterator(); var7.hasNext(); var6 = var6.prepend(this.attribType(var8, (Env)var2))) {
               var8 = (JCTree.JCExpression)var7.next();
            }

            this.types.setBounds(var5, var6.reverse());
         }

         var10000 = var5.tsym;
      }

      var3 = var1.iterator();

      while(var3.hasNext()) {
         var4 = (JCTree.JCTypeParameter)var3.next();
         this.chk.checkNonCyclic(var4.pos(), (Type.TypeVar)var4.type);
      }

   }

   void attribAnnotationTypes(List var1, Env var2) {
      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         JCTree.JCAnnotation var4 = (JCTree.JCAnnotation)var3.head;
         this.attribType(var4.annotationType, var2);
      }

   }

   public Object attribLazyConstantValue(Env var1, JCTree.JCVariableDecl var2, Type var3) {
      JCDiagnostic.DiagnosticPosition var4 = this.deferredLintHandler.setPos(var2.pos());

      Object var6;
      try {
         this.memberEnter.typeAnnotate(var2.init, var1, (Symbol)null, var2.pos());
         this.annotate.flush();
         Type var5 = this.attribExpr(var2.init, var1, var3);
         if (var5.constValue() != null) {
            var6 = this.coerce(var5, var3).constValue();
            return var6;
         }

         var6 = null;
      } finally {
         this.deferredLintHandler.setPos(var4);
      }

      return var6;
   }

   Type attribBase(JCTree var1, Env var2, boolean var3, boolean var4, boolean var5) {
      Type var6 = var1.type != null ? var1.type : this.attribType(var1, var2);
      return this.checkBase(var6, var1, var2, var3, var4, var5);
   }

   Type checkBase(Type var1, JCTree var2, Env var3, boolean var4, boolean var5, boolean var6) {
      if (var1.tsym.isAnonymous()) {
         this.log.error(var2.pos(), "cant.inherit.from.anon", new Object[0]);
         return this.types.createErrorType(var1);
      } else if (var1.isErroneous()) {
         return var1;
      } else {
         if (var1.hasTag(TypeTag.TYPEVAR) && !var4 && !var5) {
            if (var1.getUpperBound() == null) {
               this.log.error(var2.pos(), "illegal.forward.ref", new Object[0]);
               return this.types.createErrorType(var1);
            }
         } else {
            var1 = this.chk.checkClassType(var2.pos(), var1, var6 | !this.allowGenerics);
         }

         if (var5 && (var1.tsym.flags() & 512L) == 0L) {
            this.log.error(var2.pos(), "intf.expected.here", new Object[0]);
            return this.types.createErrorType(var1);
         } else if (var6 && var4 && (var1.tsym.flags() & 512L) != 0L) {
            this.log.error(var2.pos(), "no.intf.expected.here", new Object[0]);
            return this.types.createErrorType(var1);
         } else {
            if (var6 && (var1.tsym.flags() & 16L) != 0L) {
               this.log.error(var2.pos(), "cant.inherit.from.final", new Object[]{var1.tsym});
            }

            this.chk.checkNonCyclic(var2.pos(), var1);
            return var1;
         }
      }
   }

   Type attribIdentAsEnumType(Env var1, JCTree.JCIdent var2) {
      Assert.check((var1.enclClass.sym.flags() & 16384L) != 0L);
      var2.type = ((AttrContext)var1.info).scope.owner.type;
      var2.sym = ((AttrContext)var1.info).scope.owner;
      return var2.type;
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      if ((((AttrContext)this.env.info).scope.owner.kind & 20) != 0) {
         this.enter.classEnter((JCTree)var1, this.env);
      } else if (this.env.tree.hasTag(JCTree.Tag.NEWCLASS) && TreeInfo.isInAnnotation(this.env, var1)) {
         this.enter.classEnter((JCTree)var1, this.env);
      }

      Symbol.ClassSymbol var2 = var1.sym;
      if (var2 == null) {
         this.result = null;
      } else {
         var2.complete();
         if (((AttrContext)this.env.info).isSelfCall && this.env.tree.hasTag(JCTree.Tag.NEWCLASS) && ((JCTree.JCNewClass)this.env.tree).encl == null) {
            var2.flags_field |= 4194304L;
         }

         this.attribClass(var1.pos(), var2);
         this.result = var1.type = var2.type;
      }

   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      Symbol.MethodSymbol var2 = var1.sym;
      boolean var3 = (var2.flags() & 8796093022208L) != 0L;
      Lint var4 = ((AttrContext)this.env.info).lint.augment((Symbol)var2);
      Lint var5 = this.chk.setLint(var4);
      Symbol.MethodSymbol var6 = this.chk.setMethod(var2);

      try {
         this.deferredLintHandler.flush(var1.pos());
         this.chk.checkDeprecatedAnnotation(var1.pos(), var2);
         Env var7 = this.memberEnter.methodEnv(var1, this.env);
         ((AttrContext)var7.info).lint = var4;
         this.attribStats(var1.typarams, var7);
         if (var2.isStatic()) {
            this.chk.checkHideClashes(var1.pos(), this.env.enclClass.type, var2);
         } else {
            this.chk.checkOverrideClashes(var1.pos(), this.env.enclClass.type, var2);
         }

         this.chk.checkOverride(var1, var2);
         if (var3 && this.types.overridesObjectMethod(var2.enclClass(), var2)) {
            this.log.error(var1, "default.overrides.object.member", new Object[]{var2.name, Kinds.kindName(var2.location()), var2.location()});
         }

         for(List var8 = var1.typarams; var8.nonEmpty(); var8 = var8.tail) {
            ((AttrContext)var7.info).scope.enterIfAbsent(((JCTree.JCTypeParameter)var8.head).type.tsym);
         }

         Symbol.ClassSymbol var13 = this.env.enclClass.sym;
         if ((var13.flags() & 8192L) != 0L && var1.params.nonEmpty()) {
            this.log.error(((JCTree.JCVariableDecl)var1.params.head).pos(), "intf.annotation.members.cant.have.params", new Object[0]);
         }

         List var9;
         for(var9 = var1.params; var9.nonEmpty(); var9 = var9.tail) {
            this.attribStat((JCTree)var9.head, var7);
         }

         this.chk.checkVarargsMethodDecl(var7, var1);
         this.chk.validate(var1.typarams, var7);
         if (var1.restype != null && !var1.restype.type.hasTag(TypeTag.VOID)) {
            this.chk.validate((JCTree)var1.restype, var7);
         }

         if (var1.recvparam != null) {
            Env var14 = this.memberEnter.methodEnv(var1, this.env);
            this.attribType(var1.recvparam, (Env)var14);
            this.chk.validate((JCTree)var1.recvparam, var14);
         }

         if ((var13.flags() & 8192L) != 0L) {
            if (var1.thrown.nonEmpty()) {
               this.log.error(((JCTree.JCExpression)var1.thrown.head).pos(), "throws.not.allowed.in.intf.annotation", new Object[0]);
            }

            if (var1.typarams.nonEmpty()) {
               this.log.error(((JCTree.JCTypeParameter)var1.typarams.head).pos(), "intf.annotation.members.cant.have.type.params", new Object[0]);
            }

            this.chk.validateAnnotationType(var1.restype);
            this.chk.validateAnnotationMethod(var1.pos(), var2);
         }

         for(var9 = var1.thrown; var9.nonEmpty(); var9 = var9.tail) {
            this.chk.checkType(((JCTree.JCExpression)var9.head).pos(), ((JCTree.JCExpression)var9.head).type, this.syms.throwableType);
         }

         if (var1.body == null) {
            if (var3 || (var1.sym.flags() & 1280L) == 0L && !this.relax) {
               this.log.error(var1.pos(), "missing.meth.body.or.decl.abstract", new Object[0]);
            }

            if (var1.defaultValue != null && (var13.flags() & 8192L) == 0L) {
               this.log.error(var1.pos(), "default.allowed.in.intf.annotation.member", new Object[0]);
            }
         } else if ((var1.sym.flags() & 1024L) != 0L && !var3) {
            if ((var13.flags() & 512L) != 0L) {
               this.log.error(var1.body.pos(), "intf.meth.cant.have.body", new Object[0]);
            } else {
               this.log.error(var1.pos(), "abstract.meth.cant.have.body", new Object[0]);
            }
         } else if ((var1.mods.flags & 256L) != 0L) {
            this.log.error(var1.pos(), "native.meth.cant.have.body", new Object[0]);
         } else {
            if (var1.name == this.names.init && var13.type != this.syms.objectType) {
               JCTree.JCBlock var15 = var1.body;
               if (!var15.stats.isEmpty() && TreeInfo.isSelfCall((JCTree)var15.stats.head)) {
                  if ((this.env.enclClass.sym.flags() & 16384L) != 0L && (var1.mods.flags & 68719476736L) == 0L && TreeInfo.isSuperCall((JCTree)var15.stats.head)) {
                     this.log.error(((JCTree.JCStatement)var1.body.stats.head).pos(), "call.to.super.not.allowed.in.enum.ctor", new Object[]{this.env.enclClass.sym});
                  }
               } else {
                  var15.stats = var15.stats.prepend(this.memberEnter.SuperCall(this.make.at(var15.pos), List.nil(), List.nil(), false));
               }
            }

            this.memberEnter.typeAnnotate(var1.body, var7, var2, (JCDiagnostic.DiagnosticPosition)null);
            this.annotate.flush();
            this.attribStat(var1.body, var7);
         }

         ((AttrContext)var7.info).scope.leave();
         this.result = var1.type = var2.type;
      } finally {
         this.chk.setLint(var5);
         this.chk.setMethod(var6);
      }
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      if (((AttrContext)this.env.info).scope.owner.kind == 16) {
         if (var1.sym != null) {
            ((AttrContext)this.env.info).scope.enter(var1.sym);
         } else {
            try {
               this.annotate.enterStart();
               this.memberEnter.memberEnter((JCTree)var1, this.env);
            } finally {
               this.annotate.enterDone();
            }
         }
      } else if (var1.init != null) {
         this.memberEnter.typeAnnotate(var1.init, this.env, var1.sym, var1.pos());
         this.annotate.flush();
      }

      Symbol.VarSymbol var2 = var1.sym;
      Lint var3 = ((AttrContext)this.env.info).lint.augment((Symbol)var2);
      Lint var4 = this.chk.setLint(var3);
      boolean var5 = this.env.tree.hasTag(JCTree.Tag.LAMBDA) && ((JCTree.JCLambda)this.env.tree).paramKind == JCTree.JCLambda.ParameterKind.IMPLICIT && (var1.sym.flags() & 8589934592L) != 0L;
      this.chk.validate(var1.vartype, this.env, !var5);

      try {
         var2.getConstValue();
         this.deferredLintHandler.flush(var1.pos());
         this.chk.checkDeprecatedAnnotation(var1.pos(), var2);
         if (var1.init != null && ((var2.flags_field & 16L) == 0L || !this.memberEnter.needsLazyConstValue(var1.init))) {
            Env var6 = this.memberEnter.initEnv(var1, this.env);
            ((AttrContext)var6.info).lint = var3;
            ((AttrContext)var6.info).enclVar = var2;
            this.attribExpr(var1.init, var6, var2.type);
         }

         this.result = var1.type = var2.type;
      } finally {
         this.chk.setLint(var4);
      }

   }

   public void visitSkip(JCTree.JCSkip var1) {
      this.result = null;
   }

   public void visitBlock(JCTree.JCBlock var1) {
      Env var2;
      if (((AttrContext)this.env.info).scope.owner.kind == 2) {
         var2 = this.env.dup(var1, ((AttrContext)this.env.info).dup(((AttrContext)this.env.info).scope.dupUnshared()));
         ((AttrContext)var2.info).scope.owner = new Symbol.MethodSymbol(var1.flags | 1048576L | ((AttrContext)this.env.info).scope.owner.flags() & 2048L, this.names.empty, (Type)null, ((AttrContext)this.env.info).scope.owner);
         if ((var1.flags & 8L) != 0L) {
            ++((AttrContext)var2.info).staticLevel;
         }

         this.memberEnter.typeAnnotate(var1, var2, ((AttrContext)var2.info).scope.owner, (JCDiagnostic.DiagnosticPosition)null);
         this.annotate.flush();
         Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)((AttrContext)this.env.info).scope.owner;
         List var4 = ((AttrContext)var2.info).scope.owner.getRawTypeAttributes();
         if ((var1.flags & 8L) != 0L) {
            var3.appendClassInitTypeAttributes(var4);
         } else {
            var3.appendInitTypeAttributes(var4);
         }

         this.attribStats(var1.stats, var2);
      } else {
         var2 = this.env.dup(var1, ((AttrContext)this.env.info).dup(((AttrContext)this.env.info).scope.dup()));

         try {
            this.attribStats(var1.stats, var2);
         } finally {
            ((AttrContext)var2.info).scope.leave();
         }
      }

      this.result = null;
   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      this.attribStat(var1.body, this.env.dup(var1));
      this.attribExpr(var1.cond, this.env, this.syms.booleanType);
      this.result = null;
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      this.attribExpr(var1.cond, this.env, this.syms.booleanType);
      this.attribStat(var1.body, this.env.dup(var1));
      this.result = null;
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      Env var2 = this.env.dup(this.env.tree, ((AttrContext)this.env.info).dup(((AttrContext)this.env.info).scope.dup()));

      try {
         this.attribStats(var1.init, var2);
         if (var1.cond != null) {
            this.attribExpr(var1.cond, var2, this.syms.booleanType);
         }

         var2.tree = var1;
         this.attribStats(var1.step, var2);
         this.attribStat(var1.body, var2);
         this.result = null;
      } finally {
         ((AttrContext)var2.info).scope.leave();
      }

   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      Env var2 = this.env.dup(this.env.tree, ((AttrContext)this.env.info).dup(((AttrContext)this.env.info).scope.dup()));

      try {
         Type var3 = this.types.cvarUpperBound(this.attribExpr(var1.expr, var2));
         this.attribStat(var1.var, var2);
         this.chk.checkNonVoid(var1.pos(), var3);
         Type var4 = this.types.elemtype(var3);
         if (var4 == null) {
            Type var5 = this.types.asSuper(var3, this.syms.iterableType.tsym);
            if (var5 == null) {
               this.log.error(var1.expr.pos(), "foreach.not.applicable.to.type", new Object[]{var3, this.diags.fragment("type.req.array.or.iterable")});
               var4 = this.types.createErrorType(var3);
            } else {
               List var6 = var5.allparams();
               var4 = var6.isEmpty() ? this.syms.objectType : this.types.wildUpperBound((Type)var6.head);
            }
         }

         this.chk.checkType(var1.expr.pos(), var4, var1.var.sym.type);
         var2.tree = var1;
         this.attribStat(var1.body, var2);
         this.result = null;
      } finally {
         ((AttrContext)var2.info).scope.leave();
      }

   }

   public void visitLabelled(JCTree.JCLabeledStatement var1) {
      for(Env var2 = this.env; var2 != null && !var2.tree.hasTag(JCTree.Tag.CLASSDEF); var2 = var2.next) {
         if (var2.tree.hasTag(JCTree.Tag.LABELLED) && ((JCTree.JCLabeledStatement)var2.tree).label == var1.label) {
            this.log.error(var1.pos(), "label.already.in.use", new Object[]{var1.label});
            break;
         }
      }

      this.attribStat(var1.body, this.env.dup(var1));
      this.result = null;
   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      Type var2 = this.attribExpr(var1.selector, this.env);
      Env var3 = this.env.dup(var1, ((AttrContext)this.env.info).dup(((AttrContext)this.env.info).scope.dup()));

      try {
         boolean var4 = this.allowEnums && (var2.tsym.flags() & 16384L) != 0L;
         boolean var5 = false;
         if (this.types.isSameType(var2, this.syms.stringType)) {
            if (this.allowStringsInSwitch) {
               var5 = true;
            } else {
               this.log.error(var1.selector.pos(), "string.switch.not.supported.in.source", new Object[]{this.sourceName});
            }
         }

         if (!var4 && !var5) {
            var2 = this.chk.checkType(var1.selector.pos(), var2, this.syms.intType);
         }

         HashSet var6 = new HashSet();
         boolean var7 = false;

         for(List var8 = var1.cases; var8.nonEmpty(); var8 = var8.tail) {
            JCTree.JCCase var9 = (JCTree.JCCase)var8.head;
            Env var10 = var3.dup(var9, ((AttrContext)this.env.info).dup(((AttrContext)var3.info).scope.dup()));

            try {
               if (var9.pat != null) {
                  if (var4) {
                     Symbol var11 = this.enumConstant(var9.pat, var2);
                     if (var11 == null) {
                        this.log.error(var9.pat.pos(), "enum.label.must.be.unqualified.enum", new Object[0]);
                     } else if (!var6.add(var11)) {
                        this.log.error(var9.pos(), "duplicate.case.label", new Object[0]);
                     }
                  } else {
                     Type var20 = this.attribExpr(var9.pat, var3, var2);
                     if (!var20.hasTag(TypeTag.ERROR)) {
                        if (var20.constValue() == null) {
                           this.log.error(var9.pat.pos(), var5 ? "string.const.req" : "const.expr.req", new Object[0]);
                        } else if (var6.contains(var20.constValue())) {
                           this.log.error(var9.pos(), "duplicate.case.label", new Object[0]);
                        } else {
                           var6.add(var20.constValue());
                        }
                     }
                  }
               } else if (var7) {
                  this.log.error(var9.pos(), "duplicate.default.label", new Object[0]);
               } else {
                  var7 = true;
               }

               this.attribStats(var9.stats, var10);
            } finally {
               ((AttrContext)var10.info).scope.leave();
               addVars(var9.stats, ((AttrContext)var3.info).scope);
            }
         }

         this.result = null;
      } finally {
         ((AttrContext)var3.info).scope.leave();
      }

   }

   private static void addVars(List var0, Scope var1) {
      for(; var0.nonEmpty(); var0 = var0.tail) {
         JCTree var2 = (JCTree)var0.head;
         if (var2.hasTag(JCTree.Tag.VARDEF)) {
            var1.enter(((JCTree.JCVariableDecl)var2).sym);
         }
      }

   }

   private Symbol enumConstant(JCTree var1, Type var2) {
      if (!var1.hasTag(JCTree.Tag.IDENT)) {
         this.log.error(var1.pos(), "enum.label.must.be.unqualified.enum", new Object[0]);
         return this.syms.errSymbol;
      } else {
         JCTree.JCIdent var3 = (JCTree.JCIdent)var1;
         Name var4 = var3.name;

         for(Scope.Entry var5 = var2.tsym.members().lookup(var4); var5.scope != null; var5 = var5.next()) {
            if (var5.sym.kind == 4) {
               Symbol var6 = var3.sym = var5.sym;
               ((Symbol.VarSymbol)var6).getConstValue();
               var3.type = var6.type;
               return (var6.flags_field & 16384L) == 0L ? null : var6;
            }
         }

         return null;
      }
   }

   public void visitSynchronized(JCTree.JCSynchronized var1) {
      this.chk.checkRefType(var1.pos(), this.attribExpr(var1.lock, this.env));
      this.attribStat(var1.body, this.env);
      this.result = null;
   }

   public void visitTry(JCTree.JCTry var1) {
      Env var2 = this.env.dup(var1, ((AttrContext)this.env.info).dup(((AttrContext)this.env.info).scope.dup()));

      try {
         boolean var3 = var1.resources.nonEmpty();
         Env var4 = var3 ? this.env.dup(var1, ((AttrContext)var2.info).dup(((AttrContext)var2.info).scope.dup())) : var2;

         try {
            Iterator var5 = var1.resources.iterator();

            while(var5.hasNext()) {
               JCTree var6 = (JCTree)var5.next();
               Check.NestedCheckContext var7 = new Check.NestedCheckContext(this.resultInfo.checkContext) {
                  public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2) {
                     Attr.this.chk.basicHandler.report(var1, Attr.this.diags.fragment("try.not.applicable.to.type", var2));
                  }
               };
               ResultInfo var8 = new ResultInfo(12, this.syms.autoCloseableType, var7);
               if (var6.hasTag(JCTree.Tag.VARDEF)) {
                  this.attribStat(var6, var4);
                  var8.check(var6, var6.type);
                  this.checkAutoCloseable(var6.pos(), var2, var6.type);
                  Symbol.VarSymbol var9 = ((JCTree.JCVariableDecl)var6).sym;
                  var9.setData(ElementKind.RESOURCE_VARIABLE);
               } else {
                  this.attribTree(var6, var4, var8);
               }
            }

            this.attribStat(var1.body, var4);
         } finally {
            if (var3) {
               ((AttrContext)var4.info).scope.leave();
            }

         }

         for(List var25 = var1.catchers; var25.nonEmpty(); var25 = var25.tail) {
            JCTree.JCCatch var26 = (JCTree.JCCatch)var25.head;
            Env var27 = var2.dup(var26, ((AttrContext)var2.info).dup(((AttrContext)var2.info).scope.dup()));

            try {
               Type var28 = this.attribStat(var26.param, var27);
               if (TreeInfo.isMultiCatch(var26)) {
                  Symbol.VarSymbol var10000 = var26.param.sym;
                  var10000.flags_field |= 549755813904L;
               }

               if (var26.param.sym.kind == 4) {
                  var26.param.sym.setData(ElementKind.EXCEPTION_PARAMETER);
               }

               this.chk.checkType(var26.param.vartype.pos(), this.chk.checkClassType(var26.param.vartype.pos(), var28), this.syms.throwableType);
               this.attribStat(var26.body, var27);
            } finally {
               ((AttrContext)var27.info).scope.leave();
            }
         }

         if (var1.finalizer != null) {
            this.attribStat(var1.finalizer, var2);
         }

         this.result = null;
      } finally {
         ((AttrContext)var2.info).scope.leave();
      }
   }

   void checkAutoCloseable(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3) {
      if (!var3.isErroneous() && this.types.asSuper(var3, this.syms.autoCloseableType.tsym) != null && !this.types.isSameType(var3, this.syms.autoCloseableType)) {
         Symbol.TypeSymbol var4 = this.syms.noSymbol;
         Log.DiscardDiagnosticHandler var5 = new Log.DiscardDiagnosticHandler(this.log);

         Symbol var9;
         try {
            var9 = this.rs.resolveQualifiedMethod(var1, var2, var3, this.names.close, List.nil(), List.nil());
         } finally {
            this.log.popDiagnosticHandler(var5);
         }

         if (var9.kind == 16 && var9.overrides(this.syms.autoCloseableClose, var3.tsym, this.types, true) && this.chk.isHandled(this.syms.interruptedExceptionType, this.types.memberType(var3, var9).getThrownTypes()) && ((AttrContext)var2.info).lint.isEnabled(Lint.LintCategory.TRY)) {
            this.log.warning(Lint.LintCategory.TRY, var1, "try.resource.throws.interrupted.exc", new Object[]{var3});
         }
      }

   }

   public void visitConditional(JCTree.JCConditional var1) {
      Type var2 = this.attribExpr(var1.cond, this.env, this.syms.booleanType);
      var1.polyKind = this.allowPoly && (!this.pt().hasTag(TypeTag.NONE) || this.pt() == Type.recoveryType) && !this.isBooleanOrNumeric(this.env, var1) ? JCTree.JCPolyExpression.PolyKind.POLY : JCTree.JCPolyExpression.PolyKind.STANDALONE;
      if (var1.polyKind == JCTree.JCPolyExpression.PolyKind.POLY && this.resultInfo.pt.hasTag(TypeTag.VOID)) {
         this.resultInfo.checkContext.report(var1, this.diags.fragment("conditional.target.cant.be.void"));
         this.result = var1.type = this.types.createErrorType(this.resultInfo.pt);
      } else {
         ResultInfo var3 = var1.polyKind == JCTree.JCPolyExpression.PolyKind.STANDALONE ? this.unknownExprInfo : this.resultInfo.dup((Check.CheckContext)(new Check.NestedCheckContext(this.resultInfo.checkContext) {
            public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2) {
               this.enclosingContext.report(var1, Attr.this.diags.fragment("incompatible.type.in.conditional", var2));
            }
         }));
         Type var4 = this.attribTree(var1.truepart, this.env, var3);
         Type var5 = this.attribTree(var1.falsepart, this.env, var3);
         Type var6 = var1.polyKind == JCTree.JCPolyExpression.PolyKind.STANDALONE ? this.condType(var1, var4, var5) : this.pt();
         if (var2.constValue() != null && var4.constValue() != null && var5.constValue() != null && !var6.hasTag(TypeTag.NONE)) {
            var6 = this.cfolder.coerce(var2.isTrue() ? var4 : var5, var6);
         }

         this.result = this.check(var1, var6, 12, this.resultInfo);
      }
   }

   private boolean isBooleanOrNumeric(Env var1, JCTree.JCExpression var2) {
      switch (var2.getTag()) {
         case LITERAL:
            return ((JCTree.JCLiteral)var2).typetag.isSubRangeOf(TypeTag.DOUBLE) || ((JCTree.JCLiteral)var2).typetag == TypeTag.BOOLEAN || ((JCTree.JCLiteral)var2).typetag == TypeTag.BOT;
         case LAMBDA:
         case REFERENCE:
            return false;
         case PARENS:
            return this.isBooleanOrNumeric(var1, ((JCTree.JCParens)var2).expr);
         case CONDEXPR:
            JCTree.JCConditional var3 = (JCTree.JCConditional)var2;
            return this.isBooleanOrNumeric(var1, var3.truepart) && this.isBooleanOrNumeric(var1, var3.falsepart);
         case APPLY:
            JCTree.JCMethodInvocation var4 = (JCTree.JCMethodInvocation)this.deferredAttr.attribSpeculative(var2, var1, this.unknownExprInfo);
            Type var5 = TreeInfo.symbol(var4.meth).type.getReturnType();
            return this.types.unboxedTypeOrType(var5).isPrimitive();
         case NEWCLASS:
            JCTree.JCExpression var6 = (JCTree.JCExpression)this.removeClassParams.translate((JCTree)((JCTree.JCNewClass)var2).clazz);
            JCTree.JCExpression var7 = (JCTree.JCExpression)this.deferredAttr.attribSpeculative(var6, var1, this.unknownTypeInfo);
            return this.types.unboxedTypeOrType(var7.type).isPrimitive();
         default:
            Type var8 = this.deferredAttr.attribSpeculative(var2, var1, this.unknownExprInfo).type;
            var8 = this.types.unboxedTypeOrType(var8);
            return var8.isPrimitive();
      }
   }

   private Type condType(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
      if (this.types.isSameType(var2, var3)) {
         return var2.baseType();
      } else {
         Type var4 = this.allowBoxing && !var2.isPrimitive() ? this.types.unboxedType(var2) : var2;
         Type var5 = this.allowBoxing && !var3.isPrimitive() ? this.types.unboxedType(var3) : var3;
         if (var4.isPrimitive() && var5.isPrimitive()) {
            if (var4.getTag().isStrictSubRangeOf(TypeTag.INT) && var5.hasTag(TypeTag.INT) && this.types.isAssignable(var5, var4)) {
               return var4.baseType();
            }

            if (var5.getTag().isStrictSubRangeOf(TypeTag.INT) && var4.hasTag(TypeTag.INT) && this.types.isAssignable(var4, var5)) {
               return var5.baseType();
            }

            TypeTag[] var6 = primitiveTags;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               TypeTag var9 = var6[var8];
               Type var10 = this.syms.typeOfTag[var9.ordinal()];
               if (this.types.isSubtype(var4, var10) && this.types.isSubtype(var5, var10)) {
                  return var10;
               }
            }
         }

         if (this.allowBoxing) {
            if (var2.isPrimitive()) {
               var2 = this.types.boxedClass(var2).type;
            }

            if (var3.isPrimitive()) {
               var3 = this.types.boxedClass(var3).type;
            }
         }

         if (this.types.isSubtype(var2, var3)) {
            return var3.baseType();
         } else if (this.types.isSubtype(var3, var2)) {
            return var2.baseType();
         } else if (this.allowBoxing && !var2.hasTag(TypeTag.VOID) && !var3.hasTag(TypeTag.VOID)) {
            return this.types.lub(var2.baseType(), var3.baseType());
         } else {
            this.log.error(var1, "neither.conditional.subtype", new Object[]{var2, var3});
            return var2.baseType();
         }
      }
   }

   public void visitIf(JCTree.JCIf var1) {
      this.attribExpr(var1.cond, this.env, this.syms.booleanType);
      this.attribStat(var1.thenpart, this.env);
      if (var1.elsepart != null) {
         this.attribStat(var1.elsepart, this.env);
      }

      this.chk.checkEmptyIf(var1);
      this.result = null;
   }

   public void visitExec(JCTree.JCExpressionStatement var1) {
      Env var2 = this.env.dup(var1);
      this.attribExpr(var1.expr, var2);
      this.result = null;
   }

   public void visitBreak(JCTree.JCBreak var1) {
      var1.target = this.findJumpTarget(var1.pos(), var1.getTag(), var1.label, this.env);
      this.result = null;
   }

   public void visitContinue(JCTree.JCContinue var1) {
      var1.target = this.findJumpTarget(var1.pos(), var1.getTag(), var1.label, this.env);
      this.result = null;
   }

   private JCTree findJumpTarget(JCDiagnostic.DiagnosticPosition var1, JCTree.Tag var2, Name var3, Env var4) {
      label52:
      for(Env var5 = var4; var5 != null; var5 = var5.next) {
         switch (var5.tree.getTag()) {
            case LAMBDA:
            case METHODDEF:
            case CLASSDEF:
               break label52;
            case REFERENCE:
            case PARENS:
            case CONDEXPR:
            case APPLY:
            case NEWCLASS:
            default:
               break;
            case LABELLED:
               JCTree.JCLabeledStatement var6 = (JCTree.JCLabeledStatement)var5.tree;
               if (var3 == var6.label) {
                  if (var2 == JCTree.Tag.CONTINUE) {
                     if (!var6.body.hasTag(JCTree.Tag.DOLOOP) && !var6.body.hasTag(JCTree.Tag.WHILELOOP) && !var6.body.hasTag(JCTree.Tag.FORLOOP) && !var6.body.hasTag(JCTree.Tag.FOREACHLOOP)) {
                        this.log.error(var1, "not.loop.label", new Object[]{var3});
                     }

                     return TreeInfo.referencedStatement(var6);
                  }

                  return var6;
               }
               break;
            case DOLOOP:
            case WHILELOOP:
            case FORLOOP:
            case FOREACHLOOP:
               if (var3 == null) {
                  return var5.tree;
               }
               break;
            case SWITCH:
               if (var3 == null && var2 == JCTree.Tag.BREAK) {
                  return var5.tree;
               }
         }
      }

      if (var3 != null) {
         this.log.error(var1, "undef.label", new Object[]{var3});
      } else if (var2 == JCTree.Tag.CONTINUE) {
         this.log.error(var1, "cont.outside.loop", new Object[0]);
      } else {
         this.log.error(var1, "break.outside.switch.loop", new Object[0]);
      }

      return null;
   }

   public void visitReturn(JCTree.JCReturn var1) {
      if (((AttrContext)this.env.info).returnResult == null) {
         this.log.error(var1.pos(), "ret.outside.meth", new Object[0]);
      } else if (var1.expr != null) {
         if (((AttrContext)this.env.info).returnResult.pt.hasTag(TypeTag.VOID)) {
            ((AttrContext)this.env.info).returnResult.checkContext.report(var1.expr.pos(), this.diags.fragment("unexpected.ret.val"));
         }

         this.attribTree(var1.expr, this.env, ((AttrContext)this.env.info).returnResult);
      } else if (!((AttrContext)this.env.info).returnResult.pt.hasTag(TypeTag.VOID) && !((AttrContext)this.env.info).returnResult.pt.hasTag(TypeTag.NONE)) {
         ((AttrContext)this.env.info).returnResult.checkContext.report(var1.pos(), this.diags.fragment("missing.ret.val"));
      }

      this.result = null;
   }

   public void visitThrow(JCTree.JCThrow var1) {
      Type var2 = this.attribExpr(var1.expr, this.env, (Type)(this.allowPoly ? Type.noType : this.syms.throwableType));
      if (this.allowPoly) {
         this.chk.checkType(var1, var2, this.syms.throwableType);
      }

      this.result = null;
   }

   public void visitAssert(JCTree.JCAssert var1) {
      this.attribExpr(var1.cond, this.env, this.syms.booleanType);
      if (var1.detail != null) {
         this.chk.checkNonVoid(var1.detail.pos(), this.attribExpr(var1.detail, this.env));
      }

      this.result = null;
   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      Env var2 = this.env.dup(var1, ((AttrContext)this.env.info).dup());
      List var4 = null;
      Name var5 = TreeInfo.name(var1.meth);
      boolean var6 = var5 == this.names._this || var5 == this.names._super;
      ListBuffer var7 = new ListBuffer();
      List var3;
      int var8;
      Type var9;
      Type var10;
      if (var6) {
         if (this.checkFirstConstructorStat(var1, this.env)) {
            ((AttrContext)var2.info).isSelfCall = true;
            var8 = this.attribArgs(16, var1.args, var2, var7);
            var3 = var7.toList();
            var4 = this.attribTypes(var1.typeargs, var2);
            var9 = this.env.enclClass.sym.type;
            if (var5 == this.names._super) {
               if (var9 == this.syms.objectType) {
                  this.log.error(var1.meth.pos(), "no.superclass", new Object[]{var9});
                  var9 = this.types.createErrorType(this.syms.objectType);
               } else {
                  var9 = this.types.supertype(var9);
               }
            }

            if (var9.hasTag(TypeTag.CLASS)) {
               for(var10 = var9.getEnclosingType(); var10 != null && var10.hasTag(TypeTag.TYPEVAR); var10 = var10.getUpperBound()) {
               }

               if (var10.hasTag(TypeTag.CLASS)) {
                  if (var1.meth.hasTag(JCTree.Tag.SELECT)) {
                     JCTree.JCExpression var11 = ((JCTree.JCFieldAccess)var1.meth).selected;
                     this.chk.checkRefType(var11.pos(), this.attribExpr(var11, var2, var10));
                  } else if (var5 == this.names._super) {
                     this.rs.resolveImplicitThis(var1.meth.pos(), var2, var9, true);
                  }
               } else if (var1.meth.hasTag(JCTree.Tag.SELECT)) {
                  this.log.error(var1.meth.pos(), "illegal.qual.not.icls", new Object[]{var9.tsym});
               }

               if (var9.tsym == this.syms.enumSym && this.allowEnums) {
                  var3 = var3.prepend(this.syms.intType).prepend(this.syms.stringType);
               }

               boolean var14 = ((AttrContext)var2.info).selectSuper;
               ((AttrContext)var2.info).selectSuper = true;
               ((AttrContext)var2.info).pendingResolutionPhase = null;
               Symbol var12 = this.rs.resolveConstructor(var1.meth.pos(), var2, var9, var3, var4);
               ((AttrContext)var2.info).selectSuper = var14;
               TreeInfo.setSymbol(var1.meth, var12);
               Type var13 = this.newMethodTemplate(this.resultInfo.pt, var3, var4);
               this.checkId(var1.meth, var9, var12, var2, new ResultInfo(var8, var13));
            }
         }

         this.result = var1.type = this.syms.voidType;
      } else {
         var8 = this.attribArgs(12, var1.args, var2, var7);
         var3 = var7.toList();
         var4 = this.attribAnyTypes(var1.typeargs, var2);
         var9 = this.newMethodTemplate(this.resultInfo.pt, var3, var4);
         ((AttrContext)var2.info).pendingResolutionPhase = null;
         var10 = this.attribTree(var1.meth, var2, new ResultInfo(var8, var9, this.resultInfo.checkContext));
         Type var15 = var10.getReturnType();
         if (var15.hasTag(TypeTag.WILDCARD)) {
            throw new AssertionError(var10);
         }

         Type var16 = var1.meth.hasTag(JCTree.Tag.SELECT) ? ((JCTree.JCFieldAccess)var1.meth).selected.type : this.env.enclClass.sym.type;
         var15 = this.adjustMethodReturnType(var16, var5, var3, var15);
         this.chk.checkRefTypes(var1.typeargs, var4);
         this.result = this.check(var1, this.capture(var15), 12, this.resultInfo);
      }

      this.chk.validate(var1.typeargs, var2);
   }

   Type adjustMethodReturnType(Type var1, Name var2, List var3, Type var4) {
      if (this.allowCovariantReturns && var2 == this.names.clone && this.types.isArray(var1)) {
         return var1;
      } else {
         return (Type)(this.allowGenerics && var2 == this.names.getClass && var3.isEmpty() ? new Type.ClassType(var4.getEnclosingType(), List.of(new Type.WildcardType(this.types.erasure(var1), BoundKind.EXTENDS, this.syms.boundClass)), var4.tsym) : var4);
      }
   }

   boolean checkFirstConstructorStat(JCTree.JCMethodInvocation var1, Env var2) {
      JCTree.JCMethodDecl var3 = var2.enclMethod;
      if (var3 != null && var3.name == this.names.init) {
         JCTree.JCBlock var4 = var3.body;
         if (((JCTree.JCStatement)var4.stats.head).hasTag(JCTree.Tag.EXEC) && ((JCTree.JCExpressionStatement)var4.stats.head).expr == var1) {
            return true;
         }
      }

      this.log.error(var1.pos(), "call.must.be.first.stmt.in.ctor", new Object[]{TreeInfo.name(var1.meth)});
      return false;
   }

   Type newMethodTemplate(Type var1, List var2, List var3) {
      Type.MethodType var4 = new Type.MethodType(var2, var1, List.nil(), this.syms.methodClass);
      return (Type)(var3 == null ? var4 : new Type.ForAll(var3, var4));
   }

   public void visitNewClass(final JCTree.JCNewClass var1) {
      Type var2 = this.types.createErrorType(var1.type);
      Env var3 = this.env.dup(var1, ((AttrContext)this.env.info).dup());
      JCTree.JCClassDecl var4 = var1.def;
      Object var5 = var1.clazz;
      JCTree.JCAnnotatedType var7 = null;
      Object var6;
      if (((JCTree.JCExpression)var5).hasTag(JCTree.Tag.TYPEAPPLY)) {
         var6 = ((JCTree.JCTypeApply)var5).clazz;
         if (((JCTree.JCExpression)var6).hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
            var7 = (JCTree.JCAnnotatedType)var6;
            var6 = var7.underlyingType;
         }
      } else if (((JCTree.JCExpression)var5).hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
         var7 = (JCTree.JCAnnotatedType)var5;
         var6 = var7.underlyingType;
      } else {
         var6 = var5;
      }

      Object var8 = var6;
      Type var9;
      List var12;
      if (var1.encl != null) {
         var9 = this.chk.checkRefType(var1.encl.pos(), this.attribExpr(var1.encl, this.env));
         var8 = this.make.at(((JCTree.JCExpression)var5).pos).Select(this.make.Type(var9), ((JCTree.JCIdent)var6).name);
         EndPosTable var10 = this.env.toplevel.endPositions;
         var10.storeEnd((JCTree)var8, var1.getEndPosition(var10));
         if (((JCTree.JCExpression)var5).hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
            JCTree.JCAnnotatedType var11 = (JCTree.JCAnnotatedType)var5;
            var12 = var11.annotations;
            if (var11.underlyingType.hasTag(JCTree.Tag.TYPEAPPLY)) {
               var8 = this.make.at(var1.pos).TypeApply((JCTree.JCExpression)var8, ((JCTree.JCTypeApply)var5).arguments);
            }

            var8 = this.make.at(var1.pos).AnnotatedType(var12, (JCTree.JCExpression)var8);
         } else if (((JCTree.JCExpression)var5).hasTag(JCTree.Tag.TYPEAPPLY)) {
            var8 = this.make.at(var1.pos).TypeApply((JCTree.JCExpression)var8, ((JCTree.JCTypeApply)var5).arguments);
         }

         var5 = var8;
      }

      var9 = TreeInfo.isEnumInit(this.env.tree) ? this.attribIdentAsEnumType(this.env, (JCTree.JCIdent)var5) : this.attribType((JCTree)var5, (Env)this.env);
      var9 = this.chk.checkDiamond(var1, var9);
      this.chk.validate((JCTree)var5, var3);
      if (var1.encl != null) {
         var1.clazz.type = var9;
         TreeInfo.setSymbol((JCTree)var6, TreeInfo.symbol((JCTree)var8));
         ((JCTree.JCExpression)var6).type = ((JCTree.JCIdent)var6).sym.type;
         if (var7 != null) {
            var7.type = ((JCTree.JCExpression)var6).type;
         }

         if (!var9.isErroneous()) {
            if (var4 != null && var9.tsym.isInterface()) {
               this.log.error(var1.encl.pos(), "anon.class.impl.intf.no.qual.for.new", new Object[0]);
            } else if (var9.tsym.isStatic()) {
               this.log.error(var1.encl.pos(), "qualified.new.of.static.class", new Object[]{var9.tsym});
            }
         }
      } else if (!var9.tsym.isInterface() && var9.getEnclosingType().hasTag(TypeTag.CLASS)) {
         this.rs.resolveImplicitThis(var1.pos(), this.env, var9);
      }

      ListBuffer var20 = new ListBuffer();
      int var21 = this.attribArgs(12, var1.args, var3, var20);
      var12 = var20.toList();
      List var13 = this.attribTypes(var1.typeargs, var3);
      if (var9.hasTag(TypeTag.CLASS)) {
         if (this.allowEnums && (var9.tsym.flags_field & 16384L) != 0L && (!this.env.tree.hasTag(JCTree.Tag.VARDEF) || (((JCTree.JCVariableDecl)this.env.tree).mods.flags & 16384L) == 0L || ((JCTree.JCVariableDecl)this.env.tree).init != var1)) {
            this.log.error(var1.pos(), "enum.cant.be.instantiated", new Object[0]);
         }

         if (var4 == null && (var9.tsym.flags() & 1536L) != 0L) {
            this.log.error(var1.pos(), "abstract.cant.be.instantiated", new Object[]{var9.tsym});
         } else if (var4 != null && var9.tsym.isInterface()) {
            if (!var12.isEmpty()) {
               this.log.error(((JCTree.JCExpression)var1.args.head).pos(), "anon.class.impl.intf.no.args", new Object[0]);
            }

            if (!var13.isEmpty()) {
               this.log.error(((JCTree.JCExpression)var1.typeargs.head).pos(), "anon.class.impl.intf.no.typeargs", new Object[0]);
            }

            var12 = List.nil();
            var13 = List.nil();
         } else if (TreeInfo.isDiamond(var1)) {
            Type.ClassType var22 = new Type.ClassType(var9.getEnclosingType(), var9.tsym.type.getTypeArguments(), var9.tsym);
            Env var15 = var3.dup(var1);
            ((AttrContext)var15.info).selectSuper = var4 != null;
            ((AttrContext)var15.info).pendingResolutionPhase = null;
            Symbol var16 = this.rs.resolveDiamond(var1.pos(), var15, var22, var12, var13);
            var1.constructor = var16.baseSymbol();
            final Symbol.TypeSymbol var17 = var9.tsym;
            ResultInfo var18 = new ResultInfo(var21, this.newMethodTemplate(this.resultInfo.pt, var12, var13), new Check.NestedCheckContext(this.resultInfo.checkContext) {
               public void report(JCDiagnostic.DiagnosticPosition var1x, JCDiagnostic var2) {
                  this.enclosingContext.report(var1.clazz, Attr.this.diags.fragment("cant.apply.diamond.1", Attr.this.diags.fragment("diamond", var17), var2));
               }
            });
            Type var19 = var1.constructorType = this.types.createErrorType(var9);
            var19 = this.checkId(this.noCheckTree, var22, var16, var15, var18);
            var1.clazz.type = this.types.createErrorType(var9);
            if (!var19.isErroneous()) {
               var1.clazz.type = var19.getReturnType();
               var1.constructorType = this.types.createMethodTypeWithReturn(var19, this.syms.voidType);
            }

            var9 = this.chk.checkClassType(var1.clazz, var1.clazz.type, true);
         } else {
            Env var14 = var3.dup(var1);
            ((AttrContext)var14.info).selectSuper = var4 != null;
            ((AttrContext)var14.info).pendingResolutionPhase = null;
            var1.constructor = this.rs.resolveConstructor(var1.pos(), var14, var9, var12, var13);
            if (var4 == null) {
               var1.constructorType = this.checkId(this.noCheckTree, var9, var1.constructor, var14, new ResultInfo(var21, this.newMethodTemplate(this.syms.voidType, var12, var13)));
               if (((AttrContext)var14.info).lastResolveVarargs()) {
                  Assert.check(var1.constructorType.isErroneous() || var1.varargsElement != null);
               }
            }

            if (var4 == null && !var9.isErroneous() && var9.getTypeArguments().nonEmpty() && this.findDiamonds) {
               this.findDiamond(var3, var1, var9);
            }
         }

         if (var4 != null) {
            if (Resolve.isStatic(this.env)) {
               JCTree.JCModifiers var10000 = var4.mods;
               var10000.flags |= 8L;
            }

            if (var9.tsym.isInterface()) {
               var4.implementing = List.of(var5);
            } else {
               var4.extending = (JCTree.JCExpression)var5;
            }

            if (this.resultInfo.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.CHECK && this.isSerializable(var9)) {
               ((AttrContext)var3.info).isSerializable = true;
            }

            this.attribStat(var4, var3);
            this.checkLambdaCandidate(var1, var4.sym, var9);
            if (var1.encl != null && !var9.tsym.isInterface()) {
               var1.args = var1.args.prepend(this.makeNullCheck(var1.encl));
               var12 = var12.prepend(var1.encl.type);
               var1.encl = null;
            }

            var9 = var4.sym.type;
            Symbol var23 = var1.constructor = this.rs.resolveConstructor(var1.pos(), var3, var9, var12, var13);
            Assert.check(var23.kind < 129);
            var1.constructor = var23;
            var1.constructorType = this.checkId(this.noCheckTree, var9, var1.constructor, var3, new ResultInfo(var21, this.newMethodTemplate(this.syms.voidType, var12, var13)));
         }

         if (var1.constructor != null && var1.constructor.kind == 16) {
            var2 = var9;
         }
      }

      this.result = this.check(var1, var2, 12, this.resultInfo);
      Infer.InferenceContext var24 = this.resultInfo.checkContext.inferenceContext();
      if (var1.constructorType != null && var24.free(var1.constructorType)) {
         var24.addFreeTypeListener(List.of(var1.constructorType), new Infer.FreeTypeListener() {
            public void typesInferred(Infer.InferenceContext var1x) {
               var1.constructorType = var1x.asInstType(var1.constructorType);
            }
         });
      }

      this.chk.validate(var1.typeargs, var3);
   }

   void findDiamond(Env var1, JCTree.JCNewClass var2, Type var3) {
      JCTree.JCTypeApply var4 = (JCTree.JCTypeApply)var2.clazz;
      List var5 = var4.arguments;

      try {
         var4.arguments = List.nil();
         ResultInfo var6 = new ResultInfo(12, (Type)(this.resultInfo.checkContext.inferenceContext().free(this.resultInfo.pt) ? Type.noType : this.pt()));
         Type var7 = this.deferredAttr.attribSpeculative(var2, var1, var6).type;
         Type var8 = this.allowPoly ? this.syms.objectType : var3;
         if (!var7.isErroneous()) {
            if (this.allowPoly && this.pt() == Infer.anyPoly) {
               if (!this.types.isSameType(var7, var3)) {
                  return;
               }
            } else if (!this.types.isAssignable(var7, this.pt().hasTag(TypeTag.NONE) ? var8 : this.pt(), this.types.noWarnings)) {
               return;
            }

            String var9 = this.types.isSameType(var3, var7) ? "diamond.redundant.args" : "diamond.redundant.args.1";
            this.log.warning(var2.clazz.pos(), var9, new Object[]{var3, var7});
         }
      } finally {
         var4.arguments = var5;
      }

   }

   private void checkLambdaCandidate(JCTree.JCNewClass var1, Symbol.ClassSymbol var2, Type var3) {
      if (this.allowLambda && this.identifyLambdaCandidate && var3.hasTag(TypeTag.CLASS) && !this.pt().hasTag(TypeTag.NONE) && this.types.isFunctionalInterface(var3.tsym)) {
         Symbol var4 = this.types.findDescriptorSymbol(var3.tsym);
         int var5 = 0;
         boolean var6 = false;
         Iterator var7 = var2.members().getElements().iterator();

         while(var7.hasNext()) {
            Symbol var8 = (Symbol)var7.next();
            if ((var8.flags() & 4096L) == 0L && !var8.isConstructor()) {
               ++var5;
               if (var8.kind == 16 && var8.name.equals(var4.name)) {
                  Type var9 = this.types.memberType(var3, var8);
                  if (this.types.overrideEquivalent(var9, this.types.memberType(var3, var4))) {
                     var6 = true;
                  }
               }
            }
         }

         if (var6 && var5 == 1) {
            this.log.note(var1.def, "potential.lambda.found", new Object[0]);
         }
      }

   }

   public JCTree.JCExpression makeNullCheck(JCTree.JCExpression var1) {
      Name var2 = TreeInfo.name(var1);
      if (var2 != this.names._this && var2 != this.names._super) {
         JCTree.Tag var3 = JCTree.Tag.NULLCHK;
         JCTree.JCUnary var4 = this.make.at(var1.pos).Unary(var3, var1);
         var4.operator = this.syms.nullcheck;
         var4.type = var1.type;
         return var4;
      } else {
         return var1;
      }
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      Object var2 = this.types.createErrorType(var1.type);
      Env var3 = this.env.dup(var1);
      Type var4;
      if (var1.elemtype != null) {
         var4 = this.attribType(var1.elemtype, (Env)var3);
         this.chk.validate((JCTree)var1.elemtype, var3);
         var2 = var4;

         for(List var5 = var1.dims; var5.nonEmpty(); var5 = var5.tail) {
            this.attribExpr((JCTree)var5.head, var3, this.syms.intType);
            var2 = new Type.ArrayType((Type)var2, this.syms.arrayClass);
         }
      } else if (this.pt().hasTag(TypeTag.ARRAY)) {
         var4 = this.types.elemtype(this.pt());
      } else {
         if (!this.pt().hasTag(TypeTag.ERROR)) {
            this.log.error(var1.pos(), "illegal.initializer.for.type", new Object[]{this.pt()});
         }

         var4 = this.types.createErrorType(this.pt());
      }

      if (var1.elems != null) {
         this.attribExprs(var1.elems, var3, var4);
         var2 = new Type.ArrayType(var4, this.syms.arrayClass);
      }

      if (!this.types.isReifiable(var4)) {
         this.log.error(var1.pos(), "generic.array.creation", new Object[0]);
      }

      this.result = this.check(var1, (Type)var2, 12, this.resultInfo);
   }

   public void visitLambda(JCTree.JCLambda var1) {
      if (!this.pt().isErroneous() && (!this.pt().hasTag(TypeTag.NONE) || this.pt() == Type.recoveryType)) {
         Env var2 = this.lambdaEnv(var1, this.env);
         boolean var3 = this.resultInfo.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.CHECK;

         try {
            Type var4 = this.pt();
            if (var3 && this.isSerializable(var4)) {
               ((AttrContext)var2.info).isSerializable = true;
            }

            List var17 = null;
            if (var1.paramKind == JCTree.JCLambda.ParameterKind.EXPLICIT) {
               this.attribStats(var1.params, var2);
               var17 = TreeInfo.types(var1.params);
            }

            Type var6;
            Object var18;
            if (this.pt() != Type.recoveryType) {
               var4 = (Type)this.targetChecker.visit(var4, var1);
               if (var17 != null) {
                  var4 = this.infer.instantiateFunctionalInterface(var1, var4, var17, this.resultInfo.checkContext);
               }

               var18 = this.types.removeWildcards(var4);
               var6 = this.types.findDescriptorType((Type)var18);
            } else {
               var18 = Type.recoveryType;
               var6 = this.fallbackDescriptorType(var1);
            }

            this.setFunctionalInfo(var2, var1, this.pt(), var6, (Type)var18, this.resultInfo.checkContext);
            if (!var6.hasTag(TypeTag.FORALL)) {
               boolean var9;
               if (var1.paramKind == JCTree.JCLambda.ParameterKind.IMPLICIT) {
                  List var7 = var6.getParameterTypes();
                  List var8 = var1.params;

                  for(var9 = false; var8.nonEmpty(); var8 = var8.tail) {
                     if (var7.isEmpty()) {
                        var9 = true;
                     }

                     Type var10 = var9 ? this.syms.errType : (Type)var7.head;
                     ((JCTree.JCVariableDecl)var8.head).vartype = this.make.at((JCDiagnostic.DiagnosticPosition)var8.head).Type(var10);
                     ((JCTree.JCVariableDecl)var8.head).sym = null;
                     var7 = var7.isEmpty() ? var7 : var7.tail;
                  }

                  this.attribStats(var1.params, var2);
                  if (var9) {
                     this.resultInfo.checkContext.report(var1, this.diags.fragment("incompatible.arg.types.in.lambda"));
                     this.result = var1.type = this.types.createErrorType((Type)var18);
                     return;
                  }
               }

               var3 = false;
               Object var19 = var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION ? new ExpressionLambdaReturnContext((JCTree.JCExpression)var1.getBody(), this.resultInfo.checkContext) : new FunctionalReturnContext(this.resultInfo.checkContext);
               ResultInfo var20 = var6.getReturnType() == Type.recoveryType ? this.recoveryInfo : new ResultInfo(12, var6.getReturnType(), (Check.CheckContext)var19);
               ((AttrContext)var2.info).returnResult = var20;
               if (var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION) {
                  this.attribTree(var1.getBody(), var2, var20);
               } else {
                  JCTree.JCBlock var21 = (JCTree.JCBlock)var1.body;
                  this.attribStats(var21.stats, var2);
               }

               this.result = this.check(var1, (Type)var18, 12, this.resultInfo);
               var9 = this.resultInfo.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.SPECULATIVE;
               this.preFlow(var1);
               this.flow.analyzeLambda(this.env, var1, this.make, var9);
               var1.type = (Type)var18;
               this.checkLambdaCompatible(var1, var6, this.resultInfo.checkContext);
               if (!var9) {
                  if (this.resultInfo.checkContext.inferenceContext().free(var6.getThrownTypes())) {
                     List var22 = this.flow.analyzeLambdaThrownTypes(this.env, var1, this.make);
                     List var11 = this.resultInfo.checkContext.inferenceContext().asUndetVars(var6.getThrownTypes());
                     this.chk.unhandled(var22, var11);
                  }

                  this.checkAccessibleTypes(var1, var2, this.resultInfo.checkContext.inferenceContext(), (Type[])(var6, (Type)var18));
               }

               this.result = this.check(var1, (Type)var18, 12, this.resultInfo);
               return;
            }

            this.resultInfo.checkContext.report(var1, this.diags.fragment("invalid.generic.lambda.target", var6, Kinds.kindName((Symbol)((Type)var18).tsym), ((Type)var18).tsym));
            this.result = var1.type = this.types.createErrorType(this.pt());
         } catch (Types.FunctionDescriptorLookupError var15) {
            JCDiagnostic var5 = var15.getDiagnostic();
            this.resultInfo.checkContext.report(var1, var5);
            this.result = var1.type = this.types.createErrorType(this.pt());
            return;
         } finally {
            ((AttrContext)var2.info).scope.leave();
            if (var3) {
               this.attribTree(var1, this.env, this.recoveryInfo);
            }

         }

      } else {
         if (this.pt().hasTag(TypeTag.NONE)) {
            this.log.error(var1.pos(), "unexpected.lambda", new Object[0]);
         }

         this.result = var1.type = this.types.createErrorType(this.pt());
      }
   }

   void preFlow(JCTree.JCLambda var1) {
      (new PostAttrAnalyzer() {
         public void scan(JCTree var1) {
            if (var1 != null && (var1.type == null || var1.type != Type.stuckType)) {
               super.scan(var1);
            }
         }
      }).scan(var1);
   }

   private Type fallbackDescriptorType(JCTree.JCExpression var1) {
      switch (var1.getTag()) {
         case LAMBDA:
            JCTree.JCLambda var2 = (JCTree.JCLambda)var1;
            List var3 = List.nil();

            JCTree.JCVariableDecl var5;
            for(Iterator var4 = var2.params.iterator(); var4.hasNext(); var3 = var5.vartype != null ? var3.append(var5.vartype.type) : var3.append(this.syms.errType)) {
               var5 = (JCTree.JCVariableDecl)var4.next();
            }

            return new Type.MethodType(var3, Type.recoveryType, List.of(this.syms.throwableType), this.syms.methodClass);
         case REFERENCE:
            return new Type.MethodType(List.nil(), Type.recoveryType, List.of(this.syms.throwableType), this.syms.methodClass);
         default:
            Assert.error("Cannot get here!");
            return null;
      }
   }

   private void checkAccessibleTypes(JCDiagnostic.DiagnosticPosition var1, Env var2, Infer.InferenceContext var3, Type... var4) {
      this.checkAccessibleTypes(var1, var2, var3, List.from((Object[])var4));
   }

   private void checkAccessibleTypes(final JCDiagnostic.DiagnosticPosition var1, final Env var2, Infer.InferenceContext var3, final List var4) {
      if (var3.free(var4)) {
         var3.addFreeTypeListener(var4, new Infer.FreeTypeListener() {
            public void typesInferred(Infer.InferenceContext var1x) {
               Attr.this.checkAccessibleTypes(var1, var2, var1x, var1x.asInstTypes(var4));
            }
         });
      } else {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Type var6 = (Type)var5.next();
            this.rs.checkAccessibleType(var2, var6);
         }
      }

   }

   private void checkLambdaCompatible(JCTree.JCLambda var1, Type var2, Check.CheckContext var3) {
      Type var4 = var3.inferenceContext().asUndetVar(var2.getReturnType());
      if (var1.getBodyKind() == LambdaExpressionTree.BodyKind.STATEMENT && var1.canCompleteNormally && !var4.hasTag(TypeTag.VOID) && var4 != Type.recoveryType) {
         var3.report(var1, this.diags.fragment("incompatible.ret.type.in.lambda", this.diags.fragment("missing.ret.val", var4)));
      }

      List var5 = var3.inferenceContext().asUndetVars(var2.getParameterTypes());
      if (!this.types.isSameTypes(var5, TreeInfo.types(var1.params))) {
         var3.report(var1, this.diags.fragment("incompatible.arg.types.in.lambda"));
      }

   }

   public Symbol.MethodSymbol removeClinit(Symbol.ClassSymbol var1) {
      return (Symbol.MethodSymbol)this.clinits.remove(var1);
   }

   public Env lambdaEnv(JCTree.JCLambda var1, Env var2) {
      Symbol var4 = ((AttrContext)var2.info).scope.owner;
      Env var3;
      if (var4.kind == 4 && var4.owner.kind == 2) {
         var3 = var2.dup(var1, ((AttrContext)var2.info).dup(((AttrContext)var2.info).scope.dupUnshared()));
         Symbol.ClassSymbol var5 = var4.enclClass();
         if ((var4.flags() & 8L) == 0L) {
            Iterator var6 = var5.members_field.getElementsByName(this.names.init).iterator();
            if (var6.hasNext()) {
               Symbol var7 = (Symbol)var6.next();
               ((AttrContext)var3.info).scope.owner = var7;
            }
         } else {
            Symbol.MethodSymbol var8 = (Symbol.MethodSymbol)this.clinits.get(var5);
            if (var8 == null) {
               Type.MethodType var9 = new Type.MethodType(List.nil(), this.syms.voidType, List.nil(), this.syms.methodClass);
               var8 = new Symbol.MethodSymbol(4106L, this.names.clinit, var9, var5);
               var8.params = List.nil();
               this.clinits.put(var5, var8);
            }

            ((AttrContext)var3.info).scope.owner = var8;
         }
      } else {
         var3 = var2.dup(var1, ((AttrContext)var2.info).dup(((AttrContext)var2.info).scope.dup()));
      }

      return var3;
   }

   public void visitReference(JCTree.JCMemberReference var1) {
      if (this.pt().isErroneous() || this.pt().hasTag(TypeTag.NONE) && this.pt() != Type.recoveryType) {
         if (this.pt().hasTag(TypeTag.NONE)) {
            this.log.error(var1.pos(), "unexpected.mref", new Object[0]);
         }

         this.result = var1.type = this.types.createErrorType(this.pt());
      } else {
         Env var2 = this.env.dup(var1);

         try {
            Type var3 = this.attribTree(var1.expr, this.env, this.memberReferenceQualifierResult(var1));
            if (var1.getMode() == MemberReferenceTree.ReferenceMode.NEW) {
               var3 = this.chk.checkConstructorRefType(var1.expr, var3);
               if (!var3.isErroneous() && var3.isRaw() && var1.typeargs != null) {
                  this.log.error(var1.expr.pos(), "invalid.mref", new Object[]{Kinds.kindName(var1.getMode()), this.diags.fragment("mref.infer.and.explicit.params")});
                  var3 = this.types.createErrorType(var3);
               }
            }

            if (var3.isErroneous()) {
               this.result = var1.type = var3;
            } else {
               if (TreeInfo.isStaticSelector(var1.expr, this.names)) {
                  this.chk.validate(var1.expr, this.env, false);
               }

               List var22 = List.nil();
               if (var1.typeargs != null) {
                  var22 = this.attribTypes(var1.typeargs, var2);
               }

               Type var6 = this.pt();
               boolean var7 = this.resultInfo.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.CHECK && this.isSerializable(var6);
               Type var5;
               Object var23;
               if (var6 != Type.recoveryType) {
                  var23 = this.types.removeWildcards((Type)this.targetChecker.visit(var6, var1));
                  var5 = this.types.findDescriptorType((Type)var23);
               } else {
                  var23 = Type.recoveryType;
                  var5 = this.fallbackDescriptorType(var1);
               }

               this.setFunctionalInfo(var2, var1, this.pt(), var5, (Type)var23, this.resultInfo.checkContext);
               List var8 = var5.getParameterTypes();
               Object var9 = this.rs.resolveMethodCheck;
               if (this.resultInfo.checkContext.inferenceContext().free(var8)) {
                  var9 = this.rs.new MethodReferenceCheck(this.resultInfo.checkContext.inferenceContext());
               }

               Pair var10 = null;
               List var11 = this.resultInfo.checkContext.inferenceContext().save();

               try {
                  var10 = this.rs.resolveMemberReference(var2, var1, var1.expr.type, var1.name, var8, var22, (Resolve.MethodCheck)var9, this.resultInfo.checkContext.inferenceContext(), this.resultInfo.checkContext.deferredAttrContext().mode);
               } finally {
                  this.resultInfo.checkContext.inferenceContext().rollback(var11);
               }

               Symbol var12 = (Symbol)var10.fst;
               Resolve.ReferenceLookupHelper var13 = (Resolve.ReferenceLookupHelper)var10.snd;
               if (var12.kind != 16) {
                  boolean var24;
                  switch (var12.kind) {
                     case 129:
                     case 130:
                     case 131:
                     case 132:
                     case 134:
                     case 135:
                     case 138:
                        var24 = true;
                        break;
                     case 133:
                     case 137:
                     default:
                        Assert.error("unexpected result kind " + var12.kind);
                        var24 = false;
                        break;
                     case 136:
                        var24 = false;
                  }

                  JCDiagnostic var25 = ((Resolve.ResolveError)var12.baseSymbol()).getDiagnostic(JCDiagnostic.DiagnosticType.FRAGMENT, var1, var3.tsym, var3, var1.name, var8, var22);
                  JCDiagnostic.DiagnosticType var26 = var24 ? JCDiagnostic.DiagnosticType.FRAGMENT : JCDiagnostic.DiagnosticType.ERROR;
                  JCDiagnostic var17 = this.diags.create(var26, this.log.currentSource(), var1, "invalid.mref", Kinds.kindName(var1.getMode()), var25);
                  if (var24 && var23 == Type.recoveryType) {
                     this.result = var1.type = (Type)var23;
                  } else {
                     if (var24) {
                        this.resultInfo.checkContext.report(var1, var17);
                     } else {
                        this.log.report(var17);
                     }

                     this.result = var1.type = this.types.createErrorType((Type)var23);
                  }
               } else {
                  var1.sym = var12.baseSymbol();
                  var1.kind = var13.referenceKind(var1.sym);
                  var1.ownerAccessible = this.rs.isAccessible(var2, (Symbol.TypeSymbol)var1.sym.enclClass());
                  if (var5.getReturnType() == Type.recoveryType) {
                     this.result = var1.type = (Type)var23;
                  } else {
                     if (this.resultInfo.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.CHECK) {
                        if (var1.getMode() == MemberReferenceTree.ReferenceMode.INVOKE && TreeInfo.isStaticSelector(var1.expr, this.names) && var1.kind.isUnbound() && !((Type)var5.getParameterTypes().head).isParameterized()) {
                           this.chk.checkRaw(var1.expr, var2);
                        }

                        if (var1.sym.isStatic() && TreeInfo.isStaticSelector(var1.expr, this.names) && var3.getTypeArguments().nonEmpty()) {
                           this.log.error(var1.expr.pos(), "invalid.mref", new Object[]{Kinds.kindName(var1.getMode()), this.diags.fragment("static.mref.with.targs")});
                           this.result = var1.type = this.types.createErrorType((Type)var23);
                           return;
                        }

                        if (var1.sym.isStatic() && !TreeInfo.isStaticSelector(var1.expr, this.names) && !var1.kind.isUnbound()) {
                           this.log.error(var1.expr.pos(), "invalid.mref", new Object[]{Kinds.kindName(var1.getMode()), this.diags.fragment("static.bound.mref")});
                           this.result = var1.type = this.types.createErrorType((Type)var23);
                           return;
                        }

                        if (!var12.isStatic() && var1.kind == JCTree.JCMemberReference.ReferenceKind.SUPER) {
                           this.rs.checkNonAbstract(var1.pos(), var1.sym);
                        }

                        if (var7) {
                           this.chk.checkElemAccessFromSerializableLambda(var1);
                        }
                     }

                     ResultInfo var14 = this.resultInfo.dup(this.newMethodTemplate((Type)(var5.getReturnType().hasTag(TypeTag.VOID) ? Type.noType : var5.getReturnType()), var1.kind.isUnbound() ? var8.tail : var8, var22), new FunctionalReturnContext(this.resultInfo.checkContext));
                     Type var15 = this.checkId(this.noCheckTree, var13.site, var12, var2, var14);
                     if (var1.kind.isUnbound() && this.resultInfo.checkContext.inferenceContext().free((Type)var8.head) && !this.types.isSubtype(this.resultInfo.checkContext.inferenceContext().asUndetVar((Type)var8.head), var3)) {
                        Assert.error("Can't get here");
                     }

                     if (!var15.isErroneous()) {
                        var15 = this.types.createMethodTypeWithReturn(var15, this.adjustMethodReturnType(var13.site, var1.name, var14.pt.getParameterTypes(), var15.getReturnType()));
                     }

                     boolean var16 = this.resultInfo.checkContext.deferredAttrContext().mode == DeferredAttr.AttrMode.SPECULATIVE;
                     var1.type = (Type)var23;
                     this.checkReferenceCompatible(var1, var5, var15, this.resultInfo.checkContext, var16);
                     if (!var16) {
                        this.checkAccessibleTypes(var1, var2, this.resultInfo.checkContext.inferenceContext(), (Type[])(var5, (Type)var23));
                     }

                     this.result = this.check(var1, (Type)var23, 12, this.resultInfo);
                  }
               }
            }
         } catch (Types.FunctionDescriptorLookupError var21) {
            JCDiagnostic var4 = var21.getDiagnostic();
            this.resultInfo.checkContext.report(var1, var4);
            this.result = var1.type = this.types.createErrorType(this.pt());
         }
      }
   }

   ResultInfo memberReferenceQualifierResult(JCTree.JCMemberReference var1) {
      return new ResultInfo(var1.getMode() == MemberReferenceTree.ReferenceMode.INVOKE ? 14 : 2, Type.noType);
   }

   void checkReferenceCompatible(JCTree.JCMemberReference var1, Type var2, Type var3, Check.CheckContext var4, boolean var5) {
      Type var6 = var4.inferenceContext().asUndetVar(var2.getReturnType());
      Type var7;
      switch (var1.getMode()) {
         case NEW:
            if (!var1.expr.type.isRaw()) {
               var7 = var1.expr.type;
               break;
            }
         default:
            var7 = var3.getReturnType();
      }

      Type var8 = var7;
      if (var6.hasTag(TypeTag.VOID)) {
         var8 = null;
      }

      if (!var6.hasTag(TypeTag.VOID) && !var7.hasTag(TypeTag.VOID) && (var7.isErroneous() || (new FunctionalReturnContext(var4)).compatible(var7, var6, this.types.noWarnings))) {
         var8 = null;
      }

      if (var8 != null) {
         var4.report(var1, this.diags.fragment("incompatible.ret.type.in.mref", this.diags.fragment("inconvertible.types", var7, var2.getReturnType())));
      }

      if (!var5) {
         List var9 = var4.inferenceContext().asUndetVars(var2.getThrownTypes());
         if (this.chk.unhandled(var3.getThrownTypes(), var9).nonEmpty()) {
            this.log.error(var1, "incompatible.thrown.types.in.mref", new Object[]{var3.getThrownTypes()});
         }
      }

   }

   private void setFunctionalInfo(final Env var1, final JCTree.JCFunctionalExpression var2, final Type var3, final Type var4, final Type var5, final Check.CheckContext var6) {
      if (var6.inferenceContext().free(var4)) {
         var6.inferenceContext().addFreeTypeListener(List.of(var3, var4), new Infer.FreeTypeListener() {
            public void typesInferred(Infer.InferenceContext var1x) {
               Attr.this.setFunctionalInfo(var1, var2, var3, var1x.asInstType(var4), var1x.asInstType(var5), var6);
            }
         });
      } else {
         ListBuffer var7 = new ListBuffer();
         if (var3.hasTag(TypeTag.CLASS)) {
            if (var3.isCompound()) {
               var7.append(this.types.removeWildcards(var5));
               Iterator var8 = ((Type.IntersectionClassType)this.pt()).interfaces_field.iterator();

               while(var8.hasNext()) {
                  Type var9 = (Type)var8.next();
                  if (var9 != var5) {
                     var7.append(this.types.removeWildcards(var9));
                  }
               }
            } else {
               var7.append(this.types.removeWildcards(var5));
            }
         }

         var2.targets = var7.toList();
         if (var6.deferredAttrContext().mode == DeferredAttr.AttrMode.CHECK && var3 != Type.recoveryType) {
            try {
               Symbol.ClassSymbol var11 = this.types.makeFunctionalInterfaceClass(var1, this.names.empty, List.of(var2.targets.head), 1024L);
               if (var11 != null) {
                  this.chk.checkImplementations(var1.tree, var11, var11);
               }
            } catch (Types.FunctionDescriptorLookupError var10) {
               JCDiagnostic var12 = var10.getDiagnostic();
               this.resultInfo.checkContext.report(var1.tree, var12);
            }
         }
      }

   }

   public void visitParens(JCTree.JCParens var1) {
      Type var2 = this.attribTree(var1.expr, this.env, this.resultInfo);
      this.result = this.check(var1, var2, this.pkind(), this.resultInfo);
      Symbol var3 = TreeInfo.symbol(var1);
      if (var3 != null && (var3.kind & 3) != 0) {
         this.log.error(var1.pos(), "illegal.start.of.type", new Object[0]);
      }

   }

   public void visitAssign(JCTree.JCAssign var1) {
      Type var2 = this.attribTree(var1.lhs, this.env.dup(var1), this.varInfo);
      Type var3 = this.capture(var2);
      this.attribExpr(var1.rhs, this.env, var2);
      this.result = this.check(var1, var3, 12, this.resultInfo);
   }

   public void visitAssignop(JCTree.JCAssignOp var1) {
      Type var2 = this.attribTree(var1.lhs, this.env, this.varInfo);
      Type var3 = this.attribExpr(var1.rhs, this.env);
      Symbol var4 = var1.operator = this.rs.resolveBinaryOperator(var1.pos(), var1.getTag().noAssignOp(), this.env, var2, var3);
      if (var4.kind == 16 && !var2.isErroneous() && !var3.isErroneous()) {
         this.chk.checkOperator(var1.pos(), (Symbol.OperatorSymbol)var4, var1.getTag().noAssignOp(), var2, var3);
         this.chk.checkDivZero(var1.rhs.pos(), var4, var3);
         this.chk.checkCastable(var1.rhs.pos(), var4.type.getReturnType(), var2);
      }

      this.result = this.check(var1, var2, 12, this.resultInfo);
   }

   public void visitUnary(JCTree.JCUnary var1) {
      Type var2 = var1.getTag().isIncOrDecUnaryOp() ? this.attribTree(var1.arg, this.env, this.varInfo) : this.chk.checkNonVoid(var1.arg.pos(), this.attribExpr(var1.arg, this.env));
      Symbol var3 = var1.operator = this.rs.resolveUnaryOperator(var1.pos(), var1.getTag(), this.env, var2);
      Type var4 = this.types.createErrorType(var1.type);
      if (var3.kind == 16 && !var2.isErroneous()) {
         var4 = var1.getTag().isIncOrDecUnaryOp() ? var1.arg.type : var3.type.getReturnType();
         int var5 = ((Symbol.OperatorSymbol)var3).opcode;
         if (var2.constValue() != null) {
            Type var6 = this.cfolder.fold1(var5, var2);
            if (var6 != null) {
               var4 = this.cfolder.coerce(var6, var4);
            }
         }
      }

      this.result = this.check(var1, var4, 12, this.resultInfo);
   }

   public void visitBinary(JCTree.JCBinary var1) {
      Type var2 = this.chk.checkNonVoid(var1.lhs.pos(), this.attribExpr(var1.lhs, this.env));
      Type var3 = this.chk.checkNonVoid(var1.lhs.pos(), this.attribExpr(var1.rhs, this.env));
      Symbol var4 = var1.operator = this.rs.resolveBinaryOperator(var1.pos(), var1.getTag(), this.env, var2, var3);
      Type var5 = this.types.createErrorType(var1.type);
      if (var4.kind == 16 && !var2.isErroneous() && !var3.isErroneous()) {
         var5 = var4.type.getReturnType();
         int var6 = this.chk.checkOperator(var1.lhs.pos(), (Symbol.OperatorSymbol)var4, var1.getTag(), var2, var3);
         if (var2.constValue() != null && var3.constValue() != null) {
            Type var7 = this.cfolder.fold2(var6, var2, var3);
            if (var7 != null) {
               var5 = this.cfolder.coerce(var7, var5);
            }
         }

         if ((var6 == 165 || var6 == 166) && !this.types.isEqualityComparable(var2, var3, new Warner(var1.pos()))) {
            this.log.error(var1.pos(), "incomparable.types", new Object[]{var2, var3});
         }

         this.chk.checkDivZero(var1.rhs.pos(), var4, var3);
      }

      this.result = this.check(var1, var5, 12, this.resultInfo);
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      Type var2 = this.attribType(var1.clazz, this.env);
      this.chk.validate(var1.clazz, this.env, false);
      Env var3 = this.env.dup(var1);
      JCTree.JCExpression var5 = TreeInfo.skipParens(var1.expr);
      boolean var6 = this.allowPoly && (var5.hasTag(JCTree.Tag.LAMBDA) || var5.hasTag(JCTree.Tag.REFERENCE));
      ResultInfo var4;
      if (var6) {
         var4 = new ResultInfo(12, var2, new Check.NestedCheckContext(this.resultInfo.checkContext) {
            public boolean compatible(Type var1, Type var2, Warner var3) {
               return Attr.this.types.isCastable(var1, var2, var3);
            }
         });
      } else {
         var4 = this.unknownExprInfo;
      }

      Type var7 = this.attribTree(var1.expr, var3, var4);
      Type var8 = var6 ? var2 : this.chk.checkCastable(var1.expr.pos(), var7, var2);
      if (var7.constValue() != null) {
         var8 = this.cfolder.coerce(var7, var8);
      }

      this.result = this.check(var1, this.capture(var8), 12, this.resultInfo);
      if (!var6) {
         this.chk.checkRedundantCast(var3, var1);
      }

   }

   public void visitTypeTest(JCTree.JCInstanceOf var1) {
      Type var2 = this.chk.checkNullOrRefType(var1.expr.pos(), this.attribExpr(var1.expr, this.env));
      Type var3 = this.attribType(var1.clazz, this.env);
      if (!var3.hasTag(TypeTag.TYPEVAR)) {
         var3 = this.chk.checkClassOrArrayType(var1.clazz.pos(), var3);
      }

      if (!var3.isErroneous() && !this.types.isReifiable(var3)) {
         this.log.error(var1.clazz.pos(), "illegal.generic.type.for.instof", new Object[0]);
         var3 = this.types.createErrorType(var3);
      }

      this.chk.validate(var1.clazz, this.env, false);
      this.chk.checkCastable(var1.expr.pos(), var2, var3);
      this.result = this.check(var1, this.syms.booleanType, 12, this.resultInfo);
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      Type var2 = this.types.createErrorType(var1.type);
      Type var3 = this.attribExpr(var1.indexed, this.env);
      this.attribExpr(var1.index, this.env, this.syms.intType);
      if (this.types.isArray(var3)) {
         var2 = this.types.elemtype(var3);
      } else if (!var3.hasTag(TypeTag.ERROR)) {
         this.log.error(var1.pos(), "array.req.but.found", new Object[]{var3});
      }

      if ((this.pkind() & 4) == 0) {
         var2 = this.capture(var2);
      }

      this.result = this.check(var1, var2, 4, this.resultInfo);
   }

   public void visitIdent(JCTree.JCIdent var1) {
      Symbol var2;
      if (!this.pt().hasTag(TypeTag.METHOD) && !this.pt().hasTag(TypeTag.FORALL)) {
         if (var1.sym != null && var1.sym.kind != 4) {
            var2 = var1.sym;
         } else {
            var2 = this.rs.resolveIdent(var1.pos(), this.env, var1.name, this.pkind());
         }
      } else {
         ((AttrContext)this.env.info).pendingResolutionPhase = null;
         var2 = this.rs.resolveMethod(var1.pos(), this.env, var1.name, this.pt().getParameterTypes(), this.pt().getTypeArguments());
      }

      var1.sym = var2;
      Env var3 = this.env;
      boolean var4 = false;
      if (this.env.enclClass.sym.owner.kind != 1 && (var2.kind & 22) != 0 && var2.owner.kind == 2 && var1.name != this.names._this && var1.name != this.names._super) {
         for(; var3.outer != null && !var2.isMemberOf(var3.enclClass.sym, this.types); var3 = var3.outer) {
            if ((var3.enclClass.sym.flags() & 4194304L) != 0L) {
               var4 = !this.allowAnonOuterThis;
            }
         }
      }

      if (var2.kind == 4) {
         Symbol.VarSymbol var5 = (Symbol.VarSymbol)var2;
         this.checkInit(var1, this.env, var5, false);
         if (this.pkind() == 4) {
            this.checkAssignable(var1.pos(), var5, (JCTree)null, this.env);
         }
      }

      if ((((AttrContext)var3.info).isSelfCall || var4) && (var2.kind & 20) != 0 && var2.owner.kind == 2 && (var2.flags() & 8L) == 0L) {
         this.chk.earlyRefError(var1.pos(), var2.kind == 4 ? var2 : this.thisSym(var1.pos(), this.env));
      }

      Env var6 = this.env;
      if (var2.kind != 63 && var2.kind != 2 && var2.owner != null && var2.owner != var6.enclClass.sym) {
         while(var6.outer != null && !this.rs.isAccessible(this.env, var6.enclClass.sym.type, var2)) {
            var6 = var6.outer;
         }
      }

      if (((AttrContext)this.env.info).isSerializable) {
         this.chk.checkElemAccessFromSerializableLambda(var1);
      }

      this.result = this.checkId(var1, var6.enclClass.sym.type, var2, this.env, this.resultInfo);
   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      int var2 = 0;
      if (var1.name != this.names._this && var1.name != this.names._super && var1.name != this.names._class) {
         if ((this.pkind() & 1) != 0) {
            var2 |= 1;
         }

         if ((this.pkind() & 2) != 0) {
            var2 = var2 | 2 | 1;
         }

         if ((this.pkind() & 28) != 0) {
            var2 = var2 | 12 | 2;
         }
      } else {
         var2 = 2;
      }

      Type var3 = this.attribTree(var1.selected, this.env, new ResultInfo(var2, Infer.anyPoly));
      if ((this.pkind() & 3) == 0) {
         var3 = this.capture(var3);
      }

      if (var2 == 2) {
         Type var4;
         for(var4 = var3; var4.hasTag(TypeTag.ARRAY); var4 = ((Type.ArrayType)var4.unannotatedType()).elemtype) {
         }

         if (var4.hasTag(TypeTag.TYPEVAR)) {
            this.log.error(var1.pos(), "type.var.cant.be.deref", new Object[0]);
            this.result = var1.type = this.types.createErrorType(var1.name, var3.tsym, var3);
            var1.sym = var1.type.tsym;
            return;
         }
      }

      Symbol var9 = TreeInfo.symbol(var1.selected);
      boolean var5 = ((AttrContext)this.env.info).selectSuper;
      ((AttrContext)this.env.info).selectSuper = var9 != null && var9.name == this.names._super;
      ((AttrContext)this.env.info).pendingResolutionPhase = null;
      Object var6 = this.selectSym(var1, var9, var3, this.env, this.resultInfo);
      if (((Symbol)var6).kind == 4 && ((Symbol)var6).name != this.names._super && ((AttrContext)this.env.info).defaultSuperCallSite != null) {
         this.log.error(var1.selected.pos(), "not.encl.class", new Object[]{var3.tsym});
         var6 = this.syms.errSymbol;
      }

      if (((Symbol)var6).exists() && !isType((Symbol)var6) && (this.pkind() & 3) != 0) {
         var3 = this.capture(var3);
         var6 = this.selectSym(var1, var9, var3, this.env, this.resultInfo);
      }

      boolean var7 = ((AttrContext)this.env.info).lastResolveVarargs();
      var1.sym = (Symbol)var6;
      if (var3.hasTag(TypeTag.TYPEVAR) && !isType((Symbol)var6) && ((Symbol)var6).kind != 63) {
         while(var3.hasTag(TypeTag.TYPEVAR)) {
            var3 = var3.getUpperBound();
         }

         var3 = this.capture(var3);
      }

      if (((Symbol)var6).kind == 4) {
         Symbol.VarSymbol var8 = (Symbol.VarSymbol)var6;
         this.checkInit(var1, this.env, var8, true);
         if (this.pkind() == 4) {
            this.checkAssignable(var1.pos(), var8, var1.selected, this.env);
         }
      }

      if (var9 != null && var9.kind == 4 && ((Symbol.VarSymbol)var9).isResourceVariable() && ((Symbol)var6).kind == 16 && ((Symbol)var6).name.equals(this.names.close) && ((Symbol)var6).overrides(this.syms.autoCloseableClose, var9.type.tsym, this.types, true) && ((AttrContext)this.env.info).lint.isEnabled(Lint.LintCategory.TRY)) {
         this.log.warning(Lint.LintCategory.TRY, var1, "try.explicit.close.call", new Object[0]);
      }

      if (isType((Symbol)var6) && (var9 == null || (var9.kind & 3) == 0)) {
         var1.type = this.check(var1.selected, this.pt(), var9 == null ? 12 : var9.kind, new ResultInfo(3, this.pt()));
      }

      if (isType(var9)) {
         if (((Symbol)var6).name == this.names._this) {
            if (((AttrContext)this.env.info).isSelfCall && var3.tsym == this.env.enclClass.sym) {
               this.chk.earlyRefError(var1.pos(), (Symbol)var6);
            }
         } else if ((((Symbol)var6).flags() & 8L) == 0L && !this.env.next.tree.hasTag(JCTree.Tag.REFERENCE) && ((Symbol)var6).name != this.names._super && (((Symbol)var6).kind == 4 || ((Symbol)var6).kind == 16)) {
            this.rs.accessBase(this.rs.new StaticError((Symbol)var6), var1.pos(), var3, ((Symbol)var6).name, true);
         }

         if (!this.allowStaticInterfaceMethods && var9.isInterface() && ((Symbol)var6).isStatic() && ((Symbol)var6).kind == 16) {
            this.log.error(var1.pos(), "static.intf.method.invoke.not.supported.in.source", new Object[]{this.sourceName});
         }
      } else if (((Symbol)var6).kind != 63 && (((Symbol)var6).flags() & 8L) != 0L && ((Symbol)var6).name != this.names._class) {
         this.chk.warnStatic(var1, "static.not.qualified.by.type", Kinds.kindName(((Symbol)var6).kind), ((Symbol)var6).owner);
      }

      if (((AttrContext)this.env.info).selectSuper && (((Symbol)var6).flags() & 8L) == 0L) {
         this.rs.checkNonAbstract(var1.pos(), (Symbol)var6);
         if (var3.isRaw()) {
            Type var10 = this.types.asSuper(this.env.enclClass.sym.type, var3.tsym);
            if (var10 != null) {
               var3 = var10;
            }
         }
      }

      if (((AttrContext)this.env.info).isSerializable) {
         this.chk.checkElemAccessFromSerializableLambda(var1);
      }

      ((AttrContext)this.env.info).selectSuper = var5;
      this.result = this.checkId(var1, var3, (Symbol)var6, this.env, this.resultInfo);
   }

   private Symbol selectSym(JCTree.JCFieldAccess var1, Symbol var2, Type var3, Env var4, ResultInfo var5) {
      JCDiagnostic.DiagnosticPosition var6 = var1.pos();
      Name var7 = var1.name;
      Symbol var8;
      switch (var3.getTag()) {
         case PACKAGE:
            return this.rs.accessBase(this.rs.findIdentInPackage(var4, var3.tsym, var7, var5.pkind), var6, var2, var3, var7, true);
         case ARRAY:
         case CLASS:
            if (!var5.pt.hasTag(TypeTag.METHOD) && !var5.pt.hasTag(TypeTag.FORALL)) {
               if (var7 != this.names._this && var7 != this.names._super) {
                  if (var7 == this.names._class) {
                     Type var11 = this.syms.classType;
                     List var12 = this.allowGenerics ? List.of(this.types.erasure(var3)) : List.nil();
                     Type.ClassType var14 = new Type.ClassType(var11.getEnclosingType(), var12, var11.tsym);
                     return new Symbol.VarSymbol(25L, this.names._class, var14, var3.tsym);
                  }

                  var8 = this.rs.findIdentInType(var4, var3, var7, var5.pkind);
                  if ((var5.pkind & 128) == 0) {
                     var8 = this.rs.accessBase(var8, var6, var2, var3, var7, true);
                  }

                  return var8;
               }

               return this.rs.resolveSelf(var6, var4, var3.tsym, var7);
            }

            return this.rs.resolveQualifiedMethod(var6, var4, var2, var3, var7, var5.pt.getParameterTypes(), var5.pt.getTypeArguments());
         case WILDCARD:
            throw new AssertionError(var1);
         case TYPEVAR:
            var8 = var3.getUpperBound() != null ? this.selectSym(var1, var2, this.capture(var3.getUpperBound()), var4, var5) : null;
            if (var8 == null) {
               this.log.error(var6, "type.var.cant.be.deref", new Object[0]);
               return this.syms.errSymbol;
            }

            Object var9 = (var8.flags() & 2L) != 0L ? this.rs.new AccessError(var4, var3, var8) : var8;
            this.rs.accessBase((Symbol)var9, var6, var2, var3, var7, true);
            return var8;
         case ERROR:
            return this.types.createErrorType(var7, var3.tsym, var3).tsym;
         default:
            if (var7 == this.names._class) {
               Type var13 = this.syms.classType;
               Type var10 = this.types.boxedClass(var3).type;
               Type.ClassType var15 = new Type.ClassType(var13.getEnclosingType(), List.of(var10), var13.tsym);
               return new Symbol.VarSymbol(25L, this.names._class, var15, var3.tsym);
            } else {
               this.log.error(var6, "cant.deref", new Object[]{var3});
               return this.syms.errSymbol;
            }
      }
   }

   Type checkId(JCTree var1, Type var2, Symbol var3, Env var4, ResultInfo var5) {
      return !var5.pt.hasTag(TypeTag.FORALL) && !var5.pt.hasTag(TypeTag.METHOD) ? this.checkIdInternal(var1, var2, var3, var5.pt, var4, var5) : this.checkMethodId(var1, var2, var3, var4, var5);
   }

   Type checkMethodId(JCTree var1, Type var2, Symbol var3, Env var4, ResultInfo var5) {
      boolean var6 = (var3.baseSymbol().flags() & 70368744177664L) != 0L;
      return var6 ? this.checkSigPolyMethodId(var1, var2, var3, var4, var5) : this.checkMethodIdInternal(var1, var2, var3, var4, var5);
   }

   Type checkSigPolyMethodId(JCTree var1, Type var2, Symbol var3, Env var4, ResultInfo var5) {
      this.checkMethodIdInternal(var1, var2, var3.baseSymbol(), var4, var5);
      ((AttrContext)var4.info).pendingResolutionPhase = Resolve.MethodResolutionPhase.BASIC;
      return var3.type;
   }

   Type checkMethodIdInternal(JCTree var1, Type var2, Symbol var3, Env var4, ResultInfo var5) {
      if ((var5.pkind & 32) != 0) {
         Type var6 = var5.pt.map(this.deferredAttr.new RecoveryDeferredTypeMap(DeferredAttr.AttrMode.SPECULATIVE, var3, ((AttrContext)var4.info).pendingResolutionPhase));
         Type var7 = this.checkIdInternal(var1, var2, var3, var6, var4, var5);
         var5.pt.map(this.deferredAttr.new RecoveryDeferredTypeMap(DeferredAttr.AttrMode.CHECK, var3, ((AttrContext)var4.info).pendingResolutionPhase));
         return var7;
      } else {
         return this.checkIdInternal(var1, var2, var3, var5.pt, var4, var5);
      }
   }

   Type checkIdInternal(JCTree var1, Type var2, Symbol var3, Type var4, Env var5, ResultInfo var6) {
      if (var4.isErroneous()) {
         return this.types.createErrorType(var2);
      } else {
         Object var7;
         Type var9;
         switch (var3.kind) {
            case 1:
            case 63:
               var7 = var3.type;
               break;
            case 2:
               var7 = var3.type;
               if (((Type)var7).hasTag(TypeTag.CLASS)) {
                  this.chk.checkForBadAuxiliaryClassAccess(var1.pos(), var5, (Symbol.ClassSymbol)var3);
                  Type var10 = ((Type)var7).getEnclosingType();
                  if (((Type)var7).tsym.type.getTypeArguments().nonEmpty()) {
                     var7 = this.types.erasure((Type)var7);
                  } else if (var10.hasTag(TypeTag.CLASS) && var2 != var10) {
                     var9 = var2;
                     if (var2.hasTag(TypeTag.CLASS)) {
                        var9 = this.types.asEnclosingSuper(var2, var10.tsym);
                     }

                     if (var9 == null) {
                        var9 = this.types.erasure(var10);
                     }

                     if (var9 != var10) {
                        var7 = new Type.ClassType(var9, List.nil(), ((Type)var7).tsym);
                     }
                  }
               }
               break;
            case 4:
               Symbol.VarSymbol var8 = (Symbol.VarSymbol)var3;
               if (this.allowGenerics && var6.pkind == 4 && var8.owner.kind == 2 && (var8.flags() & 8L) == 0L && (var2.hasTag(TypeTag.CLASS) || var2.hasTag(TypeTag.TYPEVAR))) {
                  var9 = this.types.asOuterSuper(var2, var8.owner);
                  if (var9 != null && var9.isRaw() && !this.types.isSameType(var8.type, var8.erasure(this.types))) {
                     this.chk.warnUnchecked(var1.pos(), "unchecked.assign.to.var", var8, var9);
                  }
               }

               var7 = var3.owner.kind == 2 && var3.name != this.names._this && var3.name != this.names._super ? this.types.memberType(var2, var3) : var3.type;
               if (var8.getConstValue() != null && this.isStaticReference(var1)) {
                  var7 = ((Type)var7).constType(var8.getConstValue());
               }

               if (var6.pkind == 12) {
                  var7 = this.capture((Type)var7);
               }
               break;
            case 16:
               var7 = this.checkMethod(var2, var3, new ResultInfo(var6.pkind, var6.pt.getReturnType(), var6.checkContext), var5, TreeInfo.args(var5.tree), var6.pt.getParameterTypes(), var6.pt.getTypeArguments());
               break;
            default:
               throw new AssertionError("unexpected kind: " + var3.kind + " in tree " + var1);
         }

         if (var3.name != this.names.init) {
            this.chk.checkDeprecated(var1.pos(), ((AttrContext)var5.info).scope.owner, var3);
            this.chk.checkSunAPI(var1.pos(), var3);
            this.chk.checkProfile(var1.pos(), var3);
         }

         return this.check(var1, (Type)var7, var3.kind, var6);
      }
   }

   private void checkInit(JCTree var1, Env var2, Symbol.VarSymbol var3, boolean var4) {
      if ((((AttrContext)var2.info).enclVar == var3 || var3.pos > var1.pos) && var3.owner.kind == 2 && this.enclosingInitEnv(var2) != null && var3.owner == ((AttrContext)var2.info).scope.owner.enclClass() && (var3.flags() & 8L) != 0L == Resolve.isStatic(var2) && (!var2.tree.hasTag(JCTree.Tag.ASSIGN) || TreeInfo.skipParens(((JCTree.JCAssign)var2.tree).lhs) != var1)) {
         String var5 = ((AttrContext)var2.info).enclVar == var3 ? "self.ref" : "forward.ref";
         if (var4 && !this.isStaticEnumField(var3)) {
            if (this.useBeforeDeclarationWarning) {
               this.log.warning(var1.pos(), var5, new Object[]{var3});
            }
         } else {
            this.log.error(var1.pos(), "illegal." + var5, new Object[0]);
         }
      }

      var3.getConstValue();
      this.checkEnumInitializer(var1, var2, var3);
   }

   Env enclosingInitEnv(Env var1) {
      while(true) {
         switch (var1.tree.getTag()) {
            case METHODDEF:
            case CLASSDEF:
            case TOPLEVEL:
               return null;
            case VARDEF:
               JCTree.JCVariableDecl var2 = (JCTree.JCVariableDecl)var1.tree;
               if (var2.sym.owner.kind == 2) {
                  return var1;
               }
               break;
            case BLOCK:
               if (var1.next.tree.hasTag(JCTree.Tag.CLASSDEF)) {
                  return var1;
               }
         }

         Assert.checkNonNull(var1.next);
         var1 = var1.next;
      }
   }

   private void checkEnumInitializer(JCTree var1, Env var2, Symbol.VarSymbol var3) {
      if (this.isStaticEnumField(var3)) {
         Symbol.ClassSymbol var4 = ((AttrContext)var2.info).scope.owner.enclClass();
         if (var4 == null || var4.owner == null) {
            return;
         }

         if (var3.owner != var4 && !this.types.isSubtype(var4.type, var3.owner.type)) {
            return;
         }

         if (!Resolve.isInitializer(var2)) {
            return;
         }

         this.log.error(var1.pos(), "illegal.enum.static.ref", new Object[0]);
      }

   }

   private boolean isStaticEnumField(Symbol.VarSymbol var1) {
      return Flags.isEnum(var1.owner) && Flags.isStatic(var1) && !Flags.isConstant(var1) && var1.name != this.names._class;
   }

   public Type checkMethod(Type var1, final Symbol var2, ResultInfo var3, Env var4, List var5, List var6, List var7) {
      if (this.allowGenerics && (var2.flags() & 8L) == 0L && (var1.hasTag(TypeTag.CLASS) || var1.hasTag(TypeTag.TYPEVAR))) {
         Type var8 = this.types.asOuterSuper(var1, var2.owner);
         if (var8 != null && var8.isRaw() && !this.types.isSameTypes(var2.type.getParameterTypes(), var2.erasure(this.types).getParameterTypes())) {
            this.chk.warnUnchecked(var4.tree.pos(), "unchecked.call.mbr.of.raw.type", var2, var8);
         }
      }

      if (((AttrContext)var4.info).defaultSuperCallSite != null) {
         Iterator var15 = this.types.interfaces(var4.enclClass.type).prepend(this.types.supertype(var4.enclClass.type)).iterator();

         while(var15.hasNext()) {
            Type var9 = (Type)var15.next();
            if (var9.tsym.isSubClass(var2.enclClass(), this.types) && !this.types.isSameType(var9, ((AttrContext)var4.info).defaultSuperCallSite)) {
               List var10 = this.types.interfaceCandidates(var9, (Symbol.MethodSymbol)var2);
               if (var10.nonEmpty() && var10.head != var2 && ((Symbol.MethodSymbol)var10.head).overrides(var2, ((Symbol.MethodSymbol)var10.head).enclClass(), this.types, true)) {
                  this.log.error(var4.tree.pos(), "illegal.default.super.call", new Object[]{((AttrContext)var4.info).defaultSuperCallSite, this.diags.fragment("overridden.default", var2, var9)});
                  break;
               }
            }
         }

         ((AttrContext)var4.info).defaultSuperCallSite = null;
      }

      if (var2.isStatic() && var1.isInterface() && var4.tree.hasTag(JCTree.Tag.APPLY)) {
         JCTree.JCMethodInvocation var16 = (JCTree.JCMethodInvocation)var4.tree;
         if (var16.meth.hasTag(JCTree.Tag.SELECT) && !TreeInfo.isStaticSelector(((JCTree.JCFieldAccess)var16.meth).selected, this.names)) {
            this.log.error(var4.tree.pos(), "illegal.static.intf.meth.call", new Object[]{var1});
         }
      }

      this.noteWarner.clear();

      try {
         Object var17 = this.rs.checkMethod(var4, var1, var2, var3, var6, var7, this.noteWarner);
         DeferredAttr.DeferredTypeMap var19 = this.deferredAttr.new DeferredTypeMap(DeferredAttr.AttrMode.CHECK, var2, ((AttrContext)var4.info).pendingResolutionPhase);
         var6 = Type.map(var6, var19);
         if (this.noteWarner.hasNonSilentLint(Lint.LintCategory.UNCHECKED)) {
            this.chk.warnUnchecked(var4.tree.pos(), "unchecked.meth.invocation.applied", Kinds.kindName(var2), var2.name, this.rs.methodArguments(var2.type.getParameterTypes()), this.rs.methodArguments(Type.map(var6, var19)), Kinds.kindName(var2.location()), var2.location());
            var17 = new Type.MethodType(((Type)var17).getParameterTypes(), this.types.erasure(((Type)var17).getReturnType()), this.types.erasure(((Type)var17).getThrownTypes()), this.syms.methodClass);
         }

         return this.chk.checkMethod((Type)var17, var2, var4, var5, var6, ((AttrContext)var4.info).lastResolveVarargs(), var3.checkContext.inferenceContext());
      } catch (Infer.InferenceException var13) {
         var3.checkContext.report(var4.tree.pos(), var13.getDiagnostic());
         return this.types.createErrorType(var1);
      } catch (Resolve.InapplicableMethodException var14) {
         final JCDiagnostic var18 = var14.getDiagnostic();
         Resolve var10003 = this.rs;
         var10003.getClass();
         Resolve.InapplicableSymbolError var20 = new Resolve.InapplicableSymbolError(var10003, (Resolve.MethodResolutionContext)null) {
            {
               var2x.getClass();
            }

            protected Pair errCandidate() {
               return new Pair(var2, var18);
            }
         };
         List var11 = Type.map(var6, this.rs.new ResolveDeferredRecoveryMap(DeferredAttr.AttrMode.CHECK, var2, ((AttrContext)var4.info).pendingResolutionPhase));
         JCDiagnostic var12 = var20.getDiagnostic(JCDiagnostic.DiagnosticType.ERROR, var4.tree, var2, var1, var2.name, var11, var7);
         this.log.report(var12);
         return this.types.createErrorType(var1);
      }
   }

   public void visitLiteral(JCTree.JCLiteral var1) {
      this.result = this.check(var1, this.litType(var1.typetag).constType(var1.value), 12, this.resultInfo);
   }

   Type litType(TypeTag var1) {
      return var1 == TypeTag.CLASS ? this.syms.stringType : this.syms.typeOfTag[var1.ordinal()];
   }

   public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
      this.result = this.check(var1, this.syms.typeOfTag[var1.typetag.ordinal()], 2, this.resultInfo);
   }

   public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
      Type var2 = this.attribType(var1.elemtype, (Env)this.env);
      Type.ArrayType var3 = new Type.ArrayType(var2, this.syms.arrayClass);
      this.result = this.check(var1, var3, 2, this.resultInfo);
   }

   public void visitTypeApply(JCTree.JCTypeApply var1) {
      Object var2 = this.types.createErrorType(var1.type);
      Type var3 = this.chk.checkClassType(var1.clazz.pos(), this.attribType(var1.clazz, (Env)this.env));
      List var4 = this.attribTypes(var1.arguments, this.env);
      if (var3.hasTag(TypeTag.CLASS)) {
         List var5 = var3.tsym.type.getTypeArguments();
         if (var4.isEmpty()) {
            var4 = var5;
         }

         if (var4.length() == var5.length()) {
            List var6 = var4;

            for(List var7 = var5; var6.nonEmpty(); var7 = var7.tail) {
               var6.head = ((Type)var6.head).withTypeVar((Type)var7.head);
               var6 = var6.tail;
            }

            Type var8 = var3.getEnclosingType();
            if (var8.hasTag(TypeTag.CLASS)) {
               JCTree.JCExpression var10 = TreeInfo.typeIn(var1.clazz);
               Type var9;
               if (var10.hasTag(JCTree.Tag.IDENT)) {
                  var9 = this.env.enclClass.sym.type;
               } else {
                  if (!var10.hasTag(JCTree.Tag.SELECT)) {
                     throw new AssertionError("" + var1);
                  }

                  var9 = ((JCTree.JCFieldAccess)var10).selected.type;
               }

               if (var8.hasTag(TypeTag.CLASS) && var9 != var8) {
                  if (var9.hasTag(TypeTag.CLASS)) {
                     var9 = this.types.asOuterSuper(var9, var8.tsym);
                  }

                  if (var9 == null) {
                     var9 = this.types.erasure(var8);
                  }

                  var8 = var9;
               }
            }

            var2 = new Type.ClassType(var8, var4, var3.tsym);
         } else {
            if (var5.length() != 0) {
               this.log.error(var1.pos(), "wrong.number.type.args", new Object[]{Integer.toString(var5.length())});
            } else {
               this.log.error(var1.pos(), "type.doesnt.take.params", new Object[]{var3.tsym});
            }

            var2 = this.types.createErrorType(var1.type);
         }
      }

      this.result = this.check(var1, (Type)var2, 2, this.resultInfo);
   }

   public void visitTypeUnion(JCTree.JCTypeUnion var1) {
      ListBuffer var2 = new ListBuffer();
      ListBuffer var3 = null;
      Iterator var4 = var1.alternatives.iterator();

      while(true) {
         while(var4.hasNext()) {
            JCTree.JCExpression var5 = (JCTree.JCExpression)var4.next();
            Type var6 = this.attribType(var5, (Env)this.env);
            var6 = this.chk.checkType(var5.pos(), this.chk.checkClassType(var5.pos(), var6), this.syms.throwableType);
            if (!var6.isErroneous()) {
               if (this.chk.intersects(var6, var2.toList())) {
                  Iterator var7 = var2.iterator();

                  label61:
                  while(true) {
                     Type var8;
                     boolean var9;
                     boolean var10;
                     do {
                        if (!var7.hasNext()) {
                           break label61;
                        }

                        var8 = (Type)var7.next();
                        var9 = this.types.isSubtype(var6, var8);
                        var10 = this.types.isSubtype(var8, var6);
                     } while(!var9 && !var10);

                     Type var11 = var9 ? var6 : var8;
                     Type var12 = var9 ? var8 : var6;
                     this.log.error(var5.pos(), "multicatch.types.must.be.disjoint", new Object[]{var11, var12});
                  }
               }

               var2.append(var6);
               if (var3 != null) {
                  var3.append(var6);
               }
            } else {
               if (var3 == null) {
                  var3 = new ListBuffer();
                  var3.appendList(var2);
               }

               var3.append(var6);
            }
         }

         Object var13 = this.check(this.noCheckTree, this.types.lub(var2.toList()), 2, this.resultInfo);
         if (((Type)var13).hasTag(TypeTag.CLASS)) {
            List var14 = (var3 == null ? var2 : var3).toList();
            var13 = new Type.UnionClassType((Type.ClassType)var13, var14);
         }

         var1.type = this.result = (Type)var13;
         return;
      }
   }

   public void visitTypeIntersection(JCTree.JCTypeIntersection var1) {
      this.attribTypes(var1.bounds, this.env);
      var1.type = this.result = this.checkIntersection(var1, var1.bounds);
   }

   public void visitTypeParameter(JCTree.JCTypeParameter var1) {
      Type.TypeVar var2 = (Type.TypeVar)var1.type;
      if (var1.annotations != null && var1.annotations.nonEmpty()) {
         this.annotateType(var1, var1.annotations);
      }

      if (!var2.bound.isErroneous()) {
         var2.bound = this.checkIntersection(var1, var1.bounds);
      }

   }

   Type checkIntersection(JCTree var1, List var2) {
      HashSet var3 = new HashSet();
      JCTree.JCExpression var5;
      if (var2.nonEmpty()) {
         ((JCTree.JCExpression)var2.head).type = this.checkBase(((JCTree.JCExpression)var2.head).type, (JCTree)var2.head, this.env, false, false, false);
         var3.add(this.types.erasure(((JCTree.JCExpression)var2.head).type));
         if (((JCTree.JCExpression)var2.head).type.isErroneous()) {
            return ((JCTree.JCExpression)var2.head).type;
         }

         if (((JCTree.JCExpression)var2.head).type.hasTag(TypeTag.TYPEVAR)) {
            if (var2.tail.nonEmpty()) {
               this.log.error(((JCTree.JCExpression)var2.tail.head).pos(), "type.var.may.not.be.followed.by.other.bounds", new Object[0]);
               return ((JCTree.JCExpression)var2.head).type;
            }
         } else {
            Iterator var4 = var2.tail.iterator();

            while(var4.hasNext()) {
               var5 = (JCTree.JCExpression)var4.next();
               var5.type = this.checkBase(var5.type, var5, this.env, false, true, false);
               if (var5.type.isErroneous()) {
                  var2 = List.of(var5);
               } else if (var5.type.hasTag(TypeTag.CLASS)) {
                  this.chk.checkNotRepeated(var5.pos(), this.types.erasure(var5.type), var3);
               }
            }
         }
      }

      if (var2.length() == 0) {
         return this.syms.objectType;
      } else if (var2.length() == 1) {
         return ((JCTree.JCExpression)var2.head).type;
      } else {
         Type.IntersectionClassType var10 = this.types.makeIntersectionType(TreeInfo.types(var2));
         List var6;
         if (!((JCTree.JCExpression)var2.head).type.isInterface()) {
            var5 = (JCTree.JCExpression)var2.head;
            var6 = var2.tail;
         } else {
            var5 = null;
            var6 = var2;
         }

         JCTree.JCClassDecl var7 = this.make.at(var1).ClassDef(this.make.Modifiers(1025L), this.names.empty, List.nil(), var5, var6, List.nil());
         Symbol.ClassSymbol var8 = (Symbol.ClassSymbol)var10.tsym;
         Assert.check((var8.flags() & 16777216L) != 0L);
         var7.sym = var8;
         var8.sourcefile = this.env.toplevel.sourcefile;
         var8.flags_field |= 268435456L;
         Env var9 = this.enter.classEnv(var7, this.env);
         this.typeEnvs.put(var8, var9);
         this.attribClass(var8);
         return var10;
      }
   }

   public void visitWildcard(JCTree.JCWildcard var1) {
      Type var2 = var1.kind.kind == BoundKind.UNBOUND ? this.syms.objectType : this.attribType(var1.inner, this.env);
      this.result = this.check(var1, new Type.WildcardType(this.chk.checkRefType(var1.pos(), var2), var1.kind.kind, this.syms.boundClass), 2, this.resultInfo);
   }

   public void visitAnnotation(JCTree.JCAnnotation var1) {
      Assert.error("should be handled in Annotate");
   }

   public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
      Type var2 = this.attribType(var1.getUnderlyingType(), (Env)this.env);
      this.attribAnnotationTypes(var1.annotations, this.env);
      this.annotateType(var1, var1.annotations);
      this.result = var1.type = var2;
   }

   public void annotateType(final JCTree var1, final List var2) {
      this.annotate.typeAnnotation(new Annotate.Worker() {
         public String toString() {
            return "annotate " + var2 + " onto " + var1;
         }

         public void run() {
            List var1x = Attr.fromAnnotations(var2);
            if (var2.size() == var1x.size()) {
               var1.type = var1.type.unannotatedType().annotatedType(var1x);
            }

         }
      });
   }

   private static List fromAnnotations(List var0) {
      if (var0.isEmpty()) {
         return List.nil();
      } else {
         ListBuffer var1 = new ListBuffer();
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            JCTree.JCAnnotation var3 = (JCTree.JCAnnotation)var2.next();
            if (var3.attribute != null) {
               var1.append((Attribute.TypeCompound)var3.attribute);
            }
         }

         return var1.toList();
      }
   }

   public void visitErroneous(JCTree.JCErroneous var1) {
      if (var1.errs != null) {
         Iterator var2 = var1.errs.iterator();

         while(var2.hasNext()) {
            JCTree var3 = (JCTree)var2.next();
            this.attribTree(var3, this.env, new ResultInfo(63, this.pt()));
         }
      }

      this.result = var1.type = this.syms.errType;
   }

   public void visitTree(JCTree var1) {
      throw new AssertionError();
   }

   public void attrib(Env var1) {
      if (var1.tree.hasTag(JCTree.Tag.TOPLEVEL)) {
         this.attribTopLevel(var1);
      } else {
         this.attribClass(var1.tree.pos(), var1.enclClass.sym);
      }

   }

   public void attribTopLevel(Env var1) {
      JCTree.JCCompilationUnit var2 = var1.toplevel;

      try {
         this.annotate.flush();
      } catch (Symbol.CompletionFailure var4) {
         this.chk.completionError(var2.pos(), var4);
      }

   }

   public void attribClass(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      try {
         this.annotate.flush();
         this.attribClass(var2);
      } catch (Symbol.CompletionFailure var4) {
         this.chk.completionError(var1, var4);
      }

   }

   void attribClass(Symbol.ClassSymbol var1) throws Symbol.CompletionFailure {
      if (!var1.type.hasTag(TypeTag.ERROR)) {
         this.chk.checkNonCyclic((JCDiagnostic.DiagnosticPosition)null, (Type)var1.type);
         Type var2 = this.types.supertype(var1.type);
         if ((var1.flags_field & 16777216L) == 0L) {
            if (var2.hasTag(TypeTag.CLASS)) {
               this.attribClass((Symbol.ClassSymbol)var2.tsym);
            }

            if (var1.owner.kind == 2 && var1.owner.type.hasTag(TypeTag.CLASS)) {
               this.attribClass((Symbol.ClassSymbol)var1.owner);
            }
         }

         if ((var1.flags_field & 268435456L) != 0L) {
            var1.flags_field &= -268435457L;
            Env var3 = this.typeEnvs.get(var1);

            Env var4;
            for(var4 = var3; ((AttrContext)var4.info).lint == null; var4 = var4.next) {
            }

            ((AttrContext)var3.info).lint = ((AttrContext)var4.info).lint.augment((Symbol)var1);
            Lint var5 = this.chk.setLint(((AttrContext)var3.info).lint);
            JavaFileObject var6 = this.log.useSource(var1.sourcefile);
            ResultInfo var7 = ((AttrContext)var3.info).returnResult;

            try {
               this.deferredLintHandler.flush(var3.tree);
               ((AttrContext)var3.info).returnResult = null;
               if (var2.tsym == this.syms.enumSym && (var1.flags_field & 16793600L) == 0L) {
                  this.log.error(var3.tree.pos(), "enum.no.subclassing", new Object[0]);
               }

               if (var2.tsym != null && (var2.tsym.flags_field & 16384L) != 0L && (var1.flags_field & 16793600L) == 0L) {
                  this.log.error(var3.tree.pos(), "enum.types.not.extensible", new Object[0]);
               }

               if (this.isSerializable(var1.type)) {
                  ((AttrContext)var3.info).isSerializable = true;
               }

               this.attribClassBody(var3, var1);
               this.chk.checkDeprecatedAnnotation(var3.tree.pos(), var1);
               this.chk.checkClassOverrideEqualsAndHashIfNeeded(var3.tree.pos(), var1);
               this.chk.checkFunctionalInterface((JCTree.JCClassDecl)var3.tree, var1);
            } finally {
               ((AttrContext)var3.info).returnResult = var7;
               this.log.useSource(var6);
               this.chk.setLint(var5);
            }
         }

      }
   }

   public void visitImport(JCTree.JCImport var1) {
   }

   private void attribClassBody(Env var1, Symbol.ClassSymbol var2) {
      JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)var1.tree;
      Assert.check(var2 == var3.sym);
      this.attribStats(var3.typarams, var1);
      if (!var2.isAnonymous()) {
         this.chk.validate(var3.typarams, var1);
         this.chk.validate((JCTree)var3.extending, var1);
         this.chk.validate(var3.implementing, var1);
      }

      var2.markAbstractIfNeeded(this.types);
      if ((var2.flags() & 1536L) == 0L && !this.relax) {
         this.chk.checkAllDefined(var3.pos(), var2);
      }

      if ((var2.flags() & 8192L) != 0L) {
         if (var3.implementing.nonEmpty()) {
            this.log.error(((JCTree.JCExpression)var3.implementing.head).pos(), "cant.extend.intf.annotation", new Object[0]);
         }

         if (var3.typarams.nonEmpty()) {
            this.log.error(((JCTree.JCTypeParameter)var3.typarams.head).pos(), "intf.annotation.cant.have.type.params", new Object[0]);
         }

         Attribute.Compound var4 = var2.attribute(this.syms.repeatableType.tsym);
         if (var4 != null) {
            JCDiagnostic.DiagnosticPosition var5 = this.getDiagnosticPosition(var3, var4.type);
            Assert.checkNonNull(var5);
            this.chk.validateRepeatable(var2, var4, var5);
         }
      } else {
         this.chk.checkCompatibleSupertypes(var3.pos(), var2.type);
         if (this.allowDefaultMethods) {
            this.chk.checkDefaultMethodClashes(var3.pos(), var2.type);
         }
      }

      this.chk.checkClassBounds(var3.pos(), var2.type);
      var3.type = var2.type;

      List var6;
      for(var6 = var3.typarams; var6.nonEmpty(); var6 = var6.tail) {
         Assert.checkNonNull(((AttrContext)var1.info).scope.lookup(((JCTree.JCTypeParameter)var6.head).name).scope);
      }

      if (!var2.type.allparams().isEmpty() && this.types.isSubtype(var2.type, this.syms.throwableType)) {
         this.log.error(var3.extending.pos(), "generic.throwable", new Object[0]);
      }

      this.chk.checkImplementations(var3);
      this.checkAutoCloseable(var3.pos(), var1, var2.type);

      for(var6 = var3.defs; var6.nonEmpty(); var6 = var6.tail) {
         this.attribStat((JCTree)var6.head, var1);
         if (var2.owner.kind != 1 && ((var2.flags() & 8L) == 0L || var2.name == this.names.empty) && (TreeInfo.flags((JCTree)var6.head) & 520L) != 0L) {
            Symbol.VarSymbol var7 = null;
            if (((JCTree)var6.head).hasTag(JCTree.Tag.VARDEF)) {
               var7 = ((JCTree.JCVariableDecl)var6.head).sym;
            }

            if (var7 == null || var7.kind != 4 || ((Symbol.VarSymbol)var7).getConstValue() == null) {
               this.log.error(((JCTree)var6.head).pos(), "icls.cant.have.static.decl", new Object[]{var2});
            }
         }
      }

      this.chk.checkCyclicConstructors(var3);
      this.chk.checkNonCyclicElements(var3);
      if (((AttrContext)var1.info).lint.isEnabled(Lint.LintCategory.SERIAL) && this.isSerializable(var2.type) && (var2.flags() & 16384L) == 0L && this.checkForSerial(var2)) {
         this.checkSerialVersionUID(var3, var2);
      }

      if (this.allowTypeAnnos) {
         this.typeAnnotations.organizeTypeAnnotationsBodies(var3);
         this.validateTypeAnnotations(var3, false);
      }

   }

   boolean checkForSerial(Symbol.ClassSymbol var1) {
      return (var1.flags() & 1024L) == 0L ? true : var1.members().anyMatch(anyNonAbstractOrDefaultMethod);
   }

   private JCDiagnostic.DiagnosticPosition getDiagnosticPosition(JCTree.JCClassDecl var1, Type var2) {
      for(List var3 = var1.mods.annotations; !var3.isEmpty(); var3 = var3.tail) {
         if (this.types.isSameType(((JCTree.JCAnnotation)var3.head).annotationType.type, var2)) {
            return ((JCTree.JCAnnotation)var3.head).pos();
         }
      }

      return null;
   }

   boolean isSerializable(Type var1) {
      try {
         this.syms.serializableType.complete();
      } catch (Symbol.CompletionFailure var3) {
         return false;
      }

      return this.types.isSubtype(var1, this.syms.serializableType);
   }

   private void checkSerialVersionUID(JCTree.JCClassDecl var1, Symbol.ClassSymbol var2) {
      Scope.Entry var3;
      for(var3 = var2.members().lookup(this.names.serialVersionUID); var3.scope != null && var3.sym.kind != 4; var3 = var3.next()) {
      }

      if (var3.scope == null) {
         this.log.warning(Lint.LintCategory.SERIAL, var1.pos(), "missing.SVUID", new Object[]{var2});
      } else {
         Symbol.VarSymbol var4 = (Symbol.VarSymbol)var3.sym;
         if ((var4.flags() & 24L) != 24L) {
            this.log.warning(Lint.LintCategory.SERIAL, TreeInfo.diagnosticPositionFor(var4, var1), "improper.SVUID", new Object[]{var2});
         } else if (!var4.type.hasTag(TypeTag.LONG)) {
            this.log.warning(Lint.LintCategory.SERIAL, TreeInfo.diagnosticPositionFor(var4, var1), "long.SVUID", new Object[]{var2});
         } else if (var4.getConstValue() == null) {
            this.log.warning(Lint.LintCategory.SERIAL, TreeInfo.diagnosticPositionFor(var4, var1), "constant.SVUID", new Object[]{var2});
         }

      }
   }

   private Type capture(Type var1) {
      return this.types.capture(var1);
   }

   public void validateTypeAnnotations(JCTree var1, boolean var2) {
      var1.accept(new TypeAnnotationsValidator(var2));
   }

   public void postAttr(JCTree var1) {
      (new PostAttrAnalyzer()).scan(var1);
   }

   static {
      primitiveTags = new TypeTag[]{TypeTag.BYTE, TypeTag.CHAR, TypeTag.SHORT, TypeTag.INT, TypeTag.LONG, TypeTag.FLOAT, TypeTag.DOUBLE, TypeTag.BOOLEAN};
      anyNonAbstractOrDefaultMethod = new Filter() {
         public boolean accepts(Symbol var1) {
            return var1.kind == 16 && (var1.flags() & 8796093023232L) != 1024L;
         }
      };
   }

   class PostAttrAnalyzer extends TreeScanner {
      private void initTypeIfNeeded(JCTree var1) {
         if (var1.type == null) {
            if (var1.hasTag(JCTree.Tag.METHODDEF)) {
               var1.type = this.dummyMethodType((JCTree.JCMethodDecl)var1);
            } else {
               var1.type = Attr.this.syms.unknownType;
            }
         }

      }

      private Type dummyMethodType(JCTree.JCMethodDecl var1) {
         Object var2 = Attr.this.syms.unknownType;
         if (var1 != null && var1.restype.hasTag(JCTree.Tag.TYPEIDENT)) {
            JCTree.JCPrimitiveTypeTree var3 = (JCTree.JCPrimitiveTypeTree)var1.restype;
            if (var3.typetag == TypeTag.VOID) {
               var2 = Attr.this.syms.voidType;
            }
         }

         return new Type.MethodType(List.nil(), (Type)var2, List.nil(), Attr.this.syms.methodClass);
      }

      private Type dummyMethodType() {
         return this.dummyMethodType((JCTree.JCMethodDecl)null);
      }

      public void scan(JCTree var1) {
         if (var1 != null) {
            if (var1 instanceof JCTree.JCExpression) {
               this.initTypeIfNeeded(var1);
            }

            super.scan(var1);
         }
      }

      public void visitIdent(JCTree.JCIdent var1) {
         if (var1.sym == null) {
            var1.sym = Attr.this.syms.unknownSymbol;
         }

      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         if (var1.sym == null) {
            var1.sym = Attr.this.syms.unknownSymbol;
         }

         super.visitSelect(var1);
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         this.initTypeIfNeeded(var1);
         if (var1.sym == null) {
            var1.sym = new Symbol.ClassSymbol(0L, var1.name, var1.type, Attr.this.syms.noSymbol);
         }

         super.visitClassDef(var1);
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         this.initTypeIfNeeded(var1);
         if (var1.sym == null) {
            var1.sym = new Symbol.MethodSymbol(0L, var1.name, var1.type, Attr.this.syms.noSymbol);
         }

         super.visitMethodDef(var1);
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         this.initTypeIfNeeded(var1);
         if (var1.sym == null) {
            var1.sym = new Symbol.VarSymbol(0L, var1.name, var1.type, Attr.this.syms.noSymbol);
            var1.sym.adr = 0;
         }

         super.visitVarDef(var1);
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         if (var1.constructor == null) {
            var1.constructor = new Symbol.MethodSymbol(0L, Attr.this.names.init, this.dummyMethodType(), Attr.this.syms.noSymbol);
         }

         if (var1.constructorType == null) {
            var1.constructorType = Attr.this.syms.unknownType;
         }

         super.visitNewClass(var1);
      }

      public void visitAssignop(JCTree.JCAssignOp var1) {
         if (var1.operator == null) {
            var1.operator = new Symbol.OperatorSymbol(Attr.this.names.empty, this.dummyMethodType(), -1, Attr.this.syms.noSymbol);
         }

         super.visitAssignop(var1);
      }

      public void visitBinary(JCTree.JCBinary var1) {
         if (var1.operator == null) {
            var1.operator = new Symbol.OperatorSymbol(Attr.this.names.empty, this.dummyMethodType(), -1, Attr.this.syms.noSymbol);
         }

         super.visitBinary(var1);
      }

      public void visitUnary(JCTree.JCUnary var1) {
         if (var1.operator == null) {
            var1.operator = new Symbol.OperatorSymbol(Attr.this.names.empty, this.dummyMethodType(), -1, Attr.this.syms.noSymbol);
         }

         super.visitUnary(var1);
      }

      public void visitLambda(JCTree.JCLambda var1) {
         super.visitLambda(var1);
         if (var1.targets == null) {
            var1.targets = List.nil();
         }

      }

      public void visitReference(JCTree.JCMemberReference var1) {
         super.visitReference(var1);
         if (var1.sym == null) {
            var1.sym = new Symbol.MethodSymbol(0L, Attr.this.names.empty, this.dummyMethodType(), Attr.this.syms.noSymbol);
         }

         if (var1.targets == null) {
            var1.targets = List.nil();
         }

      }
   }

   private final class TypeAnnotationsValidator extends TreeScanner {
      private final boolean sigOnly;

      public TypeAnnotationsValidator(boolean var2) {
         this.sigOnly = var2;
      }

      public void visitAnnotation(JCTree.JCAnnotation var1) {
         Attr.this.chk.validateTypeAnnotation(var1, false);
         super.visitAnnotation(var1);
      }

      public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
         if (!var1.underlyingType.type.isErroneous()) {
            super.visitAnnotatedType(var1);
         }

      }

      public void visitTypeParameter(JCTree.JCTypeParameter var1) {
         Attr.this.chk.validateTypeAnnotations(var1.annotations, true);
         this.scan(var1.bounds);
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         if (var1.recvparam != null && !var1.recvparam.vartype.type.isErroneous()) {
            this.checkForDeclarationAnnotations(var1.recvparam.mods.annotations, var1.recvparam.vartype.type.tsym);
         }

         if (var1.restype != null && var1.restype.type != null) {
            this.validateAnnotatedType(var1.restype, var1.restype.type);
         }

         if (this.sigOnly) {
            this.scan(var1.mods);
            this.scan(var1.restype);
            this.scan(var1.typarams);
            this.scan(var1.recvparam);
            this.scan(var1.params);
            this.scan(var1.thrown);
         } else {
            this.scan(var1.defaultValue);
            this.scan(var1.body);
         }

      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         if (var1.sym != null && var1.sym.type != null) {
            this.validateAnnotatedType(var1.vartype, var1.sym.type);
         }

         this.scan(var1.mods);
         this.scan(var1.vartype);
         if (!this.sigOnly) {
            this.scan(var1.init);
         }

      }

      public void visitTypeCast(JCTree.JCTypeCast var1) {
         if (var1.clazz != null && var1.clazz.type != null) {
            this.validateAnnotatedType(var1.clazz, var1.clazz.type);
         }

         super.visitTypeCast(var1);
      }

      public void visitTypeTest(JCTree.JCInstanceOf var1) {
         if (var1.clazz != null && var1.clazz.type != null) {
            this.validateAnnotatedType(var1.clazz, var1.clazz.type);
         }

         super.visitTypeTest(var1);
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         if (var1.clazz != null && var1.clazz.type != null) {
            if (var1.clazz.hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
               this.checkForDeclarationAnnotations(((JCTree.JCAnnotatedType)var1.clazz).annotations, var1.clazz.type.tsym);
            }

            if (var1.def != null) {
               this.checkForDeclarationAnnotations(var1.def.mods.annotations, var1.clazz.type.tsym);
            }

            this.validateAnnotatedType(var1.clazz, var1.clazz.type);
         }

         super.visitNewClass(var1);
      }

      public void visitNewArray(JCTree.JCNewArray var1) {
         if (var1.elemtype != null && var1.elemtype.type != null) {
            if (var1.elemtype.hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
               this.checkForDeclarationAnnotations(((JCTree.JCAnnotatedType)var1.elemtype).annotations, var1.elemtype.type.tsym);
            }

            this.validateAnnotatedType(var1.elemtype, var1.elemtype.type);
         }

         super.visitNewArray(var1);
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         if (this.sigOnly) {
            this.scan(var1.mods);
            this.scan(var1.typarams);
            this.scan(var1.extending);
            this.scan(var1.implementing);
         }

         Iterator var2 = var1.defs.iterator();

         while(var2.hasNext()) {
            JCTree var3 = (JCTree)var2.next();
            if (!var3.hasTag(JCTree.Tag.CLASSDEF)) {
               this.scan(var3);
            }
         }

      }

      public void visitBlock(JCTree.JCBlock var1) {
         if (!this.sigOnly) {
            this.scan(var1.stats);
         }

      }

      private void validateAnnotatedType(JCTree var1, Type var2) {
         if (!var2.isPrimitiveOrVoid()) {
            Object var3 = var1;
            Type var4 = var2;
            boolean var5 = true;

            while(true) {
               while(var5) {
                  if (((JCTree)var3).hasTag(JCTree.Tag.TYPEAPPLY)) {
                     List var6 = var4.getTypeArguments();
                     List var7 = ((JCTree.JCTypeApply)var3).getTypeArguments();
                     if (var7.length() > 0 && var6.length() == var7.length()) {
                        for(int var8 = 0; var8 < var6.length(); ++var8) {
                           this.validateAnnotatedType((JCTree)var7.get(var8), (Type)var6.get(var8));
                        }
                     }

                     var3 = ((JCTree.JCTypeApply)var3).clazz;
                  }

                  if (((JCTree)var3).hasTag(JCTree.Tag.SELECT)) {
                     var3 = ((JCTree.JCFieldAccess)var3).getExpression();
                     if (var4 != null && !var4.hasTag(TypeTag.NONE)) {
                        var4 = var4.getEnclosingType();
                     }
                  } else if (((JCTree)var3).hasTag(JCTree.Tag.ANNOTATED_TYPE)) {
                     JCTree.JCAnnotatedType var15 = (JCTree.JCAnnotatedType)var3;
                     if (var4 == null || var4.hasTag(TypeTag.NONE)) {
                        if (var15.getAnnotations().size() == 1) {
                           Attr.this.log.error(var15.underlyingType.pos(), "cant.type.annotate.scoping.1", new Object[]{((JCTree.JCAnnotation)var15.getAnnotations().head).attribute});
                        } else {
                           ListBuffer var16 = new ListBuffer();
                           Iterator var18 = var15.getAnnotations().iterator();

                           while(var18.hasNext()) {
                              JCTree.JCAnnotation var9 = (JCTree.JCAnnotation)var18.next();
                              var16.add(var9.attribute);
                           }

                           Attr.this.log.error(var15.underlyingType.pos(), "cant.type.annotate.scoping", new Object[]{var16.toList()});
                        }

                        var5 = false;
                     }

                     var3 = var15.underlyingType;
                  } else if (((JCTree)var3).hasTag(JCTree.Tag.IDENT)) {
                     var5 = false;
                  } else if (((JCTree)var3).hasTag(JCTree.Tag.WILDCARD)) {
                     JCTree.JCWildcard var13 = (JCTree.JCWildcard)var3;
                     if (var13.getKind() == Tree.Kind.EXTENDS_WILDCARD) {
                        this.validateAnnotatedType(var13.getBound(), ((Type.WildcardType)var4.unannotatedType()).getExtendsBound());
                     } else if (var13.getKind() == Tree.Kind.SUPER_WILDCARD) {
                        this.validateAnnotatedType(var13.getBound(), ((Type.WildcardType)var4.unannotatedType()).getSuperBound());
                     }

                     var5 = false;
                  } else if (((JCTree)var3).hasTag(JCTree.Tag.TYPEARRAY)) {
                     JCTree.JCArrayTypeTree var12 = (JCTree.JCArrayTypeTree)var3;
                     this.validateAnnotatedType(var12.getType(), ((Type.ArrayType)var4.unannotatedType()).getComponentType());
                     var5 = false;
                  } else {
                     Iterator var14;
                     JCTree var17;
                     if (((JCTree)var3).hasTag(JCTree.Tag.TYPEUNION)) {
                        JCTree.JCTypeUnion var11 = (JCTree.JCTypeUnion)var3;
                        var14 = var11.getTypeAlternatives().iterator();

                        while(var14.hasNext()) {
                           var17 = (JCTree)var14.next();
                           this.validateAnnotatedType(var17, var17.type);
                        }

                        var5 = false;
                     } else if (!((JCTree)var3).hasTag(JCTree.Tag.TYPEINTERSECTION)) {
                        if (((JCTree)var3).getKind() != Tree.Kind.PRIMITIVE_TYPE && ((JCTree)var3).getKind() != Tree.Kind.ERRONEOUS) {
                           Assert.error("Unexpected tree: " + var3 + " with kind: " + ((JCTree)var3).getKind() + " within: " + var1 + " with kind: " + var1.getKind());
                        } else {
                           var5 = false;
                        }
                     } else {
                        JCTree.JCTypeIntersection var10 = (JCTree.JCTypeIntersection)var3;
                        var14 = var10.getBounds().iterator();

                        while(var14.hasNext()) {
                           var17 = (JCTree)var14.next();
                           this.validateAnnotatedType(var17, var17.type);
                        }

                        var5 = false;
                     }
                  }
               }

               return;
            }
         }
      }

      private void checkForDeclarationAnnotations(List var1, Symbol var2) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            JCTree.JCAnnotation var4 = (JCTree.JCAnnotation)var3.next();
            if (!var4.type.isErroneous() && Attr.this.typeAnnotations.annotationType(var4.attribute, var2) == TypeAnnotations.AnnotationType.DECLARATION) {
               Attr.this.log.error(var4.pos(), "annotation.type.not.applicable", new Object[0]);
            }
         }

      }
   }

   class ExpressionLambdaReturnContext extends FunctionalReturnContext {
      JCTree.JCExpression expr;

      ExpressionLambdaReturnContext(JCTree.JCExpression var2, Check.CheckContext var3) {
         super(var3);
         this.expr = var2;
      }

      public boolean compatible(Type var1, Type var2, Warner var3) {
         return TreeInfo.isExpressionStatement(this.expr) && var2.hasTag(TypeTag.VOID) || super.compatible(var1, var2, var3);
      }
   }

   class FunctionalReturnContext extends Check.NestedCheckContext {
      FunctionalReturnContext(Check.CheckContext var2) {
         super(var2);
      }

      public boolean compatible(Type var1, Type var2, Warner var3) {
         return Attr.this.chk.basicHandler.compatible(var1, this.inferenceContext().asUndetVar(var2), var3);
      }

      public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2) {
         this.enclosingContext.report(var1, Attr.this.diags.fragment("incompatible.ret.type.in.lambda", var2));
      }
   }

   class RecoveryInfo extends ResultInfo {
      public RecoveryInfo(final DeferredAttr.DeferredAttrContext var2) {
         super(12, Type.recoveryType, new Check.NestedCheckContext(Attr.this.chk.basicHandler) {
            public DeferredAttr.DeferredAttrContext deferredAttrContext() {
               return var2;
            }

            public boolean compatible(Type var1, Type var2x, Warner var3) {
               return true;
            }

            public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2x) {
               Attr.this.chk.basicHandler.report(var1, var2x);
            }
         });
      }
   }

   class ResultInfo {
      final int pkind;
      final Type pt;
      final Check.CheckContext checkContext;

      ResultInfo(int var2, Type var3) {
         this(var2, var3, Attr.this.chk.basicHandler);
      }

      protected ResultInfo(int var2, Type var3, Check.CheckContext var4) {
         this.pkind = var2;
         this.pt = var3;
         this.checkContext = var4;
      }

      protected Type check(JCDiagnostic.DiagnosticPosition var1, Type var2) {
         return Attr.this.chk.checkType(var1, var2, this.pt, this.checkContext);
      }

      protected ResultInfo dup(Type var1) {
         return Attr.this.new ResultInfo(this.pkind, var1, this.checkContext);
      }

      protected ResultInfo dup(Check.CheckContext var1) {
         return Attr.this.new ResultInfo(this.pkind, this.pt, var1);
      }

      protected ResultInfo dup(Type var1, Check.CheckContext var2) {
         return Attr.this.new ResultInfo(this.pkind, var1, var2);
      }

      public String toString() {
         return this.pt != null ? this.pt.toString() : "";
      }
   }

   private static class BreakAttr extends RuntimeException {
      static final long serialVersionUID = -6924771130405446405L;
      private Env env;

      private BreakAttr(Env var1) {
         this.env = var1;
      }

      // $FF: synthetic method
      BreakAttr(Env var1, Object var2) {
         this(var1);
      }
   }

   private class IdentAttributer extends SimpleTreeVisitor {
      private IdentAttributer() {
      }

      public Symbol visitMemberSelect(MemberSelectTree var1, Env var2) {
         Symbol var3 = (Symbol)this.visit(var1.getExpression(), var2);
         if (var3.kind != 63 && var3.kind != 137) {
            Name var4 = (Name)var1.getIdentifier();
            if (var3.kind == 1) {
               var2.toplevel.packge = (Symbol.PackageSymbol)var3;
               return Attr.this.rs.findIdentInPackage(var2, (Symbol.TypeSymbol)var3, var4, 3);
            } else {
               var2.enclClass.sym = (Symbol.ClassSymbol)var3;
               return Attr.this.rs.findMemberType(var2, var3.asType(), var4, (Symbol.TypeSymbol)var3);
            }
         } else {
            return var3;
         }
      }

      public Symbol visitIdentifier(IdentifierTree var1, Env var2) {
         return Attr.this.rs.findIdent(var2, (Name)var1.getName(), 3);
      }

      // $FF: synthetic method
      IdentAttributer(Object var2) {
         this();
      }
   }
}
