package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.DeferredLintHandler;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Kinds;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.jvm.ClassReader;
import com.sun.tools.javac.jvm.Profile;
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Abort;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.MandatoryWarningHandler;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Pair;
import com.sun.tools.javac.util.Warner;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.tools.JavaFileManager;

public class Check {
   protected static final Context.Key checkKey = new Context.Key();
   private final Names names;
   private final Log log;
   private final Resolve rs;
   private final Symtab syms;
   private final Enter enter;
   private final DeferredAttr deferredAttr;
   private final Infer infer;
   private final Types types;
   private final JCDiagnostic.Factory diags;
   private boolean warnOnSyntheticConflicts;
   private boolean suppressAbortOnBadClassFile;
   private boolean enableSunApiLintControl;
   private final TreeInfo treeinfo;
   private final JavaFileManager fileManager;
   private final Profile profile;
   private final boolean warnOnAccessToSensitiveMembers;
   private Lint lint;
   private Symbol.MethodSymbol method;
   boolean allowGenerics;
   boolean allowVarargs;
   boolean allowAnnotations;
   boolean allowCovariantReturns;
   boolean allowSimplifiedVarargs;
   boolean allowDefaultMethods;
   boolean allowStrictMethodClashCheck;
   boolean complexInference;
   char syntheticNameChar;
   public Map compiled = new HashMap();
   private MandatoryWarningHandler deprecationHandler;
   private MandatoryWarningHandler uncheckedHandler;
   private MandatoryWarningHandler sunApiHandler;
   private DeferredLintHandler deferredLintHandler;
   CheckContext basicHandler = new CheckContext() {
      public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2) {
         Check.this.log.error(var1, "prob.found.req", new Object[]{var2});
      }

      public boolean compatible(Type var1, Type var2, Warner var3) {
         return Check.this.types.isAssignable(var1, var2, var3);
      }

      public Warner checkWarner(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
         return Check.this.convertWarner(var1, var2, var3);
      }

      public Infer.InferenceContext inferenceContext() {
         return Check.this.infer.emptyContext;
      }

      public DeferredAttr.DeferredAttrContext deferredAttrContext() {
         return Check.this.deferredAttr.emptyDeferredAttrContext;
      }

      public String toString() {
         return "CheckContext: basicHandler";
      }
   };
   private static final boolean ignoreAnnotatedCasts = true;
   Types.UnaryVisitor isTypeArgErroneous = new Types.UnaryVisitor() {
      public Boolean visitType(Type var1, Void var2) {
         return var1.isErroneous();
      }

      public Boolean visitTypeVar(Type.TypeVar var1, Void var2) {
         return (Boolean)this.visit(var1.getUpperBound());
      }

      public Boolean visitCapturedType(Type.CapturedType var1, Void var2) {
         return (Boolean)this.visit(var1.getUpperBound()) || (Boolean)this.visit(var1.getLowerBound());
      }

      public Boolean visitWildcardType(Type.WildcardType var1, Void var2) {
         return (Boolean)this.visit(var1.type);
      }
   };
   Warner overrideWarner = new Warner();
   private Filter equalsHasCodeFilter = new Filter() {
      public boolean accepts(Symbol var1) {
         return Symbol.MethodSymbol.implementation_filter.accepts(var1) && (var1.flags() & 35184372088832L) == 0L;
      }
   };
   private Set defaultTargets;
   private final Name[] dfltTargetMeta;

   public static Check instance(Context var0) {
      Check var1 = (Check)var0.get(checkKey);
      if (var1 == null) {
         var1 = new Check(var0);
      }

      return var1;
   }

   protected Check(Context var1) {
      var1.put((Context.Key)checkKey, (Object)this);
      this.names = Names.instance(var1);
      this.dfltTargetMeta = new Name[]{this.names.PACKAGE, this.names.TYPE, this.names.FIELD, this.names.METHOD, this.names.CONSTRUCTOR, this.names.ANNOTATION_TYPE, this.names.LOCAL_VARIABLE, this.names.PARAMETER};
      this.log = Log.instance(var1);
      this.rs = Resolve.instance(var1);
      this.syms = Symtab.instance(var1);
      this.enter = Enter.instance(var1);
      this.deferredAttr = DeferredAttr.instance(var1);
      this.infer = Infer.instance(var1);
      this.types = Types.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      Options var2 = Options.instance(var1);
      this.lint = Lint.instance(var1);
      this.treeinfo = TreeInfo.instance(var1);
      this.fileManager = (JavaFileManager)var1.get(JavaFileManager.class);
      Source var3 = Source.instance(var1);
      this.allowGenerics = var3.allowGenerics();
      this.allowVarargs = var3.allowVarargs();
      this.allowAnnotations = var3.allowAnnotations();
      this.allowCovariantReturns = var3.allowCovariantReturns();
      this.allowSimplifiedVarargs = var3.allowSimplifiedVarargs();
      this.allowDefaultMethods = var3.allowDefaultMethods();
      this.allowStrictMethodClashCheck = var3.allowStrictMethodClashCheck();
      this.complexInference = var2.isSet("complexinference");
      this.warnOnSyntheticConflicts = var2.isSet("warnOnSyntheticConflicts");
      this.suppressAbortOnBadClassFile = var2.isSet("suppressAbortOnBadClassFile");
      this.enableSunApiLintControl = var2.isSet("enableSunApiLintControl");
      this.warnOnAccessToSensitiveMembers = var2.isSet("warnOnAccessToSensitiveMembers");
      Target var4 = Target.instance(var1);
      this.syntheticNameChar = var4.syntheticNameChar();
      this.profile = Profile.instance(var1);
      boolean var5 = this.lint.isEnabled(Lint.LintCategory.DEPRECATION);
      boolean var6 = this.lint.isEnabled(Lint.LintCategory.UNCHECKED);
      boolean var7 = this.lint.isEnabled(Lint.LintCategory.SUNAPI);
      boolean var8 = var3.enforceMandatoryWarnings();
      this.deprecationHandler = new MandatoryWarningHandler(this.log, var5, var8, "deprecated", Lint.LintCategory.DEPRECATION);
      this.uncheckedHandler = new MandatoryWarningHandler(this.log, var6, var8, "unchecked", Lint.LintCategory.UNCHECKED);
      this.sunApiHandler = new MandatoryWarningHandler(this.log, var7, var8, "sunapi", (Lint.LintCategory)null);
      this.deferredLintHandler = DeferredLintHandler.instance(var1);
   }

   Lint setLint(Lint var1) {
      Lint var2 = this.lint;
      this.lint = var1;
      return var2;
   }

   Symbol.MethodSymbol setMethod(Symbol.MethodSymbol var1) {
      Symbol.MethodSymbol var2 = this.method;
      this.method = var1;
      return var2;
   }

   void warnDeprecated(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if (!this.lint.isSuppressed(Lint.LintCategory.DEPRECATION)) {
         this.deprecationHandler.report(var1, "has.been.deprecated", var2, var2.location());
      }

   }

   public void warnUnchecked(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      if (!this.lint.isSuppressed(Lint.LintCategory.UNCHECKED)) {
         this.uncheckedHandler.report(var1, var2, var3);
      }

   }

   void warnUnsafeVararg(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      if (this.lint.isEnabled(Lint.LintCategory.VARARGS) && this.allowSimplifiedVarargs) {
         this.log.warning(Lint.LintCategory.VARARGS, var1, var2, var3);
      }

   }

   public void warnSunApi(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      if (!this.lint.isSuppressed(Lint.LintCategory.SUNAPI)) {
         this.sunApiHandler.report(var1, var2, var3);
      }

   }

   public void warnStatic(JCDiagnostic.DiagnosticPosition var1, String var2, Object... var3) {
      if (this.lint.isEnabled(Lint.LintCategory.STATIC)) {
         this.log.warning(Lint.LintCategory.STATIC, var1, var2, var3);
      }

   }

   public void reportDeferredDiagnostics() {
      this.deprecationHandler.reportDeferredDiagnostic();
      this.uncheckedHandler.reportDeferredDiagnostic();
      this.sunApiHandler.reportDeferredDiagnostic();
   }

   public Type completionError(JCDiagnostic.DiagnosticPosition var1, Symbol.CompletionFailure var2) {
      this.log.error(JCDiagnostic.DiagnosticFlag.NON_DEFERRABLE, var1, "cant.access", new Object[]{var2.sym, var2.getDetailValue()});
      if (var2 instanceof ClassReader.BadClassFile && !this.suppressAbortOnBadClassFile) {
         throw new Abort();
      } else {
         return this.syms.errType;
      }
   }

   Type typeTagError(JCDiagnostic.DiagnosticPosition var1, Object var2, Object var3) {
      if (var3 instanceof Type && ((Type)var3).hasTag(TypeTag.VOID)) {
         this.log.error(var1, "illegal.start.of.type", new Object[0]);
         return this.syms.errType;
      } else {
         this.log.error(var1, "type.found.req", new Object[]{var3, var2});
         return this.types.createErrorType(var3 instanceof Type ? (Type)var3 : this.syms.errType);
      }
   }

   void earlyRefError(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      this.log.error(var1, "cant.ref.before.ctor.called", new Object[]{var2});
   }

   void duplicateError(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if (!var2.type.isErroneous()) {
         Symbol var3 = var2.location();
         if (var3.kind == 16 && ((Symbol.MethodSymbol)var3).isStaticOrInstanceInit()) {
            this.log.error(var1, "already.defined.in.clinit", new Object[]{Kinds.kindName(var2), var2, Kinds.kindName(var2.location()), Kinds.kindName((Symbol)var2.location().enclClass()), var2.location().enclClass()});
         } else {
            this.log.error(var1, "already.defined", new Object[]{Kinds.kindName(var2), var2, Kinds.kindName(var2.location()), var2.location()});
         }
      }

   }

   void varargsDuplicateError(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Symbol var3) {
      if (!var2.type.isErroneous() && !var3.type.isErroneous()) {
         this.log.error(var1, "array.and.varargs", new Object[]{var2, var3, var3.location()});
      }

   }

   void checkTransparentVar(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2, Scope var3) {
      if (var3.next != null) {
         for(Scope.Entry var4 = var3.next.lookup(var2.name); var4.scope != null && var4.sym.owner == var2.owner; var4 = var4.next()) {
            if (var4.sym.kind == 4 && (var4.sym.owner.kind & 20) != 0 && var2.name != this.names.error) {
               this.duplicateError(var1, var4.sym);
               return;
            }
         }
      }

   }

   void checkTransparentClass(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2, Scope var3) {
      if (var3.next != null) {
         for(Scope.Entry var4 = var3.next.lookup(var2.name); var4.scope != null && var4.sym.owner == var2.owner; var4 = var4.next()) {
            if (var4.sym.kind == 2 && !var4.sym.type.hasTag(TypeTag.TYPEVAR) && (var4.sym.owner.kind & 20) != 0 && var2.name != this.names.error) {
               this.duplicateError(var1, var4.sym);
               return;
            }
         }
      }

   }

   boolean checkUniqueClassName(JCDiagnostic.DiagnosticPosition var1, Name var2, Scope var3) {
      for(Scope.Entry var4 = var3.lookup(var2); var4.scope == var3; var4 = var4.next()) {
         if (var4.sym.kind == 2 && var4.sym.name != this.names.error) {
            this.duplicateError(var1, var4.sym);
            return false;
         }
      }

      for(Symbol var5 = var3.owner; var5 != null; var5 = var5.owner) {
         if (var5.kind == 2 && var5.name == var2 && var5.name != this.names.error) {
            this.duplicateError(var1, var5);
            return true;
         }
      }

      return true;
   }

   Name localClassName(Symbol.ClassSymbol var1) {
      int var2 = 1;

      while(true) {
         Name var3 = this.names.fromString("" + var1.owner.enclClass().flatname + this.syntheticNameChar + var2 + var1.name);
         if (this.compiled.get(var3) == null) {
            return var3;
         }

         ++var2;
      }
   }

   Type checkType(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
      return this.checkType(var1, var2, var3, this.basicHandler);
   }

   Type checkType(final JCDiagnostic.DiagnosticPosition var1, final Type var2, final Type var3, final CheckContext var4) {
      Infer.InferenceContext var5 = var4.inferenceContext();
      if (var5.free(var3) || var5.free(var2)) {
         var5.addFreeTypeListener(List.of(var3, var2), new Infer.FreeTypeListener() {
            public void typesInferred(Infer.InferenceContext var1x) {
               Check.this.checkType(var1, var1x.asInstType(var2), var1x.asInstType(var3), var4);
            }
         });
      }

      if (var3.hasTag(TypeTag.ERROR)) {
         return var3;
      } else if (var3.hasTag(TypeTag.NONE)) {
         return var2;
      } else if (var4.compatible(var2, var3, var4.checkWarner(var1, var2, var3))) {
         return var2;
      } else if (var2.isNumeric() && var3.isNumeric()) {
         var4.report(var1, this.diags.fragment("possible.loss.of.precision", var2, var3));
         return this.types.createErrorType(var2);
      } else {
         var4.report(var1, this.diags.fragment("inconvertible.types", var2, var3));
         return this.types.createErrorType(var2);
      }
   }

   Type checkCastable(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
      return this.checkCastable(var1, var2, var3, this.basicHandler);
   }

   Type checkCastable(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3, CheckContext var4) {
      if (this.types.isCastable(var2, var3, this.castWarner(var1, var2, var3))) {
         return var3;
      } else {
         var4.report(var1, this.diags.fragment("inconvertible.types", var2, var3));
         return this.types.createErrorType(var2);
      }
   }

   public void checkRedundantCast(Env var1, final JCTree.JCTypeCast var2) {
      if (!var2.type.isErroneous() && this.types.isSameType(var2.expr.type, var2.clazz.type) && !TreeInfo.containsTypeAnnotation(var2.clazz) && !this.is292targetTypeCast(var2)) {
         this.deferredLintHandler.report(new DeferredLintHandler.LintLogger() {
            public void report() {
               if (Check.this.lint.isEnabled(Lint.LintCategory.CAST)) {
                  Check.this.log.warning(Lint.LintCategory.CAST, var2.pos(), "redundant.cast", new Object[]{var2.expr.type});
               }

            }
         });
      }

   }

   private boolean is292targetTypeCast(JCTree.JCTypeCast var1) {
      boolean var2 = false;
      JCTree.JCExpression var3 = TreeInfo.skipParens(var1.expr);
      if (var3.hasTag(JCTree.Tag.APPLY)) {
         JCTree.JCMethodInvocation var4 = (JCTree.JCMethodInvocation)var3;
         Symbol var5 = TreeInfo.symbol(var4.meth);
         var2 = var5 != null && var5.kind == 16 && (var5.flags() & 137438953472L) != 0L;
      }

      return var2;
   }

   private boolean checkExtends(Type var1, Type var2) {
      if (var1.isUnbound()) {
         return true;
      } else if (!var1.hasTag(TypeTag.WILDCARD)) {
         var1 = this.types.cvarUpperBound(var1);
         return this.types.isSubtype(var1, var2);
      } else if (var1.isExtendsBound()) {
         return this.types.isCastable(var2, this.types.wildUpperBound(var1), this.types.noWarnings);
      } else if (var1.isSuperBound()) {
         return !this.types.notSoftSubtype(this.types.wildLowerBound(var1), var2);
      } else {
         return true;
      }
   }

   Type checkNonVoid(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      if (var2.hasTag(TypeTag.VOID)) {
         this.log.error(var1, "void.not.allowed.here", new Object[0]);
         return this.types.createErrorType(var2);
      } else {
         return var2;
      }
   }

   Type checkClassOrArrayType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      return !var2.hasTag(TypeTag.CLASS) && !var2.hasTag(TypeTag.ARRAY) && !var2.hasTag(TypeTag.ERROR) ? this.typeTagError(var1, this.diags.fragment("type.req.class.array"), this.asTypeParam(var2)) : var2;
   }

   Type checkClassType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      return !var2.hasTag(TypeTag.CLASS) && !var2.hasTag(TypeTag.ERROR) ? this.typeTagError(var1, this.diags.fragment("type.req.class"), this.asTypeParam(var2)) : var2;
   }

   private Object asTypeParam(Type var1) {
      return var1.hasTag(TypeTag.TYPEVAR) ? this.diags.fragment("type.parameter", var1) : var1;
   }

   Type checkConstructorRefType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      var2 = this.checkClassOrArrayType(var1, var2);
      if (var2.hasTag(TypeTag.CLASS)) {
         if ((var2.tsym.flags() & 1536L) != 0L) {
            this.log.error(var1, "abstract.cant.be.instantiated", new Object[]{var2.tsym});
            var2 = this.types.createErrorType(var2);
         } else if ((var2.tsym.flags() & 16384L) != 0L) {
            this.log.error(var1, "enum.cant.be.instantiated", new Object[0]);
            var2 = this.types.createErrorType(var2);
         } else {
            var2 = this.checkClassType(var1, var2, true);
         }
      } else if (var2.hasTag(TypeTag.ARRAY) && !this.types.isReifiable(((Type.ArrayType)var2).elemtype)) {
         this.log.error(var1, "generic.array.creation", new Object[0]);
         var2 = this.types.createErrorType(var2);
      }

      return var2;
   }

   Type checkClassType(JCDiagnostic.DiagnosticPosition var1, Type var2, boolean var3) {
      var2 = this.checkClassType(var1, var2);
      if (var3 && var2.isParameterized()) {
         for(List var4 = var2.getTypeArguments(); var4.nonEmpty(); var4 = var4.tail) {
            if (((Type)var4.head).hasTag(TypeTag.WILDCARD)) {
               return this.typeTagError(var1, this.diags.fragment("type.req.exact"), var4.head);
            }
         }
      }

      return var2;
   }

   Type checkRefType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      return var2.isReference() ? var2 : this.typeTagError(var1, this.diags.fragment("type.req.ref"), var2);
   }

   List checkRefTypes(List var1, List var2) {
      List var3 = var1;

      for(List var4 = var2; var4.nonEmpty(); var4 = var4.tail) {
         var4.head = this.checkRefType(((JCTree.JCExpression)var3.head).pos(), (Type)var4.head);
         var3 = var3.tail;
      }

      return var2;
   }

   Type checkNullOrRefType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      return !var2.isReference() && !var2.hasTag(TypeTag.BOT) ? this.typeTagError(var1, this.diags.fragment("type.req.ref"), var2) : var2;
   }

   boolean checkDisjoint(JCDiagnostic.DiagnosticPosition var1, long var2, long var4, long var6) {
      if ((var2 & var4) != 0L && (var2 & var6) != 0L) {
         this.log.error(var1, "illegal.combination.of.modifiers", new Object[]{Flags.asFlagSet(TreeInfo.firstFlag(var2 & var4)), Flags.asFlagSet(TreeInfo.firstFlag(var2 & var6))});
         return false;
      } else {
         return true;
      }
   }

   Type checkDiamond(JCTree.JCNewClass var1, Type var2) {
      if (TreeInfo.isDiamond(var1) && !var2.isErroneous()) {
         if (var1.def != null) {
            this.log.error(var1.clazz.pos(), "cant.apply.diamond.1", new Object[]{var2, this.diags.fragment("diamond.and.anon.class", var2)});
            return this.types.createErrorType(var2);
         } else if (var2.tsym.type.getTypeArguments().isEmpty()) {
            this.log.error(var1.clazz.pos(), "cant.apply.diamond.1", new Object[]{var2, this.diags.fragment("diamond.non.generic", var2)});
            return this.types.createErrorType(var2);
         } else if (var1.typeargs != null && var1.typeargs.nonEmpty()) {
            this.log.error(var1.clazz.pos(), "cant.apply.diamond.1", new Object[]{var2, this.diags.fragment("diamond.and.explicit.params", var2)});
            return this.types.createErrorType(var2);
         } else {
            return var2;
         }
      } else {
         return this.checkClassType(var1.clazz.pos(), var2, true);
      }
   }

   void checkVarargsMethodDecl(Env var1, JCTree.JCMethodDecl var2) {
      Symbol.MethodSymbol var3 = var2.sym;
      if (this.allowSimplifiedVarargs) {
         boolean var4 = var3.attribute(this.syms.trustMeType.tsym) != null;
         Type var5 = null;
         if (var3.isVarArgs()) {
            var5 = this.types.elemtype(((JCTree.JCVariableDecl)var2.params.last()).type);
         }

         if (var4 && !this.isTrustMeAllowedOnMethod(var3)) {
            if (var5 != null) {
               this.log.error(var2, "varargs.invalid.trustme.anno", new Object[]{this.syms.trustMeType.tsym, this.diags.fragment("varargs.trustme.on.virtual.varargs", var3)});
            } else {
               this.log.error(var2, "varargs.invalid.trustme.anno", new Object[]{this.syms.trustMeType.tsym, this.diags.fragment("varargs.trustme.on.non.varargs.meth", var3)});
            }
         } else if (var4 && var5 != null && this.types.isReifiable(var5)) {
            this.warnUnsafeVararg(var2, "varargs.redundant.trustme.anno", this.syms.trustMeType.tsym, this.diags.fragment("varargs.trustme.on.reifiable.varargs", var5));
         } else if (!var4 && var5 != null && !this.types.isReifiable(var5)) {
            this.warnUnchecked(((JCTree.JCVariableDecl)var2.params.head).pos(), "unchecked.varargs.non.reifiable.type", var5);
         }

      }
   }

   private boolean isTrustMeAllowedOnMethod(Symbol var1) {
      return (var1.flags() & 17179869184L) != 0L && (var1.isConstructor() || (var1.flags() & 24L) != 0L);
   }

   Type checkMethod(final Type var1, final Symbol var2, final Env var3, final List var4, final List var5, final boolean var6, Infer.InferenceContext var7) {
      if (var7.free(var1)) {
         var7.addFreeTypeListener(List.of(var1), new Infer.FreeTypeListener() {
            public void typesInferred(Infer.InferenceContext var1x) {
               Check.this.checkMethod(var1x.asInstType(var1), var2, var3, var4, var5, var6, var1x);
            }
         });
         return var1;
      } else {
         List var9 = var1.getParameterTypes();
         List var10 = var2.type.getParameterTypes();
         if (var10.length() != var9.length()) {
            var10 = var9;
         }

         Type var11 = var6 ? (Type)var9.last() : null;
         if (var2.name == this.names.init && var2.owner == this.syms.enumSym) {
            var9 = var9.tail.tail;
            var10 = var10.tail.tail;
         }

         List var12 = var4;
         Type var16;
         if (var4 != null) {
            while(var9.head != var11) {
               JCTree var13 = (JCTree)var12.head;
               Warner var14 = this.convertWarner(var13.pos(), var13.type, (Type)var10.head);
               this.assertConvertible(var13, var13.type, (Type)var9.head, var14);
               var12 = var12.tail;
               var9 = var9.tail;
               var10 = var10.tail;
            }

            if (var6) {
               for(var16 = this.types.elemtype(var11); var12.tail != null; var12 = var12.tail) {
                  JCTree var18 = (JCTree)var12.head;
                  Warner var15 = this.convertWarner(var18.pos(), var18.type, var16);
                  this.assertConvertible(var18, var18.type, var16, var15);
               }
            } else if ((var2.flags() & 70385924046848L) == 17179869184L && this.allowVarargs) {
               var16 = (Type)var1.getParameterTypes().last();
               Type var19 = (Type)var5.last();
               if (this.types.isSubtypeUnchecked(var19, this.types.elemtype(var16)) && !this.types.isSameType(this.types.erasure(var16), this.types.erasure(var19))) {
                  this.log.warning(((JCTree.JCExpression)var4.last()).pos(), "inexact.non-varargs.call", new Object[]{this.types.elemtype(var16), var16});
               }
            }
         }

         if (var6) {
            var16 = (Type)var1.getParameterTypes().last();
            if (!this.types.isReifiable(var16) && (!this.allowSimplifiedVarargs || var2.attribute(this.syms.trustMeType.tsym) == null || !this.isTrustMeAllowedOnMethod(var2))) {
               this.warnUnchecked(var3.tree.pos(), "unchecked.generic.array.creation", var16);
            }

            if ((var2.baseSymbol().flags() & 70368744177664L) == 0L) {
               TreeInfo.setVarargsElement(var3.tree, this.types.elemtype(var16));
            }
         }

         JCTree.JCPolyExpression.PolyKind var17 = var2.type.hasTag(TypeTag.FORALL) && var2.type.getReturnType().containsAny(((Type.ForAll)var2.type).tvars) ? JCTree.JCPolyExpression.PolyKind.POLY : JCTree.JCPolyExpression.PolyKind.STANDALONE;
         TreeInfo.setPolyKind(var3.tree, var17);
         return var1;
      }
   }

   private void assertConvertible(JCTree var1, Type var2, Type var3, Warner var4) {
      if (!this.types.isConvertible(var2, var3, var4)) {
         if (!var3.isCompound() || !this.types.isSubtype(var2, this.types.supertype(var3)) || !this.types.isSubtypeUnchecked(var2, this.types.interfaces(var3), var4)) {
            ;
         }
      }
   }

   public boolean checkValidGenericType(Type var1) {
      return this.firstIncompatibleTypeArg(var1) == null;
   }

   private Type firstIncompatibleTypeArg(Type var1) {
      List var2 = var1.tsym.type.allparams();
      List var3 = var1.allparams();
      List var4 = var1.getTypeArguments();
      List var5 = var1.tsym.type.getTypeArguments();

      ListBuffer var6;
      for(var6 = new ListBuffer(); var4.nonEmpty() && var5.nonEmpty(); var5 = var5.tail) {
         var6.append(this.types.subst(((Type)var5.head).getUpperBound(), var2, var3));
         var4 = var4.tail;
      }

      var4 = var1.getTypeArguments();

      for(List var7 = this.types.substBounds(var2, var2, this.types.capture(var1).allparams()); var4.nonEmpty() && var7.nonEmpty(); var7 = var7.tail) {
         ((Type)var4.head).withTypeVar((Type.TypeVar)var7.head);
         var4 = var4.tail;
      }

      var4 = var1.getTypeArguments();

      List var8;
      for(var8 = var6.toList(); var4.nonEmpty() && var8.nonEmpty(); var8 = var8.tail) {
         Type var9 = (Type)var4.head;
         if (!this.isTypeArgErroneous(var9) && !((Type)var8.head).isErroneous() && !this.checkExtends(var9, (Type)var8.head)) {
            return (Type)var4.head;
         }

         var4 = var4.tail;
      }

      var4 = var1.getTypeArguments();
      var8 = var6.toList();

      for(Iterator var11 = this.types.capture(var1).getTypeArguments().iterator(); var11.hasNext(); var4 = var4.tail) {
         Type var10 = (Type)var11.next();
         if (var10.hasTag(TypeTag.TYPEVAR) && var10.getUpperBound().isErroneous() && !((Type)var8.head).isErroneous() && !this.isTypeArgErroneous((Type)var4.head)) {
            return (Type)var4.head;
         }

         var8 = var8.tail;
      }

      return null;
   }

   boolean isTypeArgErroneous(Type var1) {
      return (Boolean)this.isTypeArgErroneous.visit(var1);
   }

   long checkFlags(JCDiagnostic.DiagnosticPosition var1, long var2, Symbol var4, JCTree var5) {
      long var8 = 0L;
      long var6;
      switch (var4.kind) {
         case 2:
            if (var4.isLocal()) {
               var6 = 23568L;
               if (var4.name.isEmpty()) {
                  var6 |= 8L;
                  var8 |= 16L;
               }

               if ((var4.owner.flags_field & 8L) == 0L && (var2 & 16384L) != 0L) {
                  this.log.error(var1, "enums.must.be.static", new Object[0]);
               }
            } else if (var4.owner.kind != 2) {
               var6 = 32273L;
            } else {
               var6 = 24087L;
               if (var4.owner.owner.kind != 1 && (var4.owner.flags_field & 8L) == 0L) {
                  if ((var2 & 16384L) != 0L) {
                     this.log.error(var1, "enums.must.be.static", new Object[0]);
                  }
               } else {
                  var6 |= 8L;
               }

               if ((var2 & 16896L) != 0L) {
                  var8 = 8L;
               }
            }

            if ((var2 & 512L) != 0L) {
               var8 |= 1024L;
            }

            if ((var2 & 16384L) != 0L) {
               var6 &= -1041L;
               var8 |= this.implicitEnumFinalFlag(var5);
            }

            var8 |= var4.owner.flags_field & 2048L;
            break;
         case 4:
            if (TreeInfo.isReceiverParam(var5)) {
               var6 = 8589934592L;
            } else if (var4.owner.kind != 2) {
               var6 = 8589934608L;
            } else if ((var4.owner.flags_field & 512L) != 0L) {
               var8 = 25L;
               var6 = 25L;
            } else {
               var6 = 16607L;
            }
            break;
         case 16:
            if (var4.name == this.names.init) {
               if ((var4.owner.flags_field & 16384L) != 0L) {
                  var8 = 2L;
                  var6 = 2L;
               } else {
                  var6 = 7L;
               }
            } else if ((var4.owner.flags_field & 512L) != 0L) {
               if ((var4.owner.flags_field & 8192L) != 0L) {
                  var6 = 1025L;
                  var8 = 1025L;
               } else if ((var2 & 8796093022216L) != 0L) {
                  var6 = 8796093025289L;
                  var8 = 1L;
                  if ((var2 & 8796093022208L) != 0L) {
                     var8 |= 1024L;
                  }
               } else {
                  var8 = 1025L;
                  var6 = 1025L;
               }
            } else {
               var6 = 3391L;
            }

            if (((var2 | var8) & 1024L) == 0L || (var2 & 8796093022208L) != 0L) {
               var8 |= var4.owner.flags_field & 2048L;
            }
            break;
         default:
            throw new AssertionError();
      }

      long var10 = var2 & 8796093026303L & ~var6;
      if (var10 != 0L) {
         if ((var10 & 512L) != 0L) {
            this.log.error(var1, "intf.not.allowed.here", new Object[0]);
            var6 |= 512L;
         } else {
            this.log.error(var1, "mod.not.allowed.here", new Object[]{Flags.asFlagSet(var10)});
         }
      } else if ((var4.kind == 2 || this.checkDisjoint(var1, var2, 1024L, 8796093022218L)) && this.checkDisjoint(var1, var2, 8L, 8796093022208L) && this.checkDisjoint(var1, var2, 1536L, 304L) && this.checkDisjoint(var1, var2, 1L, 6L) && this.checkDisjoint(var1, var2, 2L, 5L) && this.checkDisjoint(var1, var2, 16L, 64L) && var4.kind != 2 && this.checkDisjoint(var1, var2, 1280L, 2048L)) {
      }

      return var2 & (var6 | -8796093026304L) | var8;
   }

   private long implicitEnumFinalFlag(JCTree var1) {
      if (!var1.hasTag(JCTree.Tag.CLASSDEF)) {
         return 0L;
      } else {
         class SpecialTreeVisitor extends JCTree.Visitor {
            boolean specialized = false;

            public void visitTree(JCTree var1) {
            }

            public void visitVarDef(JCTree.JCVariableDecl var1) {
               if ((var1.mods.flags & 16384L) != 0L && var1.init instanceof JCTree.JCNewClass && ((JCTree.JCNewClass)var1.init).def != null) {
                  this.specialized = true;
               }

            }
         }

         SpecialTreeVisitor var2 = new SpecialTreeVisitor();
         JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)var1;
         Iterator var4 = var3.defs.iterator();

         do {
            if (!var4.hasNext()) {
               return 16L;
            }

            JCTree var5 = (JCTree)var4.next();
            var5.accept(var2);
         } while(!var2.specialized);

         return 0L;
      }
   }

   void validate(JCTree var1, Env var2) {
      this.validate(var1, var2, true);
   }

   void validate(JCTree var1, Env var2, boolean var3) {
      (new Validator(var2)).validateTree(var1, var3, true);
   }

   void validate(List var1, Env var2) {
      for(List var3 = var1; var3.nonEmpty(); var3 = var3.tail) {
         this.validate((JCTree)var3.head, var2);
      }

   }

   void checkRaw(JCTree var1, Env var2) {
      if (this.lint.isEnabled(Lint.LintCategory.RAW) && var1.type.hasTag(TypeTag.CLASS) && !TreeInfo.isDiamond(var1) && !this.withinAnonConstr(var2) && var1.type.isRaw()) {
         this.log.warning(Lint.LintCategory.RAW, var1.pos(), "raw.class.use", new Object[]{var1.type, var1.type.tsym.type});
      }

   }

   private boolean withinAnonConstr(Env var1) {
      return var1.enclClass.name.isEmpty() && var1.enclMethod != null && var1.enclMethod.name == this.names.init;
   }

   boolean subset(Type var1, List var2) {
      for(List var3 = var2; var3.nonEmpty(); var3 = var3.tail) {
         if (this.types.isSubtype(var1, (Type)var3.head)) {
            return true;
         }
      }

      return false;
   }

   boolean intersects(Type var1, List var2) {
      for(List var3 = var2; var3.nonEmpty(); var3 = var3.tail) {
         if (this.types.isSubtype(var1, (Type)var3.head) || this.types.isSubtype((Type)var3.head, var1)) {
            return true;
         }
      }

      return false;
   }

   List incl(Type var1, List var2) {
      return this.subset(var1, var2) ? var2 : this.excl(var1, var2).prepend(var1);
   }

   List excl(Type var1, List var2) {
      if (var2.isEmpty()) {
         return var2;
      } else {
         List var3 = this.excl(var1, var2.tail);
         if (this.types.isSubtype((Type)var2.head, var1)) {
            return var3;
         } else {
            return var3 == var2.tail ? var2 : var3.prepend(var2.head);
         }
      }
   }

   List union(List var1, List var2) {
      List var3 = var1;

      for(List var4 = var2; var4.nonEmpty(); var4 = var4.tail) {
         var3 = this.incl((Type)var4.head, var3);
      }

      return var3;
   }

   List diff(List var1, List var2) {
      List var3 = var1;

      for(List var4 = var2; var4.nonEmpty(); var4 = var4.tail) {
         var3 = this.excl((Type)var4.head, var3);
      }

      return var3;
   }

   public List intersect(List var1, List var2) {
      List var3 = List.nil();

      List var4;
      for(var4 = var1; var4.nonEmpty(); var4 = var4.tail) {
         if (this.subset((Type)var4.head, var2)) {
            var3 = this.incl((Type)var4.head, var3);
         }
      }

      for(var4 = var2; var4.nonEmpty(); var4 = var4.tail) {
         if (this.subset((Type)var4.head, var1)) {
            var3 = this.incl((Type)var4.head, var3);
         }
      }

      return var3;
   }

   boolean isUnchecked(Symbol.ClassSymbol var1) {
      return var1.kind == 63 || var1.isSubClass(this.syms.errorType.tsym, this.types) || var1.isSubClass(this.syms.runtimeExceptionType.tsym, this.types);
   }

   boolean isUnchecked(Type var1) {
      return var1.hasTag(TypeTag.TYPEVAR) ? this.isUnchecked(this.types.supertype(var1)) : (var1.hasTag(TypeTag.CLASS) ? this.isUnchecked((Symbol.ClassSymbol)var1.tsym) : var1.hasTag(TypeTag.BOT));
   }

   boolean isUnchecked(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      try {
         return this.isUnchecked(var2);
      } catch (Symbol.CompletionFailure var4) {
         this.completionError(var1, var4);
         return true;
      }
   }

   boolean isHandled(Type var1, List var2) {
      return this.isUnchecked(var1) || this.subset(var1, var2);
   }

   List unhandled(List var1, List var2) {
      List var3 = List.nil();

      for(List var4 = var1; var4.nonEmpty(); var4 = var4.tail) {
         if (!this.isHandled((Type)var4.head, var2)) {
            var3 = var3.prepend(var4.head);
         }
      }

      return var3;
   }

   static int protection(long var0) {
      switch ((short)((int)(var0 & 7L))) {
         case 0:
            return 2;
         case 1:
         case 3:
         default:
            return 0;
         case 2:
            return 3;
         case 4:
            return 1;
      }
   }

   Object cannotOverride(Symbol.MethodSymbol var1, Symbol.MethodSymbol var2) {
      String var3;
      if ((var2.owner.flags() & 512L) == 0L) {
         var3 = "cant.override";
      } else if ((var1.owner.flags() & 512L) == 0L) {
         var3 = "cant.implement";
      } else {
         var3 = "clashes.with";
      }

      return this.diags.fragment(var3, var1, var1.location(), var2, var2.location());
   }

   Object uncheckedOverrides(Symbol.MethodSymbol var1, Symbol.MethodSymbol var2) {
      String var3;
      if ((var2.owner.flags() & 512L) == 0L) {
         var3 = "unchecked.override";
      } else if ((var1.owner.flags() & 512L) == 0L) {
         var3 = "unchecked.implement";
      } else {
         var3 = "unchecked.clash.with";
      }

      return this.diags.fragment(var3, var1, var1.location(), var2, var2.location());
   }

   Object varargsOverrides(Symbol.MethodSymbol var1, Symbol.MethodSymbol var2) {
      String var3;
      if ((var2.owner.flags() & 512L) == 0L) {
         var3 = "varargs.override";
      } else if ((var1.owner.flags() & 512L) == 0L) {
         var3 = "varargs.implement";
      } else {
         var3 = "varargs.clash.with";
      }

      return this.diags.fragment(var3, var1, var1.location(), var2, var2.location());
   }

   void checkOverride(JCTree var1, Symbol.MethodSymbol var2, Symbol.MethodSymbol var3, Symbol.ClassSymbol var4) {
      if ((var2.flags() & 2147487744L) == 0L && (var3.flags() & 4096L) == 0L) {
         if ((var2.flags() & 8L) != 0L && (var3.flags() & 8L) == 0L) {
            this.log.error(TreeInfo.diagnosticPositionFor(var2, var1), "override.static", new Object[]{this.cannotOverride(var2, var3)});
            var2.flags_field |= 35184372088832L;
         } else if ((var3.flags() & 16L) != 0L || (var2.flags() & 8L) == 0L && (var3.flags() & 8L) != 0L) {
            this.log.error(TreeInfo.diagnosticPositionFor(var2, var1), "override.meth", new Object[]{this.cannotOverride(var2, var3), Flags.asFlagSet(var3.flags() & 24L)});
            var2.flags_field |= 35184372088832L;
         } else if ((var2.owner.flags() & 8192L) == 0L) {
            if ((var4.flags() & 512L) == 0L && protection(var2.flags()) > protection(var3.flags())) {
               this.log.error(TreeInfo.diagnosticPositionFor(var2, var1), "override.weaker.access", new Object[]{this.cannotOverride(var2, var3), var3.flags() == 0L ? "package" : Flags.asFlagSet(var3.flags() & 7L)});
               var2.flags_field |= 35184372088832L;
            } else {
               Type var5 = this.types.memberType(var4.type, var2);
               Type var6 = this.types.memberType(var4.type, var3);
               List var7 = var5.getTypeArguments();
               List var8 = var6.getTypeArguments();
               Type var9 = var5.getReturnType();
               Type var10 = this.types.subst(var6.getReturnType(), var8, var7);
               this.overrideWarner.clear();
               boolean var11 = this.types.returnTypeSubstitutable(var5, var6, var10, this.overrideWarner);
               if (!var11) {
                  if (this.allowCovariantReturns || var2.owner == var4 || !var2.owner.isSubClass(var3.owner, this.types)) {
                     this.log.error(TreeInfo.diagnosticPositionFor(var2, var1), "override.incompatible.ret", new Object[]{this.cannotOverride(var2, var3), var9, var10});
                     var2.flags_field |= 35184372088832L;
                     return;
                  }
               } else if (this.overrideWarner.hasNonSilentLint(Lint.LintCategory.UNCHECKED)) {
                  this.warnUnchecked(TreeInfo.diagnosticPositionFor(var2, var1), "override.unchecked.ret", this.uncheckedOverrides(var2, var3), var9, var10);
               }

               List var12 = this.types.subst(var6.getThrownTypes(), var8, var7);
               List var13 = this.unhandled(var5.getThrownTypes(), this.types.erasure(var12));
               List var14 = this.unhandled(var5.getThrownTypes(), var12);
               if (var13.nonEmpty()) {
                  this.log.error(TreeInfo.diagnosticPositionFor(var2, var1), "override.meth.doesnt.throw", new Object[]{this.cannotOverride(var2, var3), var14.head});
                  var2.flags_field |= 35184372088832L;
               } else if (var14.nonEmpty()) {
                  this.warnUnchecked(TreeInfo.diagnosticPositionFor(var2, var1), "override.unchecked.thrown", this.cannotOverride(var2, var3), var14.head);
               } else {
                  if (((var2.flags() ^ var3.flags()) & 17179869184L) != 0L && this.lint.isEnabled(Lint.LintCategory.OVERRIDES)) {
                     this.log.warning(TreeInfo.diagnosticPositionFor(var2, var1), (var2.flags() & 17179869184L) != 0L ? "override.varargs.missing" : "override.varargs.extra", new Object[]{this.varargsOverrides(var2, var3)});
                  }

                  if ((var3.flags() & 2147483648L) != 0L) {
                     this.log.warning(TreeInfo.diagnosticPositionFor(var2, var1), "override.bridge", new Object[]{this.uncheckedOverrides(var2, var3)});
                  }

                  if (!this.isDeprecatedOverrideIgnorable(var3, var4)) {
                     Lint var15 = this.setLint(this.lint.augment((Symbol)var2));

                     try {
                        this.checkDeprecated(TreeInfo.diagnosticPositionFor(var2, var1), var2, var3);
                     } finally {
                        this.setLint(var15);
                     }
                  }

               }
            }
         }
      }
   }

   private boolean isDeprecatedOverrideIgnorable(Symbol.MethodSymbol var1, Symbol.ClassSymbol var2) {
      Symbol.ClassSymbol var3 = var1.enclClass();
      Type var4 = this.types.supertype(var2.type);
      if (!var4.hasTag(TypeTag.CLASS)) {
         return true;
      } else {
         Symbol.MethodSymbol var5 = var1.implementation((Symbol.ClassSymbol)var4.tsym, this.types, false);
         if (var3 != null && (var3.flags() & 512L) != 0L) {
            List var6 = this.types.interfaces(var2.type);
            return var6.contains(var3.type) ? false : var5 != null;
         } else {
            return var5 != var1;
         }
      }
   }

   public void checkCompatibleConcretes(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      Type var3 = this.types.supertype(var2);
      if (var3.hasTag(TypeTag.CLASS)) {
         for(Type var4 = var3; var4.hasTag(TypeTag.CLASS) && var4.tsym.type.isParameterized(); var4 = this.types.supertype(var4)) {
            for(Scope.Entry var5 = var4.tsym.members().elems; var5 != null; var5 = var5.sibling) {
               Symbol var6 = var5.sym;
               if (var6.kind == 16 && (var6.flags() & 2147487752L) == 0L && var6.isInheritedIn(var2.tsym, this.types) && ((Symbol.MethodSymbol)var6).implementation(var2.tsym, this.types, true) == var6) {
                  Type var7 = this.types.memberType(var4, var6);
                  int var8 = var7.getParameterTypes().length();
                  if (var7 != var6.type) {
                     for(Type var9 = var3; var9.hasTag(TypeTag.CLASS); var9 = this.types.supertype(var9)) {
                        for(Scope.Entry var10 = var9.tsym.members().lookup(var6.name); var10.scope != null; var10 = var10.next()) {
                           Symbol var11 = var10.sym;
                           if (var11 != var6 && var11.kind == 16 && (var11.flags() & 2147487752L) == 0L && var11.type.getParameterTypes().length() == var8 && var11.isInheritedIn(var2.tsym, this.types) && ((Symbol.MethodSymbol)var11).implementation(var2.tsym, this.types, true) == var11) {
                              Type var12 = this.types.memberType(var9, var11);
                              if (this.types.overrideEquivalent(var7, var12)) {
                                 this.log.error(var1, "concrete.inheritance.conflict", new Object[]{var6, var4, var11, var9, var3});
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   public boolean checkCompatibleAbstracts(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
      return this.checkCompatibleAbstracts(var1, var2, var3, this.types.makeIntersectionType(var2, var3));
   }

   public boolean checkCompatibleAbstracts(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3, Type var4) {
      if ((var4.tsym.flags() & 16777216L) != 0L) {
         var2 = this.types.capture(var2);
         var3 = this.types.capture(var3);
      }

      return this.firstIncompatibility(var1, var2, var3, var4) == null;
   }

   private Symbol firstIncompatibility(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3, Type var4) {
      HashMap var5 = new HashMap();
      this.closure(var2, var5);
      HashMap var6;
      if (var2 == var3) {
         var6 = var5;
      } else {
         this.closure(var3, var5, var6 = new HashMap());
      }

      Iterator var7 = var5.values().iterator();

      while(var7.hasNext()) {
         Type var8 = (Type)var7.next();
         Iterator var9 = var6.values().iterator();

         while(var9.hasNext()) {
            Type var10 = (Type)var9.next();
            Symbol var11 = this.firstDirectIncompatibility(var1, var8, var10, var4);
            if (var11 != null) {
               return var11;
            }
         }
      }

      return null;
   }

   private void closure(Type var1, Map var2) {
      if (var1.hasTag(TypeTag.CLASS)) {
         if (var2.put(var1.tsym, var1) == null) {
            this.closure(this.types.supertype(var1), var2);
            Iterator var3 = this.types.interfaces(var1).iterator();

            while(var3.hasNext()) {
               Type var4 = (Type)var3.next();
               this.closure(var4, var2);
            }
         }

      }
   }

   private void closure(Type var1, Map var2, Map var3) {
      if (var1.hasTag(TypeTag.CLASS)) {
         if (var2.get(var1.tsym) == null) {
            if (var3.put(var1.tsym, var1) == null) {
               this.closure(this.types.supertype(var1), var2, var3);
               Iterator var4 = this.types.interfaces(var1).iterator();

               while(var4.hasNext()) {
                  Type var5 = (Type)var4.next();
                  this.closure(var5, var2, var3);
               }
            }

         }
      }
   }

   private Symbol firstDirectIncompatibility(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3, Type var4) {
      for(Scope.Entry var5 = var2.tsym.members().elems; var5 != null; var5 = var5.sibling) {
         Symbol var6 = var5.sym;
         Type var7 = null;
         if (var6.kind == 16 && var6.isInheritedIn(var4.tsym, this.types) && (var6.flags() & 4096L) == 0L) {
            Symbol.MethodSymbol var8 = ((Symbol.MethodSymbol)var6).implementation(var4.tsym, this.types, false);
            if (var8 == null || (var8.flags() & 1024L) != 0L) {
               for(Scope.Entry var9 = var3.tsym.members().lookup(var6.name); var9.scope != null; var9 = var9.next()) {
                  Symbol var10 = var9.sym;
                  if (var6 != var10 && var10.kind == 16 && var10.isInheritedIn(var4.tsym, this.types) && (var10.flags() & 4096L) == 0L) {
                     if (var7 == null) {
                        var7 = this.types.memberType(var2, var6);
                     }

                     Type var11 = this.types.memberType(var3, var10);
                     if (!this.types.overrideEquivalent(var7, var11)) {
                        if (this.checkNameClash((Symbol.ClassSymbol)var4.tsym, var6, var10) && !this.checkCommonOverriderIn(var6, var10, var4)) {
                           this.log.error(var1, "name.clash.same.erasure.no.override", new Object[]{var6, var6.location(), var10, var10.location()});
                           return var10;
                        }
                     } else {
                        List var12 = var7.getTypeArguments();
                        List var13 = var11.getTypeArguments();
                        Type var14 = var7.getReturnType();
                        Type var15 = this.types.subst(var11.getReturnType(), var13, var12);
                        boolean var16 = this.types.isSameType(var14, var15) || !var14.isPrimitiveOrVoid() && !var15.isPrimitiveOrVoid() && (this.types.covariantReturnType(var14, var15, this.types.noWarnings) || this.types.covariantReturnType(var15, var14, this.types.noWarnings)) || this.checkCommonOverriderIn(var6, var10, var4);
                        if (!var16) {
                           this.log.error(var1, "types.incompatible.diff.ret", new Object[]{var2, var3, var10.name + "(" + this.types.memberType(var3, var10).getParameterTypes() + ")"});
                           return var10;
                        }
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   boolean checkCommonOverriderIn(Symbol var1, Symbol var2, Type var3) {
      HashMap var4 = new HashMap();
      Type var5 = this.types.memberType(var3, var1);
      Type var6 = this.types.memberType(var3, var2);
      this.closure(var3, var4);
      Iterator var7 = var4.values().iterator();

      while(var7.hasNext()) {
         Type var8 = (Type)var7.next();

         for(Scope.Entry var9 = var8.tsym.members().lookup(var1.name); var9.scope != null; var9 = var9.next()) {
            Symbol var10 = var9.sym;
            if (var10 != var1 && var10 != var2 && var10.kind == 16 && (var10.flags() & 2147487744L) == 0L) {
               Type var11 = this.types.memberType(var3, var10);
               if (this.types.overrideEquivalent(var11, var5) && this.types.overrideEquivalent(var11, var6) && this.types.returnTypeSubstitutable(var11, var5) && this.types.returnTypeSubstitutable(var11, var6)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   void checkOverride(JCTree.JCMethodDecl var1, Symbol.MethodSymbol var2) {
      Symbol.ClassSymbol var3 = (Symbol.ClassSymbol)var2.owner;
      if ((var3.flags() & 16384L) != 0L && this.names.finalize.equals(var2.name) && var2.overrides(this.syms.enumFinalFinalize, var3, this.types, false)) {
         this.log.error(var1.pos(), "enum.no.finalize", new Object[0]);
      } else {
         Iterator var5;
         for(Type var4 = var3.type; var4.hasTag(TypeTag.CLASS); var4 = this.types.supertype(var4)) {
            if (var4 != var3.type) {
               this.checkOverride(var1, (Type)var4, (Symbol.ClassSymbol)var3, (Symbol.MethodSymbol)var2);
            }

            var5 = this.types.interfaces(var4).iterator();

            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               this.checkOverride(var1, (Type)var6, (Symbol.ClassSymbol)var3, (Symbol.MethodSymbol)var2);
            }
         }

         if (var2.attribute(this.syms.overrideType.tsym) != null && !this.isOverrider(var2)) {
            JCDiagnostic.DiagnosticPosition var7 = var1.pos();
            var5 = var1.getModifiers().annotations.iterator();

            while(var5.hasNext()) {
               JCTree.JCAnnotation var8 = (JCTree.JCAnnotation)var5.next();
               if (var8.annotationType.type.tsym == this.syms.overrideType.tsym) {
                  var7 = var8.pos();
                  break;
               }
            }

            this.log.error(var7, "method.does.not.override.superclass", new Object[0]);
         }

      }
   }

   void checkOverride(JCTree var1, Type var2, Symbol.ClassSymbol var3, Symbol.MethodSymbol var4) {
      Symbol.TypeSymbol var5 = var2.tsym;

      for(Scope.Entry var6 = var5.members().lookup(var4.name); var6.scope != null; var6 = var6.next()) {
         if (var4.overrides(var6.sym, var3, this.types, false) && (var6.sym.flags() & 1024L) == 0L) {
            this.checkOverride(var1, var4, (Symbol.MethodSymbol)var6.sym, var3);
         }
      }

   }

   public void checkClassOverrideEqualsAndHashIfNeeded(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      if (var2 != (Symbol.ClassSymbol)this.syms.objectType.tsym && !var2.isInterface() && !var2.isEnum() && (var2.flags() & 8192L) == 0L && (var2.flags() & 1024L) == 0L) {
         if (var2.isAnonymous()) {
            List var3 = this.types.interfaces(var2.type);
            if (var3 != null && !var3.isEmpty() && ((Type)var3.head).tsym == this.syms.comparatorType.tsym) {
               return;
            }
         }

         this.checkClassOverrideEqualsAndHash(var1, var2);
      }
   }

   private void checkClassOverrideEqualsAndHash(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      if (this.lint.isEnabled(Lint.LintCategory.OVERRIDES)) {
         Symbol.MethodSymbol var3 = (Symbol.MethodSymbol)this.syms.objectType.tsym.members().lookup(this.names.equals).sym;
         Symbol.MethodSymbol var4 = (Symbol.MethodSymbol)this.syms.objectType.tsym.members().lookup(this.names.hashCode).sym;
         boolean var5 = this.types.implementation(var3, var2, false, this.equalsHasCodeFilter).owner == var2;
         boolean var6 = this.types.implementation(var4, var2, false, this.equalsHasCodeFilter) != var4;
         if (var5 && !var6) {
            this.log.warning(Lint.LintCategory.OVERRIDES, var1, "override.equals.but.not.hashcode", new Object[]{var2});
         }
      }

   }

   private boolean checkNameClash(Symbol.ClassSymbol var1, Symbol var2, Symbol var3) {
      ClashFilter var4 = new ClashFilter(var1.type);
      return var4.accepts(var2) && var4.accepts(var3) && this.types.hasSameArgs(var2.erasure(this.types), var3.erasure(this.types));
   }

   void checkAllDefined(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      Symbol.MethodSymbol var3 = this.types.firstUnimplementedAbstract(var2);
      if (var3 != null) {
         Symbol.MethodSymbol var4 = new Symbol.MethodSymbol(var3.flags(), var3.name, this.types.memberType(var2.type, var3), var3.owner);
         this.log.error(var1, "does.not.override.abstract", new Object[]{var2, var4, var4.location()});
      }

   }

   void checkNonCyclicDecl(JCTree.JCClassDecl var1) {
      CycleChecker var2 = new CycleChecker();
      var2.scan(var1);
      if (!var2.errorFound && !var2.partialCheck) {
         Symbol.ClassSymbol var10000 = var1.sym;
         var10000.flags_field |= 1073741824L;
      }

   }

   void checkNonCyclic(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      this.checkNonCyclicInternal(var1, var2);
   }

   void checkNonCyclic(JCDiagnostic.DiagnosticPosition var1, Type.TypeVar var2) {
      this.checkNonCyclic1(var1, var2, List.nil());
   }

   private void checkNonCyclic1(JCDiagnostic.DiagnosticPosition var1, Type var2, List var3) {
      if (!var2.hasTag(TypeTag.TYPEVAR) || (var2.tsym.flags() & 268435456L) == 0L) {
         Type.TypeVar var4;
         if (var3.contains(var2)) {
            var4 = (Type.TypeVar)var2.unannotatedType();
            var4.bound = this.types.createErrorType(var2);
            this.log.error(var1, "cyclic.inheritance", new Object[]{var2});
         } else if (var2.hasTag(TypeTag.TYPEVAR)) {
            var4 = (Type.TypeVar)var2.unannotatedType();
            var3 = var3.prepend(var4);
            Iterator var5 = this.types.getBounds(var4).iterator();

            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               this.checkNonCyclic1(var1, var6, var3);
            }
         }

      }
   }

   private boolean checkNonCyclicInternal(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      boolean var3 = true;
      Symbol.TypeSymbol var4 = var2.tsym;
      if ((var4.flags_field & 1073741824L) != 0L) {
         return true;
      } else {
         if ((var4.flags_field & 134217728L) != 0L) {
            this.noteCyclic(var1, (Symbol.ClassSymbol)var4);
         } else if (!var4.type.isErroneous()) {
            try {
               var4.flags_field |= 134217728L;
               if (var4.type.hasTag(TypeTag.CLASS)) {
                  Type.ClassType var5 = (Type.ClassType)var4.type;
                  if (var5.interfaces_field != null) {
                     for(List var6 = var5.interfaces_field; var6.nonEmpty(); var6 = var6.tail) {
                        var3 &= this.checkNonCyclicInternal(var1, (Type)var6.head);
                     }
                  }

                  if (var5.supertype_field != null) {
                     Type var10 = var5.supertype_field;
                     if (var10 != null && var10.hasTag(TypeTag.CLASS)) {
                        var3 &= this.checkNonCyclicInternal(var1, var10);
                     }
                  }

                  if (var4.owner.kind == 2) {
                     var3 &= this.checkNonCyclicInternal(var1, var4.owner.type);
                  }
               }
            } finally {
               var4.flags_field &= -134217729L;
            }
         }

         if (var3) {
            var3 = (var4.flags_field & 268435456L) == 0L && var4.completer == null;
         }

         if (var3) {
            var4.flags_field |= 1073741824L;
         }

         return var3;
      }
   }

   private void noteCyclic(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      this.log.error(var1, "cyclic.inheritance", new Object[]{var2});

      for(List var3 = this.types.interfaces(var2.type); var3.nonEmpty(); var3 = var3.tail) {
         var3.head = this.types.createErrorType((Symbol.ClassSymbol)((Type)var3.head).tsym, Type.noType);
      }

      Type var4 = this.types.supertype(var2.type);
      if (var4.hasTag(TypeTag.CLASS)) {
         ((Type.ClassType)var2.type).supertype_field = this.types.createErrorType((Symbol.ClassSymbol)var4.tsym, Type.noType);
      }

      var2.type = this.types.createErrorType(var2, var2.type);
      var2.flags_field |= 1073741824L;
   }

   void checkImplementations(JCTree.JCClassDecl var1) {
      this.checkImplementations(var1, var1.sym, var1.sym);
   }

   void checkImplementations(JCTree var1, Symbol.ClassSymbol var2, Symbol.ClassSymbol var3) {
      for(List var4 = this.types.closure(var3.type); var4.nonEmpty(); var4 = var4.tail) {
         Symbol.ClassSymbol var5 = (Symbol.ClassSymbol)((Type)var4.head).tsym;
         if ((this.allowGenerics || var2 != var5) && (var5.flags() & 1024L) != 0L) {
            for(Scope.Entry var6 = var5.members().elems; var6 != null; var6 = var6.sibling) {
               if (var6.sym.kind == 16 && (var6.sym.flags() & 1032L) == 1024L) {
                  Symbol.MethodSymbol var7 = (Symbol.MethodSymbol)var6.sym;
                  Symbol.MethodSymbol var8 = var7.implementation(var2, this.types, false);
                  if (var8 != null && var8 != var7 && (var8.owner.flags() & 512L) == (var2.flags() & 512L)) {
                     this.checkOverride(var1, var8, var7, var2);
                  }
               }
            }
         }
      }

   }

   void checkCompatibleSupertypes(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      List var3 = this.types.interfaces(var2);
      Type var4 = this.types.supertype(var2);
      if (var4.hasTag(TypeTag.CLASS) && (var4.tsym.flags() & 1024L) != 0L) {
         var3 = var3.prepend(var4);
      }

      for(List var5 = var3; var5.nonEmpty(); var5 = var5.tail) {
         if (this.allowGenerics && !((Type)var5.head).getTypeArguments().isEmpty() && !this.checkCompatibleAbstracts(var1, (Type)var5.head, (Type)var5.head, var2)) {
            return;
         }

         for(List var6 = var3; var6 != var5; var6 = var6.tail) {
            if (!this.checkCompatibleAbstracts(var1, (Type)var5.head, (Type)var6.head, var2)) {
               return;
            }
         }
      }

      this.checkCompatibleConcretes(var1, var2);
   }

   void checkConflicts(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Symbol.TypeSymbol var3) {
      for(Type var4 = var3.type; var4 != Type.noType; var4 = this.types.supertype(var4)) {
         for(Scope.Entry var5 = var4.tsym.members().lookup(var2.name); var5.scope == var4.tsym.members(); var5 = var5.next()) {
            if (var2.kind == var5.sym.kind && this.types.isSameType(this.types.erasure(var2.type), this.types.erasure(var5.sym.type)) && var2 != var5.sym && (var2.flags() & 4096L) != (var5.sym.flags() & 4096L) && (var2.flags() & 2097152L) == 0L && (var5.sym.flags() & 2097152L) == 0L && (var2.flags() & 2147483648L) == 0L && (var5.sym.flags() & 2147483648L) == 0L) {
               this.syntheticError(var1, (var5.sym.flags() & 4096L) == 0L ? var5.sym : var2);
               return;
            }
         }
      }

   }

   void checkOverrideClashes(JCDiagnostic.DiagnosticPosition var1, Type var2, Symbol.MethodSymbol var3) {
      ClashFilter var4 = new ClashFilter(var2);
      List var5 = List.nil();
      boolean var6 = false;
      Iterator var7 = this.types.membersClosure(var2, false).getElementsByName(var3.name, var4).iterator();

      while(true) {
         while(var7.hasNext()) {
            Symbol var8 = (Symbol)var7.next();
            if (!var3.overrides(var8, var2.tsym, this.types, false)) {
               if (var8 != var3 && !var6) {
                  var5 = var5.prepend((Symbol.MethodSymbol)var8);
               }
            } else {
               if (var8 != var3) {
                  var6 = true;
                  var5 = List.nil();
               }

               Iterator var9 = this.types.membersClosure(var2, false).getElementsByName(var3.name, var4).iterator();

               while(var9.hasNext()) {
                  Symbol var10 = (Symbol)var9.next();
                  if (var10 != var8 && !this.types.isSubSignature(var3.type, this.types.memberType(var2, var10), this.allowStrictMethodClashCheck) && this.types.hasSameArgs(var10.erasure(this.types), var8.erasure(this.types))) {
                     var3.flags_field |= 4398046511104L;
                     String var11 = var8 == var3 ? "name.clash.same.erasure.no.override" : "name.clash.same.erasure.no.override.1";
                     this.log.error(var1, var11, new Object[]{var3, var3.location(), var10, var10.location(), var8, var8.location()});
                     return;
                  }
               }
            }
         }

         if (!var6) {
            var7 = var5.iterator();

            while(var7.hasNext()) {
               Symbol.MethodSymbol var12 = (Symbol.MethodSymbol)var7.next();
               this.checkPotentiallyAmbiguousOverloads(var1, var2, var3, var12);
            }
         }

         return;
      }
   }

   void checkHideClashes(JCDiagnostic.DiagnosticPosition var1, Type var2, Symbol.MethodSymbol var3) {
      ClashFilter var4 = new ClashFilter(var2);
      Iterator var5 = this.types.membersClosure(var2, true).getElementsByName(var3.name, var4).iterator();

      while(var5.hasNext()) {
         Symbol var6 = (Symbol)var5.next();
         if (!this.types.isSubSignature(var3.type, this.types.memberType(var2, var6), this.allowStrictMethodClashCheck)) {
            if (this.types.hasSameArgs(var6.erasure(this.types), var3.erasure(this.types))) {
               this.log.error(var1, "name.clash.same.erasure.no.hide", new Object[]{var3, var3.location(), var6, var6.location()});
               return;
            }

            this.checkPotentiallyAmbiguousOverloads(var1, var2, var3, (Symbol.MethodSymbol)var6);
         }
      }

   }

   void checkDefaultMethodClashes(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      DefaultMethodClashFilter var3 = new DefaultMethodClashFilter(var2);
      Iterator var4 = this.types.membersClosure(var2, false).getElements(var3).iterator();

      while(true) {
         while(true) {
            Symbol var5;
            List var6;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               var5 = (Symbol)var4.next();
               Assert.check(var5.kind == 16);
               var6 = this.types.interfaceCandidates(var2, (Symbol.MethodSymbol)var5);
            } while(var6.size() <= 1);

            ListBuffer var7 = new ListBuffer();
            ListBuffer var8 = new ListBuffer();
            Iterator var9 = var6.iterator();

            while(var9.hasNext()) {
               Symbol.MethodSymbol var10 = (Symbol.MethodSymbol)var9.next();
               if ((var10.flags() & 8796093022208L) != 0L) {
                  var8 = var8.append(var10);
               } else if ((var10.flags() & 1024L) != 0L) {
                  var7 = var7.append(var10);
               }

               if (var8.nonEmpty() && var8.size() + var7.size() >= 2) {
                  Symbol var12 = (Symbol)var8.first();
                  String var11;
                  Symbol var13;
                  if (var8.size() > 1) {
                     var11 = "types.incompatible.unrelated.defaults";
                     var13 = (Symbol)var8.toList().tail.head;
                  } else {
                     var11 = "types.incompatible.abstract.default";
                     var13 = (Symbol)var7.first();
                  }

                  this.log.error(var1, var11, new Object[]{Kinds.kindName((Symbol)var2.tsym), var2, var5.name, this.types.memberType(var2, var5).getParameterTypes(), var12.location(), var13.location()});
                  break;
               }
            }
         }
      }
   }

   void checkPotentiallyAmbiguousOverloads(JCDiagnostic.DiagnosticPosition var1, Type var2, Symbol.MethodSymbol var3, Symbol.MethodSymbol var4) {
      if (var3 != var4 && this.allowDefaultMethods && this.lint.isEnabled(Lint.LintCategory.OVERLOADS) && (var3.flags() & 281474976710656L) == 0L && (var4.flags() & 281474976710656L) == 0L) {
         Type var5 = this.types.memberType(var2, var3);
         Type var6 = this.types.memberType(var2, var4);
         if (var5.hasTag(TypeTag.FORALL) && var6.hasTag(TypeTag.FORALL) && this.types.hasSameBounds((Type.ForAll)var5, (Type.ForAll)var6)) {
            var6 = this.types.subst(var6, ((Type.ForAll)var6).tvars, ((Type.ForAll)var5).tvars);
         }

         int var7 = Math.max(var5.getParameterTypes().length(), var6.getParameterTypes().length());
         List var8 = this.rs.adjustArgs(var5.getParameterTypes(), var3, var7, true);
         List var9 = this.rs.adjustArgs(var6.getParameterTypes(), var4, var7, true);
         if (var8.length() != var9.length()) {
            return;
         }

         boolean var10;
         for(var10 = false; var8.nonEmpty() && var9.nonEmpty(); var9 = var9.tail) {
            Type var11 = (Type)var8.head;
            Type var12 = (Type)var9.head;
            if (!this.types.isSubtype(var12, var11) && !this.types.isSubtype(var11, var12)) {
               if (!this.types.isFunctionalInterface(var11) || !this.types.isFunctionalInterface(var12) || this.types.findDescriptorType(var11).getParameterTypes().length() <= 0 || this.types.findDescriptorType(var11).getParameterTypes().length() != this.types.findDescriptorType(var12).getParameterTypes().length()) {
                  break;
               }

               var10 = true;
            }

            var8 = var8.tail;
         }

         if (var10) {
            var3.flags_field |= 281474976710656L;
            var4.flags_field |= 281474976710656L;
            this.log.warning(Lint.LintCategory.OVERLOADS, var1, "potentially.ambiguous.overload", new Object[]{var3, var3.location(), var4, var4.location()});
            return;
         }
      }

   }

   void checkElemAccessFromSerializableLambda(JCTree var1) {
      if (this.warnOnAccessToSensitiveMembers) {
         Symbol var2 = TreeInfo.symbol(var1);
         if ((var2.kind & 20) == 0) {
            return;
         }

         if (var2.kind == 4 && ((var2.flags() & 8589934592L) != 0L || var2.isLocal() || var2.name == this.names._this || var2.name == this.names._super)) {
            return;
         }

         if (!this.types.isSubtype(var2.owner.type, this.syms.serializableType) && this.isEffectivelyNonPublic(var2)) {
            this.log.warning(var1.pos(), "access.to.sensitive.member.from.serializable.element", new Object[]{var2});
         }
      }

   }

   private boolean isEffectivelyNonPublic(Symbol var1) {
      if (var1.packge() == this.syms.rootPackage) {
         return false;
      } else {
         while(var1.kind != 1) {
            if ((var1.flags() & 1L) == 0L) {
               return true;
            }

            var1 = var1.owner;
         }

         return false;
      }
   }

   private void syntheticError(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if (!var2.type.isErroneous()) {
         if (this.warnOnSyntheticConflicts) {
            this.log.warning(var1, "synthetic.name.conflict", new Object[]{var2, var2.location()});
         } else {
            this.log.error(var1, "synthetic.name.conflict", new Object[]{var2, var2.location()});
         }
      }

   }

   void checkClassBounds(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      this.checkClassBounds(var1, new HashMap(), var2);
   }

   void checkClassBounds(JCDiagnostic.DiagnosticPosition var1, Map var2, Type var3) {
      if (!var3.isErroneous()) {
         for(List var4 = this.types.interfaces(var3); var4.nonEmpty(); var4 = var4.tail) {
            Type var5 = (Type)var4.head;
            Type var6 = (Type)var2.put(var5.tsym, var5);
            if (var6 != null) {
               List var7 = var6.allparams();
               List var8 = var5.allparams();
               if (!this.types.containsTypeEquivalent(var7, var8)) {
                  this.log.error(var1, "cant.inherit.diff.arg", new Object[]{var5.tsym, Type.toString(var7), Type.toString(var8)});
               }
            }

            this.checkClassBounds(var1, var2, var5);
         }

         Type var9 = this.types.supertype(var3);
         if (var9 != Type.noType) {
            this.checkClassBounds(var1, var2, var9);
         }

      }
   }

   void checkNotRepeated(JCDiagnostic.DiagnosticPosition var1, Type var2, Set var3) {
      if (var3.contains(var2)) {
         this.log.error(var1, "repeated.interface", new Object[0]);
      } else {
         var3.add(var2);
      }

   }

   void validateAnnotationTree(JCTree var1) {
      class AnnotationValidator extends TreeScanner {
         public void visitAnnotation(JCTree.JCAnnotation var1) {
            if (!var1.type.isErroneous()) {
               super.visitAnnotation(var1);
               Check.this.validateAnnotation(var1);
            }

         }
      }

      var1.accept(new AnnotationValidator());
   }

   void validateAnnotationType(JCTree var1) {
      if (var1 != null) {
         this.validateAnnotationType(var1.pos(), var1.type);
      }

   }

   void validateAnnotationType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      if (!var2.isPrimitive()) {
         if (!this.types.isSameType(var2, this.syms.stringType)) {
            if ((var2.tsym.flags() & 16384L) == 0L) {
               if ((var2.tsym.flags() & 8192L) == 0L) {
                  if (this.types.cvarLowerBound(var2).tsym != this.syms.classType.tsym) {
                     if (this.types.isArray(var2) && !this.types.isArray(this.types.elemtype(var2))) {
                        this.validateAnnotationType(var1, this.types.elemtype(var2));
                     } else {
                        this.log.error(var1, "invalid.annotation.member.type", new Object[0]);
                     }
                  }
               }
            }
         }
      }
   }

   void validateAnnotationMethod(JCDiagnostic.DiagnosticPosition var1, Symbol.MethodSymbol var2) {
      for(Type var3 = this.syms.annotationType; var3.hasTag(TypeTag.CLASS); var3 = this.types.supertype(var3)) {
         Scope var4 = var3.tsym.members();

         for(Scope.Entry var5 = var4.lookup(var2.name); var5.scope != null; var5 = var5.next()) {
            if (var5.sym.kind == 16 && (var5.sym.flags() & 5L) != 0L && this.types.overrideEquivalent(var2.type, var5.sym.type)) {
               this.log.error(var1, "intf.annotation.member.clash", new Object[]{var5.sym, var3});
            }
         }
      }

   }

   public void validateAnnotations(List var1, Symbol var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JCTree.JCAnnotation var4 = (JCTree.JCAnnotation)var3.next();
         this.validateAnnotation(var4, var2);
      }

   }

   public void validateTypeAnnotations(List var1, boolean var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         JCTree.JCAnnotation var4 = (JCTree.JCAnnotation)var3.next();
         this.validateTypeAnnotation(var4, var2);
      }

   }

   private void validateAnnotation(JCTree.JCAnnotation var1, Symbol var2) {
      this.validateAnnotationTree(var1);
      if (!this.annotationApplicable(var1, var2)) {
         this.log.error(var1.pos(), "annotation.type.not.applicable", new Object[0]);
      }

      if (var1.annotationType.type.tsym == this.syms.functionalInterfaceType.tsym) {
         if (var2.kind != 2) {
            this.log.error(var1.pos(), "bad.functional.intf.anno", new Object[0]);
         } else if (!var2.isInterface() || (var2.flags() & 8192L) != 0L) {
            this.log.error(var1.pos(), "bad.functional.intf.anno.1", new Object[]{this.diags.fragment("not.a.functional.intf", var2)});
         }
      }

   }

   public void validateTypeAnnotation(JCTree.JCAnnotation var1, boolean var2) {
      Assert.checkNonNull(var1.type, "annotation tree hasn't been attributed yet: " + var1);
      this.validateAnnotationTree(var1);
      if (var1.hasTag(JCTree.Tag.TYPE_ANNOTATION) && !var1.annotationType.type.isErroneous() && !this.isTypeAnnotation(var1, var2)) {
         this.log.error(var1.pos(), "annotation.type.not.applicable", new Object[0]);
      }

   }

   public void validateRepeatable(Symbol.TypeSymbol var1, Attribute.Compound var2, JCDiagnostic.DiagnosticPosition var3) {
      Assert.check(this.types.isSameType(var2.type, this.syms.repeatableType));
      Type var4 = null;
      List var5 = var2.values;
      if (!var5.isEmpty()) {
         Assert.check(((Symbol.MethodSymbol)((Pair)var5.head).fst).name == this.names.value);
         var4 = ((Attribute.Class)((Pair)var5.head).snd).getValue();
      }

      if (var4 != null) {
         this.validateValue(var4.tsym, var1, var3);
         this.validateRetention(var4.tsym, var1, var3);
         this.validateDocumented(var4.tsym, var1, var3);
         this.validateInherited(var4.tsym, var1, var3);
         this.validateTarget(var4.tsym, var1, var3);
         this.validateDefault(var4.tsym, var3);
      }
   }

   private void validateValue(Symbol.TypeSymbol var1, Symbol.TypeSymbol var2, JCDiagnostic.DiagnosticPosition var3) {
      Scope.Entry var4 = var1.members().lookup(this.names.value);
      if (var4.scope != null && var4.sym.kind == 16) {
         Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)var4.sym;
         Type var6 = var5.getReturnType();
         if (!var6.hasTag(TypeTag.ARRAY) || !this.types.isSameType(((Type.ArrayType)var6).elemtype, var2.type)) {
            this.log.error(var3, "invalid.repeatable.annotation.value.return", new Object[]{var1, var6, this.types.makeArrayType(var2.type)});
         }
      } else {
         this.log.error(var3, "invalid.repeatable.annotation.no.value", new Object[]{var1});
      }

   }

   private void validateRetention(Symbol var1, Symbol var2, JCDiagnostic.DiagnosticPosition var3) {
      Attribute.RetentionPolicy var4 = this.types.getRetention(var1);
      Attribute.RetentionPolicy var5 = this.types.getRetention(var2);
      boolean var6 = false;
      switch (var5) {
         case RUNTIME:
            if (var4 != Attribute.RetentionPolicy.RUNTIME) {
               var6 = true;
            }
            break;
         case CLASS:
            if (var4 == Attribute.RetentionPolicy.SOURCE) {
               var6 = true;
            }
      }

      if (var6) {
         this.log.error(var3, "invalid.repeatable.annotation.retention", new Object[]{var1, var4, var2, var5});
      }

   }

   private void validateDocumented(Symbol var1, Symbol var2, JCDiagnostic.DiagnosticPosition var3) {
      if (var2.attribute(this.syms.documentedType.tsym) != null && var1.attribute(this.syms.documentedType.tsym) == null) {
         this.log.error(var3, "invalid.repeatable.annotation.not.documented", new Object[]{var1, var2});
      }

   }

   private void validateInherited(Symbol var1, Symbol var2, JCDiagnostic.DiagnosticPosition var3) {
      if (var2.attribute(this.syms.inheritedType.tsym) != null && var1.attribute(this.syms.inheritedType.tsym) == null) {
         this.log.error(var3, "invalid.repeatable.annotation.not.inherited", new Object[]{var1, var2});
      }

   }

   private void validateTarget(Symbol var1, Symbol var2, JCDiagnostic.DiagnosticPosition var3) {
      Attribute.Array var5 = this.getAttributeTargetAttribute(var1);
      Object var4;
      if (var5 == null) {
         var4 = this.getDefaultTargetSet();
      } else {
         var4 = new HashSet();
         Attribute[] var6 = var5.values;
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Attribute var9 = var6[var8];
            if (var9 instanceof Attribute.Enum) {
               Attribute.Enum var10 = (Attribute.Enum)var9;
               ((Set)var4).add(var10.value.name);
            }
         }
      }

      Attribute.Array var14 = this.getAttributeTargetAttribute(var2);
      Object var13;
      if (var14 == null) {
         var13 = this.getDefaultTargetSet();
      } else {
         var13 = new HashSet();
         Attribute[] var15 = var14.values;
         int var16 = var15.length;

         for(int var17 = 0; var17 < var16; ++var17) {
            Attribute var11 = var15[var17];
            if (var11 instanceof Attribute.Enum) {
               Attribute.Enum var12 = (Attribute.Enum)var11;
               ((Set)var13).add(var12.value.name);
            }
         }
      }

      if (!this.isTargetSubsetOf((Set)var4, (Set)var13)) {
         this.log.error(var3, "invalid.repeatable.annotation.incompatible.target", new Object[]{var1, var2});
      }

   }

   private Set getDefaultTargetSet() {
      if (this.defaultTargets == null) {
         HashSet var1 = new HashSet();
         var1.add(this.names.ANNOTATION_TYPE);
         var1.add(this.names.CONSTRUCTOR);
         var1.add(this.names.FIELD);
         var1.add(this.names.LOCAL_VARIABLE);
         var1.add(this.names.METHOD);
         var1.add(this.names.PACKAGE);
         var1.add(this.names.PARAMETER);
         var1.add(this.names.TYPE);
         this.defaultTargets = Collections.unmodifiableSet(var1);
      }

      return this.defaultTargets;
   }

   private boolean isTargetSubsetOf(Set var1, Set var2) {
      Iterator var3 = var1.iterator();

      boolean var5;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         Name var4 = (Name)var3.next();
         var5 = false;
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            Name var7 = (Name)var6.next();
            if (var7 == var4) {
               var5 = true;
               break;
            }

            if (var7 == this.names.TYPE && var4 == this.names.ANNOTATION_TYPE) {
               var5 = true;
               break;
            }

            if (var7 == this.names.TYPE_USE && (var4 == this.names.TYPE || var4 == this.names.ANNOTATION_TYPE || var4 == this.names.TYPE_PARAMETER)) {
               var5 = true;
               break;
            }
         }
      } while(var5);

      return false;
   }

   private void validateDefault(Symbol var1, JCDiagnostic.DiagnosticPosition var2) {
      Scope var3 = var1.members();
      Iterator var4 = var3.getElements().iterator();

      while(var4.hasNext()) {
         Symbol var5 = (Symbol)var4.next();
         if (var5.name != this.names.value && var5.kind == 16 && ((Symbol.MethodSymbol)var5).defaultValue == null) {
            this.log.error(var2, "invalid.repeatable.annotation.elem.nondefault", new Object[]{var1, var5});
         }
      }

   }

   boolean isOverrider(Symbol var1) {
      if (var1.kind == 16 && !var1.isStatic()) {
         Symbol.MethodSymbol var2 = (Symbol.MethodSymbol)var1;
         Symbol.TypeSymbol var3 = (Symbol.TypeSymbol)var2.owner;
         Iterator var4 = this.types.closure(var3.type).iterator();

         while(true) {
            Type var5;
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               var5 = (Type)var4.next();
            } while(var5 == var3.type);

            Scope var6 = var5.tsym.members();

            for(Scope.Entry var7 = var6.lookup(var2.name); var7.scope != null; var7 = var7.next()) {
               if (!var7.sym.isStatic() && var2.overrides(var7.sym, var3, this.types, true)) {
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   protected boolean isTypeAnnotation(JCTree.JCAnnotation var1, boolean var2) {
      Attribute.Compound var3 = var1.annotationType.type.tsym.attribute(this.syms.annotationTargetType.tsym);
      if (var3 == null) {
         return false;
      } else {
         Attribute var4 = var3.member(this.names.value);
         if (!(var4 instanceof Attribute.Array)) {
            return false;
         } else {
            Attribute.Array var5 = (Attribute.Array)var4;
            Attribute[] var6 = var5.values;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Attribute var9 = var6[var8];
               if (!(var9 instanceof Attribute.Enum)) {
                  return false;
               }

               Attribute.Enum var10 = (Attribute.Enum)var9;
               if (var10.value.name == this.names.TYPE_USE) {
                  return true;
               }

               if (var2 && var10.value.name == this.names.TYPE_PARAMETER) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   boolean annotationApplicable(JCTree.JCAnnotation var1, Symbol var2) {
      Attribute.Array var3 = this.getAttributeTargetAttribute(var1.annotationType.type.tsym);
      Name[] var4;
      if (var3 == null) {
         var4 = this.defaultTargetMetaInfo(var1, var2);
      } else {
         var4 = new Name[var3.values.length];

         for(int var5 = 0; var5 < var3.values.length; ++var5) {
            Attribute var6 = var3.values[var5];
            if (!(var6 instanceof Attribute.Enum)) {
               return true;
            }

            Attribute.Enum var7 = (Attribute.Enum)var6;
            var4[var5] = var7.value.name;
         }
      }

      Name[] var9 = var4;
      int var10 = var4.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         Name var8 = var9[var11];
         if (var8 == this.names.TYPE) {
            if (var2.kind == 2) {
               return true;
            }
         } else if (var8 == this.names.FIELD) {
            if (var2.kind == 4 && var2.owner.kind != 16) {
               return true;
            }
         } else if (var8 == this.names.METHOD) {
            if (var2.kind == 16 && !var2.isConstructor()) {
               return true;
            }
         } else if (var8 == this.names.PARAMETER) {
            if (var2.kind == 4 && var2.owner.kind == 16 && (var2.flags() & 8589934592L) != 0L) {
               return true;
            }
         } else if (var8 == this.names.CONSTRUCTOR) {
            if (var2.kind == 16 && var2.isConstructor()) {
               return true;
            }
         } else if (var8 == this.names.LOCAL_VARIABLE) {
            if (var2.kind == 4 && var2.owner.kind == 16 && (var2.flags() & 8589934592L) == 0L) {
               return true;
            }
         } else if (var8 == this.names.ANNOTATION_TYPE) {
            if (var2.kind == 2 && (var2.flags() & 8192L) != 0L) {
               return true;
            }
         } else if (var8 == this.names.PACKAGE) {
            if (var2.kind == 1) {
               return true;
            }
         } else if (var8 == this.names.TYPE_USE) {
            if (var2.kind == 2 || var2.kind == 4 || var2.kind == 16 && !var2.isConstructor() && !var2.type.getReturnType().hasTag(TypeTag.VOID) || var2.kind == 16 && var2.isConstructor()) {
               return true;
            }
         } else {
            if (var8 != this.names.TYPE_PARAMETER) {
               return true;
            }

            if (var2.kind == 2 && var2.type.hasTag(TypeTag.TYPEVAR)) {
               return true;
            }
         }
      }

      return false;
   }

   Attribute.Array getAttributeTargetAttribute(Symbol var1) {
      Attribute.Compound var2 = var1.attribute(this.syms.annotationTargetType.tsym);
      if (var2 == null) {
         return null;
      } else {
         Attribute var3 = var2.member(this.names.value);
         return !(var3 instanceof Attribute.Array) ? null : (Attribute.Array)var3;
      }
   }

   private Name[] defaultTargetMetaInfo(JCTree.JCAnnotation var1, Symbol var2) {
      return this.dfltTargetMeta;
   }

   public boolean validateAnnotationDeferErrors(JCTree.JCAnnotation var1) {
      boolean var2 = false;
      Log.DiscardDiagnosticHandler var3 = new Log.DiscardDiagnosticHandler(this.log);

      try {
         var2 = this.validateAnnotation(var1);
      } finally {
         this.log.popDiagnosticHandler(var3);
      }

      return var2;
   }

   private boolean validateAnnotation(JCTree.JCAnnotation var1) {
      boolean var2 = true;
      LinkedHashSet var3 = new LinkedHashSet();

      for(Scope.Entry var4 = var1.annotationType.type.tsym.members().elems; var4 != null; var4 = var4.sibling) {
         if (var4.sym.kind == 16 && var4.sym.name != this.names.clinit && (var4.sym.flags() & 4096L) == 0L) {
            var3.add((Symbol.MethodSymbol)var4.sym);
         }
      }

      Iterator var12 = var1.args.iterator();

      while(var12.hasNext()) {
         JCTree var5 = (JCTree)var12.next();
         if (var5.hasTag(JCTree.Tag.ASSIGN)) {
            JCTree.JCAssign var6 = (JCTree.JCAssign)var5;
            Symbol var7 = TreeInfo.symbol(var6.lhs);
            if (var7 != null && !var7.type.isErroneous() && !var3.remove(var7)) {
               var2 = false;
               this.log.error(var6.lhs.pos(), "duplicate.annotation.member.value", new Object[]{var7.name, var1.type});
            }
         }
      }

      List var13 = List.nil();
      Iterator var14 = var3.iterator();

      while(var14.hasNext()) {
         Symbol.MethodSymbol var17 = (Symbol.MethodSymbol)var14.next();
         if (var17.defaultValue == null && !var17.type.isErroneous()) {
            var13 = var13.append(var17.name);
         }
      }

      var13 = var13.reverse();
      if (var13.nonEmpty()) {
         var2 = false;
         String var15 = var13.size() > 1 ? "annotation.missing.default.value.1" : "annotation.missing.default.value";
         this.log.error(var1.pos(), var15, new Object[]{var1.type, var13});
      }

      if (var1.annotationType.type.tsym == this.syms.annotationTargetType.tsym && var1.args.tail != null) {
         if (!((JCTree.JCExpression)var1.args.head).hasTag(JCTree.Tag.ASSIGN)) {
            return false;
         } else {
            JCTree.JCAssign var16 = (JCTree.JCAssign)var1.args.head;
            Symbol var18 = TreeInfo.symbol(var16.lhs);
            if (var18.name != this.names.value) {
               return false;
            } else {
               JCTree.JCExpression var19 = var16.rhs;
               if (!var19.hasTag(JCTree.Tag.NEWARRAY)) {
                  return false;
               } else {
                  JCTree.JCNewArray var8 = (JCTree.JCNewArray)var19;
                  HashSet var9 = new HashSet();
                  Iterator var10 = var8.elems.iterator();

                  while(var10.hasNext()) {
                     JCTree var11 = (JCTree)var10.next();
                     if (!var9.add(TreeInfo.symbol(var11))) {
                        var2 = false;
                        this.log.error(var11.pos(), "repeated.annotation.target", new Object[0]);
                     }
                  }

                  return var2;
               }
            }
         }
      } else {
         return var2;
      }
   }

   void checkDeprecatedAnnotation(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if (this.allowAnnotations && this.lint.isEnabled(Lint.LintCategory.DEP_ANN) && (var2.flags() & 131072L) != 0L && !this.syms.deprecatedType.isErroneous() && var2.attribute(this.syms.deprecatedType.tsym) == null) {
         this.log.warning(Lint.LintCategory.DEP_ANN, var1, "missing.deprecated.annotation", new Object[0]);
      }

   }

   void checkDeprecated(final JCDiagnostic.DiagnosticPosition var1, Symbol var2, final Symbol var3) {
      if ((var3.flags() & 131072L) != 0L && (var2.flags() & 131072L) == 0L && var3.outermostClass() != var2.outermostClass()) {
         this.deferredLintHandler.report(new DeferredLintHandler.LintLogger() {
            public void report() {
               Check.this.warnDeprecated(var1, var3);
            }
         });
      }

   }

   void checkSunAPI(final JCDiagnostic.DiagnosticPosition var1, final Symbol var2) {
      if ((var2.flags() & 274877906944L) != 0L) {
         this.deferredLintHandler.report(new DeferredLintHandler.LintLogger() {
            public void report() {
               if (Check.this.enableSunApiLintControl) {
                  Check.this.warnSunApi(var1, "sun.proprietary", var2);
               } else {
                  Check.this.log.mandatoryWarning(var1, "sun.proprietary", new Object[]{var2});
               }

            }
         });
      }

   }

   void checkProfile(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if (this.profile != Profile.DEFAULT && (var2.flags() & 35184372088832L) != 0L) {
         this.log.error(var1, "not.in.profile", new Object[]{var2, this.profile});
      }

   }

   void checkNonCyclicElements(JCTree.JCClassDecl var1) {
      if ((var1.sym.flags_field & 8192L) != 0L) {
         Assert.check((var1.sym.flags_field & 134217728L) == 0L);

         Symbol.ClassSymbol var10000;
         try {
            var10000 = var1.sym;
            var10000.flags_field |= 134217728L;
            Iterator var2 = var1.defs.iterator();

            while(var2.hasNext()) {
               JCTree var3 = (JCTree)var2.next();
               if (var3.hasTag(JCTree.Tag.METHODDEF)) {
                  JCTree.JCMethodDecl var4 = (JCTree.JCMethodDecl)var3;
                  this.checkAnnotationResType(var4.pos(), var4.restype.type);
               }
            }
         } finally {
            var10000 = var1.sym;
            var10000.flags_field &= -134217729L;
            var10000 = var1.sym;
            var10000.flags_field |= 34359738368L;
         }

      }
   }

   void checkNonCyclicElementsInternal(JCDiagnostic.DiagnosticPosition var1, Symbol.TypeSymbol var2) {
      if ((var2.flags_field & 34359738368L) == 0L) {
         if ((var2.flags_field & 134217728L) != 0L) {
            this.log.error(var1, "cyclic.annotation.element", new Object[0]);
         } else {
            try {
               var2.flags_field |= 134217728L;

               for(Scope.Entry var3 = var2.members().elems; var3 != null; var3 = var3.sibling) {
                  Symbol var4 = var3.sym;
                  if (var4.kind == 16) {
                     this.checkAnnotationResType(var1, ((Symbol.MethodSymbol)var4).type.getReturnType());
                  }
               }
            } finally {
               var2.flags_field &= -134217729L;
               var2.flags_field |= 34359738368L;
            }

         }
      }
   }

   void checkAnnotationResType(JCDiagnostic.DiagnosticPosition var1, Type var2) {
      switch (var2.getTag()) {
         case CLASS:
            if ((var2.tsym.flags() & 8192L) != 0L) {
               this.checkNonCyclicElementsInternal(var1, var2.tsym);
            }
            break;
         case ARRAY:
            this.checkAnnotationResType(var1, this.types.elemtype(var2));
      }

   }

   void checkCyclicConstructors(JCTree.JCClassDecl var1) {
      HashMap var2 = new HashMap();

      for(List var3 = var1.defs; var3.nonEmpty(); var3 = var3.tail) {
         JCTree.JCMethodInvocation var4 = TreeInfo.firstConstructorCall((JCTree)var3.head);
         if (var4 != null) {
            JCTree.JCMethodDecl var5 = (JCTree.JCMethodDecl)var3.head;
            if (TreeInfo.name(var4.meth) == this.names._this) {
               var2.put(var5.sym, TreeInfo.symbol(var4.meth));
            } else {
               Symbol.MethodSymbol var10000 = var5.sym;
               var10000.flags_field |= 1073741824L;
            }
         }
      }

      Symbol[] var8 = new Symbol[0];
      var8 = (Symbol[])var2.keySet().toArray(var8);
      Symbol[] var9 = var8;
      int var10 = var8.length;

      for(int var6 = 0; var6 < var10; ++var6) {
         Symbol var7 = var9[var6];
         this.checkCyclicConstructor(var1, var7, var2);
      }

   }

   private void checkCyclicConstructor(JCTree.JCClassDecl var1, Symbol var2, Map var3) {
      if (var2 != null && (var2.flags_field & 1073741824L) == 0L) {
         if ((var2.flags_field & 134217728L) != 0L) {
            this.log.error(TreeInfo.diagnosticPositionFor(var2, var1), "recursive.ctor.invocation", new Object[0]);
         } else {
            var2.flags_field |= 134217728L;
            this.checkCyclicConstructor(var1, (Symbol)var3.remove(var2), var3);
            var2.flags_field &= -134217729L;
         }

         var2.flags_field |= 1073741824L;
      }

   }

   int checkOperator(JCDiagnostic.DiagnosticPosition var1, Symbol.OperatorSymbol var2, JCTree.Tag var3, Type var4, Type var5) {
      if (var2.opcode == 277) {
         this.log.error(var1, "operator.cant.be.applied.1", new Object[]{this.treeinfo.operatorName(var3), var4, var5});
      }

      return var2.opcode;
   }

   void checkDivZero(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Type var3) {
      if (var3.constValue() != null && this.lint.isEnabled(Lint.LintCategory.DIVZERO) && var3.getTag().isSubRangeOf(TypeTag.LONG) && ((Number)((Number)var3.constValue())).longValue() == 0L) {
         int var4 = ((Symbol.OperatorSymbol)var2).opcode;
         if (var4 == 108 || var4 == 112 || var4 == 109 || var4 == 113) {
            this.log.warning(Lint.LintCategory.DIVZERO, var1, "div.zero", new Object[0]);
         }
      }

   }

   void checkEmptyIf(JCTree.JCIf var1) {
      if (var1.thenpart.hasTag(JCTree.Tag.SKIP) && var1.elsepart == null && this.lint.isEnabled(Lint.LintCategory.EMPTY)) {
         this.log.warning(Lint.LintCategory.EMPTY, var1.thenpart.pos(), "empty.if", new Object[0]);
      }

   }

   boolean checkUnique(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Scope var3) {
      if (var2.type.isErroneous()) {
         return true;
      } else if (var2.owner.name == this.names.any) {
         return false;
      } else {
         for(Scope.Entry var4 = var3.lookup(var2.name); var4.scope == var3; var4 = var4.next()) {
            if (var2 != var4.sym && (var4.sym.flags() & 4398046511104L) == 0L && var2.kind == var4.sym.kind && var2.name != this.names.error && (var2.kind != 16 || this.types.hasSameArgs(var2.type, var4.sym.type) || this.types.hasSameArgs(this.types.erasure(var2.type), this.types.erasure(var4.sym.type)))) {
               if ((var2.flags() & 17179869184L) != (var4.sym.flags() & 17179869184L)) {
                  this.varargsDuplicateError(var1, var2, var4.sym);
                  return true;
               } else if (var2.kind == 16 && !this.types.hasSameArgs(var2.type, var4.sym.type, false)) {
                  this.duplicateErasureError(var1, var2, var4.sym);
                  var2.flags_field |= 4398046511104L;
                  return true;
               } else {
                  this.duplicateError(var1, var4.sym);
                  return false;
               }
            }
         }

         return true;
      }
   }

   void duplicateErasureError(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Symbol var3) {
      if (!var2.type.isErroneous() && !var3.type.isErroneous()) {
         this.log.error(var1, "name.clash.same.erasure", new Object[]{var2, var3});
      }

   }

   boolean checkUniqueImport(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Scope var3) {
      return this.checkUniqueImport(var1, var2, var3, false);
   }

   boolean checkUniqueStaticImport(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Scope var3) {
      return this.checkUniqueImport(var1, var2, var3, true);
   }

   private boolean checkUniqueImport(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Scope var3, boolean var4) {
      for(Scope.Entry var5 = var3.lookup(var2.name); var5.scope != null; var5 = var5.next()) {
         boolean var6 = var5.scope == var3;
         if ((var6 || var2 != var5.sym) && var2.kind == var5.sym.kind && var2.name != this.names.error && (!var4 || !var5.isStaticallyImported())) {
            if (!var5.sym.type.isErroneous()) {
               if (!var6) {
                  if (var4) {
                     this.log.error(var1, "already.defined.static.single.import", new Object[]{var5.sym});
                  } else {
                     this.log.error(var1, "already.defined.single.import", new Object[]{var5.sym});
                  }
               } else if (var2 != var5.sym) {
                  this.log.error(var1, "already.defined.this.unit", new Object[]{var5.sym});
               }
            }

            return false;
         }
      }

      return true;
   }

   public void checkCanonical(JCTree var1) {
      if (!this.isCanonical(var1)) {
         this.log.error(var1.pos(), "import.requires.canonical", new Object[]{TreeInfo.symbol(var1)});
      }

   }

   private boolean isCanonical(JCTree var1) {
      while(((JCTree)var1).hasTag(JCTree.Tag.SELECT)) {
         JCTree.JCFieldAccess var2 = (JCTree.JCFieldAccess)var1;
         if (var2.sym.owner != TreeInfo.symbol(var2.selected)) {
            return false;
         }

         var1 = var2.selected;
      }

      return true;
   }

   void checkForBadAuxiliaryClassAccess(JCDiagnostic.DiagnosticPosition var1, Env var2, Symbol.ClassSymbol var3) {
      if (this.lint.isEnabled(Lint.LintCategory.AUXILIARYCLASS) && (var3.flags() & 17592186044416L) != 0L && this.rs.isAccessible(var2, (Symbol.TypeSymbol)var3) && !this.fileManager.isSameFile(var3.sourcefile, var2.toplevel.sourcefile)) {
         this.log.warning(var1, "auxiliary.class.accessed.from.outside.of.its.source.file", new Object[]{var3, var3.sourcefile});
      }

   }

   public Warner castWarner(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
      return new ConversionWarner(var1, "unchecked.cast.to.type", var2, var3);
   }

   public Warner convertWarner(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
      return new ConversionWarner(var1, "unchecked.assign", var2, var3);
   }

   public void checkFunctionalInterface(JCTree.JCClassDecl var1, Symbol.ClassSymbol var2) {
      Attribute.Compound var3 = var2.attribute(this.syms.functionalInterfaceType.tsym);
      if (var3 != null) {
         try {
            this.types.findDescriptorSymbol(var2);
         } catch (Types.FunctionDescriptorLookupError var8) {
            JCDiagnostic.DiagnosticPosition var5 = var1.pos();
            Iterator var6 = var1.getModifiers().annotations.iterator();

            while(var6.hasNext()) {
               JCTree.JCAnnotation var7 = (JCTree.JCAnnotation)var6.next();
               if (var7.annotationType.type.tsym == this.syms.functionalInterfaceType.tsym) {
                  var5 = var7.pos();
                  break;
               }
            }

            this.log.error(var5, "bad.functional.intf.anno.1", new Object[]{var8.getDiagnostic()});
         }
      }

   }

   private class ConversionWarner extends Warner {
      final String uncheckedKey;
      final Type found;
      final Type expected;

      public ConversionWarner(JCDiagnostic.DiagnosticPosition var2, String var3, Type var4, Type var5) {
         super(var2);
         this.uncheckedKey = var3;
         this.found = var4;
         this.expected = var5;
      }

      public void warn(Lint.LintCategory var1) {
         boolean var2 = this.warned;
         super.warn(var1);
         if (!var2) {
            switch (var1) {
               case UNCHECKED:
                  Check.this.warnUnchecked(this.pos(), "prob.found.req", Check.this.diags.fragment(this.uncheckedKey), this.found, this.expected);
                  break;
               case VARARGS:
                  if (Check.this.method != null && Check.this.method.attribute(Check.this.syms.trustMeType.tsym) != null && Check.this.isTrustMeAllowedOnMethod(Check.this.method) && !Check.this.types.isReifiable((Type)Check.this.method.type.getParameterTypes().last())) {
                     Check.this.warnUnsafeVararg(this.pos(), "varargs.unsafe.use.varargs.param", Check.this.method.params.last());
                  }
                  break;
               default:
                  throw new AssertionError("Unexpected lint: " + var1);
            }

         }
      }
   }

   private class DefaultMethodClashFilter implements Filter {
      Type site;

      DefaultMethodClashFilter(Type var2) {
         this.site = var2;
      }

      public boolean accepts(Symbol var1) {
         return var1.kind == 16 && (var1.flags() & 8796093022208L) != 0L && var1.isInheritedIn(this.site.tsym, Check.this.types) && !var1.isConstructor();
      }
   }

   private class ClashFilter implements Filter {
      Type site;

      ClashFilter(Type var2) {
         this.site = var2;
      }

      boolean shouldSkip(Symbol var1) {
         return (var1.flags() & 4398046511104L) != 0L && var1.owner == this.site.tsym;
      }

      public boolean accepts(Symbol var1) {
         return var1.kind == 16 && (var1.flags() & 4096L) == 0L && !this.shouldSkip(var1) && var1.isInheritedIn(this.site.tsym, Check.this.types) && !var1.isConstructor();
      }
   }

   class CycleChecker extends TreeScanner {
      List seenClasses = List.nil();
      boolean errorFound = false;
      boolean partialCheck = false;

      private void checkSymbol(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
         if (var2 != null && var2.kind == 2) {
            Env var3 = Check.this.enter.getEnv((Symbol.TypeSymbol)var2);
            if (var3 != null) {
               DiagnosticSource var4 = Check.this.log.currentSource();

               try {
                  Check.this.log.useSource(var3.toplevel.sourcefile);
                  this.scan(var3.tree);
               } finally {
                  Check.this.log.useSource(var4.getFile());
               }
            } else if (var2.kind == 2) {
               this.checkClass(var1, var2, List.nil());
            }
         } else {
            this.partialCheck = true;
         }

      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         super.visitSelect(var1);
         this.checkSymbol(var1.pos(), var1.sym);
      }

      public void visitIdent(JCTree.JCIdent var1) {
         this.checkSymbol(var1.pos(), var1.sym);
      }

      public void visitTypeApply(JCTree.JCTypeApply var1) {
         this.scan(var1.clazz);
      }

      public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
         this.scan(var1.elemtype);
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         List var2 = List.nil();
         if (var1.getExtendsClause() != null) {
            var2 = var2.prepend(var1.getExtendsClause());
         }

         JCTree var4;
         if (var1.getImplementsClause() != null) {
            for(Iterator var3 = var1.getImplementsClause().iterator(); var3.hasNext(); var2 = var2.prepend(var4)) {
               var4 = (JCTree)var3.next();
            }
         }

         this.checkClass(var1.pos(), var1.sym, var2);
      }

      void checkClass(JCDiagnostic.DiagnosticPosition var1, Symbol var2, List var3) {
         if ((var2.flags_field & 1073741824L) == 0L) {
            if (this.seenClasses.contains(var2)) {
               this.errorFound = true;
               Check.this.noteCyclic(var1, (Symbol.ClassSymbol)var2);
            } else if (!var2.type.isErroneous()) {
               try {
                  this.seenClasses = this.seenClasses.prepend(var2);
                  if (!var2.type.hasTag(TypeTag.CLASS)) {
                     return;
                  }

                  if (var3.nonEmpty()) {
                     this.scan(var3);
                  } else {
                     Type.ClassType var4 = (Type.ClassType)var2.type;
                     if (var4.supertype_field == null || var4.interfaces_field == null) {
                        this.partialCheck = true;
                        return;
                     }

                     this.checkSymbol(var1, var4.supertype_field.tsym);
                     Iterator var5 = var4.interfaces_field.iterator();

                     while(var5.hasNext()) {
                        Type var6 = (Type)var5.next();
                        this.checkSymbol(var1, var6.tsym);
                     }
                  }

                  if (var2.owner.kind == 2) {
                     this.checkSymbol(var1, var2.owner);
                  }

                  return;
               } finally {
                  this.seenClasses = this.seenClasses.tail;
               }
            }

         }
      }
   }

   class Validator extends JCTree.Visitor {
      boolean checkRaw;
      boolean isOuter;
      Env env;

      Validator(Env var2) {
         this.env = var2;
      }

      public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
         this.validateTree(var1.elemtype, this.checkRaw, this.isOuter);
      }

      public void visitTypeApply(JCTree.JCTypeApply var1) {
         if (var1.type.hasTag(TypeTag.CLASS)) {
            List var2 = var1.arguments;
            List var3 = var1.type.tsym.type.getTypeArguments();
            Type var4 = Check.this.firstIncompatibleTypeArg(var1.type);
            if (var4 != null) {
               for(Iterator var5 = var1.arguments.iterator(); var5.hasNext(); var3 = var3.tail) {
                  JCTree var6 = (JCTree)var5.next();
                  if (var6.type == var4) {
                     Check.this.log.error(var6, "not.within.bounds", new Object[]{var4, var3.head});
                  }
               }
            }

            var3 = var1.type.tsym.type.getTypeArguments();

            for(boolean var7 = var1.type.tsym.flatName() == Check.this.names.java_lang_Class; var2.nonEmpty() && var3.nonEmpty(); var3 = var3.tail) {
               this.validateTree((JCTree)var2.head, !this.isOuter || !var7, false);
               var2 = var2.tail;
            }

            if (var1.type.getEnclosingType().isRaw()) {
               Check.this.log.error(var1.pos(), "improperly.formed.type.inner.raw.param", new Object[0]);
            }

            if (var1.clazz.hasTag(JCTree.Tag.SELECT)) {
               this.visitSelectInternal((JCTree.JCFieldAccess)var1.clazz);
            }
         }

      }

      public void visitTypeParameter(JCTree.JCTypeParameter var1) {
         this.validateTrees(var1.bounds, true, this.isOuter);
         Check.this.checkClassBounds(var1.pos(), var1.type);
      }

      public void visitWildcard(JCTree.JCWildcard var1) {
         if (var1.inner != null) {
            this.validateTree(var1.inner, true, this.isOuter);
         }

      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         if (var1.type.hasTag(TypeTag.CLASS)) {
            this.visitSelectInternal(var1);
            if (var1.selected.type.isParameterized() && var1.type.tsym.type.getTypeArguments().nonEmpty()) {
               Check.this.log.error(var1.pos(), "improperly.formed.type.param.missing", new Object[0]);
            }
         }

      }

      public void visitSelectInternal(JCTree.JCFieldAccess var1) {
         if (var1.type.tsym.isStatic() && var1.selected.type.isParameterized()) {
            Check.this.log.error(var1.pos(), "cant.select.static.class.from.param.type", new Object[0]);
         } else {
            var1.selected.accept(this);
         }

      }

      public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
         var1.underlyingType.accept(this);
      }

      public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
         if (var1.type.hasTag(TypeTag.VOID)) {
            Check.this.log.error(var1.pos(), "void.not.allowed.here", new Object[0]);
         }

         super.visitTypeIdent(var1);
      }

      public void visitTree(JCTree var1) {
      }

      public void validateTree(JCTree var1, boolean var2, boolean var3) {
         if (var1 != null) {
            boolean var4 = this.checkRaw;
            this.checkRaw = var2;
            this.isOuter = var3;

            try {
               var1.accept(this);
               if (var2) {
                  Check.this.checkRaw(var1, this.env);
               }
            } catch (Symbol.CompletionFailure var9) {
               Check.this.completionError(var1.pos(), var9);
            } finally {
               this.checkRaw = var4;
            }
         }

      }

      public void validateTrees(List var1, boolean var2, boolean var3) {
         for(List var4 = var1; var4.nonEmpty(); var4 = var4.tail) {
            this.validateTree((JCTree)var4.head, var2, var3);
         }

      }
   }

   static class NestedCheckContext implements CheckContext {
      CheckContext enclosingContext;

      NestedCheckContext(CheckContext var1) {
         this.enclosingContext = var1;
      }

      public boolean compatible(Type var1, Type var2, Warner var3) {
         return this.enclosingContext.compatible(var1, var2, var3);
      }

      public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2) {
         this.enclosingContext.report(var1, var2);
      }

      public Warner checkWarner(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
         return this.enclosingContext.checkWarner(var1, var2, var3);
      }

      public Infer.InferenceContext inferenceContext() {
         return this.enclosingContext.inferenceContext();
      }

      public DeferredAttr.DeferredAttrContext deferredAttrContext() {
         return this.enclosingContext.deferredAttrContext();
      }
   }

   public interface CheckContext {
      boolean compatible(Type var1, Type var2, Warner var3);

      void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2);

      Warner checkWarner(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3);

      Infer.InferenceContext inferenceContext();

      DeferredAttr.DeferredAttrContext deferredAttrContext();
   }
}
