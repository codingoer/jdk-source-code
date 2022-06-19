package com.sun.tools.javac.comp;

import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.GraphUtils;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Options;
import com.sun.tools.javac.util.Pair;
import com.sun.tools.javac.util.Warner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Infer {
   protected static final Context.Key inferKey = new Context.Key();
   Resolve rs;
   Check chk;
   Symtab syms;
   Types types;
   JCDiagnostic.Factory diags;
   Log log;
   boolean allowGraphInference;
   public static final Type anyPoly = new Type.JCNoType();
   protected final InferenceException inferenceException;
   static final int MAX_INCORPORATION_STEPS = 100;
   EnumSet incorporationStepsLegacy;
   EnumSet incorporationStepsGraph;
   Map incorporationCache;
   final InferenceContext emptyContext;

   public static Infer instance(Context var0) {
      Infer var1 = (Infer)var0.get(inferKey);
      if (var1 == null) {
         var1 = new Infer(var0);
      }

      return var1;
   }

   protected Infer(Context var1) {
      this.incorporationStepsLegacy = EnumSet.of(Infer.IncorporationStep.EQ_CHECK_LEGACY);
      this.incorporationStepsGraph = EnumSet.complementOf(EnumSet.of(Infer.IncorporationStep.EQ_CHECK_LEGACY));
      this.incorporationCache = new HashMap();
      this.emptyContext = new InferenceContext(List.nil());
      var1.put((Context.Key)inferKey, (Object)this);
      this.rs = Resolve.instance(var1);
      this.chk = Check.instance(var1);
      this.syms = Symtab.instance(var1);
      this.types = Types.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.log = Log.instance(var1);
      this.inferenceException = new InferenceException(this.diags);
      Options var2 = Options.instance(var1);
      this.allowGraphInference = Source.instance(var1).allowGraphInference() && var2.isUnset("useLegacyInference");
   }

   Type instantiateMethod(Env var1, List var2, Type.MethodType var3, Attr.ResultInfo var4, Symbol.MethodSymbol var5, List var6, boolean var7, boolean var8, Resolve.MethodResolutionContext var9, Warner var10) throws InferenceException {
      InferenceContext var11 = new InferenceContext(var2);
      this.inferenceException.clear();

      Type.MethodType var18;
      try {
         DeferredAttr.DeferredAttrContext var12 = var9.deferredAttrContext(var5, var11, var4, var10);
         var9.methodCheck.argumentsAcceptable(var1, var12, var6, var3.getParameterTypes(), var10);
         if (this.allowGraphInference && var4 != null && !var10.hasNonSilentLint(Lint.LintCategory.UNCHECKED)) {
            this.checkWithinBounds(var11, var10);
            Type var13 = this.generateReturnConstraints(var1.tree, var4, var3, var11);
            var3 = (Type.MethodType)this.types.createMethodTypeWithReturn(var3, var13);
            if (var4.checkContext.inferenceContext().free(var4.pt)) {
               var11.dupTo(var4.checkContext.inferenceContext());
               var12.complete();
               Type.MethodType var14 = var3;
               return var14;
            }
         }

         var12.complete();
         if (this.allowGraphInference) {
            var11.solve(var10);
         } else {
            var11.solveLegacy(true, var10, Infer.LegacyInferenceSteps.EQ_LOWER.steps);
         }

         var3 = (Type.MethodType)var11.asInstType(var3);
         if (!this.allowGraphInference && var11.restvars().nonEmpty() && var4 != null && !var10.hasNonSilentLint(Lint.LintCategory.UNCHECKED)) {
            this.generateReturnConstraints(var1.tree, var4, var3, var11);
            var11.solveLegacy(false, var10, Infer.LegacyInferenceSteps.EQ_UPPER.steps);
            var3 = (Type.MethodType)var11.asInstType(var3);
         }

         if (var4 != null && this.rs.verboseResolutionMode.contains(Resolve.VerboseResolutionMode.DEFERRED_INST)) {
            this.log.note(var1.tree.pos, "deferred.method.inst", new Object[]{var5, var3, var4.pt});
         }

         var18 = var3;
      } finally {
         if (var4 == null && this.allowGraphInference) {
            var11.notifyChange(var11.boundedVars());
         } else {
            var11.notifyChange();
         }

         if (var4 == null) {
            var11.captureTypeCache.clear();
         }

      }

      return var18;
   }

   Type generateReturnConstraints(JCTree var1, Attr.ResultInfo var2, Type.MethodType var3, InferenceContext var4) {
      InferenceContext var5 = var2.checkContext.inferenceContext();
      Type var6 = var3.getReturnType();
      if (var3.getReturnType().containsAny(var4.inferencevars) && var5 != this.emptyContext) {
         var6 = this.types.capture(var6);
         Iterator var7 = var6.getTypeArguments().iterator();

         while(var7.hasNext()) {
            Type var8 = (Type)var7.next();
            if (var8.hasTag(TypeTag.TYPEVAR) && ((Type.TypeVar)var8).isCaptured()) {
               var4.addVar((Type.TypeVar)var8);
            }
         }
      }

      Type var10 = var4.asUndetVar(var6);
      Object var11 = var2.pt;
      if (var10.hasTag(TypeTag.VOID)) {
         var11 = this.syms.voidType;
      } else if (((Type)var11).hasTag(TypeTag.NONE)) {
         var11 = var6.isPrimitive() ? var6 : this.syms.objectType;
      } else if (var10.hasTag(TypeTag.UNDETVAR)) {
         if (var2.pt.isReference()) {
            var11 = this.generateReturnConstraintsUndetVarToReference(var1, (Type.UndetVar)var10, (Type)var11, var2, var4);
         } else if (((Type)var11).isPrimitive()) {
            var11 = this.generateReturnConstraintsPrimitive(var1, (Type.UndetVar)var10, (Type)var11, var2, var4);
         }
      }

      Assert.check(this.allowGraphInference || !var5.free((Type)var11), "legacy inference engine cannot handle constraints on both sides of a subtyping assertion");
      Warner var9 = new Warner();
      if (var2.checkContext.compatible(var10, var5.asUndetVar((Type)var11), var9) && (this.allowGraphInference || !var9.hasLint(Lint.LintCategory.UNCHECKED))) {
         return var6;
      } else {
         throw this.inferenceException.setMessage("infer.no.conforming.instance.exists", new Object[]{var4.restvars(), var3.getReturnType(), var11});
      }
   }

   private Type generateReturnConstraintsPrimitive(JCTree var1, Type.UndetVar var2, Type var3, Attr.ResultInfo var4, InferenceContext var5) {
      if (!this.allowGraphInference) {
         return this.types.boxedClass(var3).type;
      } else {
         Iterator var6 = var2.getBounds(Type.UndetVar.InferenceBound.EQ, Type.UndetVar.InferenceBound.UPPER, Type.UndetVar.InferenceBound.LOWER).iterator();

         Type var8;
         do {
            if (!var6.hasNext()) {
               return this.types.boxedClass(var3).type;
            }

            Type var7 = (Type)var6.next();
            var8 = this.types.unboxedType(var7);
         } while(var8 == null || var8.hasTag(TypeTag.NONE));

         return this.generateReferenceToTargetConstraint(var1, var2, var3, var4, var5);
      }
   }

   private Type generateReturnConstraintsUndetVarToReference(JCTree var1, Type.UndetVar var2, Type var3, Attr.ResultInfo var4, InferenceContext var5) {
      Type var6 = this.types.capture(var3);
      Iterator var7;
      Type var8;
      Type var9;
      if (var6 == var3) {
         var7 = var2.getBounds(Type.UndetVar.InferenceBound.EQ, Type.UndetVar.InferenceBound.LOWER).iterator();

         while(var7.hasNext()) {
            var8 = (Type)var7.next();
            var9 = this.types.capture(var8);
            if (var9 != var8) {
               return this.generateReferenceToTargetConstraint(var1, var2, var3, var4, var5);
            }
         }

         var7 = var2.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

         while(var7.hasNext()) {
            var8 = (Type)var7.next();
            Iterator var11 = var2.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

            while(var11.hasNext()) {
               Type var10 = (Type)var11.next();
               if (var8 != var10 && !var5.free(var8) && !var5.free(var10) && this.commonSuperWithDiffParameterization(var8, var10)) {
                  return this.generateReferenceToTargetConstraint(var1, var2, var3, var4, var5);
               }
            }
         }
      }

      if (var3.isParameterized()) {
         var7 = var2.getBounds(Type.UndetVar.InferenceBound.EQ, Type.UndetVar.InferenceBound.LOWER).iterator();

         while(var7.hasNext()) {
            var8 = (Type)var7.next();
            var9 = this.types.asSuper(var8, var3.tsym);
            if (var9 != null && var9.isRaw()) {
               return this.generateReferenceToTargetConstraint(var1, var2, var3, var4, var5);
            }
         }
      }

      return var3;
   }

   private boolean commonSuperWithDiffParameterization(Type var1, Type var2) {
      Pair var3 = this.getParameterizedSupers(var1, var2);
      return var3 != null && !this.types.isSameType((Type)var3.fst, (Type)var3.snd);
   }

   private Type generateReferenceToTargetConstraint(JCTree var1, Type.UndetVar var2, Type var3, Attr.ResultInfo var4, InferenceContext var5) {
      var5.solve(List.of(var2.qtype), new Warner());
      var5.notifyChange();
      Type var6 = var4.checkContext.inferenceContext().cachedCapture(var1, var2.inst, false);
      return this.types.isConvertible(var6, var4.checkContext.inferenceContext().asUndetVar(var3)) ? this.syms.objectType : var3;
   }

   private void instantiateAsUninferredVars(List var1, InferenceContext var2) {
      ListBuffer var3 = new ListBuffer();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Type var5 = (Type)var4.next();
         Type.UndetVar var6 = (Type.UndetVar)var2.asUndetVar(var5);
         List var7 = var6.getBounds(Type.UndetVar.InferenceBound.UPPER);
         if (Type.containsAny(var7, var1)) {
            Symbol.TypeVariableSymbol var8 = new Symbol.TypeVariableSymbol(4096L, var6.qtype.tsym.name, (Type)null, var6.qtype.tsym.owner);
            var8.type = new Type.TypeVar(var8, this.types.makeIntersectionType(var6.getBounds(Type.UndetVar.InferenceBound.UPPER)), (Type)null);
            var3.append(var6);
            var6.inst = var8.type;
         } else if (var7.nonEmpty()) {
            var6.inst = this.types.glb(var7);
         } else {
            var6.inst = this.syms.objectType;
         }
      }

      List var9 = var1;

      for(Iterator var10 = var3.iterator(); var10.hasNext(); var9 = var9.tail) {
         Type var11 = (Type)var10.next();
         Type.UndetVar var12 = (Type.UndetVar)var11;
         Type.TypeVar var13 = (Type.TypeVar)var12.inst;
         var13.bound = this.types.glb(var2.asInstTypes(this.types.getBounds(var13)));
         if (var13.bound.isErroneous()) {
            this.reportBoundError(var12, Infer.BoundErrorKind.BAD_UPPER);
         }
      }

   }

   Type instantiatePolymorphicSignatureInstance(Env var1, Symbol.MethodSymbol var2, Resolve.MethodResolutionContext var3, List var4) {
      Object var5;
      switch (var1.next.tree.getTag()) {
         case TYPECAST:
            JCTree.JCTypeCast var6 = (JCTree.JCTypeCast)var1.next.tree;
            var5 = TreeInfo.skipParens(var6.expr) == var1.tree ? var6.clazz.type : this.syms.objectType;
            break;
         case EXEC:
            JCTree.JCExpressionStatement var7 = (JCTree.JCExpressionStatement)var1.next.tree;
            var5 = TreeInfo.skipParens(var7.expr) == var1.tree ? this.syms.voidType : this.syms.objectType;
            break;
         default:
            var5 = this.syms.objectType;
      }

      List var9 = Type.map(var4, new ImplicitArgType(var2, var3.step));
      List var10 = var2 != null ? var2.getThrownTypes() : List.of(this.syms.throwableType);
      Type.MethodType var8 = new Type.MethodType(var9, (Type)var5, var10, this.syms.methodClass);
      return var8;
   }

   public Type instantiateFunctionalInterface(JCDiagnostic.DiagnosticPosition var1, Type var2, List var3, Check.CheckContext var4) {
      if (this.types.capture(var2) == var2) {
         return var2;
      } else {
         Type var5 = var2.tsym.type;
         InferenceContext var6 = new InferenceContext(var2.tsym.type.getTypeArguments());
         Assert.check(var3 != null);
         List var7 = this.types.findDescriptorType(var5).getParameterTypes();
         if (var7.size() != var3.size()) {
            var4.report(var1, this.diags.fragment("incompatible.arg.types.in.lambda"));
            return this.types.createErrorType(var2);
         } else {
            Type var9;
            for(Iterator var8 = var7.iterator(); var8.hasNext(); var3 = var3.tail) {
               var9 = (Type)var8.next();
               if (!this.types.isSameType(var6.asUndetVar(var9), (Type)var3.head)) {
                  var4.report(var1, this.diags.fragment("no.suitable.functional.intf.inst", var2));
                  return this.types.createErrorType(var2);
               }
            }

            try {
               var6.solve(var6.boundedVars(), this.types.noWarnings);
            } catch (InferenceException var12) {
               var4.report(var1, this.diags.fragment("no.suitable.functional.intf.inst", var2));
            }

            List var13 = var2.getTypeArguments();

            for(Iterator var14 = var6.undetvars.iterator(); var14.hasNext(); var13 = var13.tail) {
               Type var10 = (Type)var14.next();
               Type.UndetVar var11 = (Type.UndetVar)var10;
               if (var11.inst == null) {
                  var11.inst = (Type)var13.head;
               }
            }

            var9 = var6.asInstType(var5);
            if (!this.chk.checkValidGenericType(var9)) {
               var4.report(var1, this.diags.fragment("no.suitable.functional.intf.inst", var2));
            }

            var4.compatible(var9, var2, this.types.noWarnings);
            return var9;
         }
      }
   }

   void checkWithinBounds(InferenceContext var1, Warner var2) throws InferenceException {
      MultiUndetVarListener var3 = new MultiUndetVarListener(var1.undetvars);
      List var4 = var1.save();

      try {
         do {
            var3.reset();
            Iterator var5;
            Type var6;
            Type.UndetVar var7;
            if (!this.allowGraphInference) {
               var5 = var1.undetvars.iterator();

               while(var5.hasNext()) {
                  var6 = (Type)var5.next();
                  var7 = (Type.UndetVar)var6;
                  Infer.IncorporationStep.CHECK_BOUNDS.apply(var7, var1, var2);
               }
            }

            var5 = var1.undetvars.iterator();

            while(var5.hasNext()) {
               var6 = (Type)var5.next();
               var7 = (Type.UndetVar)var6;
               EnumSet var8 = this.allowGraphInference ? this.incorporationStepsGraph : this.incorporationStepsLegacy;
               Iterator var9 = var8.iterator();

               while(var9.hasNext()) {
                  IncorporationStep var10 = (IncorporationStep)var9.next();
                  if (var10.accepts(var7, var1)) {
                     var10.apply(var7, var1, var2);
                  }
               }
            }
         } while(var3.changed && this.allowGraphInference);
      } finally {
         var3.detach();
         if (this.incorporationCache.size() == 100) {
            var1.rollback(var4);
         }

         this.incorporationCache.clear();
      }

   }

   private Pair getParameterizedSupers(Type var1, Type var2) {
      Type var3 = this.types.lub(var1, var2);
      if (var3 != this.syms.errType && var3 != this.syms.botType && var3.isParameterized()) {
         Type var4 = this.types.asSuper(var1, var3.tsym);
         Type var5 = this.types.asSuper(var2, var3.tsym);
         return new Pair(var4, var5);
      } else {
         return null;
      }
   }

   void checkCompatibleUpperBounds(Type.UndetVar var1, InferenceContext var2) {
      List var3 = Type.filter(var1.getBounds(Type.UndetVar.InferenceBound.UPPER), new BoundFilter(var2));
      Type var4 = null;
      if (var3.isEmpty()) {
         var4 = this.syms.objectType;
      } else if (var3.tail.isEmpty()) {
         var4 = (Type)var3.head;
      } else {
         var4 = this.types.glb(var3);
      }

      if (var4 == null || var4.isErroneous()) {
         this.reportBoundError(var1, Infer.BoundErrorKind.BAD_UPPER);
      }

   }

   void reportBoundError(Type.UndetVar var1, BoundErrorKind var2) {
      throw var2.setMessage(this.inferenceException, var1);
   }

   class InferenceContext {
      List undetvars;
      List inferencevars;
      Map freeTypeListeners = new HashMap();
      List freetypeListeners = List.nil();
      Type.Mapping fromTypeVarFun = new Type.Mapping("fromTypeVarFunWithBounds") {
         public Type apply(Type var1) {
            if (var1.hasTag(TypeTag.TYPEVAR)) {
               Type.TypeVar var2 = (Type.TypeVar)var1;
               return (Type)(var2.isCaptured() ? new Type.CapturedUndetVar((Type.CapturedType)var2, Infer.this.types) : new Type.UndetVar(var2, Infer.this.types));
            } else {
               return var1.map(this);
            }
         }
      };
      Map captureTypeCache = new HashMap();

      public InferenceContext(List var2) {
         this.undetvars = Type.map(var2, this.fromTypeVarFun);
         this.inferencevars = var2;
      }

      void addVar(Type.TypeVar var1) {
         this.undetvars = this.undetvars.prepend(this.fromTypeVarFun.apply(var1));
         this.inferencevars = this.inferencevars.prepend(var1);
      }

      List inferenceVars() {
         return this.inferencevars;
      }

      List restvars() {
         return this.filterVars(new Filter() {
            public boolean accepts(Type.UndetVar var1) {
               return var1.inst == null;
            }
         });
      }

      List instvars() {
         return this.filterVars(new Filter() {
            public boolean accepts(Type.UndetVar var1) {
               return var1.inst != null;
            }
         });
      }

      final List boundedVars() {
         return this.filterVars(new Filter() {
            public boolean accepts(Type.UndetVar var1) {
               return var1.getBounds(Type.UndetVar.InferenceBound.UPPER).diff(var1.getDeclaredBounds()).appendList(var1.getBounds(Type.UndetVar.InferenceBound.EQ, Type.UndetVar.InferenceBound.LOWER)).nonEmpty();
            }
         });
      }

      private List filterVars(Filter var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = this.undetvars.iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            Type.UndetVar var5 = (Type.UndetVar)var4;
            if (var1.accepts(var5)) {
               var2.append(var5.qtype);
            }
         }

         return var2.toList();
      }

      final boolean free(Type var1) {
         return var1.containsAny(this.inferencevars);
      }

      final boolean free(List var1) {
         Iterator var2 = var1.iterator();

         Type var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (Type)var2.next();
         } while(!this.free(var3));

         return true;
      }

      final List freeVarsIn(Type var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = this.inferenceVars().iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            if (var1.contains(var4)) {
               var2.add(var4);
            }
         }

         return var2.toList();
      }

      final List freeVarsIn(List var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            var2.appendList(this.freeVarsIn(var4));
         }

         ListBuffer var6 = new ListBuffer();
         Iterator var7 = var2.iterator();

         while(var7.hasNext()) {
            Type var5 = (Type)var7.next();
            if (!var6.contains(var5)) {
               var6.add(var5);
            }
         }

         return var6.toList();
      }

      final Type asUndetVar(Type var1) {
         return Infer.this.types.subst(var1, this.inferencevars, this.undetvars);
      }

      final List asUndetVars(List var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            var2.append(this.asUndetVar(var4));
         }

         return var2.toList();
      }

      List instTypes() {
         ListBuffer var1 = new ListBuffer();
         Iterator var2 = this.undetvars.iterator();

         while(var2.hasNext()) {
            Type var3 = (Type)var2.next();
            Type.UndetVar var4 = (Type.UndetVar)var3;
            var1.append(var4.inst != null ? var4.inst : var4.qtype);
         }

         return var1.toList();
      }

      Type asInstType(Type var1) {
         return Infer.this.types.subst(var1, this.inferencevars, this.instTypes());
      }

      List asInstTypes(List var1) {
         ListBuffer var2 = new ListBuffer();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            Type var4 = (Type)var3.next();
            var2.append(this.asInstType(var4));
         }

         return var2.toList();
      }

      void addFreeTypeListener(List var1, FreeTypeListener var2) {
         this.freeTypeListeners.put(var2, this.freeVarsIn(var1));
      }

      void notifyChange() {
         this.notifyChange(this.inferencevars.diff(this.restvars()));
      }

      void notifyChange(List var1) {
         InferenceException var2 = null;
         Iterator var3 = (new HashMap(this.freeTypeListeners)).entrySet().iterator();

         while(true) {
            Map.Entry var4;
            do {
               if (!var3.hasNext()) {
                  if (var2 != null) {
                     throw var2;
                  }

                  return;
               }

               var4 = (Map.Entry)var3.next();
            } while(Type.containsAny((List)var4.getValue(), this.inferencevars.diff(var1)));

            try {
               ((FreeTypeListener)var4.getKey()).typesInferred(this);
               this.freeTypeListeners.remove(var4.getKey());
            } catch (InferenceException var6) {
               if (var2 == null) {
                  var2 = var6;
               }
            }
         }
      }

      List save() {
         ListBuffer var1 = new ListBuffer();
         Iterator var2 = this.undetvars.iterator();

         while(var2.hasNext()) {
            Type var3 = (Type)var2.next();
            Type.UndetVar var4 = (Type.UndetVar)var3;
            Type.UndetVar var5 = new Type.UndetVar((Type.TypeVar)var4.qtype, Infer.this.types);
            Type.UndetVar.InferenceBound[] var6 = Type.UndetVar.InferenceBound.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Type.UndetVar.InferenceBound var9 = var6[var8];
               Iterator var10 = var4.getBounds(var9).iterator();

               while(var10.hasNext()) {
                  Type var11 = (Type)var10.next();
                  var5.addBound(var9, var11, Infer.this.types);
               }
            }

            var5.inst = var4.inst;
            var1.add(var5);
         }

         return var1.toList();
      }

      void rollback(List var1) {
         Assert.check(var1 != null && var1.length() == this.undetvars.length());

         for(Iterator var2 = this.undetvars.iterator(); var2.hasNext(); var1 = var1.tail) {
            Type var3 = (Type)var2.next();
            Type.UndetVar var4 = (Type.UndetVar)var3;
            Type.UndetVar var5 = (Type.UndetVar)var1.head;
            Type.UndetVar.InferenceBound[] var6 = Type.UndetVar.InferenceBound.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Type.UndetVar.InferenceBound var9 = var6[var8];
               var4.setBounds(var9, var5.getBounds(var9));
            }

            var4.inst = var5.inst;
         }

      }

      void dupTo(InferenceContext var1) {
         var1.inferencevars = var1.inferencevars.appendList(this.inferencevars.diff(var1.inferencevars));
         var1.undetvars = var1.undetvars.appendList(this.undetvars.diff(var1.undetvars));
         Iterator var2 = this.inferencevars.iterator();

         while(var2.hasNext()) {
            Type var3 = (Type)var2.next();
            var1.freeTypeListeners.put(new FreeTypeListener() {
               public void typesInferred(InferenceContext var1) {
                  InferenceContext.this.notifyChange();
               }
            }, List.of(var3));
         }

      }

      private void solve(GraphStrategy var1, Warner var2) {
         this.solve(var1, new HashMap(), var2);
      }

      private void solve(GraphStrategy var1, Map var2, Warner var3) {
         GraphSolver var4 = Infer.this.new GraphSolver(this, var2, var3);
         var4.solve(var1);
      }

      public void solve(Warner var1) {
         this.solve((GraphStrategy)(new LeafSolver() {
            public boolean done() {
               return InferenceContext.this.restvars().isEmpty();
            }
         }), var1);
      }

      public void solve(final List var1, Warner var2) {
         this.solve((GraphStrategy)(new BestLeafSolver(var1) {
            public boolean done() {
               return !InferenceContext.this.free(InferenceContext.this.asInstTypes(var1));
            }
         }), var2);
      }

      public void solveAny(List var1, Map var2, Warner var3) {
         this.solve(new BestLeafSolver(var1.intersect(this.restvars())) {
            public boolean done() {
               return InferenceContext.this.instvars().intersect(this.varsToSolve).nonEmpty();
            }
         }, var2, var3);
      }

      private boolean solveBasic(EnumSet var1) {
         return this.solveBasic(this.inferencevars, var1);
      }

      private boolean solveBasic(List var1, EnumSet var2) {
         boolean var3 = false;
         Iterator var4 = var1.intersect(this.restvars()).iterator();

         while(true) {
            while(var4.hasNext()) {
               Type var5 = (Type)var4.next();
               Type.UndetVar var6 = (Type.UndetVar)this.asUndetVar(var5);
               Iterator var7 = var2.iterator();

               while(var7.hasNext()) {
                  InferenceStep var8 = (InferenceStep)var7.next();
                  if (var8.accepts(var6, this)) {
                     var6.inst = var8.solve(var6, this);
                     var3 = true;
                     break;
                  }
               }
            }

            return var3;
         }
      }

      public void solveLegacy(boolean var1, Warner var2, EnumSet var3) {
         label28:
         while(true) {
            boolean var4 = !this.solveBasic(var3);
            if (!this.restvars().isEmpty() && !var1) {
               if (!var4) {
                  Iterator var5 = this.undetvars.iterator();

                  while(true) {
                     if (!var5.hasNext()) {
                        continue label28;
                     }

                     Type var6 = (Type)var5.next();
                     Type.UndetVar var7 = (Type.UndetVar)var6;
                     var7.substBounds(this.inferenceVars(), this.instTypes(), Infer.this.types);
                  }
               }

               Infer.this.instantiateAsUninferredVars(this.restvars(), this);
            }

            Infer.this.checkWithinBounds(this, var2);
            return;
         }
      }

      private Infer infer() {
         return Infer.this;
      }

      public String toString() {
         return "Inference vars: " + this.inferencevars + '\n' + "Undet vars: " + this.undetvars;
      }

      Type cachedCapture(JCTree var1, Type var2, boolean var3) {
         Type var4 = (Type)this.captureTypeCache.get(var1);
         if (var4 != null) {
            return var4;
         } else {
            Type var5 = Infer.this.types.capture(var2);
            if (var5 != var2 && !var3) {
               this.captureTypeCache.put(var1, var5);
            }

            return var5;
         }
      }
   }

   interface FreeTypeListener {
      void typesInferred(InferenceContext var1);
   }

   class GraphSolver {
      InferenceContext inferenceContext;
      Map stuckDeps;
      Warner warn;

      GraphSolver(InferenceContext var2, Map var3, Warner var4) {
         this.inferenceContext = var2;
         this.stuckDeps = var3;
         this.warn = var4;
      }

      void solve(GraphStrategy var1) {
         Infer.this.checkWithinBounds(this.inferenceContext, this.warn);

         InferenceGraph.Node var3;
         for(InferenceGraph var2 = new InferenceGraph(this.stuckDeps); !var1.done(); var2.deleteNode(var3)) {
            var3 = var1.pickNode(var2);
            List var4 = List.from((Iterable)var3.data);
            List var5 = this.inferenceContext.save();

            try {
               label33:
               for(; Type.containsAny(this.inferenceContext.restvars(), var4); Infer.this.checkWithinBounds(this.inferenceContext, this.warn)) {
                  GraphInferenceSteps[] var6 = Infer.GraphInferenceSteps.values();
                  int var7 = var6.length;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     GraphInferenceSteps var9 = var6[var8];
                     if (this.inferenceContext.solveBasic(var4, var9.steps)) {
                        continue label33;
                     }
                  }

                  throw Infer.this.inferenceException.setMessage();
               }
            } catch (InferenceException var10) {
               this.inferenceContext.rollback(var5);
               Infer.this.instantiateAsUninferredVars(var4, this.inferenceContext);
               Infer.this.checkWithinBounds(this.inferenceContext, this.warn);
            }
         }

      }

      class InferenceGraph {
         ArrayList nodes;

         InferenceGraph(Map var2) {
            this.initNodes(var2);
         }

         public Node findNode(Type var1) {
            Iterator var2 = this.nodes.iterator();

            Node var3;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (Node)var2.next();
            } while(!((ListBuffer)var3.data).contains(var1));

            return var3;
         }

         public void deleteNode(Node var1) {
            Assert.check(this.nodes.contains(var1));
            this.nodes.remove(var1);
            this.notifyUpdate(var1, (Node)null);
         }

         void notifyUpdate(Node var1, Node var2) {
            Iterator var3 = this.nodes.iterator();

            while(var3.hasNext()) {
               Node var4 = (Node)var3.next();
               var4.graphChanged(var1, var2);
            }

         }

         void initNodes(Map var1) {
            this.nodes = new ArrayList();
            Iterator var2 = GraphSolver.this.inferenceContext.restvars().iterator();

            while(var2.hasNext()) {
               Type var3 = (Type)var2.next();
               this.nodes.add(new Node(var3));
            }

            var2 = this.nodes.iterator();

            Iterator var6;
            Node var7;
            while(var2.hasNext()) {
               Node var11 = (Node)var2.next();
               Type var4 = (Type)((ListBuffer)var11.data).first();
               Set var5 = (Set)var1.get(var4);
               var6 = this.nodes.iterator();

               while(var6.hasNext()) {
                  var7 = (Node)var6.next();
                  Type var8 = (Type)((ListBuffer)var7.data).first();
                  Type.UndetVar var9 = (Type.UndetVar)GraphSolver.this.inferenceContext.asUndetVar(var4);
                  if (Type.containsAny(var9.getBounds(Type.UndetVar.InferenceBound.values()), List.of(var8))) {
                     var11.addDependency(Infer.DependencyKind.BOUND, var7);
                  }

                  if (var5 != null && var5.contains(var8)) {
                     var11.addDependency(Infer.DependencyKind.STUCK, var7);
                  }
               }
            }

            ArrayList var10 = new ArrayList();

            List var13;
            for(Iterator var12 = GraphUtils.tarjan(this.nodes).iterator(); var12.hasNext(); var10.add(var13.head)) {
               var13 = (List)var12.next();
               if (var13.length() > 1) {
                  Node var14 = (Node)var13.head;
                  var14.mergeWith(var13.tail);
                  var6 = var13.iterator();

                  while(var6.hasNext()) {
                     var7 = (Node)var6.next();
                     this.notifyUpdate(var7, var14);
                  }
               }
            }

            this.nodes = var10;
         }

         String toDot() {
            StringBuilder var1 = new StringBuilder();
            Iterator var2 = GraphSolver.this.inferenceContext.undetvars.iterator();

            while(var2.hasNext()) {
               Type var3 = (Type)var2.next();
               Type.UndetVar var4 = (Type.UndetVar)var3;
               var1.append(String.format("var %s - upper bounds = %s, lower bounds = %s, eq bounds = %s\\n", var4.qtype, var4.getBounds(Type.UndetVar.InferenceBound.UPPER), var4.getBounds(Type.UndetVar.InferenceBound.LOWER), var4.getBounds(Type.UndetVar.InferenceBound.EQ)));
            }

            return GraphUtils.toDot(this.nodes, "inferenceGraph" + this.hashCode(), var1.toString());
         }

         class Node extends GraphUtils.TarjanNode {
            EnumMap deps = new EnumMap(DependencyKind.class);

            Node(Type var2) {
               super(ListBuffer.of(var2));
            }

            public GraphUtils.DependencyKind[] getSupportedDependencyKinds() {
               return Infer.DependencyKind.values();
            }

            public String getDependencyName(GraphUtils.Node var1, GraphUtils.DependencyKind var2) {
               if (var2 == Infer.DependencyKind.STUCK) {
                  return "";
               } else {
                  StringBuilder var3 = new StringBuilder();
                  String var4 = "";
                  Iterator var5 = ((ListBuffer)this.data).iterator();

                  while(var5.hasNext()) {
                     Type var6 = (Type)var5.next();
                     Type.UndetVar var7 = (Type.UndetVar)GraphSolver.this.inferenceContext.asUndetVar(var6);
                     Iterator var8 = var7.getBounds(Type.UndetVar.InferenceBound.values()).iterator();

                     while(var8.hasNext()) {
                        Type var9 = (Type)var8.next();
                        if (var9.containsAny(List.from((Iterable)var1.data))) {
                           var3.append(var4);
                           var3.append(var9);
                           var4 = ",";
                        }
                     }
                  }

                  return var3.toString();
               }
            }

            public Iterable getAllDependencies() {
               return this.getDependencies(Infer.DependencyKind.values());
            }

            public Iterable getDependenciesByKind(GraphUtils.DependencyKind var1) {
               return this.getDependencies((DependencyKind)var1);
            }

            protected Set getDependencies(DependencyKind... var1) {
               LinkedHashSet var2 = new LinkedHashSet();
               DependencyKind[] var3 = var1;
               int var4 = var1.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  DependencyKind var6 = var3[var5];
                  Set var7 = (Set)this.deps.get(var6);
                  if (var7 != null) {
                     var2.addAll(var7);
                  }
               }

               return var2;
            }

            protected void addDependency(DependencyKind var1, Node var2) {
               Object var3 = (Set)this.deps.get(var1);
               if (var3 == null) {
                  var3 = new LinkedHashSet();
                  this.deps.put(var1, var3);
               }

               ((Set)var3).add(var2);
            }

            protected void addDependencies(DependencyKind var1, Set var2) {
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  Node var4 = (Node)var3.next();
                  this.addDependency(var1, var4);
               }

            }

            protected Set removeDependency(Node var1) {
               HashSet var2 = new HashSet();
               DependencyKind[] var3 = Infer.DependencyKind.values();
               int var4 = var3.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  DependencyKind var6 = var3[var5];
                  Set var7 = (Set)this.deps.get(var6);
                  if (var7 != null && var7.remove(var1)) {
                     var2.add(var6);
                  }
               }

               return var2;
            }

            protected Set closure(DependencyKind... var1) {
               boolean var2 = true;
               HashSet var3 = new HashSet();
               var3.add(this);

               while(var2) {
                  var2 = false;

                  Node var5;
                  for(Iterator var4 = (new HashSet(var3)).iterator(); var4.hasNext(); var2 = var3.addAll(var5.getDependencies(var1))) {
                     var5 = (Node)var4.next();
                  }
               }

               return var3;
            }

            protected boolean isLeaf() {
               Set var1 = this.getDependencies(Infer.DependencyKind.BOUND, Infer.DependencyKind.STUCK);
               if (var1.isEmpty()) {
                  return true;
               } else {
                  Iterator var2 = var1.iterator();

                  Node var3;
                  do {
                     if (!var2.hasNext()) {
                        return true;
                     }

                     var3 = (Node)var2.next();
                  } while(var3 == this);

                  return false;
               }
            }

            protected void mergeWith(List var1) {
               Iterator var2 = var1.iterator();

               int var5;
               while(var2.hasNext()) {
                  Node var3 = (Node)var2.next();
                  Assert.check(((ListBuffer)var3.data).length() == 1, "Attempt to merge a compound node!");
                  ((ListBuffer)this.data).appendList((ListBuffer)var3.data);
                  DependencyKind[] var4 = Infer.DependencyKind.values();
                  var5 = var4.length;

                  for(int var6 = 0; var6 < var5; ++var6) {
                     DependencyKind var7 = var4[var6];
                     this.addDependencies(var7, var3.getDependencies(var7));
                  }
               }

               EnumMap var10 = new EnumMap(DependencyKind.class);
               DependencyKind[] var11 = Infer.DependencyKind.values();
               int var12 = var11.length;

               for(var5 = 0; var5 < var12; ++var5) {
                  DependencyKind var13 = var11[var5];
                  Iterator var14 = this.getDependencies(var13).iterator();

                  while(var14.hasNext()) {
                     Node var8 = (Node)var14.next();
                     Object var9 = (Set)var10.get(var13);
                     if (var9 == null) {
                        var9 = new LinkedHashSet();
                        var10.put(var13, var9);
                     }

                     if (((ListBuffer)this.data).contains(((ListBuffer)var8.data).first())) {
                        ((Set)var9).add(this);
                     } else {
                        ((Set)var9).add(var8);
                     }
                  }
               }

               this.deps = var10;
            }

            private void graphChanged(Node var1, Node var2) {
               Iterator var3 = this.removeDependency(var1).iterator();

               while(var3.hasNext()) {
                  DependencyKind var4 = (DependencyKind)var3.next();
                  if (var2 != null) {
                     this.addDependency(var4, var2);
                  }
               }

            }
         }
      }
   }

   static enum DependencyKind implements GraphUtils.DependencyKind {
      BOUND("dotted"),
      STUCK("dashed");

      final String dotSyle;

      private DependencyKind(String var3) {
         this.dotSyle = var3;
      }

      public String getDotStyle() {
         return this.dotSyle;
      }
   }

   static enum GraphInferenceSteps {
      EQ(EnumSet.of(Infer.InferenceStep.EQ)),
      EQ_LOWER(EnumSet.of(Infer.InferenceStep.EQ, Infer.InferenceStep.LOWER)),
      EQ_LOWER_THROWS_UPPER_CAPTURED(EnumSet.of(Infer.InferenceStep.EQ, Infer.InferenceStep.LOWER, Infer.InferenceStep.UPPER, Infer.InferenceStep.THROWS, Infer.InferenceStep.CAPTURED));

      final EnumSet steps;

      private GraphInferenceSteps(EnumSet var3) {
         this.steps = var3;
      }
   }

   static enum LegacyInferenceSteps {
      EQ_LOWER(EnumSet.of(Infer.InferenceStep.EQ, Infer.InferenceStep.LOWER)),
      EQ_UPPER(EnumSet.of(Infer.InferenceStep.EQ, Infer.InferenceStep.UPPER_LEGACY));

      final EnumSet steps;

      private LegacyInferenceSteps(EnumSet var3) {
         this.steps = var3;
      }
   }

   static enum InferenceStep {
      EQ(Type.UndetVar.InferenceBound.EQ) {
         Type solve(Type.UndetVar var1, InferenceContext var2) {
            return (Type)this.filterBounds(var1, var2).head;
         }
      },
      LOWER(Type.UndetVar.InferenceBound.LOWER) {
         Type solve(Type.UndetVar var1, InferenceContext var2) {
            Infer var3 = var2.infer();
            List var4 = this.filterBounds(var1, var2);
            Type var5 = var4.tail.tail == null ? (Type)var4.head : var3.types.lub(var4);
            if (!var5.isPrimitive() && !var5.hasTag(TypeTag.ERROR)) {
               return var5;
            } else {
               throw var3.inferenceException.setMessage("no.unique.minimal.instance.exists", new Object[]{var1.qtype, var4});
            }
         }
      },
      THROWS(Type.UndetVar.InferenceBound.UPPER) {
         public boolean accepts(Type.UndetVar var1, InferenceContext var2) {
            if ((var1.qtype.tsym.flags() & 140737488355328L) == 0L) {
               return false;
            } else if (var1.getBounds(Type.UndetVar.InferenceBound.EQ, Type.UndetVar.InferenceBound.LOWER, Type.UndetVar.InferenceBound.UPPER).diff(var1.getDeclaredBounds()).nonEmpty()) {
               return false;
            } else {
               Infer var3 = var2.infer();
               Iterator var4 = var1.getDeclaredBounds().iterator();

               Type var5;
               do {
                  if (!var4.hasNext()) {
                     return false;
                  }

                  var5 = (Type)var4.next();
               } while(var1.isInterface() || var3.types.asSuper(var3.syms.runtimeExceptionType, var5.tsym) == null);

               return true;
            }
         }

         Type solve(Type.UndetVar var1, InferenceContext var2) {
            return var2.infer().syms.runtimeExceptionType;
         }
      },
      UPPER(Type.UndetVar.InferenceBound.UPPER) {
         Type solve(Type.UndetVar var1, InferenceContext var2) {
            Infer var3 = var2.infer();
            List var4 = this.filterBounds(var1, var2);
            Type var5 = var4.tail.tail == null ? (Type)var4.head : var3.types.glb(var4);
            if (!var5.isPrimitive() && !var5.hasTag(TypeTag.ERROR)) {
               return var5;
            } else {
               throw var3.inferenceException.setMessage("no.unique.maximal.instance.exists", new Object[]{var1.qtype, var4});
            }
         }
      },
      UPPER_LEGACY(Type.UndetVar.InferenceBound.UPPER) {
         public boolean accepts(Type.UndetVar var1, InferenceContext var2) {
            return !var2.free(var1.getBounds(this.ib)) && !var1.isCaptured();
         }

         Type solve(Type.UndetVar var1, InferenceContext var2) {
            return UPPER.solve(var1, var2);
         }
      },
      CAPTURED(Type.UndetVar.InferenceBound.UPPER) {
         public boolean accepts(Type.UndetVar var1, InferenceContext var2) {
            return var1.isCaptured() && !var2.free(var1.getBounds(Type.UndetVar.InferenceBound.UPPER, Type.UndetVar.InferenceBound.LOWER));
         }

         Type solve(Type.UndetVar var1, InferenceContext var2) {
            Infer var3 = var2.infer();
            Type var4 = UPPER.filterBounds(var1, var2).nonEmpty() ? UPPER.solve(var1, var2) : var3.syms.objectType;
            Type var5 = LOWER.filterBounds(var1, var2).nonEmpty() ? LOWER.solve(var1, var2) : var3.syms.botType;
            Type.CapturedType var6 = (Type.CapturedType)var1.qtype;
            return new Type.CapturedType(var6.tsym.name, var6.tsym.owner, var4, var5, var6.wildcard);
         }
      };

      final Type.UndetVar.InferenceBound ib;

      private InferenceStep(Type.UndetVar.InferenceBound var3) {
         this.ib = var3;
      }

      abstract Type solve(Type.UndetVar var1, InferenceContext var2);

      public boolean accepts(Type.UndetVar var1, InferenceContext var2) {
         return this.filterBounds(var1, var2).nonEmpty() && !var1.isCaptured();
      }

      List filterBounds(Type.UndetVar var1, InferenceContext var2) {
         return Type.filter(var1.getBounds(this.ib), new BoundFilter(var2));
      }

      // $FF: synthetic method
      InferenceStep(Type.UndetVar.InferenceBound var3, Object var4) {
         this(var3);
      }
   }

   abstract class BestLeafSolver extends LeafSolver {
      List varsToSolve;
      final Map treeCache = new HashMap();
      final Pair noPath = new Pair((Object)null, Integer.MAX_VALUE);

      BestLeafSolver(List var2) {
         super();
         this.varsToSolve = var2;
      }

      Pair computeTreeToLeafs(GraphSolver.InferenceGraph.Node var1) {
         Pair var2 = (Pair)this.treeCache.get(var1);
         if (var2 == null) {
            if (var1.isLeaf()) {
               var2 = new Pair(List.of(var1), ((ListBuffer)var1.data).length());
            } else {
               Pair var3 = new Pair(List.of(var1), ((ListBuffer)var1.data).length());
               Iterator var4 = var1.getAllDependencies().iterator();

               while(var4.hasNext()) {
                  GraphSolver.InferenceGraph.Node var5 = (GraphSolver.InferenceGraph.Node)var4.next();
                  if (var5 != var1) {
                     Pair var6 = this.computeTreeToLeafs(var5);
                     var3 = new Pair(((List)var3.fst).prependList((List)var6.fst), (Integer)var3.snd + (Integer)var6.snd);
                  }
               }

               var2 = var3;
            }

            this.treeCache.put(var1, var2);
         }

         return var2;
      }

      public GraphSolver.InferenceGraph.Node pickNode(GraphSolver.InferenceGraph var1) {
         this.treeCache.clear();
         Pair var2 = this.noPath;
         Iterator var3 = var1.nodes.iterator();

         while(var3.hasNext()) {
            GraphSolver.InferenceGraph.Node var4 = (GraphSolver.InferenceGraph.Node)var3.next();
            if (!Collections.disjoint((Collection)var4.data, this.varsToSolve)) {
               Pair var5 = this.computeTreeToLeafs(var4);
               if ((Integer)var5.snd < (Integer)var2.snd) {
                  var2 = var5;
               }
            }
         }

         if (var2 == this.noPath) {
            throw new GraphStrategy.NodeNotFoundException(var1);
         } else {
            return (GraphSolver.InferenceGraph.Node)((List)var2.fst).head;
         }
      }
   }

   abstract class LeafSolver implements GraphStrategy {
      public GraphSolver.InferenceGraph.Node pickNode(GraphSolver.InferenceGraph var1) {
         if (var1.nodes.isEmpty()) {
            throw new GraphStrategy.NodeNotFoundException(var1);
         } else {
            return (GraphSolver.InferenceGraph.Node)var1.nodes.get(0);
         }
      }

      boolean isSubtype(Type var1, Type var2, Warner var3, Infer var4) {
         return this.doIncorporationOp(Infer.IncorporationBinaryOpKind.IS_SUBTYPE, var1, var2, var3, var4);
      }

      boolean isSameType(Type var1, Type var2, Infer var3) {
         return this.doIncorporationOp(Infer.IncorporationBinaryOpKind.IS_SAME_TYPE, var1, var2, (Warner)null, var3);
      }

      void addBound(Type.UndetVar.InferenceBound var1, Type.UndetVar var2, Type var3, Infer var4) {
         this.doIncorporationOp(this.opFor(var1), var2, var3, (Warner)null, var4);
      }

      IncorporationBinaryOpKind opFor(Type.UndetVar.InferenceBound var1) {
         switch (var1) {
            case EQ:
               return Infer.IncorporationBinaryOpKind.ADD_EQ_BOUND;
            case LOWER:
               return Infer.IncorporationBinaryOpKind.ADD_LOWER_BOUND;
            case UPPER:
               return Infer.IncorporationBinaryOpKind.ADD_UPPER_BOUND;
            default:
               Assert.error("Can't get here!");
               return null;
         }
      }

      boolean doIncorporationOp(IncorporationBinaryOpKind var1, Type var2, Type var3, Warner var4, Infer var5) {
         IncorporationBinaryOp var6 = var5.new IncorporationBinaryOp(var1, var2, var3);
         Boolean var7 = (Boolean)var5.incorporationCache.get(var6);
         if (var7 == null) {
            var5.incorporationCache.put(var6, var7 = var6.apply(var4));
         }

         return var7;
      }
   }

   interface GraphStrategy {
      GraphSolver.InferenceGraph.Node pickNode(GraphSolver.InferenceGraph var1) throws NodeNotFoundException;

      boolean done();

      public static class NodeNotFoundException extends RuntimeException {
         private static final long serialVersionUID = 0L;
         GraphSolver.InferenceGraph graph;

         public NodeNotFoundException(GraphSolver.InferenceGraph var1) {
            this.graph = var1;
         }
      }
   }

   static enum BoundErrorKind {
      BAD_UPPER {
         Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2) {
            return var1.setMessage("incompatible.upper.bounds", new Object[]{var2.qtype, var2.getBounds(Type.UndetVar.InferenceBound.UPPER)});
         }
      },
      BAD_EQ_UPPER {
         Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2) {
            return var1.setMessage("incompatible.eq.upper.bounds", new Object[]{var2.qtype, var2.getBounds(Type.UndetVar.InferenceBound.EQ), var2.getBounds(Type.UndetVar.InferenceBound.UPPER)});
         }
      },
      BAD_EQ_LOWER {
         Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2) {
            return var1.setMessage("incompatible.eq.lower.bounds", new Object[]{var2.qtype, var2.getBounds(Type.UndetVar.InferenceBound.EQ), var2.getBounds(Type.UndetVar.InferenceBound.LOWER)});
         }
      },
      UPPER {
         Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2) {
            return var1.setMessage("inferred.do.not.conform.to.upper.bounds", new Object[]{var2.inst, var2.getBounds(Type.UndetVar.InferenceBound.UPPER)});
         }
      },
      LOWER {
         Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2) {
            return var1.setMessage("inferred.do.not.conform.to.lower.bounds", new Object[]{var2.inst, var2.getBounds(Type.UndetVar.InferenceBound.LOWER)});
         }
      },
      EQ {
         Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2) {
            return var1.setMessage("inferred.do.not.conform.to.eq.bounds", new Object[]{var2.inst, var2.getBounds(Type.UndetVar.InferenceBound.EQ)});
         }
      };

      private BoundErrorKind() {
      }

      abstract Resolve.InapplicableMethodException setMessage(InferenceException var1, Type.UndetVar var2);

      // $FF: synthetic method
      BoundErrorKind(Object var3) {
         this();
      }
   }

   protected static class BoundFilter implements Filter {
      InferenceContext inferenceContext;

      public BoundFilter(InferenceContext var1) {
         this.inferenceContext = var1;
      }

      public boolean accepts(Type var1) {
         return !var1.isErroneous() && !this.inferenceContext.free(var1) && !var1.hasTag(TypeTag.BOT);
      }
   }

   class IncorporationBinaryOp {
      IncorporationBinaryOpKind opKind;
      Type op1;
      Type op2;

      IncorporationBinaryOp(IncorporationBinaryOpKind var2, Type var3, Type var4) {
         this.opKind = var2;
         this.op1 = var3;
         this.op2 = var4;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof IncorporationBinaryOp)) {
            return false;
         } else {
            IncorporationBinaryOp var2 = (IncorporationBinaryOp)var1;
            return this.opKind == var2.opKind && Infer.this.types.isSameType(this.op1, var2.op1, true) && Infer.this.types.isSameType(this.op2, var2.op2, true);
         }
      }

      public int hashCode() {
         int var1 = this.opKind.hashCode();
         var1 *= 127;
         var1 += Infer.this.types.hashCode(this.op1);
         var1 *= 127;
         var1 += Infer.this.types.hashCode(this.op2);
         return var1;
      }

      boolean apply(Warner var1) {
         return this.opKind.apply(this.op1, this.op2, var1, Infer.this.types);
      }
   }

   static enum IncorporationBinaryOpKind {
      IS_SUBTYPE {
         boolean apply(Type var1, Type var2, Warner var3, Types var4) {
            return var4.isSubtypeUnchecked(var1, var2, var3);
         }
      },
      IS_SAME_TYPE {
         boolean apply(Type var1, Type var2, Warner var3, Types var4) {
            return var4.isSameType(var1, var2);
         }
      },
      ADD_UPPER_BOUND {
         boolean apply(Type var1, Type var2, Warner var3, Types var4) {
            Type.UndetVar var5 = (Type.UndetVar)var1;
            var5.addBound(Type.UndetVar.InferenceBound.UPPER, var2, var4);
            return true;
         }
      },
      ADD_LOWER_BOUND {
         boolean apply(Type var1, Type var2, Warner var3, Types var4) {
            Type.UndetVar var5 = (Type.UndetVar)var1;
            var5.addBound(Type.UndetVar.InferenceBound.LOWER, var2, var4);
            return true;
         }
      },
      ADD_EQ_BOUND {
         boolean apply(Type var1, Type var2, Warner var3, Types var4) {
            Type.UndetVar var5 = (Type.UndetVar)var1;
            var5.addBound(Type.UndetVar.InferenceBound.EQ, var2, var4);
            return true;
         }
      };

      private IncorporationBinaryOpKind() {
      }

      abstract boolean apply(Type var1, Type var2, Warner var3, Types var4);

      // $FF: synthetic method
      IncorporationBinaryOpKind(Object var3) {
         this();
      }
   }

   static enum IncorporationStep {
      CHECK_BOUNDS {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            var1.substBounds(var2.inferenceVars(), var2.instTypes(), var4.types);
            var4.checkCompatibleUpperBounds(var1, var2);
            if (var1.inst != null) {
               Type var5 = var1.inst;
               Iterator var6 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

               Type var7;
               while(var6.hasNext()) {
                  var7 = (Type)var6.next();
                  if (!this.isSubtype(var5, var2.asUndetVar(var7), var3, var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.UPPER);
                  }
               }

               var6 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               while(var6.hasNext()) {
                  var7 = (Type)var6.next();
                  if (!this.isSubtype(var2.asUndetVar(var7), var5, var3, var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.LOWER);
                  }
               }

               var6 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

               while(var6.hasNext()) {
                  var7 = (Type)var6.next();
                  if (!this.isSameType(var5, var2.asUndetVar(var7), var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.EQ);
                  }
               }
            }

         }

         boolean accepts(Type.UndetVar var1, InferenceContext var2) {
            return true;
         }
      },
      EQ_CHECK_LEGACY {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Type var5 = null;
            Iterator var6 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

            while(var6.hasNext()) {
               Type var7 = (Type)var6.next();
               Assert.check(!var2.free(var7));
               if (var5 != null && !this.isSameType(var7, var5, var4)) {
                  var4.reportBoundError(var1, Infer.BoundErrorKind.EQ);
               }

               var5 = var7;
               Iterator var8 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               Type var9;
               while(var8.hasNext()) {
                  var9 = (Type)var8.next();
                  Assert.check(!var2.free(var9));
                  if (!this.isSubtype(var9, var7, var3, var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.BAD_EQ_LOWER);
                  }
               }

               var8 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

               while(var8.hasNext()) {
                  var9 = (Type)var8.next();
                  if (!var2.free(var9) && !this.isSubtype(var7, var9, var3, var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.BAD_EQ_UPPER);
                  }
               }
            }

         }
      },
      EQ_CHECK {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

            while(true) {
               Type var6;
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  var6 = (Type)var5.next();
               } while(var6.containsAny(var2.inferenceVars()));

               Iterator var7 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

               Type var8;
               while(var7.hasNext()) {
                  var8 = (Type)var7.next();
                  if (!this.isSubtype(var6, var2.asUndetVar(var8), var3, var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.BAD_EQ_UPPER);
                  }
               }

               var7 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               while(var7.hasNext()) {
                  var8 = (Type)var7.next();
                  if (!this.isSubtype(var2.asUndetVar(var8), var6, var3, var4)) {
                     var4.reportBoundError(var1, Infer.BoundErrorKind.BAD_EQ_LOWER);
                  }
               }
            }
         }
      },
      CROSS_UPPER_LOWER {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               Iterator var7 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               while(var7.hasNext()) {
                  Type var8 = (Type)var7.next();
                  this.isSubtype(var2.asUndetVar(var8), var2.asUndetVar(var6), var3, var4);
               }
            }

         }
      },
      CROSS_UPPER_EQ {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               Iterator var7 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

               while(var7.hasNext()) {
                  Type var8 = (Type)var7.next();
                  this.isSubtype(var2.asUndetVar(var8), var2.asUndetVar(var6), var3, var4);
               }
            }

         }
      },
      CROSS_EQ_LOWER {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               Iterator var7 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               while(var7.hasNext()) {
                  Type var8 = (Type)var7.next();
                  this.isSubtype(var2.asUndetVar(var8), var2.asUndetVar(var6), var3, var4);
               }
            }

         }
      },
      CROSS_UPPER_UPPER {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            List var5 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER);

            for(List var6 = var5.tail; var5.nonEmpty(); var6 = var5.tail) {
               for(List var7 = var6; var7.nonEmpty(); var7 = var7.tail) {
                  Type var8 = (Type)var5.head;
                  Type var9 = (Type)var7.head;
                  if (var8 != var9 && !var8.hasTag(TypeTag.WILDCARD) && !var9.hasTag(TypeTag.WILDCARD)) {
                     Pair var10 = var4.getParameterizedSupers(var8, var9);
                     if (var10 != null) {
                        List var11 = ((Type)var10.fst).allparams();

                        List var12;
                        for(var12 = ((Type)var10.snd).allparams(); var11.nonEmpty() && var12.nonEmpty(); var12 = var12.tail) {
                           if (!((Type)var11.head).hasTag(TypeTag.WILDCARD) && !((Type)var12.head).hasTag(TypeTag.WILDCARD)) {
                              this.isSameType(var2.asUndetVar((Type)var11.head), var2.asUndetVar((Type)var12.head), var4);
                           }

                           var11 = var11.tail;
                        }

                        Assert.check(var11.isEmpty() && var12.isEmpty());
                     }
                  }
               }

               var5 = var5.tail;
            }

         }

         boolean accepts(Type.UndetVar var1, InferenceContext var2) {
            return !var1.isCaptured() && var1.getBounds(Type.UndetVar.InferenceBound.UPPER).nonEmpty();
         }
      },
      CROSS_EQ_EQ {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

            while(var5.hasNext()) {
               Type var6 = (Type)var5.next();
               Iterator var7 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

               while(var7.hasNext()) {
                  Type var8 = (Type)var7.next();
                  if (var6 != var8) {
                     this.isSameType(var2.asUndetVar(var8), var2.asUndetVar(var6), var4);
                  }
               }
            }

         }
      },
      PROP_UPPER {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

            while(true) {
               Type.UndetVar var7;
               do {
                  Type var6;
                  do {
                     if (!var5.hasNext()) {
                        return;
                     }

                     var6 = (Type)var5.next();
                  } while(!var2.inferenceVars().contains(var6));

                  var7 = (Type.UndetVar)var2.asUndetVar(var6);
               } while(var7.isCaptured());

               this.addBound(Type.UndetVar.InferenceBound.LOWER, var7, var2.asInstType(var1.qtype), var4);
               Iterator var8 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               Type var9;
               while(var8.hasNext()) {
                  var9 = (Type)var8.next();
                  this.addBound(Type.UndetVar.InferenceBound.LOWER, var7, var2.asInstType(var9), var4);
               }

               var8 = var7.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

               while(var8.hasNext()) {
                  var9 = (Type)var8.next();
                  this.addBound(Type.UndetVar.InferenceBound.UPPER, var1, var2.asInstType(var9), var4);
               }
            }
         }
      },
      PROP_LOWER {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

            while(true) {
               Type.UndetVar var7;
               do {
                  Type var6;
                  do {
                     if (!var5.hasNext()) {
                        return;
                     }

                     var6 = (Type)var5.next();
                  } while(!var2.inferenceVars().contains(var6));

                  var7 = (Type.UndetVar)var2.asUndetVar(var6);
               } while(var7.isCaptured());

               this.addBound(Type.UndetVar.InferenceBound.UPPER, var7, var2.asInstType(var1.qtype), var4);
               Iterator var8 = var1.getBounds(Type.UndetVar.InferenceBound.UPPER).iterator();

               Type var9;
               while(var8.hasNext()) {
                  var9 = (Type)var8.next();
                  this.addBound(Type.UndetVar.InferenceBound.UPPER, var7, var2.asInstType(var9), var4);
               }

               var8 = var7.getBounds(Type.UndetVar.InferenceBound.LOWER).iterator();

               while(var8.hasNext()) {
                  var9 = (Type)var8.next();
                  this.addBound(Type.UndetVar.InferenceBound.LOWER, var1, var2.asInstType(var9), var4);
               }
            }
         }
      },
      PROP_EQ {
         public void apply(Type.UndetVar var1, InferenceContext var2, Warner var3) {
            Infer var4 = var2.infer();
            Iterator var5 = var1.getBounds(Type.UndetVar.InferenceBound.EQ).iterator();

            while(true) {
               Type.UndetVar var7;
               do {
                  Type var6;
                  do {
                     if (!var5.hasNext()) {
                        return;
                     }

                     var6 = (Type)var5.next();
                  } while(!var2.inferenceVars().contains(var6));

                  var7 = (Type.UndetVar)var2.asUndetVar(var6);
               } while(var7.isCaptured());

               this.addBound(Type.UndetVar.InferenceBound.EQ, var7, var2.asInstType(var1.qtype), var4);
               Type.UndetVar.InferenceBound[] var8 = Type.UndetVar.InferenceBound.values();
               int var9 = var8.length;

               int var10;
               Type.UndetVar.InferenceBound var11;
               Iterator var12;
               Type var13;
               for(var10 = 0; var10 < var9; ++var10) {
                  var11 = var8[var10];
                  var12 = var1.getBounds(var11).iterator();

                  while(var12.hasNext()) {
                     var13 = (Type)var12.next();
                     if (var13 != var7) {
                        this.addBound(var11, var7, var2.asInstType(var13), var4);
                     }
                  }
               }

               var8 = Type.UndetVar.InferenceBound.values();
               var9 = var8.length;

               for(var10 = 0; var10 < var9; ++var10) {
                  var11 = var8[var10];
                  var12 = var7.getBounds(var11).iterator();

                  while(var12.hasNext()) {
                     var13 = (Type)var12.next();
                     if (var13 != var1) {
                        this.addBound(var11, var1, var2.asInstType(var13), var4);
                     }
                  }
               }
            }
         }
      };

      private IncorporationStep() {
      }

      abstract void apply(Type.UndetVar var1, InferenceContext var2, Warner var3);

      boolean accepts(Type.UndetVar var1, InferenceContext var2) {
         return !var1.isCaptured();
      }

      boolean isSubtype(Type var1, Type var2, Warner var3, Infer var4) {
         return this.doIncorporationOp(Infer.IncorporationBinaryOpKind.IS_SUBTYPE, var1, var2, var3, var4);
      }

      boolean isSameType(Type var1, Type var2, Infer var3) {
         return this.doIncorporationOp(Infer.IncorporationBinaryOpKind.IS_SAME_TYPE, var1, var2, (Warner)null, var3);
      }

      void addBound(Type.UndetVar.InferenceBound var1, Type.UndetVar var2, Type var3, Infer var4) {
         this.doIncorporationOp(this.opFor(var1), var2, var3, (Warner)null, var4);
      }

      IncorporationBinaryOpKind opFor(Type.UndetVar.InferenceBound var1) {
         switch (var1) {
            case EQ:
               return Infer.IncorporationBinaryOpKind.ADD_EQ_BOUND;
            case LOWER:
               return Infer.IncorporationBinaryOpKind.ADD_LOWER_BOUND;
            case UPPER:
               return Infer.IncorporationBinaryOpKind.ADD_UPPER_BOUND;
            default:
               Assert.error("Can't get here!");
               return null;
         }
      }

      boolean doIncorporationOp(IncorporationBinaryOpKind var1, Type var2, Type var3, Warner var4, Infer var5) {
         IncorporationBinaryOp var6 = var5.new IncorporationBinaryOp(var1, var2, var3);
         Boolean var7 = (Boolean)var5.incorporationCache.get(var6);
         if (var7 == null) {
            var5.incorporationCache.put(var6, var7 = var6.apply(var4));
         }

         return var7;
      }

      // $FF: synthetic method
      IncorporationStep(Object var3) {
         this();
      }
   }

   class MultiUndetVarListener implements Type.UndetVar.UndetVarListener {
      boolean changed;
      List undetvars;

      public MultiUndetVarListener(List var2) {
         this.undetvars = var2;

         Type.UndetVar var5;
         for(Iterator var3 = var2.iterator(); var3.hasNext(); var5.listener = this) {
            Type var4 = (Type)var3.next();
            var5 = (Type.UndetVar)var4;
         }

      }

      public void varChanged(Type.UndetVar var1, Set var2) {
         if (Infer.this.incorporationCache.size() < 100) {
            this.changed = true;
         }

      }

      void reset() {
         this.changed = false;
      }

      void detach() {
         Type.UndetVar var3;
         for(Iterator var1 = this.undetvars.iterator(); var1.hasNext(); var3.listener = null) {
            Type var2 = (Type)var1.next();
            var3 = (Type.UndetVar)var2;
         }

      }
   }

   class ImplicitArgType extends DeferredAttr.DeferredTypeMap {
      public ImplicitArgType(Symbol var2, Resolve.MethodResolutionPhase var3) {
         DeferredAttr var10001 = Infer.this.rs.deferredAttr;
         var10001.getClass();
         super(DeferredAttr.AttrMode.SPECULATIVE, var2, var3);
      }

      public Type apply(Type var1) {
         var1 = Infer.this.types.erasure(super.apply(var1));
         if (var1.hasTag(TypeTag.BOT)) {
            var1 = Infer.this.types.boxedClass(Infer.this.syms.voidType).type;
         }

         return var1;
      }
   }

   public static class InferenceException extends Resolve.InapplicableMethodException {
      private static final long serialVersionUID = 0L;
      List messages = List.nil();

      InferenceException(JCDiagnostic.Factory var1) {
         super(var1);
      }

      Resolve.InapplicableMethodException setMessage() {
         return this;
      }

      Resolve.InapplicableMethodException setMessage(JCDiagnostic var1) {
         this.messages = this.messages.append(var1);
         return this;
      }

      public JCDiagnostic getDiagnostic() {
         return (JCDiagnostic)this.messages.head;
      }

      void clear() {
         this.messages = List.nil();
      }
   }
}
