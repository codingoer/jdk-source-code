package com.sun.tools.javac.comp;

import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.tools.javac.api.Formattable;
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
import com.sun.tools.javac.jvm.Target;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.DiagnosticSource;
import com.sun.tools.javac.util.FatalError;
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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.lang.model.element.ElementVisitor;

public class Resolve {
   protected static final Context.Key resolveKey = new Context.Key();
   Names names;
   Log log;
   Symtab syms;
   Attr attr;
   DeferredAttr deferredAttr;
   Check chk;
   Infer infer;
   ClassReader reader;
   TreeInfo treeinfo;
   Types types;
   JCDiagnostic.Factory diags;
   public final boolean boxingEnabled;
   public final boolean varargsEnabled;
   public final boolean allowMethodHandles;
   public final boolean allowFunctionalInterfaceMostSpecific;
   public final boolean checkVarargsAccessAfterResolution;
   private final boolean debugResolve;
   private final boolean compactMethodDiags;
   final EnumSet verboseResolutionMode;
   Scope polymorphicSignatureScope;
   private final SymbolNotFoundError varNotFound;
   private final SymbolNotFoundError methodNotFound;
   private final SymbolNotFoundError methodWithCorrectStaticnessNotFound;
   private final SymbolNotFoundError typeNotFound;
   Types.SimpleVisitor accessibilityChecker = new Types.SimpleVisitor() {
      void visit(List var1, Env var2) {
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            this.visit(var4, var2);
         }

      }

      public Void visitType(Type var1, Env var2) {
         return null;
      }

      public Void visitArrayType(Type.ArrayType var1, Env var2) {
         this.visit(var1.elemtype, var2);
         return null;
      }

      public Void visitClassType(Type.ClassType var1, Env var2) {
         this.visit(var1.getTypeArguments(), var2);
         if (!Resolve.this.isAccessible(var2, (Type)var1, true)) {
            Resolve.this.accessBase(Resolve.this.new AccessError(var1.tsym), var2.tree.pos(), var2.enclClass.sym, var1, var1.tsym.name, true);
         }

         return null;
      }

      public Void visitWildcardType(Type.WildcardType var1, Env var2) {
         this.visit(var1.type, var2);
         return null;
      }

      public Void visitMethodType(Type.MethodType var1, Env var2) {
         this.visit(var1.getParameterTypes(), var2);
         this.visit(var1.getReturnType(), var2);
         this.visit(var1.getThrownTypes(), var2);
         return null;
      }
   };
   MethodCheck nilMethodCheck = new MethodCheck() {
      public void argumentsAcceptable(Env var1, DeferredAttr.DeferredAttrContext var2, List var3, List var4, Warner var5) {
      }

      public MethodCheck mostSpecificCheck(List var1, boolean var2) {
         return this;
      }
   };
   MethodCheck arityMethodCheck = new AbstractMethodCheck() {
      void checkArg(JCDiagnostic.DiagnosticPosition var1, boolean var2, Type var3, Type var4, DeferredAttr.DeferredAttrContext var5, Warner var6) {
      }

      public String toString() {
         return "arityMethodCheck";
      }
   };
   MethodCheck resolveMethodCheck = new AbstractMethodCheck() {
      void checkArg(JCDiagnostic.DiagnosticPosition var1, boolean var2, Type var3, Type var4, DeferredAttr.DeferredAttrContext var5, Warner var6) {
         Attr.ResultInfo var7 = this.methodCheckResult(var2, var4, var5, var6);
         var7.check(var1, var3);
      }

      public void argumentsAcceptable(Env var1, DeferredAttr.DeferredAttrContext var2, List var3, List var4, Warner var5) {
         super.argumentsAcceptable(var1, var2, var3, var4, var5);
         if (var2.phase.isVarargsRequired() && (var2.mode == DeferredAttr.AttrMode.CHECK || !Resolve.this.checkVarargsAccessAfterResolution)) {
            this.varargsAccessible(var1, Resolve.this.types.elemtype((Type)var4.last()), var2.inferenceContext);
         }

      }

      private void varargsAccessible(final Env var1, final Type var2, Infer.InferenceContext var3) {
         if (var3.free(var2)) {
            var3.addFreeTypeListener(List.of(var2), new Infer.FreeTypeListener() {
               public void typesInferred(Infer.InferenceContext var1x) {
                  varargsAccessible(var1, var1x.asInstType(var2), var1x);
               }
            });
         } else if (!Resolve.this.isAccessible(var1, Resolve.this.types.erasure(var2))) {
            Symbol.ClassSymbol var4 = var1.enclClass.sym;
            this.reportMC(var1.tree, Resolve.MethodCheckDiag.INACCESSIBLE_VARARGS, var3, new Object[]{var2, Kinds.kindName((Symbol)var4), var4});
         }

      }

      private Attr.ResultInfo methodCheckResult(final boolean var1, Type var2, DeferredAttr.DeferredAttrContext var3, Warner var4) {
         MethodCheckContext var5 = new MethodCheckContext(!var3.phase.isBoxingRequired(), var3, var4) {
            MethodCheckDiag methodDiag;

            {
               this.methodDiag = var1 ? Resolve.MethodCheckDiag.VARARG_MISMATCH : Resolve.MethodCheckDiag.ARG_MISMATCH;
            }

            public void report(JCDiagnostic.DiagnosticPosition var1x, JCDiagnostic var2) {
               reportMC(var1x, this.methodDiag, this.deferredAttrContext.inferenceContext, new Object[]{var2});
            }
         };
         return Resolve.this.new MethodResultInfo(var2, var5);
      }

      public MethodCheck mostSpecificCheck(List var1, boolean var2) {
         return Resolve.this.new MostSpecificCheck(var2, var1);
      }

      public String toString() {
         return "resolveMethodCheck";
      }
   };
   private final InapplicableMethodException inapplicableMethodException;
   Warner noteWarner = new Warner();
   LogResolveHelper basicLogResolveHelper = new LogResolveHelper() {
      public boolean resolveDiagnosticNeeded(Type var1, List var2, List var3) {
         return !var1.isErroneous();
      }

      public List getArgumentTypes(ResolveError var1, Symbol var2, Name var3, List var4) {
         return var4;
      }
   };
   LogResolveHelper methodLogResolveHelper = new LogResolveHelper() {
      public boolean resolveDiagnosticNeeded(Type var1, List var2, List var3) {
         return !var1.isErroneous() && !Type.isErroneous(var2) && (var3 == null || !Type.isErroneous(var3));
      }

      public List getArgumentTypes(ResolveError var1, Symbol var2, Name var3, List var4) {
         return Resolve.this.syms.operatorNames.contains(var3) ? var4 : Type.map(var4, Resolve.this.new ResolveDeferredRecoveryMap(DeferredAttr.AttrMode.SPECULATIVE, var2, Resolve.this.currentResolutionContext.step));
      }
   };
   private final Formattable.LocalizedString noArgs = new Formattable.LocalizedString("compiler.misc.no.args");
   final List methodResolutionSteps;
   MethodResolutionContext currentResolutionContext;

   protected Resolve(Context var1) {
      this.methodResolutionSteps = List.of(Resolve.MethodResolutionPhase.BASIC, Resolve.MethodResolutionPhase.BOX, Resolve.MethodResolutionPhase.VARARITY);
      this.currentResolutionContext = null;
      var1.put((Context.Key)resolveKey, (Object)this);
      this.syms = Symtab.instance(var1);
      this.varNotFound = new SymbolNotFoundError(133);
      this.methodNotFound = new SymbolNotFoundError(136);
      this.methodWithCorrectStaticnessNotFound = new SymbolNotFoundError(138, "method found has incorrect staticness");
      this.typeNotFound = new SymbolNotFoundError(137);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.attr = Attr.instance(var1);
      this.deferredAttr = DeferredAttr.instance(var1);
      this.chk = Check.instance(var1);
      this.infer = Infer.instance(var1);
      this.reader = ClassReader.instance(var1);
      this.treeinfo = TreeInfo.instance(var1);
      this.types = Types.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      Source var2 = Source.instance(var1);
      this.boxingEnabled = var2.allowBoxing();
      this.varargsEnabled = var2.allowVarargs();
      Options var3 = Options.instance(var1);
      this.debugResolve = var3.isSet("debugresolve");
      this.compactMethodDiags = var3.isSet(Option.XDIAGS, "compact") || var3.isUnset(Option.XDIAGS) && var3.isUnset("rawDiagnostics");
      this.verboseResolutionMode = Resolve.VerboseResolutionMode.getVerboseResolutionMode(var3);
      Target var4 = Target.instance(var1);
      this.allowMethodHandles = var4.hasMethodHandles();
      this.allowFunctionalInterfaceMostSpecific = var2.allowFunctionalInterfaceMostSpecific();
      this.checkVarargsAccessAfterResolution = var2.allowPostApplicabilityVarargsAccessCheck();
      this.polymorphicSignatureScope = new Scope(this.syms.noSymbol);
      this.inapplicableMethodException = new InapplicableMethodException(this.diags);
   }

   public static Resolve instance(Context var0) {
      Resolve var1 = (Resolve)var0.get(resolveKey);
      if (var1 == null) {
         var1 = new Resolve(var0);
      }

      return var1;
   }

   void reportVerboseResolutionDiagnostic(JCDiagnostic.DiagnosticPosition var1, Name var2, Type var3, List var4, List var5, Symbol var6) {
      boolean var7 = var6.kind < 128;
      if (!var7 || this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.SUCCESS)) {
         if (var7 || this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.FAILURE)) {
            if (var6.name != this.names.init || var6.owner != this.syms.objectType.tsym || this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.OBJECT_INIT)) {
               if (var3 != this.syms.predefClass.type || this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.PREDEF)) {
                  if (!this.currentResolutionContext.internalResolution || this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.INTERNAL)) {
                     int var8 = 0;
                     int var9 = -1;
                     ListBuffer var10 = new ListBuffer();
                     Iterator var11 = this.currentResolutionContext.candidates.iterator();

                     while(true) {
                        MethodResolutionContext.Candidate var12;
                        do {
                           do {
                              do {
                                 if (!var11.hasNext()) {
                                    String var15 = var7 ? "verbose.resolve.multi" : "verbose.resolve.multi.1";
                                    List var16 = Type.map(var4, this.deferredAttr.new RecoveryDeferredTypeMap(DeferredAttr.AttrMode.SPECULATIVE, var6, this.currentResolutionContext.step));
                                    JCDiagnostic var13 = this.diags.note(this.log.currentSource(), var1, var15, var2, var3.tsym, var9, this.currentResolutionContext.step, this.methodArguments(var16), this.methodArguments(var5));
                                    JCDiagnostic.MultilineDiagnostic var14 = new JCDiagnostic.MultilineDiagnostic(var13, var10.toList());
                                    this.log.report(var14);
                                    return;
                                 }

                                 var12 = (MethodResolutionContext.Candidate)var11.next();
                              } while(this.currentResolutionContext.step != var12.step);
                           } while(var12.isApplicable() && !this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.APPLICABLE));
                        } while(!var12.isApplicable() && !this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.INAPPLICABLE));

                        var10.append(var12.isApplicable() ? this.getVerboseApplicableCandidateDiag(var8, var12.sym, var12.mtype) : this.getVerboseInapplicableCandidateDiag(var8, var12.sym, var12.details));
                        if (var12.sym == var6) {
                           var9 = var8;
                        }

                        ++var8;
                     }
                  }
               }
            }
         }
      }
   }

   JCDiagnostic getVerboseApplicableCandidateDiag(int var1, Symbol var2, Type var3) {
      JCDiagnostic var4 = null;
      if (var2.type.hasTag(TypeTag.FORALL)) {
         var4 = this.diags.fragment("partial.inst.sig", var3);
      }

      String var5 = var4 == null ? "applicable.method.found" : "applicable.method.found.1";
      return this.diags.fragment(var5, var1, var2, var4);
   }

   JCDiagnostic getVerboseInapplicableCandidateDiag(int var1, Symbol var2, JCDiagnostic var3) {
      return this.diags.fragment("not.applicable.method.found", var1, var2, var3);
   }

   protected static boolean isStatic(Env var0) {
      return var0.outer != null && ((AttrContext)var0.info).staticLevel > ((AttrContext)var0.outer.info).staticLevel;
   }

   static boolean isInitializer(Env var0) {
      Symbol var1 = ((AttrContext)var0.info).scope.owner;
      return var1.isConstructor() || var1.owner.kind == 2 && (var1.kind == 4 || var1.kind == 16 && (var1.flags() & 1048576L) != 0L) && (var1.flags() & 8L) == 0L;
   }

   public boolean isAccessible(Env var1, Symbol.TypeSymbol var2) {
      return this.isAccessible(var1, var2, false);
   }

   public boolean isAccessible(Env var1, Symbol.TypeSymbol var2, boolean var3) {
      boolean var4 = false;
      switch ((short)((int)(var2.flags() & 7L))) {
         case 0:
            var4 = var1.toplevel.packge == var2.owner || var1.toplevel.packge == var2.packge() || var1.enclMethod != null && (var1.enclMethod.mods.flags & 536870912L) != 0L;
            break;
         case 1:
         case 3:
         default:
            var4 = true;
            break;
         case 2:
            var4 = var1.enclClass.sym.outermostClass() == var2.owner.outermostClass();
            break;
         case 4:
            var4 = var1.toplevel.packge == var2.owner || var1.toplevel.packge == var2.packge() || this.isInnerSubClass(var1.enclClass.sym, var2.owner);
      }

      return var3 && var2.type.getEnclosingType() != Type.noType ? var4 && this.isAccessible(var1, var2.type.getEnclosingType(), var3) : var4;
   }

   private boolean isInnerSubClass(Symbol.ClassSymbol var1, Symbol var2) {
      while(var1 != null && !var1.isSubClass(var2, this.types)) {
         var1 = var1.owner.enclClass();
      }

      return var1 != null;
   }

   boolean isAccessible(Env var1, Type var2) {
      return this.isAccessible(var1, var2, false);
   }

   boolean isAccessible(Env var1, Type var2, boolean var3) {
      return var2.hasTag(TypeTag.ARRAY) ? this.isAccessible(var1, this.types.cvarUpperBound(this.types.elemtype(var2))) : this.isAccessible(var1, var2.tsym, var3);
   }

   public boolean isAccessible(Env var1, Type var2, Symbol var3) {
      return this.isAccessible(var1, var2, var3, false);
   }

   public boolean isAccessible(Env var1, Type var2, Symbol var3, boolean var4) {
      if (var3.name == this.names.init && var3.owner != var2.tsym) {
         return false;
      } else {
         switch ((short)((int)(var3.flags() & 7L))) {
            case 0:
               return (var1.toplevel.packge == var3.owner.owner || var1.toplevel.packge == var3.packge()) && this.isAccessible(var1, var2, var4) && var3.isInheritedIn(var2.tsym, this.types) && this.notOverriddenIn(var2, var3);
            case 1:
            case 3:
            default:
               return this.isAccessible(var1, var2, var4) && this.notOverriddenIn(var2, var3);
            case 2:
               return (var1.enclClass.sym == var3.owner || var1.enclClass.sym.outermostClass() == var3.owner.outermostClass()) && var3.isInheritedIn(var2.tsym, this.types);
            case 4:
               return (var1.toplevel.packge == var3.owner.owner || var1.toplevel.packge == var3.packge() || this.isProtectedAccessible(var3, var1.enclClass.sym, var2) || ((AttrContext)var1.info).selectSuper && (var3.flags() & 8L) == 0L && var3.kind != 2) && this.isAccessible(var1, var2, var4) && this.notOverriddenIn(var2, var3);
         }
      }
   }

   private boolean notOverriddenIn(Type var1, Symbol var2) {
      if (var2.kind == 16 && !var2.isConstructor() && !var2.isStatic()) {
         Symbol.MethodSymbol var3 = ((Symbol.MethodSymbol)var2).implementation(var1.tsym, this.types, true);
         return var3 == null || var3 == var2 || var2.owner == var3.owner || !this.types.isSubSignature(this.types.memberType(var1, var3), this.types.memberType(var1, var2));
      } else {
         return true;
      }
   }

   private boolean isProtectedAccessible(Symbol var1, Symbol.ClassSymbol var2, Type var3) {
      for(Type var4 = var3.hasTag(TypeTag.TYPEVAR) ? var3.getUpperBound() : var3; var2 != null && (!var2.isSubClass(var1.owner, this.types) || (var2.flags() & 512L) != 0L || (var1.flags() & 8L) == 0L && var1.kind != 2 && !var4.tsym.isSubClass(var2, this.types)); var2 = var2.owner.enclClass()) {
      }

      return var2 != null;
   }

   void checkAccessibleType(Env var1, Type var2) {
      this.accessibilityChecker.visit(var2, var1);
   }

   Type rawInstantiate(Env var1, Type var2, Symbol var3, Attr.ResultInfo var4, List var5, List var6, boolean var7, boolean var8, Warner var9) throws Infer.InferenceException {
      Type var10 = this.types.memberType(var2, var3);
      List var11 = List.nil();
      if (var6 == null) {
         var6 = List.nil();
      }

      List var13;
      if (var10.hasTag(TypeTag.FORALL) || !var6.nonEmpty()) {
         Type.ForAll var12;
         if (var10.hasTag(TypeTag.FORALL) && var6.nonEmpty()) {
            var12 = (Type.ForAll)var10;
            if (var6.length() != var12.tvars.length()) {
               throw this.inapplicableMethodException.setMessage("arg.length.mismatch");
            }

            var13 = var12.tvars;

            for(List var14 = var6; var13.nonEmpty() && var14.nonEmpty(); var14 = var14.tail) {
               for(List var15 = this.types.subst(this.types.getBounds((Type.TypeVar)var13.head), var12.tvars, var6); var15.nonEmpty(); var15 = var15.tail) {
                  if (!this.types.isSubtypeUnchecked((Type)var14.head, (Type)var15.head, var9)) {
                     throw this.inapplicableMethodException.setMessage("explicit.param.do.not.conform.to.bounds", var14.head, var15);
                  }
               }

               var13 = var13.tail;
            }

            var10 = this.types.subst(var12.qtype, var12.tvars, var6);
         } else if (var10.hasTag(TypeTag.FORALL)) {
            var12 = (Type.ForAll)var10;
            var13 = this.types.newInstances(var12.tvars);
            var11 = var11.appendList(var13);
            var10 = this.types.subst(var12.qtype, var12.tvars, var13);
         }
      }

      boolean var16 = var11.tail != null;

      for(var13 = var5; var13.tail != null && !var16; var13 = var13.tail) {
         if (((Type)var13.head).hasTag(TypeTag.FORALL)) {
            var16 = true;
         }
      }

      if (var16) {
         return this.infer.instantiateMethod(var1, var11, (Type.MethodType)var10, var4, (Symbol.MethodSymbol)var3, var5, var7, var8, this.currentResolutionContext, var9);
      } else {
         DeferredAttr.DeferredAttrContext var17 = this.currentResolutionContext.deferredAttrContext(var3, this.infer.emptyContext, var4, var9);
         this.currentResolutionContext.methodCheck.argumentsAcceptable(var1, var17, var5, var10.getParameterTypes(), var9);
         var17.complete();
         return var10;
      }
   }

   Type checkMethod(Env var1, Type var2, Symbol var3, Attr.ResultInfo var4, List var5, List var6, Warner var7) {
      MethodResolutionContext var8 = this.currentResolutionContext;

      Type var10;
      try {
         this.currentResolutionContext = new MethodResolutionContext();
         this.currentResolutionContext.attrMode = DeferredAttr.AttrMode.CHECK;
         if (var1.tree.hasTag(JCTree.Tag.REFERENCE)) {
            this.currentResolutionContext.methodCheck = new MethodReferenceCheck(var4.checkContext.inferenceContext());
         }

         MethodResolutionPhase var9 = this.currentResolutionContext.step = ((AttrContext)var1.info).pendingResolutionPhase;
         var10 = this.rawInstantiate(var1, var2, var3, var4, var5, var6, var9.isBoxingRequired(), var9.isVarargsRequired(), var7);
      } finally {
         this.currentResolutionContext = var8;
      }

      return var10;
   }

   Type instantiate(Env var1, Type var2, Symbol var3, Attr.ResultInfo var4, List var5, List var6, boolean var7, boolean var8, Warner var9) {
      try {
         return this.rawInstantiate(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } catch (InapplicableMethodException var11) {
         return null;
      }
   }

   List dummyArgs(int var1) {
      ListBuffer var2 = new ListBuffer();

      for(int var3 = 0; var3 < var1; ++var3) {
         var2.append(Type.noType);
      }

      return var2.toList();
   }

   Symbol findField(Env var1, Type var2, Name var3, Symbol.TypeSymbol var4) {
      while(var4.type.hasTag(TypeTag.TYPEVAR)) {
         var4 = var4.type.getUpperBound().tsym;
      }

      Object var5 = this.varNotFound;

      for(Scope.Entry var7 = var4.members().lookup(var3); var7.scope != null; var7 = var7.next()) {
         if (var7.sym.kind == 4 && (var7.sym.flags_field & 4096L) == 0L) {
            return (Symbol)(this.isAccessible(var1, var2, var7.sym) ? var7.sym : new AccessError(var1, var2, var7.sym));
         }
      }

      Type var8 = this.types.supertype(var4.type);
      Symbol var6;
      if (var8 != null && (var8.hasTag(TypeTag.CLASS) || var8.hasTag(TypeTag.TYPEVAR))) {
         var6 = this.findField(var1, var2, var3, var8.tsym);
         if (var6.kind < ((Symbol)var5).kind) {
            var5 = var6;
         }
      }

      for(List var9 = this.types.interfaces(var4.type); ((Symbol)var5).kind != 129 && var9.nonEmpty(); var9 = var9.tail) {
         var6 = this.findField(var1, var2, var3, ((Type)var9.head).tsym);
         if (((Symbol)var5).exists() && var6.exists() && var6.owner != ((Symbol)var5).owner) {
            var5 = new AmbiguityError((Symbol)var5, var6);
         } else if (var6.kind < ((Symbol)var5).kind) {
            var5 = var6;
         }
      }

      return (Symbol)var5;
   }

   public Symbol.VarSymbol resolveInternalField(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, Name var4) {
      Symbol var5 = this.findField(var2, var3, var4, var3.tsym);
      if (var5.kind == 4) {
         return (Symbol.VarSymbol)var5;
      } else {
         throw new FatalError(this.diags.fragment("fatal.err.cant.locate.field", var4));
      }
   }

   Symbol findVar(Env var1, Name var2) {
      Object var3 = this.varNotFound;
      Env var5 = var1;

      Symbol var4;
      for(boolean var6 = false; var5.outer != null; var5 = var5.outer) {
         if (isStatic(var5)) {
            var6 = true;
         }

         Scope.Entry var7;
         for(var7 = ((AttrContext)var5.info).scope.lookup(var2); var7.scope != null && (var7.sym.kind != 4 || (var7.sym.flags_field & 4096L) != 0L); var7 = var7.next()) {
         }

         var4 = var7.scope != null ? var7.sym : this.findField(var5, var5.enclClass.sym.type, var2, var5.enclClass.sym);
         if (var4.exists()) {
            if (var6 && var4.kind == 4 && var4.owner.kind == 2 && (var4.flags() & 8L) == 0L) {
               return new StaticError(var4);
            }

            return var4;
         }

         if (var4.kind < ((Symbol)var3).kind) {
            var3 = var4;
         }

         if ((var5.enclClass.sym.flags() & 8L) != 0L) {
            var6 = true;
         }
      }

      var4 = this.findField(var1, this.syms.predefClass.type, var2, this.syms.predefClass);
      if (var4.exists()) {
         return var4;
      } else if (((Symbol)var3).exists()) {
         return (Symbol)var3;
      } else {
         Symbol var13 = null;
         Scope[] var8 = new Scope[]{var1.toplevel.namedImportScope, var1.toplevel.starImportScope};
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Scope var11 = var8[var10];

            for(Scope.Entry var12 = var11.lookup(var2); var12.scope != null; var12 = var12.next()) {
               var4 = var12.sym;
               if (var4.kind == 4) {
                  if (((Symbol)var3).kind < 129 && var4.owner != ((Symbol)var3).owner) {
                     return new AmbiguityError((Symbol)var3, var4);
                  }

                  if (((Symbol)var3).kind >= 4) {
                     var13 = var12.getOrigin().owner;
                     var3 = this.isAccessible(var1, var13.type, var4) ? var4 : new AccessError(var1, var13.type, var4);
                  }
               }
            }

            if (((Symbol)var3).exists()) {
               break;
            }
         }

         if (((Symbol)var3).kind == 4 && ((Symbol)var3).owner.type != var13.type) {
            return ((Symbol)var3).clone(var13);
         } else {
            return (Symbol)var3;
         }
      }
   }

   Symbol selectBest(Env var1, Type var2, List var3, List var4, Symbol var5, Symbol var6, boolean var7, boolean var8, boolean var9) {
      if (var5.kind != 63 && var5.isInheritedIn(var2.tsym, this.types)) {
         if (var8 && (var5.flags() & 17179869184L) == 0L) {
            return (Symbol)(((Symbol)var6).kind >= 128 ? new BadVarargsMethod((ResolveError)((Symbol)var6).baseSymbol()) : var6);
         } else {
            Assert.check(var5.kind < 129);

            try {
               Type var10 = this.rawInstantiate(var1, var2, var5, (Attr.ResultInfo)null, var3, var4, var7, var8, this.types.noWarnings);
               if (!var9 || this.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.PREDEF)) {
                  this.currentResolutionContext.addApplicableCandidate(var5, var10);
               }
            } catch (InapplicableMethodException var11) {
               if (!var9) {
                  this.currentResolutionContext.addInapplicableCandidate(var5, var11.getDiagnostic());
               }

               switch (((Symbol)var6).kind) {
                  case 135:
                     if (var9) {
                        return (Symbol)var6;
                     }

                     var6 = new InapplicableSymbolsError(this.currentResolutionContext);
                  default:
                     return (Symbol)var6;
                  case 136:
                     return new InapplicableSymbolError(this.currentResolutionContext);
               }
            }

            if (!this.isAccessible(var1, var2, var5)) {
               return (Symbol)(((Symbol)var6).kind == 136 ? new AccessError(var1, var2, var5) : var6);
            } else {
               return ((Symbol)var6).kind > 129 ? var5 : this.mostSpecific(var3, var5, (Symbol)var6, var1, var2, var7 && var9, var8);
            }
         }
      } else {
         return (Symbol)var6;
      }
   }

   Symbol mostSpecific(List var1, Symbol var2, Symbol var3, Env var4, Type var5, boolean var6, boolean var7) {
      switch (var3.kind) {
         case 16:
            if (var2 == var3) {
               return var2;
            } else {
               boolean var8 = this.signatureMoreSpecific(var1, var4, var5, var2, var3, var6, var7);
               boolean var9 = this.signatureMoreSpecific(var1, var4, var5, var3, var2, var6, var7);
               if (var8 && var9) {
                  Type var16 = this.types.memberType(var5, var2);
                  Type var17 = this.types.memberType(var5, var3);
                  if (!this.types.overrideEquivalent(var16, var17)) {
                     return this.ambiguityError(var2, var3);
                  } else if ((var2.flags() & 2147483648L) != (var3.flags() & 2147483648L)) {
                     return (var2.flags() & 2147483648L) != 0L ? var3 : var2;
                  } else {
                     Symbol.TypeSymbol var18 = (Symbol.TypeSymbol)var2.owner;
                     Symbol.TypeSymbol var19 = (Symbol.TypeSymbol)var3.owner;
                     if (this.types.asSuper(var18.type, var19) != null && ((var2.owner.flags_field & 512L) == 0L || (var3.owner.flags_field & 512L) != 0L) && var2.overrides(var3, var18, this.types, false)) {
                        return var2;
                     } else if (this.types.asSuper(var19.type, var18) != null && ((var3.owner.flags_field & 512L) == 0L || (var2.owner.flags_field & 512L) != 0L) && var3.overrides(var2, var19, this.types, false)) {
                        return var3;
                     } else {
                        boolean var20 = (var2.flags() & 1024L) != 0L;
                        boolean var21 = (var3.flags() & 1024L) != 0L;
                        if (var20 && !var21) {
                           return var3;
                        } else {
                           if (var21 && !var20) {
                              return var2;
                           }

                           return this.ambiguityError(var2, var3);
                        }
                     }
                  }
               } else if (var8) {
                  return var2;
               } else {
                  if (var9) {
                     return var3;
                  }

                  return this.ambiguityError(var2, var3);
               }
            }
         case 129:
            AmbiguityError var10 = (AmbiguityError)var3.baseSymbol();
            boolean var11 = true;
            boolean var12 = true;

            Symbol var14;
            Symbol var15;
            for(Iterator var13 = var10.ambiguousSyms.iterator(); var13.hasNext(); var12 &= var15 == var14) {
               var14 = (Symbol)var13.next();
               var15 = this.mostSpecific(var1, var2, var14, var4, var5, var6, var7);
               var11 &= var15 == var2;
            }

            if (var11) {
               return var2;
            }

            if (!var12) {
               var10.addAmbiguousSymbol(var2);
            }

            return var10;
         default:
            throw new AssertionError();
      }
   }

   private boolean signatureMoreSpecific(List var1, Env var2, Type var3, Symbol var4, Symbol var5, boolean var6, boolean var7) {
      this.noteWarner.clear();
      int var8 = Math.max(Math.max(var4.type.getParameterTypes().length(), var1.length()), var5.type.getParameterTypes().length());
      MethodResolutionContext var9 = this.currentResolutionContext;

      boolean var11;
      try {
         this.currentResolutionContext = new MethodResolutionContext();
         this.currentResolutionContext.step = var9.step;
         this.currentResolutionContext.methodCheck = var9.methodCheck.mostSpecificCheck(var1, !var6);
         Type var10 = this.instantiate(var2, var3, var5, (Attr.ResultInfo)null, this.adjustArgs(this.types.cvarLowerBounds(this.types.memberType(var3, var4).getParameterTypes()), var4, var8, var7), (List)null, var6, var7, this.noteWarner);
         var11 = var10 != null && !this.noteWarner.hasLint(Lint.LintCategory.UNCHECKED);
      } finally {
         this.currentResolutionContext = var9;
      }

      return var11;
   }

   List adjustArgs(List var1, Symbol var2, int var3, boolean var4) {
      if ((var2.flags() & 17179869184L) != 0L && var4) {
         Type var5 = this.types.elemtype((Type)var1.last());
         if (var5 == null) {
            Assert.error("Bad varargs = " + var1.last() + " " + var2);
         }

         List var6;
         for(var6 = var1.reverse().tail.prepend(var5).reverse(); var6.length() < var3; var6 = var6.append(var6.last())) {
         }

         return var6;
      } else {
         return var1;
      }
   }

   Type mostSpecificReturnType(Type var1, Type var2) {
      Type var3 = var1.getReturnType();
      Type var4 = var2.getReturnType();
      if (var1.hasTag(TypeTag.FORALL) && var2.hasTag(TypeTag.FORALL)) {
         var3 = this.types.subst(var3, var1.getTypeArguments(), var2.getTypeArguments());
      }

      if (this.types.isSubtype(var3, var4)) {
         return var1;
      } else if (this.types.isSubtype(var4, var3)) {
         return var2;
      } else if (this.types.returnTypeSubstitutable(var1, var2)) {
         return var1;
      } else {
         return this.types.returnTypeSubstitutable(var2, var1) ? var2 : null;
      }
   }

   Symbol ambiguityError(Symbol var1, Symbol var2) {
      if (((var1.flags() | var2.flags()) & 4398046511104L) != 0L) {
         return (var1.flags() & 4398046511104L) == 0L ? var1 : var2;
      } else {
         return new AmbiguityError(var1, var2);
      }
   }

   Symbol findMethodInScope(Env var1, Type var2, Name var3, List var4, List var5, Scope var6, Symbol var7, boolean var8, boolean var9, boolean var10, boolean var11) {
      Symbol var13;
      for(Iterator var12 = var6.getElementsByName(var3, new LookupFilter(var11)).iterator(); var12.hasNext(); var7 = this.selectBest(var1, var2, var4, var5, var13, var7, var8, var9, var10)) {
         var13 = (Symbol)var12.next();
      }

      return var7;
   }

   Symbol findMethod(Env var1, Type var2, Name var3, List var4, List var5, boolean var6, boolean var7, boolean var8) {
      SymbolNotFoundError var9 = this.methodNotFound;
      Symbol var10 = this.findMethod(var1, var2, var3, var4, var5, var2.tsym.type, var9, var6, var7, var8);
      return var10;
   }

   private Symbol findMethod(Env var1, Type var2, Name var3, List var4, List var5, Type var6, Symbol var7, boolean var8, boolean var9, boolean var10) {
      List[] var11 = (List[])(new List[]{List.nil(), List.nil()});
      InterfaceLookupPhase var12 = Resolve.InterfaceLookupPhase.ABSTRACT_OK;
      Iterator var13 = this.superclasses(var6).iterator();

      while(var13.hasNext()) {
         Symbol.TypeSymbol var14 = (Symbol.TypeSymbol)var13.next();
         var7 = this.findMethodInScope(var1, var2, var3, var4, var5, var14.members(), (Symbol)var7, var8, var9, var10, true);
         if (var3 == this.names.init) {
            return (Symbol)var7;
         }

         var12 = var12 == null ? null : var12.update(var14, this);
         Type var16;
         if (var12 != null) {
            for(Iterator var15 = this.types.interfaces(var14.type).iterator(); var15.hasNext(); var11[var12.ordinal()] = this.types.union(this.types.closure(var16), var11[var12.ordinal()])) {
               var16 = (Type)var15.next();
            }
         }
      }

      Object var20 = ((Symbol)var7).kind < 63 && (((Symbol)var7).flags() & 1024L) == 0L ? var7 : this.methodNotFound;
      InterfaceLookupPhase[] var21 = Resolve.InterfaceLookupPhase.values();
      int var22 = var21.length;

      label60:
      for(int var23 = 0; var23 < var22; ++var23) {
         InterfaceLookupPhase var17 = var21[var23];
         Iterator var18 = var11[var17.ordinal()].iterator();

         while(true) {
            Type var19;
            do {
               do {
                  if (!var18.hasNext()) {
                     continue label60;
                  }

                  var19 = (Type)var18.next();
               } while(!var19.isInterface());
            } while(var17 == Resolve.InterfaceLookupPhase.DEFAULT_OK && (var19.tsym.flags() & 8796093022208L) == 0L);

            var7 = this.findMethodInScope(var1, var2, var3, var4, var5, var19.tsym.members(), (Symbol)var7, var8, var9, var10, true);
            if (var20 != var7 && ((Symbol)var20).kind < 63 && ((Symbol)var7).kind < 63 && this.types.isSubSignature(((Symbol)var20).type, ((Symbol)var7).type)) {
               var7 = var20;
            }
         }
      }

      return (Symbol)var7;
   }

   Iterable superclasses(final Type var1) {
      return new Iterable() {
         public Iterator iterator() {
            return new Iterator() {
               List seen = List.nil();
               Symbol.TypeSymbol currentSym = this.symbolFor(var1);
               Symbol.TypeSymbol prevSym = null;

               public boolean hasNext() {
                  if (this.currentSym == Resolve.this.syms.noSymbol) {
                     this.currentSym = this.symbolFor(Resolve.this.types.supertype(this.prevSym.type));
                  }

                  return this.currentSym != null;
               }

               public Symbol.TypeSymbol next() {
                  this.prevSym = this.currentSym;
                  this.currentSym = Resolve.this.syms.noSymbol;
                  Assert.check(this.prevSym != null || this.prevSym != Resolve.this.syms.noSymbol);
                  return this.prevSym;
               }

               public void remove() {
                  throw new UnsupportedOperationException();
               }

               Symbol.TypeSymbol symbolFor(Type var1x) {
                  if (!var1x.hasTag(TypeTag.CLASS) && !var1x.hasTag(TypeTag.TYPEVAR)) {
                     return null;
                  } else {
                     while(var1x.hasTag(TypeTag.TYPEVAR)) {
                        var1x = var1x.getUpperBound();
                     }

                     if (this.seen.contains(var1x.tsym)) {
                        return null;
                     } else {
                        this.seen = this.seen.prepend(var1x.tsym);
                        return var1x.tsym;
                     }
                  }
               }
            };
         }
      };
   }

   Symbol findFun(Env var1, Name var2, List var3, List var4, boolean var5, boolean var6) {
      Object var7 = this.methodNotFound;
      Env var9 = var1;

      Symbol var8;
      for(boolean var10 = false; var9.outer != null; var9 = var9.outer) {
         if (isStatic(var9)) {
            var10 = true;
         }

         Assert.check(((AttrContext)var9.info).preferredTreeForDiagnostics == null);
         ((AttrContext)var9.info).preferredTreeForDiagnostics = var1.tree;

         try {
            var8 = this.findMethod(var9, var9.enclClass.sym.type, var2, var3, var4, var5, var6, false);
            if (var8.exists()) {
               if (var10 && var8.kind == 16 && var8.owner.kind == 2 && (var8.flags() & 8L) == 0L) {
                  StaticError var16 = new StaticError(var8);
                  return var16;
               }

               Symbol var11 = var8;
               return var11;
            }

            if (var8.kind < ((Symbol)var7).kind) {
               var7 = var8;
            }
         } finally {
            ((AttrContext)var9.info).preferredTreeForDiagnostics = null;
         }

         if ((var9.enclClass.sym.flags() & 8L) != 0L) {
            var10 = true;
         }
      }

      var8 = this.findMethod(var1, this.syms.predefClass.type, var2, var3, var4, var5, var6, false);
      if (var8.exists()) {
         return var8;
      } else {
         Type var12;
         Object var15;
         Scope.Entry var17;
         for(var17 = var1.toplevel.namedImportScope.lookup(var2); var17.scope != null; var17 = var17.next()) {
            var15 = var17.sym;
            var12 = var17.getOrigin().owner.type;
            if (((Symbol)var15).kind == 16) {
               if (var17.sym.owner.type != var12) {
                  var15 = ((Symbol)var15).clone(var17.getOrigin().owner);
               }

               if (!this.isAccessible(var1, var12, (Symbol)var15)) {
                  var15 = new AccessError(var1, var12, (Symbol)var15);
               }

               var7 = this.selectBest(var1, var12, var3, var4, (Symbol)var15, (Symbol)var7, var5, var6, false);
            }
         }

         if (((Symbol)var7).exists()) {
            return (Symbol)var7;
         } else {
            for(var17 = var1.toplevel.starImportScope.lookup(var2); var17.scope != null; var17 = var17.next()) {
               var15 = var17.sym;
               var12 = var17.getOrigin().owner.type;
               if (((Symbol)var15).kind == 16) {
                  if (var17.sym.owner.type != var12) {
                     var15 = ((Symbol)var15).clone(var17.getOrigin().owner);
                  }

                  if (!this.isAccessible(var1, var12, (Symbol)var15)) {
                     var15 = new AccessError(var1, var12, (Symbol)var15);
                  }

                  var7 = this.selectBest(var1, var12, var3, var4, (Symbol)var15, (Symbol)var7, var5, var6, false);
               }
            }

            return (Symbol)var7;
         }
      }
   }

   Symbol loadClass(Env var1, Name var2) {
      try {
         Symbol.ClassSymbol var3 = this.reader.loadClass(var2);
         return (Symbol)(this.isAccessible(var1, (Symbol.TypeSymbol)var3) ? var3 : new AccessError(var3));
      } catch (ClassReader.BadClassFile var4) {
         throw var4;
      } catch (Symbol.CompletionFailure var5) {
         return this.typeNotFound;
      }
   }

   Symbol findImmediateMemberType(Env var1, Type var2, Name var3, Symbol.TypeSymbol var4) {
      for(Scope.Entry var5 = var4.members().lookup(var3); var5.scope != null; var5 = var5.next()) {
         if (var5.sym.kind == 2) {
            return (Symbol)(this.isAccessible(var1, var2, var5.sym) ? var5.sym : new AccessError(var1, var2, var5.sym));
         }
      }

      return this.typeNotFound;
   }

   Symbol findInheritedMemberType(Env var1, Type var2, Name var3, Symbol.TypeSymbol var4) {
      Object var5 = this.typeNotFound;
      Type var7 = this.types.supertype(var4.type);
      Symbol var6;
      if (var7 != null && var7.hasTag(TypeTag.CLASS)) {
         var6 = this.findMemberType(var1, var2, var3, var7.tsym);
         if (var6.kind < ((Symbol)var5).kind) {
            var5 = var6;
         }
      }

      for(List var8 = this.types.interfaces(var4.type); ((Symbol)var5).kind != 129 && var8.nonEmpty(); var8 = var8.tail) {
         var6 = this.findMemberType(var1, var2, var3, ((Type)var8.head).tsym);
         if (((Symbol)var5).kind < 129 && var6.kind < 129 && var6.owner != ((Symbol)var5).owner) {
            var5 = new AmbiguityError((Symbol)var5, var6);
         } else if (var6.kind < ((Symbol)var5).kind) {
            var5 = var6;
         }
      }

      return (Symbol)var5;
   }

   Symbol findMemberType(Env var1, Type var2, Name var3, Symbol.TypeSymbol var4) {
      Symbol var5 = this.findImmediateMemberType(var1, var2, var3, var4);
      return var5 != this.typeNotFound ? var5 : this.findInheritedMemberType(var1, var2, var3, var4);
   }

   Symbol findGlobalType(Env var1, Scope var2, Name var3) {
      Object var4 = this.typeNotFound;

      for(Scope.Entry var5 = var2.lookup(var3); var5.scope != null; var5 = var5.next()) {
         Symbol var6 = this.loadClass(var1, var5.sym.flatName());
         if (((Symbol)var4).kind == 2 && var6.kind == 2 && var4 != var6) {
            return new AmbiguityError((Symbol)var4, var6);
         }

         if (var6.kind < ((Symbol)var4).kind) {
            var4 = var6;
         }
      }

      return (Symbol)var4;
   }

   Symbol findTypeVar(Env var1, Name var2, boolean var3) {
      for(Scope.Entry var4 = ((AttrContext)var1.info).scope.lookup(var2); var4.scope != null; var4 = var4.next()) {
         if (var4.sym.kind == 2) {
            if (var3 && var4.sym.type.hasTag(TypeTag.TYPEVAR) && var4.sym.owner.kind == 2) {
               return new StaticError(var4.sym);
            }

            return var4.sym;
         }
      }

      return this.typeNotFound;
   }

   Symbol findType(Env var1, Name var2) {
      Object var3 = this.typeNotFound;
      boolean var5 = false;

      Symbol var4;
      for(Env var6 = var1; var6.outer != null; var6 = var6.outer) {
         if (isStatic(var6)) {
            var5 = true;
         }

         Symbol var7 = this.findTypeVar(var6, var2, var5);
         var4 = this.findImmediateMemberType(var6, var6.enclClass.sym.type, var2, var6.enclClass.sym);
         if (var7 != this.typeNotFound && (var4 == this.typeNotFound || var7.kind == 2 && var7.exists() && var7.owner.kind == 16)) {
            return var7;
         }

         if (var4 == this.typeNotFound) {
            var4 = this.findInheritedMemberType(var6, var6.enclClass.sym.type, var2, var6.enclClass.sym);
         }

         if (var5 && var4.kind == 2 && var4.type.hasTag(TypeTag.CLASS) && var4.type.getEnclosingType().hasTag(TypeTag.CLASS) && var6.enclClass.sym.type.isParameterized() && var4.type.getEnclosingType().isParameterized()) {
            return new StaticError(var4);
         }

         if (var4.exists()) {
            return var4;
         }

         if (var4.kind < ((Symbol)var3).kind) {
            var3 = var4;
         }

         JCTree.JCClassDecl var8 = var6.baseClause ? (JCTree.JCClassDecl)var6.tree : var6.enclClass;
         if ((var8.sym.flags() & 8L) != 0L) {
            var5 = true;
         }
      }

      if (!var1.tree.hasTag(JCTree.Tag.IMPORT)) {
         var4 = this.findGlobalType(var1, var1.toplevel.namedImportScope, var2);
         if (var4.exists()) {
            return var4;
         }

         if (var4.kind < ((Symbol)var3).kind) {
            var3 = var4;
         }

         var4 = this.findGlobalType(var1, var1.toplevel.packge.members(), var2);
         if (var4.exists()) {
            return var4;
         }

         if (var4.kind < ((Symbol)var3).kind) {
            var3 = var4;
         }

         var4 = this.findGlobalType(var1, var1.toplevel.starImportScope, var2);
         if (var4.exists()) {
            return var4;
         }

         if (var4.kind < ((Symbol)var3).kind) {
            var3 = var4;
         }
      }

      return (Symbol)var3;
   }

   Symbol findIdent(Env var1, Name var2, int var3) {
      Object var4 = this.typeNotFound;
      Symbol var5;
      if ((var3 & 4) != 0) {
         var5 = this.findVar(var1, var2);
         if (var5.exists()) {
            return var5;
         }

         if (var5.kind < ((Symbol)var4).kind) {
            var4 = var5;
         }
      }

      if ((var3 & 2) != 0) {
         var5 = this.findType(var1, var2);
         if (var5.kind == 2) {
            this.reportDependence(var1.enclClass.sym, var5);
         }

         if (var5.exists()) {
            return var5;
         }

         if (var5.kind < ((Symbol)var4).kind) {
            var4 = var5;
         }
      }

      return (Symbol)((var3 & 1) != 0 ? this.reader.enterPackage(var2) : var4);
   }

   public void reportDependence(Symbol var1, Symbol var2) {
   }

   Symbol findIdentInPackage(Env var1, Symbol.TypeSymbol var2, Name var3, int var4) {
      Name var5 = Symbol.TypeSymbol.formFullName(var3, var2);
      Object var6 = this.typeNotFound;
      Symbol.PackageSymbol var7 = null;
      if ((var4 & 1) != 0) {
         var7 = this.reader.enterPackage(var5);
         if (var7.exists()) {
            return var7;
         }
      }

      if ((var4 & 2) != 0) {
         Symbol var8 = this.loadClass(var1, var5);
         if (var8.exists()) {
            if (var3 == var8.name) {
               return var8;
            }
         } else if (var8.kind < ((Symbol)var6).kind) {
            var6 = var8;
         }
      }

      return (Symbol)(var7 != null ? var7 : var6);
   }

   Symbol findIdentInType(Env var1, Type var2, Name var3, int var4) {
      Object var5 = this.typeNotFound;
      Symbol var6;
      if ((var4 & 4) != 0) {
         var6 = this.findField(var1, var2, var3, var2.tsym);
         if (var6.exists()) {
            return var6;
         }

         if (var6.kind < ((Symbol)var5).kind) {
            var5 = var6;
         }
      }

      if ((var4 & 2) != 0) {
         var6 = this.findMemberType(var1, var2, var3, var2.tsym);
         if (var6.exists()) {
            return var6;
         }

         if (var6.kind < ((Symbol)var5).kind) {
            var5 = var6;
         }
      }

      return (Symbol)var5;
   }

   Symbol accessInternal(Symbol var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, boolean var6, List var7, List var8, LogResolveHelper var9) {
      if (var1.kind >= 129) {
         ResolveError var10 = (ResolveError)var1.baseSymbol();
         var1 = var10.access(var5, var6 ? var4.tsym : this.syms.noSymbol);
         var7 = var9.getArgumentTypes(var10, var1, var5, var7);
         if (var9.resolveDiagnosticNeeded(var4, var7, var8)) {
            this.logResolveError(var10, var2, var3, var4, var5, var7, var8);
         }
      }

      return var1;
   }

   Symbol accessMethod(Symbol var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, boolean var6, List var7, List var8) {
      return this.accessInternal(var1, var2, var3, var4, var5, var6, var7, var8, this.methodLogResolveHelper);
   }

   Symbol accessMethod(Symbol var1, JCDiagnostic.DiagnosticPosition var2, Type var3, Name var4, boolean var5, List var6, List var7) {
      return this.accessMethod(var1, var2, var3.tsym, var3, var4, var5, var6, var7);
   }

   Symbol accessBase(Symbol var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, boolean var6) {
      return this.accessInternal(var1, var2, var3, var4, var5, var6, List.nil(), (List)null, this.basicLogResolveHelper);
   }

   Symbol accessBase(Symbol var1, JCDiagnostic.DiagnosticPosition var2, Type var3, Name var4, boolean var5) {
      return this.accessBase(var1, var2, var3.tsym, var3, var4, var5);
   }

   void checkNonAbstract(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      if ((var2.flags() & 1024L) != 0L && (var2.flags() & 8796093022208L) == 0L) {
         this.log.error(var1, "abstract.cant.be.accessed.directly", new Object[]{Kinds.kindName(var2), var2, var2.location()});
      }

   }

   public void printscopes(Scope var1) {
      while(var1 != null) {
         if (var1.owner != null) {
            System.err.print(var1.owner + ": ");
         }

         for(Scope.Entry var2 = var1.elems; var2 != null; var2 = var2.sibling) {
            if ((var2.sym.flags() & 1024L) != 0L) {
               System.err.print("abstract ");
            }

            System.err.print(var2.sym + " ");
         }

         System.err.println();
         var1 = var1.next;
      }

   }

   void printscopes(Env var1) {
      while(var1.outer != null) {
         System.err.println("------------------------------");
         this.printscopes(((AttrContext)var1.info).scope);
         var1 = var1.outer;
      }

   }

   public void printscopes(Type var1) {
      while(var1.hasTag(TypeTag.CLASS)) {
         this.printscopes(var1.tsym.members());
         var1 = this.types.supertype(var1);
      }

   }

   Symbol resolveIdent(JCDiagnostic.DiagnosticPosition var1, Env var2, Name var3, int var4) {
      return this.accessBase(this.findIdent(var2, var3, var4), var1, var2.enclClass.sym.type, var3, false);
   }

   Symbol resolveMethod(JCDiagnostic.DiagnosticPosition var1, Env var2, Name var3, List var4, List var5) {
      return this.lookupMethod(var2, var1, var2.enclClass.sym, (MethodCheck)this.resolveMethodCheck, new BasicLookupHelper(var3, var2.enclClass.sym.type, var4, var5) {
         Symbol doLookup(Env var1, MethodResolutionPhase var2) {
            return Resolve.this.findFun(var1, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired());
         }
      });
   }

   Symbol resolveQualifiedMethod(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, Name var4, List var5, List var6) {
      return this.resolveQualifiedMethod(var1, var2, var3.tsym, var3, var4, var5, var6);
   }

   Symbol resolveQualifiedMethod(JCDiagnostic.DiagnosticPosition var1, Env var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
      return this.resolveQualifiedMethod(new MethodResolutionContext(), var1, var2, var3, var4, var5, var6, var7);
   }

   private Symbol resolveQualifiedMethod(MethodResolutionContext var1, JCDiagnostic.DiagnosticPosition var2, Env var3, Symbol var4, Type var5, Name var6, List var7, List var8) {
      return this.lookupMethod(var3, var2, var4, (MethodResolutionContext)var1, new BasicLookupHelper(var6, var5, var7, var8) {
         Symbol doLookup(Env var1, MethodResolutionPhase var2) {
            return Resolve.this.findMethod(var1, this.site, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired(), false);
         }

         Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Symbol var4) {
            if (var4.kind >= 129) {
               var4 = super.access(var1, var2, var3, var4);
            } else if (Resolve.this.allowMethodHandles) {
               Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)var4;
               if ((var5.flags() & 70368744177664L) != 0L) {
                  return Resolve.this.findPolymorphicSignatureInstance(var1, var4, this.argtypes);
               }
            }

            return var4;
         }
      });
   }

   Symbol findPolymorphicSignatureInstance(Env var1, final Symbol var2, List var3) {
      Type var4 = this.infer.instantiatePolymorphicSignatureInstance(var1, (Symbol.MethodSymbol)var2, this.currentResolutionContext, var3);
      Iterator var5 = this.polymorphicSignatureScope.getElementsByName(var2.name).iterator();

      Symbol var6;
      do {
         if (!var5.hasNext()) {
            long var8 = 137438954496L | var2.flags() & 7L;
            Symbol.MethodSymbol var7 = new Symbol.MethodSymbol(var8, var2.name, var4, var2.owner) {
               public Symbol baseSymbol() {
                  return var2;
               }
            };
            if (!var4.isErroneous()) {
               this.polymorphicSignatureScope.enter(var7);
            }

            return var7;
         }

         var6 = (Symbol)var5.next();
      } while(!this.types.isSameType(var4, var6.type));

      return var6;
   }

   public Symbol.MethodSymbol resolveInternalMethod(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, Name var4, List var5, List var6) {
      MethodResolutionContext var7 = new MethodResolutionContext();
      var7.internalResolution = true;
      Symbol var8 = this.resolveQualifiedMethod(var7, var1, var2, var3.tsym, var3, var4, var5, var6);
      if (var8.kind == 16) {
         return (Symbol.MethodSymbol)var8;
      } else {
         throw new FatalError(this.diags.fragment("fatal.err.cant.locate.meth", var4));
      }
   }

   Symbol resolveConstructor(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, List var4, List var5) {
      return this.resolveConstructor(new MethodResolutionContext(), var1, var2, var3, var4, var5);
   }

   private Symbol resolveConstructor(MethodResolutionContext var1, final JCDiagnostic.DiagnosticPosition var2, Env var3, Type var4, List var5, List var6) {
      return this.lookupMethod(var3, var2, var4.tsym, (MethodResolutionContext)var1, new BasicLookupHelper(this.names.init, var4, var5, var6) {
         Symbol doLookup(Env var1, MethodResolutionPhase var2x) {
            return Resolve.this.findConstructor(var2, var1, this.site, this.argtypes, this.typeargtypes, var2x.isBoxingRequired(), var2x.isVarargsRequired());
         }
      });
   }

   public Symbol.MethodSymbol resolveInternalConstructor(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, List var4, List var5) {
      MethodResolutionContext var6 = new MethodResolutionContext();
      var6.internalResolution = true;
      Symbol var7 = this.resolveConstructor(var6, var1, var2, var3, var4, var5);
      if (var7.kind == 16) {
         return (Symbol.MethodSymbol)var7;
      } else {
         throw new FatalError(this.diags.fragment("fatal.err.cant.locate.ctor", var3));
      }
   }

   Symbol findConstructor(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, List var4, List var5, boolean var6, boolean var7) {
      Symbol var8 = this.findMethod(var2, var3, this.names.init, var4, var5, var6, var7, false);
      this.chk.checkDeprecated(var1, ((AttrContext)var2.info).scope.owner, var8);
      return var8;
   }

   Symbol resolveDiamond(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, List var4, List var5) {
      return this.lookupMethod(var2, var1, var3.tsym, (MethodCheck)this.resolveMethodCheck, new BasicLookupHelper(this.names.init, var3, var4, var5) {
         Symbol doLookup(Env var1, MethodResolutionPhase var2) {
            return Resolve.this.findDiamond(var1, this.site, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired());
         }

         Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Symbol var4) {
            if (var4.kind >= 129) {
               if (var4.kind != 135 && var4.kind != 134) {
                  var4 = super.access(var1, var2, var3, var4);
               } else {
                  final JCDiagnostic var5 = var4.kind == 135 ? (JCDiagnostic)((InapplicableSymbolError)var4.baseSymbol()).errCandidate().snd : null;
                  InapplicableSymbolError var6 = new InapplicableSymbolError(var4.kind, "diamondError", Resolve.this.currentResolutionContext) {
                     JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5x, List var6, List var7) {
                        String var8 = var5 == null ? "cant.apply.diamond" : "cant.apply.diamond.1";
                        return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, var8, Resolve.this.diags.fragment("diamond", var4.tsym), var5);
                     }
                  };
                  var4 = Resolve.this.accessMethod(var6, var2, this.site, Resolve.this.names.init, true, this.argtypes, this.typeargtypes);
                  ((AttrContext)var1.info).pendingResolutionPhase = Resolve.this.currentResolutionContext.step;
               }
            }

            return var4;
         }
      });
   }

   private Symbol findDiamond(Env var1, Type var2, List var3, List var4, boolean var5, boolean var6) {
      Object var7 = this.methodNotFound;

      for(Scope.Entry var8 = var2.tsym.members().lookup(this.names.init); var8.scope != null; var8 = var8.next()) {
         final Symbol var9 = var8.sym;
         if (var9.kind == 16 && (var9.flags_field & 4096L) == 0L) {
            List var10 = var8.sym.type.hasTag(TypeTag.FORALL) ? ((Type.ForAll)var9.type).tvars : List.nil();
            Type.ForAll var11 = new Type.ForAll(var2.tsym.type.getTypeArguments().appendList(var10), this.types.createMethodTypeWithReturn(var9.type.asMethodType(), var2));
            Symbol.MethodSymbol var12 = new Symbol.MethodSymbol(var9.flags(), this.names.init, var11, var2.tsym) {
               public Symbol baseSymbol() {
                  return var9;
               }
            };
            var7 = this.selectBest(var1, var2, var3, var4, var12, (Symbol)var7, var5, var6, false);
         }
      }

      return (Symbol)var7;
   }

   Symbol resolveOperator(JCDiagnostic.DiagnosticPosition var1, JCTree.Tag var2, Env var3, List var4) {
      MethodResolutionContext var5 = this.currentResolutionContext;

      Symbol var7;
      try {
         this.currentResolutionContext = new MethodResolutionContext();
         Name var6 = this.treeinfo.operatorName(var2);
         var7 = this.lookupMethod(var3, var1, this.syms.predefClass, (MethodResolutionContext)this.currentResolutionContext, new BasicLookupHelper(var6, this.syms.predefClass.type, var4, (List)null, Resolve.MethodResolutionPhase.BOX) {
            Symbol doLookup(Env var1, MethodResolutionPhase var2) {
               return Resolve.this.findMethod(var1, this.site, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired(), true);
            }

            Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Symbol var4) {
               return Resolve.this.accessMethod(var4, var2, var1.enclClass.sym.type, this.name, false, this.argtypes, (List)null);
            }
         });
      } finally {
         this.currentResolutionContext = var5;
      }

      return var7;
   }

   Symbol resolveUnaryOperator(JCDiagnostic.DiagnosticPosition var1, JCTree.Tag var2, Env var3, Type var4) {
      return this.resolveOperator(var1, var2, var3, List.of(var4));
   }

   Symbol resolveBinaryOperator(JCDiagnostic.DiagnosticPosition var1, JCTree.Tag var2, Env var3, Type var4, Type var5) {
      return this.resolveOperator(var1, var2, var3, List.of(var4, var5));
   }

   Symbol getMemberReference(JCDiagnostic.DiagnosticPosition var1, Env var2, JCTree.JCMemberReference var3, Type var4, Name var5) {
      var4 = this.types.capture(var4);
      ReferenceLookupHelper var6 = this.makeReferenceLookupHelper(var3, var4, var5, List.nil(), (List)null, Resolve.MethodResolutionPhase.VARARITY);
      Env var7 = var2.dup(var2.tree, ((AttrContext)var2.info).dup());
      Symbol var8 = this.lookupMethod(var7, var2.tree.pos(), var4.tsym, (MethodCheck)this.nilMethodCheck, var6);
      ((AttrContext)var2.info).pendingResolutionPhase = ((AttrContext)var7.info).pendingResolutionPhase;
      return var8;
   }

   ReferenceLookupHelper makeReferenceLookupHelper(JCTree.JCMemberReference var1, Type var2, Name var3, List var4, List var5, MethodResolutionPhase var6) {
      Object var7;
      if (!var3.equals(this.names.init)) {
         var7 = new MethodReferenceLookupHelper(var1, var3, var2, var4, var5, var6);
      } else if (var2.hasTag(TypeTag.ARRAY)) {
         var7 = new ArrayConstructorReferenceLookupHelper(var1, var2, var4, var5, var6);
      } else {
         var7 = new ConstructorReferenceLookupHelper(var1, var2, var4, var5, var6);
      }

      return (ReferenceLookupHelper)var7;
   }

   Symbol resolveMemberReferenceByArity(Env var1, JCTree.JCMemberReference var2, Type var3, Name var4, List var5, Infer.InferenceContext var6) {
      boolean var7 = TreeInfo.isStaticSelector(var2.expr, this.names);
      var3 = this.types.capture(var3);
      ReferenceLookupHelper var8 = this.makeReferenceLookupHelper(var2, var3, var4, var5, (List)null, Resolve.MethodResolutionPhase.VARARITY);
      Env var9 = var1.dup(var1.tree, ((AttrContext)var1.info).dup());
      Object var10 = this.lookupMethod(var9, var1.tree.pos(), var3.tsym, (MethodCheck)this.arityMethodCheck, var8);
      if (var7 && !var4.equals(this.names.init) && !((Symbol)var10).isStatic() && ((Symbol)var10).kind < 128) {
         var10 = this.methodNotFound;
      }

      Object var11 = this.methodNotFound;
      ReferenceLookupHelper var12 = null;
      Env var13 = var1.dup(var1.tree, ((AttrContext)var1.info).dup());
      if (var7) {
         var12 = var8.unboundLookup(var6);
         var11 = this.lookupMethod(var13, var1.tree.pos(), var3.tsym, (MethodCheck)this.arityMethodCheck, var12);
         if (((Symbol)var11).isStatic() && ((Symbol)var11).kind < 128) {
            var11 = this.methodNotFound;
         }
      }

      Symbol var14 = this.choose((Symbol)var10, (Symbol)var11);
      ((AttrContext)var1.info).pendingResolutionPhase = var14 == var11 ? ((AttrContext)var13.info).pendingResolutionPhase : ((AttrContext)var9.info).pendingResolutionPhase;
      return var14;
   }

   Pair resolveMemberReference(Env var1, JCTree.JCMemberReference var2, Type var3, Name var4, List var5, List var6, MethodCheck var7, Infer.InferenceContext var8, DeferredAttr.AttrMode var9) {
      var3 = this.types.capture(var3);
      ReferenceLookupHelper var10 = this.makeReferenceLookupHelper(var2, var3, var4, var5, var6, Resolve.MethodResolutionPhase.VARARITY);
      Env var11 = var1.dup(var1.tree, ((AttrContext)var1.info).dup());
      boolean var13 = false;
      MethodResolutionContext var14 = new MethodResolutionContext();
      var14.methodCheck = var7;
      Symbol var12;
      Object var15 = var12 = this.lookupMethod(var11, var1.tree.pos(), var3.tsym, (MethodResolutionContext)var14, var10);
      SearchResultKind var16 = Resolve.SearchResultKind.NOT_APPLICABLE_MATCH;
      boolean var17 = TreeInfo.isStaticSelector(var2.expr, this.names);
      boolean var18 = var17 && var2.getMode() == MemberReferenceTree.ReferenceMode.INVOKE;
      if (((Symbol)var15).kind != 134 && ((Symbol)var15).kind != 135 && var18) {
         if (!((Symbol)var15).isStatic()) {
            var13 = true;
            if (this.hasAnotherApplicableMethod(var14, (Symbol)var15, true)) {
               var16 = Resolve.SearchResultKind.BAD_MATCH_MORE_SPECIFIC;
            } else {
               var16 = Resolve.SearchResultKind.BAD_MATCH;
               if (((Symbol)var15).kind < 128) {
                  var15 = this.methodWithCorrectStaticnessNotFound;
               }
            }
         } else if (((Symbol)var15).kind < 128) {
            var16 = Resolve.SearchResultKind.GOOD_MATCH;
         }
      }

      Symbol var19 = null;
      Object var20 = this.methodNotFound;
      ReferenceLookupHelper var21 = null;
      Env var22 = var1.dup(var1.tree, ((AttrContext)var1.info).dup());
      SearchResultKind var23 = Resolve.SearchResultKind.NOT_APPLICABLE_MATCH;
      boolean var24 = false;
      if (var17) {
         var21 = var10.unboundLookup(var8);
         MethodResolutionContext var25 = new MethodResolutionContext();
         var25.methodCheck = var7;
         var20 = var19 = this.lookupMethod(var22, var1.tree.pos(), var3.tsym, (MethodResolutionContext)var25, var21);
         if (((Symbol)var20).kind != 135 && ((Symbol)var20).kind != 134 && var18) {
            if (((Symbol)var20).isStatic()) {
               var24 = true;
               if (this.hasAnotherApplicableMethod(var25, (Symbol)var20, false)) {
                  var23 = Resolve.SearchResultKind.BAD_MATCH_MORE_SPECIFIC;
               } else {
                  var23 = Resolve.SearchResultKind.BAD_MATCH;
                  if (((Symbol)var20).kind < 128) {
                     var20 = this.methodWithCorrectStaticnessNotFound;
                  }
               }
            } else if (((Symbol)var20).kind < 128) {
               var23 = Resolve.SearchResultKind.GOOD_MATCH;
            }
         }
      }

      Symbol var26 = this.choose((Symbol)var15, (Symbol)var20);
      if (var26.kind < 128 && (var13 || var24)) {
         if (var13) {
            var15 = this.methodWithCorrectStaticnessNotFound;
         }

         if (var24) {
            var20 = this.methodWithCorrectStaticnessNotFound;
         }

         var26 = this.choose((Symbol)var15, (Symbol)var20);
      }

      if (var26 == this.methodWithCorrectStaticnessNotFound && var9 == DeferredAttr.AttrMode.CHECK) {
         Symbol var27 = var12;
         String var28 = "non-static.cant.be.ref";
         if (var13 && var24) {
            if (var23 == Resolve.SearchResultKind.BAD_MATCH_MORE_SPECIFIC) {
               var27 = var19;
               var28 = "static.method.in.unbound.lookup";
            }
         } else if (!var13) {
            var27 = var19;
            var28 = "static.method.in.unbound.lookup";
         }

         this.log.error(var2.expr.pos(), "invalid.mref", new Object[]{Kinds.kindName(var2.getMode()), this.diags.fragment(var28, Kinds.kindName(var27), var27)});
      }

      Pair var29 = new Pair(var26, var26 == var20 ? var21 : var10);
      ((AttrContext)var1.info).pendingResolutionPhase = var26 == var20 ? ((AttrContext)var22.info).pendingResolutionPhase : ((AttrContext)var11.info).pendingResolutionPhase;
      return var29;
   }

   boolean hasAnotherApplicableMethod(MethodResolutionContext var1, Symbol var2, boolean var3) {
      Iterator var4 = var1.candidates.iterator();

      MethodResolutionContext.Candidate var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (MethodResolutionContext.Candidate)var4.next();
      } while(var1.step != var5.step || !var5.isApplicable() || var5.sym == var2 || var5.sym.isStatic() != var3);

      return true;
   }

   private Symbol choose(Symbol var1, Symbol var2) {
      if (this.lookupSuccess(var1) && this.lookupSuccess(var2)) {
         return this.ambiguityError(var1, var2);
      } else if (!this.lookupSuccess(var1) && (!this.canIgnore(var2) || this.canIgnore(var1))) {
         return !this.lookupSuccess(var2) && (!this.canIgnore(var1) || this.canIgnore(var2)) ? var1 : var2;
      } else {
         return var1;
      }
   }

   private boolean lookupSuccess(Symbol var1) {
      return var1.kind == 16 || var1.kind == 129;
   }

   private boolean canIgnore(Symbol var1) {
      switch (var1.kind) {
         case 134:
            InapplicableSymbolsError var3 = (InapplicableSymbolsError)var1.baseSymbol();
            return var3.filterCandidates(var3.mapCandidates()).isEmpty();
         case 135:
            InapplicableSymbolError var2 = (InapplicableSymbolError)var1.baseSymbol();
            return (new MethodResolutionDiagHelper.Template(Resolve.MethodCheckDiag.ARITY_MISMATCH.regex(), new MethodResolutionDiagHelper.Template[0])).matches(var2.errCandidate().snd);
         case 136:
            return true;
         case 137:
         default:
            return false;
         case 138:
            return false;
      }
   }

   Symbol lookupMethod(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, MethodCheck var4, LookupHelper var5) {
      MethodResolutionContext var6 = new MethodResolutionContext();
      var6.methodCheck = var4;
      return this.lookupMethod(var1, var2, var3, var6, var5);
   }

   Symbol lookupMethod(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, MethodResolutionContext var4, LookupHelper var5) {
      MethodResolutionContext var6 = this.currentResolutionContext;

      try {
         Object var7 = this.methodNotFound;
         this.currentResolutionContext = var4;
         Iterator var8 = this.methodResolutionSteps.iterator();

         while(true) {
            if (var8.hasNext()) {
               MethodResolutionPhase var9 = (MethodResolutionPhase)var8.next();
               if (var9.isApplicable(this.boxingEnabled, this.varargsEnabled) && !var5.shouldStop((Symbol)var7, var9)) {
                  MethodResolutionPhase var10 = this.currentResolutionContext.step;
                  Object var11 = var7;
                  this.currentResolutionContext.step = var9;
                  Symbol var12 = var5.lookup(var1, var9);
                  var5.debug(var2, var12);
                  var7 = var9.mergeResults((Symbol)var7, var12);
                  ((AttrContext)var1.info).pendingResolutionPhase = var11 == var7 ? var10 : var9;
                  continue;
               }
            }

            Symbol var16 = var5.access(var1, var2, var3, (Symbol)var7);
            return var16;
         }
      } finally {
         this.currentResolutionContext = var6;
      }
   }

   Symbol resolveSelf(JCDiagnostic.DiagnosticPosition var1, Env var2, Symbol.TypeSymbol var3, Name var4) {
      Env var5 = var2;

      for(boolean var6 = false; var5.outer != null; var5 = var5.outer) {
         if (isStatic(var5)) {
            var6 = true;
         }

         if (var5.enclClass.sym == var3) {
            Object var7 = ((AttrContext)var5.info).scope.lookup(var4).sym;
            if (var7 != null) {
               if (var6) {
                  var7 = new StaticError((Symbol)var7);
               }

               return this.accessBase((Symbol)var7, var1, var2.enclClass.sym.type, var4, true);
            }
         }

         if ((var5.enclClass.sym.flags() & 8L) != 0L) {
            var6 = true;
         }
      }

      if (var3.isInterface() && var4 == this.names._super && !isStatic(var2) && this.types.isDirectSuperInterface(var3, var2.enclClass.sym)) {
         Iterator var9 = this.pruneInterfaces(var2.enclClass.type).iterator();

         Type var8;
         while(var9.hasNext()) {
            var8 = (Type)var9.next();
            if (var8.tsym == var3) {
               ((AttrContext)var2.info).defaultSuperCallSite = var8;
               return new Symbol.VarSymbol(0L, this.names._super, this.types.asSuper(var2.enclClass.type, var3), var2.enclClass.sym);
            }
         }

         var9 = this.types.interfaces(var2.enclClass.type).iterator();

         while(var9.hasNext()) {
            var8 = (Type)var9.next();
            if (var8.tsym.isSubClass(var3, this.types) && var8.tsym != var3) {
               this.log.error(var1, "illegal.default.super.call", new Object[]{var3, this.diags.fragment("redundant.supertype", var3, var8)});
               return this.syms.errSymbol;
            }
         }

         Assert.error();
      }

      this.log.error(var1, "not.encl.class", new Object[]{var3});
      return this.syms.errSymbol;
   }

   private List pruneInterfaces(Type var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = this.types.interfaces(var1).iterator();

      while(var3.hasNext()) {
         Type var4 = (Type)var3.next();
         boolean var5 = true;
         Iterator var6 = this.types.interfaces(var1).iterator();

         while(var6.hasNext()) {
            Type var7 = (Type)var6.next();
            if (var4 != var7 && this.types.isSubtypeNoCapture(var7, var4)) {
               var5 = false;
            }
         }

         if (var5) {
            var2.append(var4);
         }
      }

      return var2.toList();
   }

   Symbol resolveSelfContaining(JCDiagnostic.DiagnosticPosition var1, Env var2, Symbol var3, boolean var4) {
      Symbol var5 = this.resolveSelfContainingInternal(var2, var3, var4);
      if (var5 == null) {
         this.log.error(var1, "encl.class.required", new Object[]{var3});
         return this.syms.errSymbol;
      } else {
         return this.accessBase(var5, var1, var2.enclClass.sym.type, var5.name, true);
      }
   }

   boolean hasEnclosingInstance(Env var1, Type var2) {
      Symbol var3 = this.resolveSelfContainingInternal(var1, var2.tsym, false);
      return var3 != null && var3.kind < 128;
   }

   private Symbol resolveSelfContainingInternal(Env var1, Symbol var2, boolean var3) {
      Name var4 = this.names._this;
      Env var5 = var3 ? var1.outer : var1;
      boolean var6 = false;
      if (var5 != null) {
         for(; var5 != null && var5.outer != null; var5 = var5.outer) {
            if (isStatic(var5)) {
               var6 = true;
            }

            if (var5.enclClass.sym.isSubClass(var2.owner, this.types)) {
               Object var7 = ((AttrContext)var5.info).scope.lookup(var4).sym;
               if (var7 != null) {
                  if (var6) {
                     var7 = new StaticError((Symbol)var7);
                  }

                  return (Symbol)var7;
               }
            }

            if ((var5.enclClass.sym.flags() & 8L) != 0L) {
               var6 = true;
            }
         }
      }

      return null;
   }

   Type resolveImplicitThis(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3) {
      return this.resolveImplicitThis(var1, var2, var3, false);
   }

   Type resolveImplicitThis(JCDiagnostic.DiagnosticPosition var1, Env var2, Type var3, boolean var4) {
      Type var5 = ((var3.tsym.owner.kind & 20) != 0 ? this.resolveSelf(var1, var2, var3.getEnclosingType().tsym, this.names._this) : this.resolveSelfContaining(var1, var2, var3.tsym, var4)).type;
      if (((AttrContext)var2.info).isSelfCall && var5.tsym == var2.enclClass.sym) {
         this.log.error(var1, "cant.ref.before.ctor.called", new Object[]{"this"});
      }

      return var5;
   }

   public void logAccessErrorInternal(Env var1, JCTree var2, Type var3) {
      AccessError var4 = new AccessError(var1, var1.enclClass.type, var3.tsym);
      this.logResolveError(var4, var2.pos(), var1.enclClass.sym, var1.enclClass.type, (Name)null, (List)null, (List)null);
   }

   private void logResolveError(ResolveError var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
      JCDiagnostic var8 = var1.getDiagnostic(JCDiagnostic.DiagnosticType.ERROR, var2, var3, var4, var5, var6, var7);
      if (var8 != null) {
         var8.setFlag(JCDiagnostic.DiagnosticFlag.RESOLVE_ERROR);
         this.log.report(var8);
      }

   }

   public Object methodArguments(List var1) {
      if (var1 != null && !var1.isEmpty()) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            if (var4.hasTag(TypeTag.DEFERRED)) {
               var2.append(((DeferredAttr.DeferredType)var4).tree);
            } else {
               var2.append(var4);
            }
         }

         return var2;
      } else {
         return this.noArgs;
      }
   }

   class MethodResolutionContext {
      private List candidates = List.nil();
      MethodResolutionPhase step = null;
      MethodCheck methodCheck;
      private boolean internalResolution;
      private DeferredAttr.AttrMode attrMode;

      MethodResolutionContext() {
         this.methodCheck = Resolve.this.resolveMethodCheck;
         this.internalResolution = false;
         this.attrMode = DeferredAttr.AttrMode.SPECULATIVE;
      }

      void addInapplicableCandidate(Symbol var1, JCDiagnostic var2) {
         Candidate var3 = new Candidate(Resolve.this.currentResolutionContext.step, var1, var2, (Type)null);
         this.candidates = this.candidates.append(var3);
      }

      void addApplicableCandidate(Symbol var1, Type var2) {
         Candidate var3 = new Candidate(Resolve.this.currentResolutionContext.step, var1, (JCDiagnostic)null, var2);
         this.candidates = this.candidates.append(var3);
      }

      DeferredAttr.DeferredAttrContext deferredAttrContext(Symbol var1, Infer.InferenceContext var2, Attr.ResultInfo var3, Warner var4) {
         DeferredAttr.DeferredAttrContext var5 = var3 == null ? Resolve.this.deferredAttr.emptyDeferredAttrContext : var3.checkContext.deferredAttrContext();
         return Resolve.this.deferredAttr.new DeferredAttrContext(this.attrMode, var1, this.step, var2, var5, var4);
      }

      DeferredAttr.AttrMode attrMode() {
         return this.attrMode;
      }

      boolean internal() {
         return this.internalResolution;
      }

      class Candidate {
         final MethodResolutionPhase step;
         final Symbol sym;
         final JCDiagnostic details;
         final Type mtype;

         private Candidate(MethodResolutionPhase var2, Symbol var3, JCDiagnostic var4, Type var5) {
            this.step = var2;
            this.sym = var3;
            this.details = var4;
            this.mtype = var5;
         }

         public boolean equals(Object var1) {
            if (var1 instanceof Candidate) {
               Symbol var2 = this.sym;
               Symbol var3 = ((Candidate)var1).sym;
               if (var2 != var3 && (var2.overrides(var3, var2.owner.type.tsym, Resolve.this.types, false) || var3.overrides(var2, var3.owner.type.tsym, Resolve.this.types, false)) || (var2.isConstructor() || var3.isConstructor()) && var2.owner != var3.owner) {
                  return true;
               }
            }

            return false;
         }

         boolean isApplicable() {
            return this.mtype != null;
         }

         // $FF: synthetic method
         Candidate(MethodResolutionPhase var2, Symbol var3, JCDiagnostic var4, Type var5, Object var6) {
            this(var2, var3, var4, var5);
         }
      }
   }

   static enum MethodResolutionPhase {
      BASIC(false, false),
      BOX(true, false),
      VARARITY(true, true) {
         public Symbol mergeResults(Symbol var1, Symbol var2) {
            Assert.check(var1.kind >= 128 && var1.kind != 129);
            if (var2.kind < 128) {
               return var2;
            } else {
               switch (var1.kind) {
                  case 134:
                  case 135:
                     switch (var2.kind) {
                        case 134:
                        default:
                           return var2;
                        case 135:
                           return var1.kind == 134 ? var1 : var2;
                        case 136:
                           return var1;
                     }
                  default:
                     return var1;
               }
            }
         }
      };

      final boolean isBoxingRequired;
      final boolean isVarargsRequired;

      private MethodResolutionPhase(boolean var3, boolean var4) {
         this.isBoxingRequired = var3;
         this.isVarargsRequired = var4;
      }

      public boolean isBoxingRequired() {
         return this.isBoxingRequired;
      }

      public boolean isVarargsRequired() {
         return this.isVarargsRequired;
      }

      public boolean isApplicable(boolean var1, boolean var2) {
         return (var2 || !this.isVarargsRequired) && (var1 || !this.isBoxingRequired);
      }

      public Symbol mergeResults(Symbol var1, Symbol var2) {
         return var2;
      }

      // $FF: synthetic method
      MethodResolutionPhase(boolean var3, boolean var4, Object var5) {
         this(var3, var4);
      }
   }

   static class MethodResolutionDiagHelper {
      static final Template skip = new Template("", new Template[0]) {
         boolean matches(Object var1) {
            return true;
         }
      };
      static final Map rewriters = new LinkedHashMap();

      static {
         String var0 = Resolve.MethodCheckDiag.ARG_MISMATCH.regex();
         rewriters.put(new Template(var0, new Template[]{skip}), new DiagnosticRewriter() {
            public JCDiagnostic rewriteDiagnostic(JCDiagnostic.Factory var1, JCDiagnostic.DiagnosticPosition var2, DiagnosticSource var3, JCDiagnostic.DiagnosticType var4, JCDiagnostic var5) {
               JCDiagnostic var6 = (JCDiagnostic)var5.getArgs()[0];
               JCDiagnostic.DiagnosticPosition var7 = var5.getDiagnosticPosition();
               if (var7 == null) {
                  var7 = var2;
               }

               return var1.create(var4, var3, var7, "prob.found.req", var6);
            }
         });
      }

      static class Template {
         String regex;
         Template[] subTemplates;

         Template(String var1, Template... var2) {
            this.regex = var1;
            this.subTemplates = var2;
         }

         boolean matches(Object var1) {
            JCDiagnostic var2 = (JCDiagnostic)var1;
            Object[] var3 = var2.getArgs();
            if (var2.getCode().matches(this.regex) && this.subTemplates.length == var2.getArgs().length) {
               for(int var4 = 0; var4 < var3.length; ++var4) {
                  if (!this.subTemplates[var4].matches(var3[var4])) {
                     return false;
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      }

      interface DiagnosticRewriter {
         JCDiagnostic rewriteDiagnostic(JCDiagnostic.Factory var1, JCDiagnostic.DiagnosticPosition var2, DiagnosticSource var3, JCDiagnostic.DiagnosticType var4, JCDiagnostic var5);
      }
   }

   class BadVarargsMethod extends ResolveError {
      ResolveError delegatedError;

      BadVarargsMethod(ResolveError var2) {
         super(var2.kind, "badVarargs");
         this.delegatedError = var2;
      }

      public Symbol baseSymbol() {
         return this.delegatedError.baseSymbol();
      }

      protected Symbol access(Name var1, Symbol.TypeSymbol var2) {
         return this.delegatedError.access(var1, var2);
      }

      public boolean exists() {
         return true;
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         return this.delegatedError.getDiagnostic(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   class AmbiguityError extends ResolveError {
      List ambiguousSyms = List.nil();

      public boolean exists() {
         return true;
      }

      AmbiguityError(Symbol var2, Symbol var3) {
         super(129, "ambiguity error");
         this.ambiguousSyms = this.flatten(var3).appendList(this.flatten(var2));
      }

      private List flatten(Symbol var1) {
         return var1.kind == 129 ? ((AmbiguityError)var1.baseSymbol()).ambiguousSyms : List.of(var1);
      }

      AmbiguityError addAmbiguousSymbol(Symbol var1) {
         this.ambiguousSyms = this.ambiguousSyms.prepend(var1);
         return this;
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         List var8 = this.ambiguousSyms.reverse();
         Symbol var9 = (Symbol)var8.head;
         Symbol var10 = (Symbol)var8.tail.head;
         Name var11 = var9.name;
         if (var11 == Resolve.this.names.init) {
            var11 = var9.owner.name;
         }

         return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "ref.ambiguous", var11, Kinds.kindName(var9), var9, var9.location(var4, Resolve.this.types), Kinds.kindName(var10), var10, var10.location(var4, Resolve.this.types));
      }

      Symbol mergeAbstracts(Type var1) {
         List var2 = this.ambiguousSyms.reverse();
         Iterator var3 = var2.iterator();

         Symbol var4;
         Type var5;
         boolean var6;
         List var7;
         do {
            if (!var3.hasNext()) {
               return this;
            }

            var4 = (Symbol)var3.next();
            var5 = Resolve.this.types.memberType(var1, var4);
            var6 = true;
            var7 = var5.getThrownTypes();

            Type var10;
            for(Iterator var8 = var2.iterator(); var8.hasNext(); var7 = Resolve.this.chk.intersect(var7, var10.getThrownTypes())) {
               Symbol var9 = (Symbol)var8.next();
               var10 = Resolve.this.types.memberType(var1, var9);
               if ((var9.flags() & 1024L) == 0L || !Resolve.this.types.overrideEquivalent(var5, var10) || !Resolve.this.types.isSameTypes(var4.erasure(Resolve.this.types).getParameterTypes(), var9.erasure(Resolve.this.types).getParameterTypes())) {
                  return this;
               }

               Type var11 = Resolve.this.mostSpecificReturnType(var5, var10);
               if (var11 == null || var11 != var5) {
                  var6 = false;
                  break;
               }
            }
         } while(!var6);

         return (Symbol)(var7 == var5.getThrownTypes() ? var4 : new Symbol.MethodSymbol(var4.flags(), var4.name, Resolve.this.types.createMethodTypeWithThrown(var4.type, var7), var4.owner));
      }

      protected Symbol access(Name var1, Symbol.TypeSymbol var2) {
         Symbol var3 = (Symbol)this.ambiguousSyms.last();
         return (Symbol)(var3.kind == 2 ? Resolve.this.types.createErrorType(var1, var2, var3.type).tsym : var3);
      }
   }

   class StaticError extends InvalidSymbolError {
      StaticError(Symbol var2) {
         super(131, var2, "static error");
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         Object var8 = this.sym.kind == 2 && this.sym.type.hasTag(TypeTag.CLASS) ? Resolve.this.types.erasure(this.sym.type).tsym : this.sym;
         return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "non-static.cant.be.ref", Kinds.kindName(this.sym), var8);
      }
   }

   class AccessError extends InvalidSymbolError {
      private Env env;
      private Type site;

      AccessError(Symbol var2) {
         this((Env)null, (Type)null, var2);
      }

      AccessError(Env var2, Type var3, Symbol var4) {
         super(130, var4, "access error");
         this.env = var2;
         this.site = var3;
         if (Resolve.this.debugResolve) {
            Resolve.this.log.error("proc.messager", new Object[]{var4 + " @ " + var3 + " is inaccessible."});
         }

      }

      public boolean exists() {
         return false;
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         if (this.sym.owner.type.hasTag(TypeTag.ERROR)) {
            return null;
         } else if (this.sym.name == Resolve.this.names.init && this.sym.owner != var4.tsym) {
            return (Resolve.this.new SymbolNotFoundError(136)).getDiagnostic(var1, var2, var3, var4, var5, var6, var7);
         } else if ((this.sym.flags() & 1L) != 0L || this.env != null && this.site != null && !Resolve.this.isAccessible(this.env, this.site)) {
            return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "not.def.access.class.intf.cant.access", this.sym, this.sym.location());
         } else {
            return (this.sym.flags() & 6L) != 0L ? Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "report.access", this.sym, Flags.asFlagSet(this.sym.flags() & 6L), this.sym.location()) : Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "not.def.public.cant.access", this.sym, this.sym.location());
         }
      }
   }

   class InapplicableSymbolsError extends InapplicableSymbolError {
      InapplicableSymbolsError(MethodResolutionContext var2) {
         super(134, "inapplicable symbols", var2);
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         Map var8 = this.mapCandidates();
         Map var9 = Resolve.this.compactMethodDiags ? this.filterCandidates(var8) : this.mapCandidates();
         if (var9.isEmpty()) {
            var9 = var8;
         }

         boolean var10 = var8.size() != var9.size();
         if (var9.size() > 1) {
            JCDiagnostic var14 = Resolve.this.diags.create(var1, (Lint.LintCategory)null, var10 ? EnumSet.of(JCDiagnostic.DiagnosticFlag.COMPRESSED) : EnumSet.noneOf(JCDiagnostic.DiagnosticFlag.class), Resolve.this.log.currentSource(), var2, "cant.apply.symbols", var5 == Resolve.this.names.init ? Kinds.KindName.CONSTRUCTOR : Kinds.absentKind(this.kind), var5 == Resolve.this.names.init ? var4.tsym.name : var5, Resolve.this.methodArguments(var6));
            return new JCDiagnostic.MultilineDiagnostic(var14, this.candidateDetails(var9, var4));
         } else if (var9.size() == 1) {
            Map.Entry var11 = (Map.Entry)var9.entrySet().iterator().next();
            final Pair var12 = new Pair(var11.getKey(), var11.getValue());
            JCDiagnostic var13 = (new InapplicableSymbolError(this.resolveContext) {
               protected Pair errCandidate() {
                  return var12;
               }
            }).getDiagnostic(var1, var2, var3, var4, var5, var6, var7);
            if (var10) {
               var13.setFlag(JCDiagnostic.DiagnosticFlag.COMPRESSED);
            }

            return var13;
         } else {
            return (Resolve.this.new SymbolNotFoundError(136)).getDiagnostic(var1, var2, var3, var4, var5, var6, var7);
         }
      }

      private Map mapCandidates() {
         LinkedHashMap var1 = new LinkedHashMap();
         Iterator var2 = this.resolveContext.candidates.iterator();

         while(var2.hasNext()) {
            MethodResolutionContext.Candidate var3 = (MethodResolutionContext.Candidate)var2.next();
            if (!var3.isApplicable()) {
               var1.put(var3.sym, var3.details);
            }
         }

         return var1;
      }

      Map filterCandidates(Map var1) {
         LinkedHashMap var2 = new LinkedHashMap();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            JCDiagnostic var5 = (JCDiagnostic)var4.getValue();
            if (!(new MethodResolutionDiagHelper.Template(Resolve.MethodCheckDiag.ARITY_MISMATCH.regex(), new MethodResolutionDiagHelper.Template[0])).matches(var5)) {
               var2.put(var4.getKey(), var5);
            }
         }

         return var2;
      }

      private List candidateDetails(Map var1, Type var2) {
         List var3 = List.nil();

         JCDiagnostic var7;
         for(Iterator var4 = var1.entrySet().iterator(); var4.hasNext(); var3 = var3.prepend(var7)) {
            Map.Entry var5 = (Map.Entry)var4.next();
            Symbol var6 = (Symbol)var5.getKey();
            var7 = Resolve.this.diags.fragment("inapplicable.method", Kinds.kindName(var6), var6.location(var2, Resolve.this.types), var6.asMemberOf(var2, Resolve.this.types), var5.getValue());
         }

         return var3;
      }
   }

   class InapplicableSymbolError extends ResolveError {
      protected MethodResolutionContext resolveContext;

      InapplicableSymbolError(MethodResolutionContext var2) {
         this(135, "inapplicable symbol error", var2);
      }

      protected InapplicableSymbolError(int var2, String var3, MethodResolutionContext var4) {
         super(var2, var3);
         this.resolveContext = var4;
      }

      public String toString() {
         return super.toString();
      }

      public boolean exists() {
         return true;
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         if (var5 == Resolve.this.names.error) {
            return null;
         } else if (Resolve.this.syms.operatorNames.contains(var5)) {
            boolean var12 = var6.size() == 1;
            String var14 = var6.size() == 1 ? "operator.cant.be.applied" : "operator.cant.be.applied.1";
            Type var15 = (Type)var6.head;
            Type var16 = !var12 ? (Type)var6.tail.head : null;
            return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, var14, var5, var15, var16);
         } else {
            Pair var8 = this.errCandidate();
            if (Resolve.this.compactMethodDiags) {
               Iterator var9 = Resolve.MethodResolutionDiagHelper.rewriters.entrySet().iterator();

               while(var9.hasNext()) {
                  Map.Entry var10 = (Map.Entry)var9.next();
                  if (((MethodResolutionDiagHelper.Template)var10.getKey()).matches(var8.snd)) {
                     JCDiagnostic var11 = ((MethodResolutionDiagHelper.DiagnosticRewriter)var10.getValue()).rewriteDiagnostic(Resolve.this.diags, var2, Resolve.this.log.currentSource(), var1, (JCDiagnostic)var8.snd);
                     var11.setFlag(JCDiagnostic.DiagnosticFlag.COMPRESSED);
                     return var11;
                  }
               }
            }

            Symbol var13 = ((Symbol)var8.fst).asMemberOf(var4, Resolve.this.types);
            return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "cant.apply.symbol", Kinds.kindName(var13), var13.name == Resolve.this.names.init ? var13.owner.name : var13.name, Resolve.this.methodArguments(var13.type.getParameterTypes()), Resolve.this.methodArguments(var6), Kinds.kindName(var13.owner), var13.owner.type, var8.snd);
         }
      }

      public Symbol access(Name var1, Symbol.TypeSymbol var2) {
         return Resolve.this.types.createErrorType(var1, var2, Resolve.this.syms.errSymbol.type).tsym;
      }

      protected Pair errCandidate() {
         MethodResolutionContext.Candidate var1 = null;
         Iterator var2 = this.resolveContext.candidates.iterator();

         while(var2.hasNext()) {
            MethodResolutionContext.Candidate var3 = (MethodResolutionContext.Candidate)var2.next();
            if (!var3.isApplicable()) {
               var1 = var3;
            }
         }

         Assert.checkNonNull(var1);
         return new Pair(var1.sym, var1.details);
      }
   }

   class SymbolNotFoundError extends ResolveError {
      SymbolNotFoundError(int var2) {
         this(var2, "symbol not found error");
      }

      SymbolNotFoundError(int var2, String var3) {
         super(var2, var3);
      }

      JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
         var6 = var6 == null ? List.nil() : var6;
         var7 = var7 == null ? List.nil() : var7;
         if (var5 == Resolve.this.names.error) {
            return null;
         } else {
            boolean var8;
            if (Resolve.this.syms.operatorNames.contains(var5)) {
               var8 = var6.size() == 1;
               String var13 = var6.size() == 1 ? "operator.cant.be.applied" : "operator.cant.be.applied.1";
               Type var14 = (Type)var6.head;
               Type var15 = !var8 ? (Type)var6.tail.head : null;
               return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, var13, var5, var14, var15);
            } else {
               var8 = false;
               if (var3 == null) {
                  var3 = var4.tsym;
               }

               if (!((Symbol)var3).name.isEmpty()) {
                  if (((Symbol)var3).kind == 1 && !var4.tsym.exists()) {
                     return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "doesnt.exist", var3);
                  }

                  var8 = !((Symbol)var3).name.equals(Resolve.this.names._this) && !((Symbol)var3).name.equals(Resolve.this.names._super);
               }

               boolean var9 = (this.kind == 136 || this.kind == 138) && var5 == Resolve.this.names.init;
               Kinds.KindName var10 = var9 ? Kinds.KindName.CONSTRUCTOR : Kinds.absentKind(this.kind);
               Name var11 = var9 ? var4.tsym.name : var5;
               String var12 = this.getErrorKey(var10, var7.nonEmpty(), var8);
               return var8 ? Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, var12, var10, var11, var7, this.args(var6), this.getLocationDiag((Symbol)var3, var4)) : Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, var12, var10, var11, var7, this.args(var6));
            }
         }
      }

      private Object args(List var1) {
         return var1.isEmpty() ? var1 : Resolve.this.methodArguments(var1);
      }

      private String getErrorKey(Kinds.KindName var1, boolean var2, boolean var3) {
         String var4 = "cant.resolve";
         String var5 = var3 ? ".location" : "";
         switch (var1) {
            case METHOD:
            case CONSTRUCTOR:
               var5 = var5 + ".args";
               var5 = var5 + (var2 ? ".params" : "");
            default:
               return var4 + var5;
         }
      }

      private JCDiagnostic getLocationDiag(Symbol var1, Type var2) {
         return var1.kind == 4 ? Resolve.this.diags.fragment("location.1", Kinds.kindName(var1), var1, var1.type) : Resolve.this.diags.fragment("location", Kinds.typeKindName(var2), var2, null);
      }
   }

   abstract class InvalidSymbolError extends ResolveError {
      Symbol sym;

      InvalidSymbolError(int var2, Symbol var3, String var4) {
         super(var2, var4);
         this.sym = var3;
      }

      public boolean exists() {
         return true;
      }

      public String toString() {
         return super.toString() + " wrongSym=" + this.sym;
      }

      public Symbol access(Name var1, Symbol.TypeSymbol var2) {
         return (Symbol)((this.sym.kind & 128) == 0 && (this.sym.kind & 2) != 0 ? Resolve.this.types.createErrorType(var1, var2, this.sym.type).tsym : this.sym);
      }
   }

   abstract class ResolveError extends Symbol {
      final String debugName;

      ResolveError(int var2, String var3) {
         super(var2, 0L, (Name)null, (Type)null, (Symbol)null);
         this.debugName = var3;
      }

      public Object accept(ElementVisitor var1, Object var2) {
         throw new AssertionError();
      }

      public String toString() {
         return this.debugName;
      }

      public boolean exists() {
         return false;
      }

      public boolean isStatic() {
         return false;
      }

      protected Symbol access(Name var1, Symbol.TypeSymbol var2) {
         return Resolve.this.types.createErrorType(var1, var2, Resolve.this.syms.errSymbol.type).tsym;
      }

      abstract JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7);
   }

   class ConstructorReferenceLookupHelper extends ReferenceLookupHelper {
      boolean needsInference;

      ConstructorReferenceLookupHelper(JCTree.JCMemberReference var2, Type var3, List var4, List var5, MethodResolutionPhase var6) {
         super(var2, Resolve.this.names.init, var3, var4, var5, var6);
         if (var3.isRaw()) {
            this.site = new Type.ClassType(var3.getEnclosingType(), var3.tsym.type.getTypeArguments(), var3.tsym);
            this.needsInference = true;
         }

      }

      protected Symbol lookup(Env var1, MethodResolutionPhase var2) {
         Symbol var3 = this.needsInference ? Resolve.this.findDiamond(var1, this.site, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired()) : Resolve.this.findMethod(var1, this.site, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired(), Resolve.this.syms.operatorNames.contains(this.name));
         return (Symbol)(var3.kind == 16 && !this.site.getEnclosingType().hasTag(TypeTag.NONE) && !Resolve.this.hasEnclosingInstance(var1, this.site) ? new InvalidSymbolError(132, var3, (String)null) {
            JCDiagnostic getDiagnostic(JCDiagnostic.DiagnosticType var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Type var4, Name var5, List var6, List var7) {
               return Resolve.this.diags.create(var1, Resolve.this.log.currentSource(), var2, "cant.access.inner.cls.constr", var4.tsym.name, var6, var4.getEnclosingType());
            }
         } : var3);
      }

      JCTree.JCMemberReference.ReferenceKind referenceKind(Symbol var1) {
         return this.site.getEnclosingType().hasTag(TypeTag.NONE) ? JCTree.JCMemberReference.ReferenceKind.TOPLEVEL : JCTree.JCMemberReference.ReferenceKind.IMPLICIT_INNER;
      }
   }

   class ArrayConstructorReferenceLookupHelper extends ReferenceLookupHelper {
      ArrayConstructorReferenceLookupHelper(JCTree.JCMemberReference var2, Type var3, List var4, List var5, MethodResolutionPhase var6) {
         super(var2, Resolve.this.names.init, var3, var4, var5, var6);
      }

      protected Symbol lookup(Env var1, MethodResolutionPhase var2) {
         Scope var3 = new Scope(Resolve.this.syms.arrayClass);
         Symbol.MethodSymbol var4 = new Symbol.MethodSymbol(1L, this.name, (Type)null, this.site.tsym);
         var4.type = new Type.MethodType(List.of(Resolve.this.syms.intType), this.site, List.nil(), Resolve.this.syms.methodClass);
         var3.enter(var4);
         return Resolve.this.findMethodInScope(var1, this.site, this.name, this.argtypes, this.typeargtypes, var3, Resolve.this.methodNotFound, var2.isBoxingRequired(), var2.isVarargsRequired(), false, false);
      }

      JCTree.JCMemberReference.ReferenceKind referenceKind(Symbol var1) {
         return JCTree.JCMemberReference.ReferenceKind.ARRAY_CTOR;
      }
   }

   class UnboundMethodReferenceLookupHelper extends MethodReferenceLookupHelper {
      UnboundMethodReferenceLookupHelper(JCTree.JCMemberReference var2, Name var3, Type var4, List var5, List var6, MethodResolutionPhase var7) {
         super(var2, var3, var4, var5.tail, var6, var7);
         if (var4.isRaw() && !((Type)var5.head).hasTag(TypeTag.NONE)) {
            Type var8 = Resolve.this.types.asSuper((Type)var5.head, var4.tsym);
            this.site = Resolve.this.types.capture(var8);
         }

      }

      ReferenceLookupHelper unboundLookup(Infer.InferenceContext var1) {
         return this;
      }

      JCTree.JCMemberReference.ReferenceKind referenceKind(Symbol var1) {
         return JCTree.JCMemberReference.ReferenceKind.UNBOUND;
      }
   }

   class MethodReferenceLookupHelper extends ReferenceLookupHelper {
      MethodReferenceLookupHelper(JCTree.JCMemberReference var2, Name var3, Type var4, List var5, List var6, MethodResolutionPhase var7) {
         super(var2, var3, var4, var5, var6, var7);
      }

      final Symbol lookup(Env var1, MethodResolutionPhase var2) {
         return Resolve.this.findMethod(var1, this.site, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired(), Resolve.this.syms.operatorNames.contains(this.name));
      }

      ReferenceLookupHelper unboundLookup(Infer.InferenceContext var1) {
         return (ReferenceLookupHelper)(!TreeInfo.isStaticSelector(this.referenceTree.expr, Resolve.this.names) || !this.argtypes.nonEmpty() || !((Type)this.argtypes.head).hasTag(TypeTag.NONE) && !Resolve.this.types.isSubtypeUnchecked(var1.asUndetVar((Type)this.argtypes.head), this.site) ? super.unboundLookup(var1) : Resolve.this.new UnboundMethodReferenceLookupHelper(this.referenceTree, this.name, this.site, this.argtypes, this.typeargtypes, this.maxPhase));
      }

      JCTree.JCMemberReference.ReferenceKind referenceKind(Symbol var1) {
         if (var1.isStatic()) {
            return JCTree.JCMemberReference.ReferenceKind.STATIC;
         } else {
            Name var2 = TreeInfo.name(this.referenceTree.getQualifierExpression());
            return var2 != null && var2 == Resolve.this.names._super ? JCTree.JCMemberReference.ReferenceKind.SUPER : JCTree.JCMemberReference.ReferenceKind.BOUND;
         }
      }
   }

   abstract class ReferenceLookupHelper extends LookupHelper {
      JCTree.JCMemberReference referenceTree;

      ReferenceLookupHelper(JCTree.JCMemberReference var2, Name var3, Type var4, List var5, List var6, MethodResolutionPhase var7) {
         super(var3, var4, var5, var6, var7);
         this.referenceTree = var2;
      }

      ReferenceLookupHelper unboundLookup(Infer.InferenceContext var1) {
         return new ReferenceLookupHelper(this.referenceTree, this.name, this.site, this.argtypes, this.typeargtypes, this.maxPhase) {
            ReferenceLookupHelper unboundLookup(Infer.InferenceContext var1) {
               return this;
            }

            Symbol lookup(Env var1, MethodResolutionPhase var2) {
               return Resolve.this.methodNotFound;
            }

            JCTree.JCMemberReference.ReferenceKind referenceKind(Symbol var1) {
               Assert.error();
               return null;
            }
         };
      }

      abstract JCTree.JCMemberReference.ReferenceKind referenceKind(Symbol var1);

      Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Symbol var4) {
         if (var4.kind == 129) {
            AmbiguityError var5 = (AmbiguityError)var4.baseSymbol();
            var4 = var5.mergeAbstracts(this.site);
         }

         return var4;
      }
   }

   abstract class BasicLookupHelper extends LookupHelper {
      BasicLookupHelper(Name var2, Type var3, List var4, List var5) {
         this(var2, var3, var4, var5, Resolve.MethodResolutionPhase.VARARITY);
      }

      BasicLookupHelper(Name var2, Type var3, List var4, List var5, MethodResolutionPhase var6) {
         super(var2, var3, var4, var5, var6);
      }

      final Symbol lookup(Env var1, MethodResolutionPhase var2) {
         Symbol var3 = this.doLookup(var1, var2);
         if (var3.kind == 129) {
            AmbiguityError var4 = (AmbiguityError)var3.baseSymbol();
            var3 = var4.mergeAbstracts(this.site);
         }

         return var3;
      }

      abstract Symbol doLookup(Env var1, MethodResolutionPhase var2);

      Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Symbol var4) {
         if (var4.kind >= 129) {
            var4 = Resolve.this.accessMethod(var4, var2, var3, this.site, this.name, true, this.argtypes, this.typeargtypes);
         }

         return var4;
      }

      void debug(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
         Resolve.this.reportVerboseResolutionDiagnostic(var1, this.name, this.site, this.argtypes, this.typeargtypes, var2);
      }
   }

   abstract class LookupHelper {
      Name name;
      Type site;
      List argtypes;
      List typeargtypes;
      MethodResolutionPhase maxPhase;

      LookupHelper(Name var2, Type var3, List var4, List var5, MethodResolutionPhase var6) {
         this.name = var2;
         this.site = var3;
         this.argtypes = var4;
         this.typeargtypes = var5;
         this.maxPhase = var6;
      }

      final boolean shouldStop(Symbol var1, MethodResolutionPhase var2) {
         return var2.ordinal() > this.maxPhase.ordinal() || var1.kind < 128 || var1.kind == 129;
      }

      abstract Symbol lookup(Env var1, MethodResolutionPhase var2);

      void debug(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
      }

      abstract Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3, Symbol var4);
   }

   static enum SearchResultKind {
      GOOD_MATCH,
      BAD_MATCH_MORE_SPECIFIC,
      BAD_MATCH,
      NOT_APPLICABLE_MATCH;
   }

   class ResolveDeferredRecoveryMap extends DeferredAttr.RecoveryDeferredTypeMap {
      public ResolveDeferredRecoveryMap(DeferredAttr.AttrMode var2, Symbol var3, MethodResolutionPhase var4) {
         DeferredAttr var10001 = Resolve.this.deferredAttr;
         var10001.getClass();
         super(var2, var3, var4);
      }

      protected Type typeOf(DeferredAttr.DeferredType var1) {
         Type var2 = super.typeOf(var1);
         if (!var2.isErroneous()) {
            switch (TreeInfo.skipParens(var1.tree).getTag()) {
               case LAMBDA:
               case REFERENCE:
                  return var1;
               case CONDEXPR:
                  return (Type)(var2 == Type.recoveryType ? var1 : var2);
            }
         }

         return var2;
      }
   }

   interface LogResolveHelper {
      boolean resolveDiagnosticNeeded(Type var1, List var2, List var3);

      List getArgumentTypes(ResolveError var1, Symbol var2, Name var3, List var4);
   }

   static enum InterfaceLookupPhase {
      ABSTRACT_OK {
         InterfaceLookupPhase update(Symbol var1, Resolve var2) {
            return (InterfaceLookupPhase)((var1.flags() & 17920L) != 0L ? this : DEFAULT_OK);
         }
      },
      DEFAULT_OK {
         InterfaceLookupPhase update(Symbol var1, Resolve var2) {
            return this;
         }
      };

      private InterfaceLookupPhase() {
      }

      abstract InterfaceLookupPhase update(Symbol var1, Resolve var2);

      // $FF: synthetic method
      InterfaceLookupPhase(Object var3) {
         this();
      }
   }

   class LookupFilter implements Filter {
      boolean abstractOk;

      LookupFilter(boolean var2) {
         this.abstractOk = var2;
      }

      public boolean accepts(Symbol var1) {
         long var2 = var1.flags();
         return var1.kind == 16 && (var2 & 4096L) == 0L && (this.abstractOk || (var2 & 8796093022208L) != 0L || (var2 & 1024L) == 0L);
      }
   }

   public static class InapplicableMethodException extends RuntimeException {
      private static final long serialVersionUID = 0L;
      JCDiagnostic diagnostic = null;
      JCDiagnostic.Factory diags;

      InapplicableMethodException(JCDiagnostic.Factory var1) {
         this.diags = var1;
      }

      InapplicableMethodException setMessage() {
         return this.setMessage((JCDiagnostic)null);
      }

      InapplicableMethodException setMessage(String var1) {
         return this.setMessage(var1 != null ? this.diags.fragment(var1) : null);
      }

      InapplicableMethodException setMessage(String var1, Object... var2) {
         return this.setMessage(var1 != null ? this.diags.fragment(var1, var2) : null);
      }

      InapplicableMethodException setMessage(JCDiagnostic var1) {
         this.diagnostic = var1;
         return this;
      }

      public JCDiagnostic getDiagnostic() {
         return this.diagnostic;
      }
   }

   class MostSpecificCheck implements MethodCheck {
      boolean strict;
      List actuals;

      MostSpecificCheck(boolean var2, List var3) {
         this.strict = var2;
         this.actuals = var3;
      }

      public void argumentsAcceptable(Env var1, DeferredAttr.DeferredAttrContext var2, List var3, List var4, Warner var5) {
         for(var4 = Resolve.this.adjustArgs(var4, var2.msym, var3.length(), var2.phase.isVarargsRequired()); var4.nonEmpty(); this.actuals = this.actuals.isEmpty() ? this.actuals : this.actuals.tail) {
            Attr.ResultInfo var6 = this.methodCheckResult((Type)var4.head, var2, var5, (Type)this.actuals.head);
            var6.check((JCDiagnostic.DiagnosticPosition)null, (Type)var3.head);
            var3 = var3.tail;
            var4 = var4.tail;
         }

      }

      Attr.ResultInfo methodCheckResult(Type var1, DeferredAttr.DeferredAttrContext var2, Warner var3, Type var4) {
         return Resolve.this.attr.new ResultInfo(12, var1, new MostSpecificCheckContext(this.strict, var2, var3, var4));
      }

      public MethodCheck mostSpecificCheck(List var1, boolean var2) {
         Assert.error("Cannot get here!");
         return null;
      }

      class MostSpecificCheckContext extends MethodCheckContext {
         Type actual;

         public MostSpecificCheckContext(boolean var2, DeferredAttr.DeferredAttrContext var3, Warner var4, Type var5) {
            super(var2, var3, var4);
            this.actual = var5;
         }

         public boolean compatible(Type var1, Type var2, Warner var3) {
            if (Resolve.this.allowFunctionalInterfaceMostSpecific && this.unrelatedFunctionalInterfaces(var1, var2) && this.actual != null && this.actual.getTag() == TypeTag.DEFERRED) {
               DeferredAttr.DeferredType var4 = (DeferredAttr.DeferredType)this.actual;
               DeferredAttr.DeferredType.SpeculativeCache.Entry var5 = var4.speculativeCache.get(this.deferredAttrContext.msym, this.deferredAttrContext.phase);
               if (var5 != null && var5.speculativeTree != Resolve.this.deferredAttr.stuckTree) {
                  return this.functionalInterfaceMostSpecific(var1, var2, var5.speculativeTree, var3);
               }
            }

            return super.compatible(var1, var2, var3);
         }

         private boolean unrelatedFunctionalInterfaces(Type var1, Type var2) {
            return Resolve.this.types.isFunctionalInterface(var1.tsym) && Resolve.this.types.isFunctionalInterface(var2.tsym) && Resolve.this.types.asSuper(var1, var2.tsym) == null && Resolve.this.types.asSuper(var2, var1.tsym) == null;
         }

         private boolean functionalInterfaceMostSpecific(Type var1, Type var2, JCTree var3, Warner var4) {
            FunctionalInterfaceMostSpecificChecker var5 = new FunctionalInterfaceMostSpecificChecker(var1, var2, var4);
            var5.scan(var3);
            return var5.result;
         }

         class FunctionalInterfaceMostSpecificChecker extends DeferredAttr.PolyScanner {
            final Type t;
            final Type s;
            final Warner warn;
            boolean result;

            FunctionalInterfaceMostSpecificChecker(Type var2, Type var3, Warner var4) {
               this.t = var2;
               this.s = var3;
               this.warn = var4;
               this.result = true;
            }

            void skip(JCTree var1) {
               this.result &= false;
            }

            public void visitConditional(JCTree.JCConditional var1) {
               this.scan(var1.truepart);
               this.scan(var1.falsepart);
            }

            public void visitReference(JCTree.JCMemberReference var1) {
               Type var2 = Resolve.this.types.findDescriptorType(this.t);
               Type var3 = Resolve.this.types.findDescriptorType(this.s);
               if (!Resolve.this.types.isSameTypes(var2.getParameterTypes(), MostSpecificCheckContext.this.inferenceContext().asUndetVars(var3.getParameterTypes()))) {
                  this.result &= false;
               } else {
                  Type var4 = var2.getReturnType();
                  Type var5 = var3.getReturnType();
                  if (var5.hasTag(TypeTag.VOID)) {
                     this.result &= true;
                  } else if (var4.hasTag(TypeTag.VOID)) {
                     this.result &= false;
                  } else if (var4.isPrimitive() != var5.isPrimitive()) {
                     boolean var6 = var1.refPolyKind == JCTree.JCPolyExpression.PolyKind.STANDALONE && var1.sym.type.getReturnType().isPrimitive();
                     this.result &= var6 == var4.isPrimitive() && var6 != var5.isPrimitive();
                  } else {
                     this.result &= Resolve.MostSpecificCheck.MostSpecificCheckContext.super.compatible(var4, var5, this.warn);
                  }
               }

            }

            public void visitLambda(JCTree.JCLambda var1) {
               Type var2 = Resolve.this.types.findDescriptorType(this.t);
               Type var3 = Resolve.this.types.findDescriptorType(this.s);
               if (!Resolve.this.types.isSameTypes(var2.getParameterTypes(), MostSpecificCheckContext.this.inferenceContext().asUndetVars(var3.getParameterTypes()))) {
                  this.result &= false;
               } else {
                  Type var4 = var2.getReturnType();
                  Type var5 = var3.getReturnType();
                  if (var5.hasTag(TypeTag.VOID)) {
                     this.result &= true;
                  } else if (var4.hasTag(TypeTag.VOID)) {
                     this.result &= false;
                  } else {
                     Iterator var6;
                     JCTree.JCExpression var7;
                     if (MostSpecificCheckContext.this.unrelatedFunctionalInterfaces(var4, var5)) {
                        for(var6 = this.lambdaResults(var1).iterator(); var6.hasNext(); this.result &= MostSpecificCheckContext.this.functionalInterfaceMostSpecific(var4, var5, var7, this.warn)) {
                           var7 = (JCTree.JCExpression)var6.next();
                        }
                     } else {
                        boolean var8;
                        if (var4.isPrimitive() != var5.isPrimitive()) {
                           for(var6 = this.lambdaResults(var1).iterator(); var6.hasNext(); this.result &= var8 == var4.isPrimitive() && var8 != var5.isPrimitive()) {
                              var7 = (JCTree.JCExpression)var6.next();
                              var8 = var7.isStandalone() && var7.type.isPrimitive();
                           }
                        } else {
                           this.result &= Resolve.MostSpecificCheck.MostSpecificCheckContext.super.compatible(var4, var5, this.warn);
                        }
                     }
                  }
               }

            }

            private List lambdaResults(JCTree.JCLambda var1) {
               if (var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION) {
                  return List.of((JCTree.JCExpression)var1.body);
               } else {
                  final ListBuffer var2 = new ListBuffer();
                  DeferredAttr.LambdaReturnScanner var3 = new DeferredAttr.LambdaReturnScanner() {
                     public void visitReturn(JCTree.JCReturn var1) {
                        if (var1.expr != null) {
                           var2.append(var1.expr);
                        }

                     }
                  };
                  var3.scan(var1.body);
                  return var2.toList();
               }
            }
         }
      }
   }

   class MethodResultInfo extends Attr.ResultInfo {
      public MethodResultInfo(Type var2, Check.CheckContext var3) {
         Attr var10001 = Resolve.this.attr;
         var10001.getClass();
         super(12, var2, var3);
      }

      protected Type check(JCDiagnostic.DiagnosticPosition var1, Type var2) {
         if (var2.hasTag(TypeTag.DEFERRED)) {
            DeferredAttr.DeferredType var5 = (DeferredAttr.DeferredType)var2;
            return var5.check(this);
         } else {
            Type var3 = this.U(var2);
            Type var4 = var1 != null && var1.getTree() != null ? this.checkContext.inferenceContext().cachedCapture(var1.getTree(), var3, true) : Resolve.this.types.capture(var3);
            return super.check(var1, Resolve.this.chk.checkNonVoid(var1, var4));
         }
      }

      private Type U(Type var1) {
         return var1 == this.pt ? var1 : Resolve.this.types.cvarUpperBound(var1);
      }

      protected MethodResultInfo dup(Type var1) {
         return Resolve.this.new MethodResultInfo(var1, this.checkContext);
      }

      protected Attr.ResultInfo dup(Check.CheckContext var1) {
         return Resolve.this.new MethodResultInfo(this.pt, var1);
      }
   }

   abstract class MethodCheckContext implements Check.CheckContext {
      boolean strict;
      DeferredAttr.DeferredAttrContext deferredAttrContext;
      Warner rsWarner;

      public MethodCheckContext(boolean var2, DeferredAttr.DeferredAttrContext var3, Warner var4) {
         this.strict = var2;
         this.deferredAttrContext = var3;
         this.rsWarner = var4;
      }

      public boolean compatible(Type var1, Type var2, Warner var3) {
         Infer.InferenceContext var4 = this.deferredAttrContext.inferenceContext;
         return this.strict ? Resolve.this.types.isSubtypeUnchecked(var4.asUndetVar(var1), var4.asUndetVar(var2), var3) : Resolve.this.types.isConvertible(var4.asUndetVar(var1), var4.asUndetVar(var2), var3);
      }

      public void report(JCDiagnostic.DiagnosticPosition var1, JCDiagnostic var2) {
         throw Resolve.this.inapplicableMethodException.setMessage(var2);
      }

      public Warner checkWarner(JCDiagnostic.DiagnosticPosition var1, Type var2, Type var3) {
         return this.rsWarner;
      }

      public Infer.InferenceContext inferenceContext() {
         return this.deferredAttrContext.inferenceContext;
      }

      public DeferredAttr.DeferredAttrContext deferredAttrContext() {
         return this.deferredAttrContext;
      }

      public String toString() {
         return "MethodReferenceCheck";
      }
   }

   class MethodReferenceCheck extends AbstractMethodCheck {
      Infer.InferenceContext pendingInferenceContext;

      MethodReferenceCheck(Infer.InferenceContext var2) {
         super();
         this.pendingInferenceContext = var2;
      }

      void checkArg(JCDiagnostic.DiagnosticPosition var1, boolean var2, Type var3, Type var4, DeferredAttr.DeferredAttrContext var5, Warner var6) {
         Attr.ResultInfo var7 = this.methodCheckResult(var2, var4, var5, var6);
         var7.check(var1, var3);
      }

      private Attr.ResultInfo methodCheckResult(final boolean var1, Type var2, DeferredAttr.DeferredAttrContext var3, Warner var4) {
         MethodCheckContext var5 = new MethodCheckContext(!var3.phase.isBoxingRequired(), var3, var4) {
            MethodCheckDiag methodDiag;

            {
               this.methodDiag = var1 ? Resolve.MethodCheckDiag.VARARG_MISMATCH : Resolve.MethodCheckDiag.ARG_MISMATCH;
            }

            public boolean compatible(Type var1x, Type var2, Warner var3) {
               var1x = MethodReferenceCheck.this.pendingInferenceContext.asUndetVar(var1x);
               if (var1x.hasTag(TypeTag.UNDETVAR) && var2.isPrimitive()) {
                  var2 = Resolve.this.types.boxedClass(var2).type;
               }

               return super.compatible(var1x, var2, var3);
            }

            public void report(JCDiagnostic.DiagnosticPosition var1x, JCDiagnostic var2) {
               MethodReferenceCheck.this.reportMC(var1x, this.methodDiag, this.deferredAttrContext.inferenceContext, new Object[]{var2});
            }
         };
         return Resolve.this.new MethodResultInfo(var2, var5);
      }

      public MethodCheck mostSpecificCheck(List var1, boolean var2) {
         return Resolve.this.new MostSpecificCheck(var2, var1);
      }
   }

   abstract class AbstractMethodCheck implements MethodCheck {
      public void argumentsAcceptable(Env var1, DeferredAttr.DeferredAttrContext var2, List var3, List var4, Warner var5) {
         boolean var6 = var2.phase.isVarargsRequired();
         JCTree var7 = this.treeForDiagnostics(var1);
         List var8 = TreeInfo.args(var7);
         Infer.InferenceContext var9 = var2.inferenceContext;
         Type var10 = var6 ? (Type)var4.last() : null;
         if (var10 == null && var3.size() != var4.size()) {
            this.reportMC(var7, Resolve.MethodCheckDiag.ARITY_MISMATCH, var9);
         }

         while(var3.nonEmpty() && var4.head != var10) {
            JCDiagnostic.DiagnosticPosition var11 = var8 != null ? (JCDiagnostic.DiagnosticPosition)var8.head : null;
            this.checkArg(var11, false, (Type)var3.head, (Type)var4.head, var2, var5);
            var3 = var3.tail;
            var4 = var4.tail;
            var8 = var8 != null ? var8.tail : var8;
         }

         if (var4.head != var10) {
            this.reportMC(var7, Resolve.MethodCheckDiag.ARITY_MISMATCH, var9);
         }

         if (var6) {
            for(Type var13 = Resolve.this.types.elemtype(var10); var3.nonEmpty(); var8 = var8 != null ? var8.tail : var8) {
               JCDiagnostic.DiagnosticPosition var12 = var8 != null ? (JCDiagnostic.DiagnosticPosition)var8.head : null;
               this.checkArg(var12, true, (Type)var3.head, var13, var2, var5);
               var3 = var3.tail;
            }
         }

      }

      private JCTree treeForDiagnostics(Env var1) {
         return ((AttrContext)var1.info).preferredTreeForDiagnostics != null ? ((AttrContext)var1.info).preferredTreeForDiagnostics : var1.tree;
      }

      abstract void checkArg(JCDiagnostic.DiagnosticPosition var1, boolean var2, Type var3, Type var4, DeferredAttr.DeferredAttrContext var5, Warner var6);

      protected void reportMC(JCDiagnostic.DiagnosticPosition var1, MethodCheckDiag var2, Infer.InferenceContext var3, Object... var4) {
         boolean var5 = var3 != Resolve.this.infer.emptyContext;
         Object var6 = var5 ? Resolve.this.infer.inferenceException : Resolve.this.inapplicableMethodException;
         if (var5 && !var2.inferKey.equals(var2.basicKey)) {
            Object[] var7 = new Object[var4.length + 1];
            System.arraycopy(var4, 0, var7, 1, var4.length);
            var7[0] = var3.inferenceVars();
            var4 = var7;
         }

         String var8 = var5 ? var2.inferKey : var2.basicKey;
         throw ((InapplicableMethodException)var6).setMessage(Resolve.this.diags.create(JCDiagnostic.DiagnosticType.FRAGMENT, Resolve.this.log.currentSource(), var1, var8, var4));
      }

      public MethodCheck mostSpecificCheck(List var1, boolean var2) {
         return Resolve.this.nilMethodCheck;
      }
   }

   static enum MethodCheckDiag {
      ARITY_MISMATCH("arg.length.mismatch", "infer.arg.length.mismatch"),
      ARG_MISMATCH("no.conforming.assignment.exists", "infer.no.conforming.assignment.exists"),
      VARARG_MISMATCH("varargs.argument.mismatch", "infer.varargs.argument.mismatch"),
      INACCESSIBLE_VARARGS("inaccessible.varargs.type", "inaccessible.varargs.type");

      final String basicKey;
      final String inferKey;

      private MethodCheckDiag(String var3, String var4) {
         this.basicKey = var3;
         this.inferKey = var4;
      }

      String regex() {
         return String.format("([a-z]*\\.)*(%s|%s)", this.basicKey, this.inferKey);
      }
   }

   interface MethodCheck {
      void argumentsAcceptable(Env var1, DeferredAttr.DeferredAttrContext var2, List var3, List var4, Warner var5);

      MethodCheck mostSpecificCheck(List var1, boolean var2);
   }

   static enum VerboseResolutionMode {
      SUCCESS("success"),
      FAILURE("failure"),
      APPLICABLE("applicable"),
      INAPPLICABLE("inapplicable"),
      DEFERRED_INST("deferred-inference"),
      PREDEF("predef"),
      OBJECT_INIT("object-init"),
      INTERNAL("internal");

      final String opt;

      private VerboseResolutionMode(String var3) {
         this.opt = var3;
      }

      static EnumSet getVerboseResolutionMode(Options var0) {
         String var1 = var0.get("verboseResolution");
         EnumSet var2 = EnumSet.noneOf(VerboseResolutionMode.class);
         if (var1 == null) {
            return var2;
         } else {
            if (var1.contains("all")) {
               var2 = EnumSet.allOf(VerboseResolutionMode.class);
            }

            java.util.List var3 = Arrays.asList(var1.split(","));
            VerboseResolutionMode[] var4 = values();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               VerboseResolutionMode var7 = var4[var6];
               if (var3.contains(var7.opt)) {
                  var2.add(var7);
               } else if (var3.contains("-" + var7.opt)) {
                  var2.remove(var7);
               }
            }

            return var2;
         }
      }
   }
}
