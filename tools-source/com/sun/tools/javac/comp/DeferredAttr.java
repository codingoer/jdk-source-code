package com.sun.tools.javac.comp;

import com.sun.source.tree.LambdaExpressionTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeCopier;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Warner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class DeferredAttr extends JCTree.Visitor {
   protected static final Context.Key deferredAttrKey = new Context.Key();
   final Attr attr;
   final Check chk;
   final JCDiagnostic.Factory diags;
   final Enter enter;
   final Infer infer;
   final Resolve rs;
   final Log log;
   final Symtab syms;
   final TreeMaker make;
   final Types types;
   final Flow flow;
   final Names names;
   final TypeEnvs typeEnvs;
   final JCTree stuckTree;
   DeferredTypeCompleter basicCompleter = new DeferredTypeCompleter() {
      public Type complete(DeferredType var1, Attr.ResultInfo var2, DeferredAttrContext var3) {
         switch (var3.mode) {
            case SPECULATIVE:
               Assert.check(var1.mode == null || var1.mode == DeferredAttr.AttrMode.SPECULATIVE);
               JCTree var4 = DeferredAttr.this.attribSpeculative(var1.tree, var1.env, var2);
               var1.speculativeCache.put(var4, var2);
               return var4.type;
            case CHECK:
               Assert.check(var1.mode != null);
               return DeferredAttr.this.attr.attribTree(var1.tree, var1.env, var2);
            default:
               Assert.error();
               return null;
         }
      }
   };
   DeferredTypeCompleter dummyCompleter = new DeferredTypeCompleter() {
      public Type complete(DeferredType var1, Attr.ResultInfo var2, DeferredAttrContext var3) {
         Assert.check(var3.mode == DeferredAttr.AttrMode.CHECK);
         return var1.tree.type = Type.stuckType;
      }
   };
   DeferredStuckPolicy dummyStuckPolicy = new DeferredStuckPolicy() {
      public boolean isStuck() {
         return false;
      }

      public Set stuckVars() {
         return Collections.emptySet();
      }

      public Set depVars() {
         return Collections.emptySet();
      }
   };
   protected UnenterScanner unenterScanner = new UnenterScanner();
   final DeferredAttrContext emptyDeferredAttrContext;
   private EnumSet deferredCheckerTags;

   public static DeferredAttr instance(Context var0) {
      DeferredAttr var1 = (DeferredAttr)var0.get(deferredAttrKey);
      if (var1 == null) {
         var1 = new DeferredAttr(var0);
      }

      return var1;
   }

   protected DeferredAttr(Context var1) {
      this.deferredCheckerTags = EnumSet.of(JCTree.Tag.LAMBDA, JCTree.Tag.REFERENCE, JCTree.Tag.PARENS, JCTree.Tag.TYPECAST, JCTree.Tag.CONDEXPR, JCTree.Tag.NEWCLASS, JCTree.Tag.APPLY, JCTree.Tag.LITERAL);
      var1.put((Context.Key)deferredAttrKey, (Object)this);
      this.attr = Attr.instance(var1);
      this.chk = Check.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      this.enter = Enter.instance(var1);
      this.infer = Infer.instance(var1);
      this.rs = Resolve.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.types = Types.instance(var1);
      this.flow = Flow.instance(var1);
      this.names = Names.instance(var1);
      this.stuckTree = this.make.Ident(this.names.empty).setType(Type.stuckType);
      this.typeEnvs = TypeEnvs.instance(var1);
      this.emptyDeferredAttrContext = new DeferredAttrContext(DeferredAttr.AttrMode.CHECK, (Symbol)null, Resolve.MethodResolutionPhase.BOX, this.infer.emptyContext, (DeferredAttrContext)null, (Warner)null) {
         void addDeferredAttrNode(DeferredType var1, Attr.ResultInfo var2, DeferredStuckPolicy var3) {
            Assert.error("Empty deferred context!");
         }

         void complete() {
            Assert.error("Empty deferred context!");
         }

         public String toString() {
            return "Empty deferred context!";
         }
      };
   }

   JCTree attribSpeculative(JCTree var1, Env var2, Attr.ResultInfo var3) {
      final JCTree var4 = (new TreeCopier(this.make)).copy(var1);
      Env var5 = var2.dup(var4, ((AttrContext)var2.info).dup(((AttrContext)var2.info).scope.dupUnshared()));
      ((AttrContext)var5.info).scope.owner = ((AttrContext)var2.info).scope.owner;
      Log.DeferredDiagnosticHandler var6 = new Log.DeferredDiagnosticHandler(this.log, new Filter() {
         public boolean accepts(final JCDiagnostic var1) {
            class PosScanner extends TreeScanner {
               boolean found = false;

               public void scan(JCTree var1x) {
                  if (var1x != null && var1x.pos() == var1.getDiagnosticPosition()) {
                     this.found = true;
                  }

                  super.scan(var1x);
               }
            }

            PosScanner var2 = new PosScanner();
            var2.scan(var4);
            return var2.found;
         }
      });

      JCTree var7;
      try {
         this.attr.attribTree(var4, var5, var3);
         this.unenterScanner.scan(var4);
         var7 = var4;
      } finally {
         this.unenterScanner.scan(var4);
         this.log.popDiagnosticHandler(var6);
      }

      return var7;
   }

   boolean isDeferred(Env var1, JCTree.JCExpression var2) {
      DeferredChecker var3 = new DeferredChecker(var1);
      var3.scan(var2);
      return var3.result.isPoly();
   }

   interface MethodAnalyzer {
      Object process(Symbol.MethodSymbol var1);

      Object reduce(Object var1, Object var2);

      boolean shouldStop(Object var1);
   }

   final class DeferredChecker extends FilterScanner {
      Env env;
      ArgumentExpressionKind result;
      MethodAnalyzer argumentKindAnalyzer = new MethodAnalyzer() {
         public ArgumentExpressionKind process(Symbol.MethodSymbol var1) {
            return DeferredAttr.ArgumentExpressionKind.methodKind(var1, DeferredAttr.this.types);
         }

         public ArgumentExpressionKind reduce(ArgumentExpressionKind var1, ArgumentExpressionKind var2) {
            switch (var1) {
               case PRIMITIVE:
                  return var2;
               case NO_POLY:
                  return var2.isPoly() ? var2 : var1;
               case POLY:
                  return var1;
               default:
                  Assert.error();
                  return null;
            }
         }

         public boolean shouldStop(ArgumentExpressionKind var1) {
            return var1.isPoly();
         }
      };
      MethodAnalyzer returnSymbolAnalyzer = new MethodAnalyzer() {
         public Symbol process(Symbol.MethodSymbol var1) {
            ArgumentExpressionKind var2 = DeferredAttr.ArgumentExpressionKind.methodKind(var1, DeferredAttr.this.types);
            return var2 != DeferredAttr.ArgumentExpressionKind.POLY && !var1.getReturnType().hasTag(TypeTag.TYPEVAR) ? var1.getReturnType().tsym : null;
         }

         public Symbol reduce(Symbol var1, Symbol var2) {
            return var1 == DeferredAttr.this.syms.errSymbol ? var2 : (var1 == var2 ? var1 : null);
         }

         public boolean shouldStop(Symbol var1) {
            return var1 == null;
         }
      };

      public DeferredChecker(Env var2) {
         super(DeferredAttr.this.deferredCheckerTags);
         this.env = var2;
      }

      public void visitLambda(JCTree.JCLambda var1) {
         this.result = DeferredAttr.ArgumentExpressionKind.POLY;
      }

      public void visitReference(JCTree.JCMemberReference var1) {
         Env var2 = this.env.dup(var1);
         JCTree.JCExpression var3 = (JCTree.JCExpression)DeferredAttr.this.attribSpeculative(var1.getQualifierExpression(), var2, DeferredAttr.this.attr.memberReferenceQualifierResult(var1));
         JCTree.JCMemberReference var4 = (JCTree.JCMemberReference)(new TreeCopier(DeferredAttr.this.make)).copy((JCTree)var1);
         var4.expr = var3;
         Symbol var5 = DeferredAttr.this.rs.getMemberReference(var1, var2, var4, var3.type, var1.name);
         var1.sym = var5;
         if (var5.kind < 128 && !var5.type.hasTag(TypeTag.FORALL) && (var5.flags() & 17179869184L) == 0L && (!TreeInfo.isStaticSelector(var3, var1.name.table.names) || !var3.type.isRaw())) {
            var1.overloadKind = JCTree.JCMemberReference.OverloadKind.UNOVERLOADED;
         } else {
            var1.overloadKind = JCTree.JCMemberReference.OverloadKind.OVERLOADED;
         }

         this.result = DeferredAttr.ArgumentExpressionKind.POLY;
      }

      public void visitTypeCast(JCTree.JCTypeCast var1) {
         this.result = DeferredAttr.ArgumentExpressionKind.NO_POLY;
      }

      public void visitConditional(JCTree.JCConditional var1) {
         this.scan(var1.truepart);
         if (!this.result.isPrimitive()) {
            this.result = DeferredAttr.ArgumentExpressionKind.POLY;
         } else {
            this.scan(var1.falsepart);
            this.result = this.reduce(DeferredAttr.ArgumentExpressionKind.PRIMITIVE);
         }
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         this.result = !TreeInfo.isDiamond(var1) && !DeferredAttr.this.attr.findDiamonds ? DeferredAttr.ArgumentExpressionKind.NO_POLY : DeferredAttr.ArgumentExpressionKind.POLY;
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         Name var2 = TreeInfo.name(var1.meth);
         if (!var1.typeargs.nonEmpty() && var2 != var2.table.names._this && var2 != var2.table.names._super) {
            Symbol var3 = this.quicklyResolveMethod(this.env, var1);
            if (var3 == null) {
               this.result = DeferredAttr.ArgumentExpressionKind.POLY;
            } else {
               this.result = (ArgumentExpressionKind)this.analyzeCandidateMethods(var3, DeferredAttr.ArgumentExpressionKind.PRIMITIVE, this.argumentKindAnalyzer);
            }
         } else {
            this.result = DeferredAttr.ArgumentExpressionKind.NO_POLY;
         }
      }

      private boolean isSimpleReceiver(JCTree var1) {
         switch (var1.getTag()) {
            case IDENT:
               return true;
            case SELECT:
               return this.isSimpleReceiver(((JCTree.JCFieldAccess)var1).selected);
            case TYPEAPPLY:
            case TYPEARRAY:
               return true;
            case ANNOTATED_TYPE:
               return this.isSimpleReceiver(((JCTree.JCAnnotatedType)var1).underlyingType);
            case APPLY:
               return true;
            case NEWCLASS:
               JCTree.JCNewClass var2 = (JCTree.JCNewClass)var1;
               return var2.encl == null && var2.def == null && !TreeInfo.isDiamond(var2);
            default:
               return false;
         }
      }

      private ArgumentExpressionKind reduce(ArgumentExpressionKind var1) {
         return (ArgumentExpressionKind)this.argumentKindAnalyzer.reduce(this.result, var1);
      }

      public void visitLiteral(JCTree.JCLiteral var1) {
         Type var2 = DeferredAttr.this.attr.litType(var1.typetag);
         this.result = DeferredAttr.ArgumentExpressionKind.standaloneKind(var2, DeferredAttr.this.types);
      }

      void skip(JCTree var1) {
         this.result = DeferredAttr.ArgumentExpressionKind.NO_POLY;
      }

      private Symbol quicklyResolveMethod(Env var1, JCTree.JCMethodInvocation var2) {
         final JCTree.JCExpression var3 = var2.meth.hasTag(JCTree.Tag.SELECT) ? ((JCTree.JCFieldAccess)var2.meth).selected : null;
         if (var3 != null && !this.isSimpleReceiver(var3)) {
            return null;
         } else {
            Type var4;
            if (var3 != null) {
               switch (var3.getTag()) {
                  case APPLY:
                     Symbol var5 = this.quicklyResolveMethod(var1, (JCTree.JCMethodInvocation)var3);
                     if (var5 == null) {
                        return null;
                     }

                     Symbol var6 = (Symbol)this.analyzeCandidateMethods(var5, DeferredAttr.this.syms.errSymbol, this.returnSymbolAnalyzer);
                     if (var6 == null) {
                        return null;
                     }

                     var4 = var6.type;
                     break;
                  case NEWCLASS:
                     JCTree.JCNewClass var7 = (JCTree.JCNewClass)var3;
                     var4 = DeferredAttr.this.attribSpeculative(var7.clazz, var1, DeferredAttr.this.attr.unknownTypeExprInfo).type;
                     break;
                  default:
                     var4 = DeferredAttr.this.attribSpeculative(var3, var1, DeferredAttr.this.attr.unknownTypeExprInfo).type;
               }
            } else {
               var4 = var1.enclClass.sym.type;
            }

            while(var4.hasTag(TypeTag.TYPEVAR)) {
               var4 = var4.getUpperBound();
            }

            var4 = DeferredAttr.this.types.capture(var4);
            List var8 = DeferredAttr.this.rs.dummyArgs(var2.args.length());
            Name var9 = TreeInfo.name(var2.meth);
            Resolve var10003 = DeferredAttr.this.rs;
            var10003.getClass();
            Resolve.LookupHelper var10 = new Resolve.LookupHelper(var10003, var9, var4, var8, List.nil(), Resolve.MethodResolutionPhase.VARARITY) {
               {
                  var2.getClass();
               }

               Symbol lookup(Env var1, Resolve.MethodResolutionPhase var2) {
                  return var3 == null ? DeferredAttr.this.rs.findFun(var1, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired()) : DeferredAttr.this.rs.findMethod(var1, this.site, this.name, this.argtypes, this.typeargtypes, var2.isBoxingRequired(), var2.isVarargsRequired(), false);
               }

               Symbol access(Env var1, JCDiagnostic.DiagnosticPosition var2, Symbol var3x, Symbol var4) {
                  return var4;
               }
            };
            return DeferredAttr.this.rs.lookupMethod(var1, var2, var4.tsym, (Resolve.MethodCheck)DeferredAttr.this.rs.arityMethodCheck, var10);
         }
      }

      Object analyzeCandidateMethods(Symbol var1, Object var2, MethodAnalyzer var3) {
         switch (var1.kind) {
            case 16:
               return var3.process((Symbol.MethodSymbol)var1);
            case 129:
               Resolve.AmbiguityError var4 = (Resolve.AmbiguityError)var1.baseSymbol();
               Object var5 = var2;
               Iterator var6 = var4.ambiguousSyms.iterator();

               while(var6.hasNext()) {
                  Symbol var7 = (Symbol)var6.next();
                  if (var7.kind == 16) {
                     var5 = var3.reduce(var5, var3.process((Symbol.MethodSymbol)var7));
                     if (var3.shouldStop(var5)) {
                        return var5;
                     }
                  }
               }

               return var5;
            default:
               return var2;
         }
      }
   }

   static enum ArgumentExpressionKind {
      POLY,
      NO_POLY,
      PRIMITIVE;

      public final boolean isPoly() {
         return this == POLY;
      }

      public final boolean isPrimitive() {
         return this == PRIMITIVE;
      }

      static ArgumentExpressionKind standaloneKind(Type var0, Types var1) {
         return var1.unboxedTypeOrType(var0).isPrimitive() ? PRIMITIVE : NO_POLY;
      }

      static ArgumentExpressionKind methodKind(Symbol var0, Types var1) {
         Type var2 = var0.type.getReturnType();
         return var0.type.hasTag(TypeTag.FORALL) && var2.containsAny(((Type.ForAll)var0.type).tvars) ? POLY : standaloneKind(var2, var1);
      }
   }

   class OverloadStuckPolicy extends CheckStuckPolicy implements DeferredStuckPolicy {
      boolean stuck;

      public boolean isStuck() {
         return super.isStuck() || this.stuck;
      }

      public OverloadStuckPolicy(Attr.ResultInfo var2, DeferredType var3) {
         super(var2, var3);
      }

      public void visitLambda(JCTree.JCLambda var1) {
         super.visitLambda(var1);
         if (var1.paramKind == JCTree.JCLambda.ParameterKind.IMPLICIT) {
            this.stuck = true;
         }

      }

      public void visitReference(JCTree.JCMemberReference var1) {
         super.visitReference(var1);
         if (var1.overloadKind == JCTree.JCMemberReference.OverloadKind.OVERLOADED) {
            this.stuck = true;
         }

      }
   }

   class CheckStuckPolicy extends PolyScanner implements DeferredStuckPolicy, Infer.FreeTypeListener {
      Type pt;
      Infer.InferenceContext inferenceContext;
      Set stuckVars = new LinkedHashSet();
      Set depVars = new LinkedHashSet();

      public boolean isStuck() {
         return !this.stuckVars.isEmpty();
      }

      public Set stuckVars() {
         return this.stuckVars;
      }

      public Set depVars() {
         return this.depVars;
      }

      public CheckStuckPolicy(Attr.ResultInfo var2, DeferredType var3) {
         this.pt = var2.pt;
         this.inferenceContext = var2.checkContext.inferenceContext();
         this.scan(var3.tree);
         if (!this.stuckVars.isEmpty()) {
            var2.checkContext.inferenceContext().addFreeTypeListener(List.from((Iterable)this.stuckVars), this);
         }

      }

      public void typesInferred(Infer.InferenceContext var1) {
         this.stuckVars.clear();
      }

      public void visitLambda(JCTree.JCLambda var1) {
         if (this.inferenceContext.inferenceVars().contains(this.pt)) {
            this.stuckVars.add(this.pt);
         }

         if (DeferredAttr.this.types.isFunctionalInterface(this.pt)) {
            Type var2 = DeferredAttr.this.types.findDescriptorType(this.pt);
            List var3 = this.inferenceContext.freeVarsIn(var2.getParameterTypes());
            if (var1.paramKind == JCTree.JCLambda.ParameterKind.IMPLICIT && var3.nonEmpty()) {
               this.stuckVars.addAll(var3);
               this.depVars.addAll(this.inferenceContext.freeVarsIn(var2.getReturnType()));
            }

            this.scanLambdaBody(var1, var2.getReturnType());
         }
      }

      public void visitReference(JCTree.JCMemberReference var1) {
         this.scan(var1.expr);
         if (this.inferenceContext.inferenceVars().contains(this.pt)) {
            this.stuckVars.add(this.pt);
         } else if (DeferredAttr.this.types.isFunctionalInterface(this.pt)) {
            Type var2 = DeferredAttr.this.types.findDescriptorType(this.pt);
            List var3 = this.inferenceContext.freeVarsIn(var2.getParameterTypes());
            if (var3.nonEmpty() && var1.overloadKind == JCTree.JCMemberReference.OverloadKind.OVERLOADED) {
               this.stuckVars.addAll(var3);
               this.depVars.addAll(this.inferenceContext.freeVarsIn(var2.getReturnType()));
            }

         }
      }

      void scanLambdaBody(JCTree.JCLambda var1, final Type var2) {
         if (var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION) {
            Type var3 = this.pt;

            try {
               this.pt = var2;
               this.scan(var1.body);
            } finally {
               this.pt = var3;
            }
         } else {
            LambdaReturnScanner var7 = new LambdaReturnScanner() {
               public void visitReturn(JCTree.JCReturn var1) {
                  if (var1.expr != null) {
                     Type var2x = CheckStuckPolicy.this.pt;

                     try {
                        CheckStuckPolicy.this.pt = var2;
                        CheckStuckPolicy.this.scan(var1.expr);
                     } finally {
                        CheckStuckPolicy.this.pt = var2x;
                     }
                  }

               }
            };
            var7.scan(var1.body);
         }

      }
   }

   static class LambdaReturnScanner extends FilterScanner {
      LambdaReturnScanner() {
         super(EnumSet.of(JCTree.Tag.BLOCK, JCTree.Tag.CASE, JCTree.Tag.CATCH, JCTree.Tag.DOLOOP, JCTree.Tag.FOREACHLOOP, JCTree.Tag.FORLOOP, JCTree.Tag.IF, JCTree.Tag.RETURN, JCTree.Tag.SYNCHRONIZED, JCTree.Tag.SWITCH, JCTree.Tag.TRY, JCTree.Tag.WHILELOOP));
      }
   }

   static class PolyScanner extends FilterScanner {
      PolyScanner() {
         super(EnumSet.of(JCTree.Tag.CONDEXPR, JCTree.Tag.PARENS, JCTree.Tag.LAMBDA, JCTree.Tag.REFERENCE));
      }
   }

   abstract static class FilterScanner extends TreeScanner {
      final Filter treeFilter;

      FilterScanner(final Set var1) {
         this.treeFilter = new Filter() {
            public boolean accepts(JCTree var1x) {
               return var1.contains(var1x.getTag());
            }
         };
      }

      public void scan(JCTree var1) {
         if (var1 != null) {
            if (this.treeFilter.accepts(var1)) {
               super.scan(var1);
            } else {
               this.skip(var1);
            }
         }

      }

      void skip(JCTree var1) {
      }
   }

   public class RecoveryDeferredTypeMap extends DeferredTypeMap {
      public RecoveryDeferredTypeMap(AttrMode var2, Symbol var3, Resolve.MethodResolutionPhase var4) {
         super(var2, var3, var4 != null ? var4 : Resolve.MethodResolutionPhase.BOX);
      }

      protected Type typeOf(DeferredType var1) {
         Type var2 = super.typeOf(var1);
         return var2 == Type.noType ? this.recover(var1) : var2;
      }

      private Type recover(DeferredType var1) {
         Attr var10004 = DeferredAttr.this.attr;
         var10004.getClass();
         var1.check(new Attr.RecoveryInfo(var10004, this.deferredAttrContext) {
            {
               var2.getClass();
            }

            protected Type check(JCDiagnostic.DiagnosticPosition var1, Type var2) {
               return DeferredAttr.this.chk.checkNonVoid(var1, super.check(var1, var2));
            }
         });
         return super.apply(var1);
      }
   }

   class DeferredTypeMap extends Type.Mapping {
      DeferredAttrContext deferredAttrContext;

      protected DeferredTypeMap(AttrMode var2, Symbol var3, Resolve.MethodResolutionPhase var4) {
         super(String.format("deferredTypeMap[%s]", var2));
         this.deferredAttrContext = DeferredAttr.this.new DeferredAttrContext(var2, var3, var4, DeferredAttr.this.infer.emptyContext, DeferredAttr.this.emptyDeferredAttrContext, DeferredAttr.this.types.noWarnings);
      }

      public Type apply(Type var1) {
         if (!var1.hasTag(TypeTag.DEFERRED)) {
            return var1.map(this);
         } else {
            DeferredType var2 = (DeferredType)var1;
            return this.typeOf(var2);
         }
      }

      protected Type typeOf(DeferredType var1) {
         switch (this.deferredAttrContext.mode) {
            case SPECULATIVE:
               return var1.speculativeType(this.deferredAttrContext.msym, this.deferredAttrContext.phase);
            case CHECK:
               return (Type)(var1.tree.type == null ? Type.noType : var1.tree.type);
            default:
               Assert.error();
               return null;
         }
      }
   }

   class DeferredAttrNode {
      DeferredType dt;
      Attr.ResultInfo resultInfo;
      DeferredStuckPolicy deferredStuckPolicy;

      DeferredAttrNode(DeferredType var2, Attr.ResultInfo var3, DeferredStuckPolicy var4) {
         this.dt = var2;
         this.resultInfo = var3;
         this.deferredStuckPolicy = var4;
      }

      boolean process(final DeferredAttrContext var1) {
         switch (var1.mode) {
            case SPECULATIVE:
               if (this.deferredStuckPolicy.isStuck()) {
                  this.dt.check(this.resultInfo, DeferredAttr.this.dummyStuckPolicy, new StructuralStuckChecker());
                  return true;
               } else {
                  Assert.error("Cannot get here");
               }
            case CHECK:
               if (this.deferredStuckPolicy.isStuck()) {
                  if (var1.parent != DeferredAttr.this.emptyDeferredAttrContext && Type.containsAny(var1.parent.inferenceContext.inferencevars, List.from((Iterable)this.deferredStuckPolicy.stuckVars()))) {
                     var1.parent.addDeferredAttrNode(this.dt, this.resultInfo.dup((Check.CheckContext)(new Check.NestedCheckContext(this.resultInfo.checkContext) {
                        public Infer.InferenceContext inferenceContext() {
                           return var1.parent.inferenceContext;
                        }

                        public DeferredAttrContext deferredAttrContext() {
                           return var1.parent;
                        }
                     })), this.deferredStuckPolicy);
                     this.dt.tree.type = Type.stuckType;
                     return true;
                  }

                  return false;
               }

               Assert.check(!var1.insideOverloadPhase(), "attribution shouldn't be happening here");
               Attr.ResultInfo var2 = this.resultInfo.dup(var1.inferenceContext.asInstType(this.resultInfo.pt));
               this.dt.check(var2, DeferredAttr.this.dummyStuckPolicy, DeferredAttr.this.basicCompleter);
               return true;
            default:
               throw new AssertionError("Bad mode");
         }
      }

      class LambdaBodyStructChecker extends TreeScanner {
         boolean isVoidCompatible = true;
         boolean isPotentiallyValueCompatible = true;

         public void visitClassDef(JCTree.JCClassDecl var1) {
         }

         public void visitLambda(JCTree.JCLambda var1) {
         }

         public void visitNewClass(JCTree.JCNewClass var1) {
         }

         public void visitReturn(JCTree.JCReturn var1) {
            if (var1.expr != null) {
               this.isVoidCompatible = false;
            } else {
               this.isPotentiallyValueCompatible = false;
            }

         }
      }

      class StructuralStuckChecker extends TreeScanner implements DeferredTypeCompleter {
         Attr.ResultInfo resultInfo;
         Infer.InferenceContext inferenceContext;
         Env env;

         public Type complete(DeferredType var1, Attr.ResultInfo var2, DeferredAttrContext var3) {
            this.resultInfo = var2;
            this.inferenceContext = var3.inferenceContext;
            this.env = var1.env;
            var1.tree.accept(this);
            var1.speculativeCache.put(DeferredAttr.this.stuckTree, var2);
            return Type.noType;
         }

         public void visitLambda(JCTree.JCLambda var1) {
            Check.CheckContext var2 = this.resultInfo.checkContext;
            Type var3 = this.resultInfo.pt;
            if (!this.inferenceContext.inferencevars.contains(var3)) {
               Type var4 = null;

               try {
                  var4 = DeferredAttr.this.types.findDescriptorType(var3);
               } catch (Types.FunctionDescriptorLookupError var10) {
                  var2.report((JCDiagnostic.DiagnosticPosition)null, var10.getDiagnostic());
               }

               if (var4.getParameterTypes().length() != var1.params.length()) {
                  var2.report(var1, DeferredAttr.this.diags.fragment("incompatible.arg.types.in.lambda"));
               }

               Type var5 = var4.getReturnType();
               boolean var6 = var5.hasTag(TypeTag.VOID);
               if (var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION) {
                  boolean var7 = !var6 || TreeInfo.isExpressionStatement((JCTree.JCExpression)var1.getBody());
                  if (!var7) {
                     this.resultInfo.checkContext.report(var1.pos(), DeferredAttr.this.diags.fragment("incompatible.ret.type.in.lambda", DeferredAttr.this.diags.fragment("missing.ret.val", var5)));
                  }
               } else {
                  LambdaBodyStructChecker var11 = DeferredAttrNode.this.new LambdaBodyStructChecker();
                  var1.body.accept(var11);
                  boolean var8 = var11.isVoidCompatible;
                  if (var6) {
                     if (!var8) {
                        this.resultInfo.checkContext.report(var1.pos(), DeferredAttr.this.diags.fragment("unexpected.ret.val"));
                     }
                  } else {
                     boolean var9 = var11.isPotentiallyValueCompatible && !this.canLambdaBodyCompleteNormally(var1);
                     if (!var9 && !var8) {
                        DeferredAttr.this.log.error(var1.body.pos(), "lambda.body.neither.value.nor.void.compatible", new Object[0]);
                     }

                     if (!var9) {
                        this.resultInfo.checkContext.report(var1.pos(), DeferredAttr.this.diags.fragment("incompatible.ret.type.in.lambda", DeferredAttr.this.diags.fragment("missing.ret.val", var5)));
                     }
                  }
               }
            }

         }

         boolean canLambdaBodyCompleteNormally(JCTree.JCLambda var1) {
            JCTree.JCLambda var2 = (JCTree.JCLambda)(new TreeCopier(DeferredAttr.this.make)).copy((JCTree)var1);
            Env var3 = DeferredAttr.this.attr.lambdaEnv(var2, this.env);
            boolean var13 = false;

            boolean var18;
            try {
               var13 = true;
               List var4 = var2.params;

               while(true) {
                  if (!var4.nonEmpty()) {
                     DeferredAttr.this.attr.attribStats(var2.params, var3);
                     Attr.ResultInfo var5 = DeferredAttr.this.attr.new ResultInfo(12, Type.noType);
                     ((AttrContext)var3.info).returnResult = var5;
                     Log.DiscardDiagnosticHandler var6 = new Log.DiscardDiagnosticHandler(DeferredAttr.this.log);

                     try {
                        JCTree.JCBlock var7 = (JCTree.JCBlock)var2.body;
                        DeferredAttr.this.attr.attribStats(var7.stats, var3);
                        DeferredAttr.this.attr.preFlow(var2);
                        DeferredAttr.this.flow.analyzeLambda(var3, var2, DeferredAttr.this.make, true);
                     } finally {
                        DeferredAttr.this.log.popDiagnosticHandler(var6);
                     }

                     var18 = var2.canCompleteNormally;
                     var13 = false;
                     break;
                  }

                  ((JCTree.JCVariableDecl)var4.head).vartype = DeferredAttr.this.make.at((JCDiagnostic.DiagnosticPosition)var4.head).Type(DeferredAttr.this.syms.errType);
                  var4 = var4.tail;
               }
            } finally {
               if (var13) {
                  JCTree.JCBlock var10 = (JCTree.JCBlock)var2.body;
                  DeferredAttr.this.unenterScanner.scan(var10.stats);
                  ((AttrContext)var3.info).scope.leave();
               }
            }

            JCTree.JCBlock var8 = (JCTree.JCBlock)var2.body;
            DeferredAttr.this.unenterScanner.scan(var8.stats);
            ((AttrContext)var3.info).scope.leave();
            return var18;
         }

         public void visitNewClass(JCTree.JCNewClass var1) {
         }

         public void visitApply(JCTree.JCMethodInvocation var1) {
         }

         public void visitReference(JCTree.JCMemberReference var1) {
            Check.CheckContext var2 = this.resultInfo.checkContext;
            Type var3 = this.resultInfo.pt;
            if (!this.inferenceContext.inferencevars.contains(var3)) {
               try {
                  DeferredAttr.this.types.findDescriptorType(var3);
               } catch (Types.FunctionDescriptorLookupError var9) {
                  var2.report((JCDiagnostic.DiagnosticPosition)null, var9.getDiagnostic());
               }

               Env var4 = this.env.dup(var1);
               JCTree.JCExpression var5 = (JCTree.JCExpression)DeferredAttr.this.attribSpeculative(var1.getQualifierExpression(), var4, DeferredAttr.this.attr.memberReferenceQualifierResult(var1));
               ListBuffer var6 = new ListBuffer();
               Iterator var7 = DeferredAttr.this.types.findDescriptorType(var3).getParameterTypes().iterator();

               while(var7.hasNext()) {
                  Type var8 = (Type)var7.next();
                  var6.append(Type.noType);
               }

               JCTree.JCMemberReference var10 = (JCTree.JCMemberReference)(new TreeCopier(DeferredAttr.this.make)).copy((JCTree)var1);
               var10.expr = var5;
               Symbol var11 = DeferredAttr.this.rs.resolveMemberReferenceByArity(var4, var10, var5.type, var1.name, var6.toList(), this.inferenceContext);
               switch (var11.kind) {
                  case 134:
                  case 135:
                  case 136:
                  case 138:
                     var2.report(var1, DeferredAttr.this.diags.fragment("incompatible.arg.types.in.mref"));
                  case 137:
               }
            }

         }
      }
   }

   class DeferredAttrContext {
      final AttrMode mode;
      final Symbol msym;
      final Resolve.MethodResolutionPhase phase;
      final Infer.InferenceContext inferenceContext;
      final DeferredAttrContext parent;
      final Warner warn;
      ArrayList deferredAttrNodes = new ArrayList();

      DeferredAttrContext(AttrMode var2, Symbol var3, Resolve.MethodResolutionPhase var4, Infer.InferenceContext var5, DeferredAttrContext var6, Warner var7) {
         this.mode = var2;
         this.msym = var3;
         this.phase = var4;
         this.parent = var6;
         this.warn = var7;
         this.inferenceContext = var5;
      }

      void addDeferredAttrNode(DeferredType var1, Attr.ResultInfo var2, DeferredStuckPolicy var3) {
         this.deferredAttrNodes.add(DeferredAttr.this.new DeferredAttrNode(var1, var2, var3));
      }

      void complete() {
         while(true) {
            if (!this.deferredAttrNodes.isEmpty()) {
               LinkedHashMap var1 = new LinkedHashMap();
               List var2 = List.nil();
               boolean var3 = false;
               Iterator var4 = List.from((Iterable)this.deferredAttrNodes).iterator();

               DeferredAttrNode var5;
               while(var4.hasNext()) {
                  var5 = (DeferredAttrNode)var4.next();
                  if (!var5.process(this)) {
                     List var6 = List.from((Iterable)var5.deferredStuckPolicy.stuckVars()).intersect(this.inferenceContext.restvars());
                     var2 = var2.prependList(var6);

                     Object var9;
                     for(Iterator var7 = List.from((Iterable)var5.deferredStuckPolicy.depVars()).intersect(this.inferenceContext.restvars()).iterator(); var7.hasNext(); ((Set)var9).addAll(var6)) {
                        Type var8 = (Type)var7.next();
                        var9 = (Set)var1.get(var8);
                        if (var9 == null) {
                           var9 = new LinkedHashSet();
                           var1.put(var8, var9);
                        }
                     }
                  } else {
                     this.deferredAttrNodes.remove(var5);
                     var3 = true;
                  }
               }

               if (var3) {
                  continue;
               }

               if (this.insideOverloadPhase()) {
                  for(var4 = this.deferredAttrNodes.iterator(); var4.hasNext(); var5.dt.tree.type = Type.noType) {
                     var5 = (DeferredAttrNode)var4.next();
                  }

                  return;
               }

               try {
                  this.inferenceContext.solveAny(var2, var1, this.warn);
                  this.inferenceContext.notifyChange();
                  continue;
               } catch (Infer.GraphStrategy.NodeNotFoundException var10) {
               }
            }

            return;
         }
      }

      private boolean insideOverloadPhase() {
         if (this == DeferredAttr.this.emptyDeferredAttrContext) {
            return false;
         } else {
            return this.mode == DeferredAttr.AttrMode.SPECULATIVE ? true : this.parent.insideOverloadPhase();
         }
      }
   }

   class UnenterScanner extends TreeScanner {
      public void visitClassDef(JCTree.JCClassDecl var1) {
         Symbol.ClassSymbol var2 = var1.sym;
         if (var2 != null) {
            DeferredAttr.this.typeEnvs.remove(var2);
            DeferredAttr.this.chk.compiled.remove(var2.flatname);
            DeferredAttr.this.syms.classes.remove(var2.flatname);
            super.visitClassDef(var1);
         }
      }
   }

   public static enum AttrMode {
      SPECULATIVE,
      CHECK;
   }

   interface DeferredStuckPolicy {
      boolean isStuck();

      Set stuckVars();

      Set depVars();
   }

   interface DeferredTypeCompleter {
      Type complete(DeferredType var1, Attr.ResultInfo var2, DeferredAttrContext var3);
   }

   public class DeferredType extends Type {
      public JCTree.JCExpression tree;
      Env env;
      AttrMode mode;
      SpeculativeCache speculativeCache;

      DeferredType(JCTree.JCExpression var2, Env var3) {
         super((Symbol.TypeSymbol)null);
         this.tree = var2;
         this.env = DeferredAttr.this.attr.copyEnv(var3);
         this.speculativeCache = new SpeculativeCache();
      }

      public TypeTag getTag() {
         return TypeTag.DEFERRED;
      }

      public String toString() {
         return "DeferredType";
      }

      Type speculativeType(Symbol var1, Resolve.MethodResolutionPhase var2) {
         SpeculativeCache.Entry var3 = this.speculativeCache.get(var1, var2);
         return (Type)(var3 != null ? var3.speculativeTree.type : Type.noType);
      }

      Type check(Attr.ResultInfo var1) {
         Object var2;
         if (!var1.pt.hasTag(TypeTag.NONE) && !var1.pt.isErroneous()) {
            if (var1.checkContext.deferredAttrContext().mode != DeferredAttr.AttrMode.SPECULATIVE && !var1.checkContext.deferredAttrContext().insideOverloadPhase()) {
               var2 = DeferredAttr.this.new CheckStuckPolicy(var1, this);
            } else {
               var2 = DeferredAttr.this.new OverloadStuckPolicy(var1, this);
            }
         } else {
            var2 = DeferredAttr.this.dummyStuckPolicy;
         }

         return this.check(var1, (DeferredStuckPolicy)var2, DeferredAttr.this.basicCompleter);
      }

      private Type check(Attr.ResultInfo var1, DeferredStuckPolicy var2, DeferredTypeCompleter var3) {
         DeferredAttrContext var4 = var1.checkContext.deferredAttrContext();
         Assert.check(var4 != DeferredAttr.this.emptyDeferredAttrContext);
         if (var2.isStuck()) {
            var4.addDeferredAttrNode(this, var1, var2);
            return Type.noType;
         } else {
            Type var5;
            try {
               var5 = var3.complete(this, var1, var4);
            } finally {
               this.mode = var4.mode;
            }

            return var5;
         }
      }

      class SpeculativeCache {
         private Map cache = new WeakHashMap();

         Entry get(Symbol var1, Resolve.MethodResolutionPhase var2) {
            List var3 = (List)this.cache.get(var1);
            if (var3 == null) {
               return null;
            } else {
               Iterator var4 = var3.iterator();

               Entry var5;
               do {
                  if (!var4.hasNext()) {
                     return null;
                  }

                  var5 = (Entry)var4.next();
               } while(!var5.matches(var2));

               return var5;
            }
         }

         void put(JCTree var1, Attr.ResultInfo var2) {
            Symbol var3 = var2.checkContext.deferredAttrContext().msym;
            List var4 = (List)this.cache.get(var3);
            if (var4 == null) {
               var4 = List.nil();
            }

            this.cache.put(var3, var4.prepend(new Entry(var1, var2)));
         }

         class Entry {
            JCTree speculativeTree;
            Attr.ResultInfo resultInfo;

            public Entry(JCTree var2, Attr.ResultInfo var3) {
               this.speculativeTree = var2;
               this.resultInfo = var3;
            }

            boolean matches(Resolve.MethodResolutionPhase var1) {
               return this.resultInfo.checkContext.deferredAttrContext().phase == var1;
            }
         }
      }
   }
}
