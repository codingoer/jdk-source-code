package com.sun.tools.javac.comp;

import com.sun.source.tree.LambdaExpressionTree;
import com.sun.tools.javac.code.Lint;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.ArrayUtils;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Bits;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import java.util.HashMap;
import java.util.Iterator;

public class Flow {
   protected static final Context.Key flowKey = new Context.Key();
   private final Names names;
   private final Log log;
   private final Symtab syms;
   private final Types types;
   private final Check chk;
   private TreeMaker make;
   private final Resolve rs;
   private final JCDiagnostic.Factory diags;
   private Env attrEnv;
   private Lint lint;
   private final boolean allowImprovedRethrowAnalysis;
   private final boolean allowImprovedCatchAnalysis;
   private final boolean allowEffectivelyFinalInInnerClasses;
   private final boolean enforceThisDotInit;

   public static Flow instance(Context var0) {
      Flow var1 = (Flow)var0.get(flowKey);
      if (var1 == null) {
         var1 = new Flow(var0);
      }

      return var1;
   }

   public void analyzeTree(Env var1, TreeMaker var2) {
      (new AliveAnalyzer()).analyzeTree(var1, var2);
      (new AssignAnalyzer()).analyzeTree(var1);
      (new FlowAnalyzer()).analyzeTree(var1, var2);
      (new CaptureAnalyzer()).analyzeTree(var1, var2);
   }

   public void analyzeLambda(Env var1, JCTree.JCLambda var2, TreeMaker var3, boolean var4) {
      Log.DiscardDiagnosticHandler var5 = null;
      if (!var4) {
         var5 = new Log.DiscardDiagnosticHandler(this.log);
      }

      try {
         (new AliveAnalyzer()).analyzeTree(var1, var2, var3);
      } finally {
         if (!var4) {
            this.log.popDiagnosticHandler(var5);
         }

      }

   }

   public List analyzeLambdaThrownTypes(final Env var1, JCTree.JCLambda var2, TreeMaker var3) {
      Log.DiscardDiagnosticHandler var4 = new Log.DiscardDiagnosticHandler(this.log);

      List var6;
      try {
         (new AssignAnalyzer() {
            Scope enclosedSymbols;

            {
               this.enclosedSymbols = new Scope(var1.enclClass.sym);
            }

            public void visitVarDef(JCTree.JCVariableDecl var1x) {
               this.enclosedSymbols.enter(var1x.sym);
               super.visitVarDef(var1x);
            }

            protected boolean trackable(Symbol.VarSymbol var1x) {
               return this.enclosedSymbols.includes(var1x) && var1x.owner.kind == 16;
            }
         }).analyzeTree(var1, var2);
         LambdaFlowAnalyzer var5 = new LambdaFlowAnalyzer();
         var5.analyzeTree(var1, var2, var3);
         var6 = var5.inferredThrownTypes;
      } finally {
         this.log.popDiagnosticHandler(var4);
      }

      return var6;
   }

   protected Flow(Context var1) {
      var1.put((Context.Key)flowKey, (Object)this);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.types = Types.instance(var1);
      this.chk = Check.instance(var1);
      this.lint = Lint.instance(var1);
      this.rs = Resolve.instance(var1);
      this.diags = JCDiagnostic.Factory.instance(var1);
      Source var2 = Source.instance(var1);
      this.allowImprovedRethrowAnalysis = var2.allowImprovedRethrowAnalysis();
      this.allowImprovedCatchAnalysis = var2.allowImprovedCatchAnalysis();
      this.allowEffectivelyFinalInInnerClasses = var2.allowEffectivelyFinalInInnerClasses();
      this.enforceThisDotInit = var2.enforceThisDotInit();
   }

   class CaptureAnalyzer extends BaseAnalyzer {
      JCTree currentTree;

      void markDead() {
      }

      void checkEffectivelyFinal(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2) {
         if (this.currentTree != null && var2.owner.kind == 16 && var2.pos < this.currentTree.getStartPosition()) {
            switch (this.currentTree.getTag()) {
               case CLASSDEF:
                  if (!Flow.this.allowEffectivelyFinalInInnerClasses) {
                     if ((var2.flags() & 16L) == 0L) {
                        this.reportInnerClsNeedsFinalError(var1, var2);
                     }
                     break;
                  }
               case LAMBDA:
                  if ((var2.flags() & 2199023255568L) == 0L) {
                     this.reportEffectivelyFinalError(var1, var2);
                  }
            }
         }

      }

      void letInit(JCTree var1) {
         var1 = TreeInfo.skipParens(var1);
         if (var1.hasTag(JCTree.Tag.IDENT) || var1.hasTag(JCTree.Tag.SELECT)) {
            Symbol var2 = TreeInfo.symbol(var1);
            if (this.currentTree != null && var2.kind == 4 && var2.owner.kind == 16 && ((Symbol.VarSymbol)var2).pos < this.currentTree.getStartPosition()) {
               switch (this.currentTree.getTag()) {
                  case CLASSDEF:
                     if (!Flow.this.allowEffectivelyFinalInInnerClasses) {
                        this.reportInnerClsNeedsFinalError(var1, var2);
                        break;
                     }
                  case LAMBDA:
                     this.reportEffectivelyFinalError(var1, var2);
               }
            }
         }

      }

      void reportEffectivelyFinalError(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
         String var3 = this.currentTree.hasTag(JCTree.Tag.LAMBDA) ? "lambda" : "inner.cls";
         Flow.this.log.error(var1, "cant.ref.non.effectively.final.var", new Object[]{var2, Flow.this.diags.fragment(var3)});
      }

      void reportInnerClsNeedsFinalError(JCDiagnostic.DiagnosticPosition var1, Symbol var2) {
         Flow.this.log.error(var1, "local.var.accessed.from.icls.needs.final", new Object[]{var2});
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         JCTree var2 = this.currentTree;

         try {
            this.currentTree = var1.sym.isLocal() ? var1 : null;
            super.visitClassDef(var1);
         } finally {
            this.currentTree = var2;
         }

      }

      public void visitLambda(JCTree.JCLambda var1) {
         JCTree var2 = this.currentTree;

         try {
            this.currentTree = var1;
            super.visitLambda(var1);
         } finally {
            this.currentTree = var2;
         }

      }

      public void visitIdent(JCTree.JCIdent var1) {
         if (var1.sym.kind == 4) {
            this.checkEffectivelyFinal(var1, (Symbol.VarSymbol)var1.sym);
         }

      }

      public void visitAssign(JCTree.JCAssign var1) {
         JCTree.JCExpression var2 = TreeInfo.skipParens(var1.lhs);
         if (!(var2 instanceof JCTree.JCIdent)) {
            this.scan(var2);
         }

         this.scan(var1.rhs);
         this.letInit(var2);
      }

      public void visitAssignop(JCTree.JCAssignOp var1) {
         this.scan(var1.lhs);
         this.scan(var1.rhs);
         this.letInit(var1.lhs);
      }

      public void visitUnary(JCTree.JCUnary var1) {
         switch (var1.getTag()) {
            case PREINC:
            case POSTINC:
            case PREDEC:
            case POSTDEC:
               this.scan(var1.arg);
               this.letInit(var1.arg);
               break;
            default:
               this.scan(var1.arg);
         }

      }

      public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      }

      public void analyzeTree(Env var1, TreeMaker var2) {
         this.analyzeTree(var1, var1.tree, var2);
      }

      public void analyzeTree(Env var1, JCTree var2, TreeMaker var3) {
         try {
            Flow.this.attrEnv = var1;
            Flow.this.make = var3;
            this.pendingExits = new ListBuffer();
            this.scan(var2);
         } finally {
            this.pendingExits = null;
            Flow.this.make = null;
         }

      }
   }

   public class AssignAnalyzer extends BaseAnalyzer {
      final Bits inits;
      final Bits uninits;
      final Bits uninitsTry;
      final Bits initsWhenTrue;
      final Bits initsWhenFalse;
      final Bits uninitsWhenTrue;
      final Bits uninitsWhenFalse;
      protected JCTree.JCVariableDecl[] vardecls;
      JCTree.JCClassDecl classDef;
      int firstadr;
      protected int nextadr;
      protected int returnadr;
      Scope unrefdResources;
      FlowKind flowKind;
      int startPos;
      private boolean isInitialConstructor;

      public AssignAnalyzer() {
         this.flowKind = Flow.FlowKind.NORMAL;
         this.isInitialConstructor = false;
         this.inits = new Bits();
         this.uninits = new Bits();
         this.uninitsTry = new Bits();
         this.initsWhenTrue = new Bits(true);
         this.initsWhenFalse = new Bits(true);
         this.uninitsWhenTrue = new Bits(true);
         this.uninitsWhenFalse = new Bits(true);
      }

      void markDead() {
         if (!this.isInitialConstructor) {
            this.inits.inclRange(this.returnadr, this.nextadr);
         } else {
            for(int var1 = this.returnadr; var1 < this.nextadr; ++var1) {
               if (!this.isFinalUninitializedStaticField(this.vardecls[var1].sym)) {
                  this.inits.incl(var1);
               }
            }
         }

         this.uninits.inclRange(this.returnadr, this.nextadr);
      }

      protected boolean trackable(Symbol.VarSymbol var1) {
         return var1.pos >= this.startPos && (var1.owner.kind == 16 || this.isFinalUninitializedField(var1));
      }

      boolean isFinalUninitializedField(Symbol.VarSymbol var1) {
         return var1.owner.kind == 2 && (var1.flags() & 8590196752L) == 16L && this.classDef.sym.isEnclosedBy((Symbol.ClassSymbol)var1.owner);
      }

      boolean isFinalUninitializedStaticField(Symbol.VarSymbol var1) {
         return this.isFinalUninitializedField(var1) && var1.isStatic();
      }

      void newVar(JCTree.JCVariableDecl var1) {
         Symbol.VarSymbol var2 = var1.sym;
         this.vardecls = (JCTree.JCVariableDecl[])ArrayUtils.ensureCapacity((Object[])this.vardecls, this.nextadr);
         if ((var2.flags() & 16L) == 0L) {
            var2.flags_field |= 2199023255552L;
         }

         var2.adr = this.nextadr;
         this.vardecls[this.nextadr] = var1;
         this.inits.excl(this.nextadr);
         this.uninits.incl(this.nextadr);
         ++this.nextadr;
      }

      void letInit(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2) {
         if (var2.adr >= this.firstadr && this.trackable(var2)) {
            if ((var2.flags() & 2199023255552L) != 0L) {
               if (!this.uninits.isMember(var2.adr)) {
                  var2.flags_field &= -2199023255553L;
               } else {
                  this.uninit(var2);
               }
            } else if ((var2.flags() & 16L) != 0L) {
               if ((var2.flags() & 8589934592L) != 0L) {
                  if ((var2.flags() & 549755813888L) != 0L) {
                     Flow.this.log.error(var1, "multicatch.parameter.may.not.be.assigned", new Object[]{var2});
                  } else {
                     Flow.this.log.error(var1, "final.parameter.may.not.be.assigned", new Object[]{var2});
                  }
               } else if (!this.uninits.isMember(var2.adr)) {
                  Flow.this.log.error(var1, this.flowKind.errKey, new Object[]{var2});
               } else {
                  this.uninit(var2);
               }
            }

            this.inits.incl(var2.adr);
         } else if ((var2.flags() & 16L) != 0L) {
            Flow.this.log.error(var1, "var.might.already.be.assigned", new Object[]{var2});
         }

      }

      void uninit(Symbol.VarSymbol var1) {
         if (!this.inits.isMember(var1.adr)) {
            this.uninits.excl(var1.adr);
            this.uninitsTry.excl(var1.adr);
         } else {
            this.uninits.excl(var1.adr);
         }

      }

      void letInit(JCTree var1) {
         var1 = TreeInfo.skipParens(var1);
         if (var1.hasTag(JCTree.Tag.IDENT) || var1.hasTag(JCTree.Tag.SELECT)) {
            Symbol var2 = TreeInfo.symbol(var1);
            if (var2.kind == 4) {
               this.letInit(var1.pos(), (Symbol.VarSymbol)var2);
            }
         }

      }

      void checkInit(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2) {
         this.checkInit(var1, var2, "var.might.not.have.been.initialized");
      }

      void checkInit(JCDiagnostic.DiagnosticPosition var1, Symbol.VarSymbol var2, String var3) {
         if ((var2.adr >= this.firstadr || var2.owner.kind != 2) && this.trackable(var2) && !this.inits.isMember(var2.adr)) {
            Flow.this.log.error(var1, var3, new Object[]{var2});
            this.inits.incl(var2.adr);
         }

      }

      private void resetBits(Bits... var1) {
         Bits[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Bits var5 = var2[var4];
            var5.reset();
         }

      }

      void split(boolean var1) {
         this.initsWhenFalse.assign(this.inits);
         this.uninitsWhenFalse.assign(this.uninits);
         this.initsWhenTrue.assign(this.inits);
         this.uninitsWhenTrue.assign(this.uninits);
         if (var1) {
            this.resetBits(this.inits, this.uninits);
         }

      }

      protected void merge() {
         this.inits.assign(this.initsWhenFalse.andSet(this.initsWhenTrue));
         this.uninits.assign(this.uninitsWhenFalse.andSet(this.uninitsWhenTrue));
      }

      void scanExpr(JCTree var1) {
         if (var1 != null) {
            this.scan(var1);
            if (this.inits.isReset()) {
               this.merge();
            }
         }

      }

      void scanExprs(List var1) {
         if (var1 != null) {
            for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
               this.scanExpr((JCTree)var2.head);
            }
         }

      }

      void scanCond(JCTree var1) {
         if (var1.type.isFalse()) {
            if (this.inits.isReset()) {
               this.merge();
            }

            this.initsWhenTrue.assign(this.inits);
            this.initsWhenTrue.inclRange(this.firstadr, this.nextadr);
            this.uninitsWhenTrue.assign(this.uninits);
            this.uninitsWhenTrue.inclRange(this.firstadr, this.nextadr);
            this.initsWhenFalse.assign(this.inits);
            this.uninitsWhenFalse.assign(this.uninits);
         } else if (var1.type.isTrue()) {
            if (this.inits.isReset()) {
               this.merge();
            }

            this.initsWhenFalse.assign(this.inits);
            this.initsWhenFalse.inclRange(this.firstadr, this.nextadr);
            this.uninitsWhenFalse.assign(this.uninits);
            this.uninitsWhenFalse.inclRange(this.firstadr, this.nextadr);
            this.initsWhenTrue.assign(this.inits);
            this.uninitsWhenTrue.assign(this.uninits);
         } else {
            this.scan(var1);
            if (!this.inits.isReset()) {
               this.split(var1.type != Flow.this.syms.unknownType);
            }
         }

         if (var1.type != Flow.this.syms.unknownType) {
            this.resetBits(this.inits, this.uninits);
         }

      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         if (var1.sym != null) {
            Lint var2 = Flow.this.lint;
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

            try {
               if (var1.sym != null) {
                  JCTree.JCClassDecl var3 = this.classDef;
                  int var4 = this.firstadr;
                  int var5 = this.nextadr;
                  ListBuffer var6 = this.pendingExits;
                  this.pendingExits = new ListBuffer();
                  if (var1.name != Flow.this.names.empty) {
                     this.firstadr = this.nextadr;
                  }

                  this.classDef = var1;

                  try {
                     List var7;
                     JCTree.JCVariableDecl var8;
                     Symbol.VarSymbol var9;
                     for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                        if (((JCTree)var7.head).hasTag(JCTree.Tag.VARDEF)) {
                           var8 = (JCTree.JCVariableDecl)var7.head;
                           if ((var8.mods.flags & 8L) != 0L) {
                              var9 = var8.sym;
                              if (this.trackable(var9)) {
                                 this.newVar(var8);
                              }
                           }
                        }
                     }

                     for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                        if (!((JCTree)var7.head).hasTag(JCTree.Tag.METHODDEF) && (TreeInfo.flags((JCTree)var7.head) & 8L) != 0L) {
                           this.scan((JCTree)var7.head);
                        }
                     }

                     for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                        if (((JCTree)var7.head).hasTag(JCTree.Tag.VARDEF)) {
                           var8 = (JCTree.JCVariableDecl)var7.head;
                           if ((var8.mods.flags & 8L) == 0L) {
                              var9 = var8.sym;
                              if (this.trackable(var9)) {
                                 this.newVar(var8);
                              }
                           }
                        }
                     }

                     for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                        if (!((JCTree)var7.head).hasTag(JCTree.Tag.METHODDEF) && (TreeInfo.flags((JCTree)var7.head) & 8L) == 0L) {
                           this.scan((JCTree)var7.head);
                        }
                     }

                     for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                        if (((JCTree)var7.head).hasTag(JCTree.Tag.METHODDEF)) {
                           this.scan((JCTree)var7.head);
                        }
                     }

                     return;
                  } finally {
                     this.pendingExits = var6;
                     this.nextadr = var5;
                     this.firstadr = var4;
                     this.classDef = var3;
                  }
               }
            } finally {
               Flow.this.lint = var2;
            }

         }
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         if (var1.body != null) {
            if ((var1.sym.flags() & 4096L) == 0L) {
               Lint var2 = Flow.this.lint;
               Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

               try {
                  if (var1.body == null) {
                     return;
                  }

                  if ((var1.sym.flags() & 562949953425408L) == 4096L) {
                     return;
                  }

                  Bits var3 = new Bits(this.inits);
                  Bits var4 = new Bits(this.uninits);
                  int var5 = this.nextadr;
                  int var6 = this.firstadr;
                  int var7 = this.returnadr;
                  Assert.check(this.pendingExits.isEmpty());
                  boolean var8 = this.isInitialConstructor;

                  try {
                     this.isInitialConstructor = TreeInfo.isInitialConstructor(var1);
                     if (!this.isInitialConstructor) {
                        this.firstadr = this.nextadr;
                     }

                     List var9;
                     for(var9 = var1.params; var9.nonEmpty(); var9 = var9.tail) {
                        JCTree.JCVariableDecl var10 = (JCTree.JCVariableDecl)var9.head;
                        this.scan(var10);
                        Assert.check((var10.sym.flags() & 8589934592L) != 0L, "Method parameter without PARAMETER flag");
                        this.initParam(var10);
                     }

                     this.scan(var1.body);
                     if (this.isInitialConstructor) {
                        boolean var21 = (var1.sym.flags() & 68719476736L) != 0L;

                        for(int var22 = this.firstadr; var22 < this.nextadr; ++var22) {
                           JCTree.JCVariableDecl var11 = this.vardecls[var22];
                           Symbol.VarSymbol var12 = var11.sym;
                           if (var12.owner == this.classDef.sym) {
                              if (var21) {
                                 this.checkInit(TreeInfo.diagnosticPositionFor(var12, var11), var12, "var.not.initialized.in.default.constructor");
                              } else {
                                 this.checkInit(TreeInfo.diagEndPos(var1.body), var12);
                              }
                           }
                        }
                     }

                     var9 = this.pendingExits.toList();
                     this.pendingExits = new ListBuffer();

                     while(var9.nonEmpty()) {
                        AssignPendingExit var23 = (AssignPendingExit)var9.head;
                        var9 = var9.tail;
                        Assert.check(var23.tree.hasTag(JCTree.Tag.RETURN), (Object)var23.tree);
                        if (this.isInitialConstructor) {
                           this.inits.assign(var23.exit_inits);

                           for(int var24 = this.firstadr; var24 < this.nextadr; ++var24) {
                              this.checkInit(var23.tree.pos(), this.vardecls[var24].sym);
                           }
                        }
                     }
                  } finally {
                     this.inits.assign(var3);
                     this.uninits.assign(var4);
                     this.nextadr = var5;
                     this.firstadr = var6;
                     this.returnadr = var7;
                     this.isInitialConstructor = var8;
                  }
               } finally {
                  Flow.this.lint = var2;
               }

            }
         }
      }

      protected void initParam(JCTree.JCVariableDecl var1) {
         this.inits.incl(var1.sym.adr);
         this.uninits.excl(var1.sym.adr);
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         Lint var2 = Flow.this.lint;
         Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

         try {
            boolean var3 = this.trackable(var1.sym);
            if (var3 && var1.sym.owner.kind == 16) {
               this.newVar(var1);
            }

            if (var1.init != null) {
               this.scanExpr(var1.init);
               if (var3) {
                  this.letInit(var1.pos(), var1.sym);
               }
            }
         } finally {
            Flow.this.lint = var2;
         }

      }

      public void visitBlock(JCTree.JCBlock var1) {
         int var2 = this.nextadr;
         this.scan(var1.stats);
         this.nextadr = var2;
      }

      public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
         ListBuffer var2 = this.pendingExits;
         FlowKind var3 = this.flowKind;
         this.flowKind = Flow.FlowKind.NORMAL;
         Bits var4 = new Bits(true);
         Bits var5 = new Bits(true);
         this.pendingExits = new ListBuffer();
         int var6 = Flow.this.log.nerrors;

         while(true) {
            Bits var7 = new Bits(this.uninits);
            var7.excludeFrom(this.nextadr);
            this.scan(var1.body);
            this.resolveContinues(var1);
            this.scanCond(var1.cond);
            if (!this.flowKind.isFinal()) {
               var4.assign(this.initsWhenFalse);
               var5.assign(this.uninitsWhenFalse);
            }

            if (Flow.this.log.nerrors != var6 || this.flowKind.isFinal() || (new Bits(var7)).diffSet(this.uninitsWhenTrue).nextBit(this.firstadr) == -1) {
               this.flowKind = var3;
               this.inits.assign(var4);
               this.uninits.assign(var5);
               this.resolveBreaks(var1, var2);
               return;
            }

            this.inits.assign(this.initsWhenTrue);
            this.uninits.assign(var7.andSet(this.uninitsWhenTrue));
            this.flowKind = Flow.FlowKind.SPECULATIVE_LOOP;
         }
      }

      public void visitWhileLoop(JCTree.JCWhileLoop var1) {
         ListBuffer var2 = this.pendingExits;
         FlowKind var3 = this.flowKind;
         this.flowKind = Flow.FlowKind.NORMAL;
         Bits var4 = new Bits(true);
         Bits var5 = new Bits(true);
         this.pendingExits = new ListBuffer();
         int var6 = Flow.this.log.nerrors;
         Bits var7 = new Bits(this.uninits);
         var7.excludeFrom(this.nextadr);

         while(true) {
            this.scanCond(var1.cond);
            if (!this.flowKind.isFinal()) {
               var4.assign(this.initsWhenFalse);
               var5.assign(this.uninitsWhenFalse);
            }

            this.inits.assign(this.initsWhenTrue);
            this.uninits.assign(this.uninitsWhenTrue);
            this.scan(var1.body);
            this.resolveContinues(var1);
            if (Flow.this.log.nerrors != var6 || this.flowKind.isFinal() || (new Bits(var7)).diffSet(this.uninits).nextBit(this.firstadr) == -1) {
               this.flowKind = var3;
               this.inits.assign(var4);
               this.uninits.assign(var5);
               this.resolveBreaks(var1, var2);
               return;
            }

            this.uninits.assign(var7.andSet(this.uninits));
            this.flowKind = Flow.FlowKind.SPECULATIVE_LOOP;
         }
      }

      public void visitForLoop(JCTree.JCForLoop var1) {
         ListBuffer var2 = this.pendingExits;
         FlowKind var3 = this.flowKind;
         this.flowKind = Flow.FlowKind.NORMAL;
         int var4 = this.nextadr;
         this.scan(var1.init);
         Bits var5 = new Bits(true);
         Bits var6 = new Bits(true);
         this.pendingExits = new ListBuffer();
         int var7 = Flow.this.log.nerrors;

         while(true) {
            Bits var8 = new Bits(this.uninits);
            var8.excludeFrom(this.nextadr);
            if (var1.cond != null) {
               this.scanCond(var1.cond);
               if (!this.flowKind.isFinal()) {
                  var5.assign(this.initsWhenFalse);
                  var6.assign(this.uninitsWhenFalse);
               }

               this.inits.assign(this.initsWhenTrue);
               this.uninits.assign(this.uninitsWhenTrue);
            } else if (!this.flowKind.isFinal()) {
               var5.assign(this.inits);
               var5.inclRange(this.firstadr, this.nextadr);
               var6.assign(this.uninits);
               var6.inclRange(this.firstadr, this.nextadr);
            }

            this.scan(var1.body);
            this.resolveContinues(var1);
            this.scan(var1.step);
            if (Flow.this.log.nerrors != var7 || this.flowKind.isFinal() || (new Bits(var8)).diffSet(this.uninits).nextBit(this.firstadr) == -1) {
               this.flowKind = var3;
               this.inits.assign(var5);
               this.uninits.assign(var6);
               this.resolveBreaks(var1, var2);
               this.nextadr = var4;
               return;
            }

            this.uninits.assign(var8.andSet(this.uninits));
            this.flowKind = Flow.FlowKind.SPECULATIVE_LOOP;
         }
      }

      public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
         this.visitVarDef(var1.var);
         ListBuffer var2 = this.pendingExits;
         FlowKind var3 = this.flowKind;
         this.flowKind = Flow.FlowKind.NORMAL;
         int var4 = this.nextadr;
         this.scan(var1.expr);
         Bits var5 = new Bits(this.inits);
         Bits var6 = new Bits(this.uninits);
         this.letInit(var1.pos(), var1.var.sym);
         this.pendingExits = new ListBuffer();
         int var7 = Flow.this.log.nerrors;

         while(true) {
            Bits var8 = new Bits(this.uninits);
            var8.excludeFrom(this.nextadr);
            this.scan(var1.body);
            this.resolveContinues(var1);
            if (Flow.this.log.nerrors != var7 || this.flowKind.isFinal() || (new Bits(var8)).diffSet(this.uninits).nextBit(this.firstadr) == -1) {
               this.flowKind = var3;
               this.inits.assign(var5);
               this.uninits.assign(var6.andSet(this.uninits));
               this.resolveBreaks(var1, var2);
               this.nextadr = var4;
               return;
            }

            this.uninits.assign(var8.andSet(this.uninits));
            this.flowKind = Flow.FlowKind.SPECULATIVE_LOOP;
         }
      }

      public void visitLabelled(JCTree.JCLabeledStatement var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.body);
         this.resolveBreaks(var1, var2);
      }

      public void visitSwitch(JCTree.JCSwitch var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         int var3 = this.nextadr;
         this.scanExpr(var1.selector);
         Bits var4 = new Bits(this.inits);
         Bits var5 = new Bits(this.uninits);
         boolean var6 = false;

         for(List var7 = var1.cases; var7.nonEmpty(); var7 = var7.tail) {
            this.inits.assign(var4);
            this.uninits.assign(this.uninits.andSet(var5));
            JCTree.JCCase var8 = (JCTree.JCCase)var7.head;
            if (var8.pat == null) {
               var6 = true;
            } else {
               this.scanExpr(var8.pat);
            }

            if (var6) {
               this.inits.assign(var4);
               this.uninits.assign(this.uninits.andSet(var5));
            }

            this.scan(var8.stats);
            this.addVars(var8.stats, var4, var5);
            if (!var6) {
               this.inits.assign(var4);
               this.uninits.assign(this.uninits.andSet(var5));
            }
         }

         if (!var6) {
            this.inits.andSet(var4);
         }

         this.resolveBreaks(var1, var2);
         this.nextadr = var3;
      }

      private void addVars(List var1, Bits var2, Bits var3) {
         for(; var1.nonEmpty(); var1 = var1.tail) {
            JCTree var4 = (JCTree)var1.head;
            if (var4.hasTag(JCTree.Tag.VARDEF)) {
               int var5 = ((JCTree.JCVariableDecl)var4).sym.adr;
               var2.excl(var5);
               var3.incl(var5);
            }
         }

      }

      public void visitTry(JCTree.JCTry var1) {
         ListBuffer var2 = new ListBuffer();
         Bits var3 = new Bits(this.uninitsTry);
         ListBuffer var4 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         Bits var5 = new Bits(this.inits);
         this.uninitsTry.assign(this.uninits);
         Iterator var6 = var1.resources.iterator();

         while(var6.hasNext()) {
            JCTree var7 = (JCTree)var6.next();
            if (var7 instanceof JCTree.JCVariableDecl) {
               JCTree.JCVariableDecl var8 = (JCTree.JCVariableDecl)var7;
               this.visitVarDef(var8);
               this.unrefdResources.enter(var8.sym);
               var2.append(var8);
            } else {
               if (!(var7 instanceof JCTree.JCExpression)) {
                  throw new AssertionError(var1);
               }

               this.scanExpr((JCTree.JCExpression)var7);
            }
         }

         this.scan(var1.body);
         this.uninitsTry.andSet(this.uninits);
         Bits var13 = new Bits(this.inits);
         Bits var14 = new Bits(this.uninits);
         int var15 = this.nextadr;
         if (!var2.isEmpty() && Flow.this.lint.isEnabled(Lint.LintCategory.TRY)) {
            Iterator var9 = var2.iterator();

            while(var9.hasNext()) {
               JCTree.JCVariableDecl var10 = (JCTree.JCVariableDecl)var9.next();
               if (this.unrefdResources.includes(var10.sym)) {
                  Flow.this.log.warning(Lint.LintCategory.TRY, var10.pos(), "try.resource.not.referenced", new Object[]{var10.sym});
                  this.unrefdResources.remove(var10.sym);
               }
            }
         }

         Bits var16 = new Bits(var5);
         Bits var17 = new Bits(this.uninitsTry);

         for(List var11 = var1.catchers; var11.nonEmpty(); var11 = var11.tail) {
            JCTree.JCVariableDecl var12 = ((JCTree.JCCatch)var11.head).param;
            this.inits.assign(var16);
            this.uninits.assign(var17);
            this.scan(var12);
            this.initParam(var12);
            this.scan(((JCTree.JCCatch)var11.head).body);
            var13.andSet(this.inits);
            var14.andSet(this.uninits);
            this.nextadr = var15;
         }

         ListBuffer var18;
         if (var1.finalizer != null) {
            this.inits.assign(var5);
            this.uninits.assign(this.uninitsTry);
            var18 = this.pendingExits;
            this.pendingExits = var4;
            this.scan(var1.finalizer);
            if (var1.finallyCanCompleteNormally) {
               this.uninits.andSet(var14);

               AssignPendingExit var19;
               for(; var18.nonEmpty(); this.pendingExits.append(var19)) {
                  var19 = (AssignPendingExit)var18.next();
                  if (var19.exit_inits != null) {
                     var19.exit_inits.orSet(this.inits);
                     var19.exit_uninits.andSet(this.uninits);
                  }
               }

               this.inits.orSet(var13);
            }
         } else {
            this.inits.assign(var13);
            this.uninits.assign(var14);
            var18 = this.pendingExits;
            this.pendingExits = var4;

            while(var18.nonEmpty()) {
               this.pendingExits.append(var18.next());
            }
         }

         this.uninitsTry.andSet(var3).andSet(this.uninits);
      }

      public void visitConditional(JCTree.JCConditional var1) {
         this.scanCond(var1.cond);
         Bits var2 = new Bits(this.initsWhenFalse);
         Bits var3 = new Bits(this.uninitsWhenFalse);
         this.inits.assign(this.initsWhenTrue);
         this.uninits.assign(this.uninitsWhenTrue);
         Bits var4;
         Bits var5;
         if (var1.truepart.type.hasTag(TypeTag.BOOLEAN) && var1.falsepart.type.hasTag(TypeTag.BOOLEAN)) {
            this.scanCond(var1.truepart);
            var4 = new Bits(this.initsWhenTrue);
            var5 = new Bits(this.initsWhenFalse);
            Bits var6 = new Bits(this.uninitsWhenTrue);
            Bits var7 = new Bits(this.uninitsWhenFalse);
            this.inits.assign(var2);
            this.uninits.assign(var3);
            this.scanCond(var1.falsepart);
            this.initsWhenTrue.andSet(var4);
            this.initsWhenFalse.andSet(var5);
            this.uninitsWhenTrue.andSet(var6);
            this.uninitsWhenFalse.andSet(var7);
         } else {
            this.scanExpr(var1.truepart);
            var4 = new Bits(this.inits);
            var5 = new Bits(this.uninits);
            this.inits.assign(var2);
            this.uninits.assign(var3);
            this.scanExpr(var1.falsepart);
            this.inits.andSet(var4);
            this.uninits.andSet(var5);
         }

      }

      public void visitIf(JCTree.JCIf var1) {
         this.scanCond(var1.cond);
         Bits var2 = new Bits(this.initsWhenFalse);
         Bits var3 = new Bits(this.uninitsWhenFalse);
         this.inits.assign(this.initsWhenTrue);
         this.uninits.assign(this.uninitsWhenTrue);
         this.scan(var1.thenpart);
         if (var1.elsepart != null) {
            Bits var4 = new Bits(this.inits);
            Bits var5 = new Bits(this.uninits);
            this.inits.assign(var2);
            this.uninits.assign(var3);
            this.scan(var1.elsepart);
            this.inits.andSet(var4);
            this.uninits.andSet(var5);
         } else {
            this.inits.andSet(var2);
            this.uninits.andSet(var3);
         }

      }

      public void visitBreak(JCTree.JCBreak var1) {
         this.recordExit(new AssignPendingExit(var1, this.inits, this.uninits));
      }

      public void visitContinue(JCTree.JCContinue var1) {
         this.recordExit(new AssignPendingExit(var1, this.inits, this.uninits));
      }

      public void visitReturn(JCTree.JCReturn var1) {
         this.scanExpr(var1.expr);
         this.recordExit(new AssignPendingExit(var1, this.inits, this.uninits));
      }

      public void visitThrow(JCTree.JCThrow var1) {
         this.scanExpr(var1.expr);
         this.markDead();
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         this.scanExpr(var1.meth);
         this.scanExprs(var1.args);
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         this.scanExpr(var1.encl);
         this.scanExprs(var1.args);
         this.scan(var1.def);
      }

      public void visitLambda(JCTree.JCLambda var1) {
         Bits var2 = new Bits(this.uninits);
         Bits var3 = new Bits(this.inits);
         int var4 = this.returnadr;
         ListBuffer var5 = this.pendingExits;

         try {
            this.returnadr = this.nextadr;
            this.pendingExits = new ListBuffer();
            List var6 = var1.params;

            while(true) {
               if (!var6.nonEmpty()) {
                  if (var1.getBodyKind() == LambdaExpressionTree.BodyKind.EXPRESSION) {
                     this.scanExpr(var1.body);
                  } else {
                     this.scan(var1.body);
                  }
                  break;
               }

               JCTree.JCVariableDecl var7 = (JCTree.JCVariableDecl)var6.head;
               this.scan(var7);
               this.inits.incl(var7.sym.adr);
               this.uninits.excl(var7.sym.adr);
               var6 = var6.tail;
            }
         } finally {
            this.returnadr = var4;
            this.uninits.assign(var2);
            this.inits.assign(var3);
            this.pendingExits = var5;
         }

      }

      public void visitNewArray(JCTree.JCNewArray var1) {
         this.scanExprs(var1.dims);
         this.scanExprs(var1.elems);
      }

      public void visitAssert(JCTree.JCAssert var1) {
         Bits var2 = new Bits(this.inits);
         Bits var3 = new Bits(this.uninits);
         this.scanCond(var1.cond);
         var3.andSet(this.uninitsWhenTrue);
         if (var1.detail != null) {
            this.inits.assign(this.initsWhenFalse);
            this.uninits.assign(this.uninitsWhenFalse);
            this.scanExpr(var1.detail);
         }

         this.inits.assign(var2);
         this.uninits.assign(var3);
      }

      public void visitAssign(JCTree.JCAssign var1) {
         JCTree.JCExpression var2 = TreeInfo.skipParens(var1.lhs);
         if (!this.isIdentOrThisDotIdent(var2)) {
            this.scanExpr(var2);
         }

         this.scanExpr(var1.rhs);
         this.letInit(var2);
      }

      private boolean isIdentOrThisDotIdent(JCTree var1) {
         if (var1.hasTag(JCTree.Tag.IDENT)) {
            return true;
         } else if (!var1.hasTag(JCTree.Tag.SELECT)) {
            return false;
         } else {
            JCTree.JCFieldAccess var2 = (JCTree.JCFieldAccess)var1;
            return var2.selected.hasTag(JCTree.Tag.IDENT) && ((JCTree.JCIdent)var2.selected).name == Flow.this.names._this;
         }
      }

      public void visitSelect(JCTree.JCFieldAccess var1) {
         super.visitSelect(var1);
         if (Flow.this.enforceThisDotInit && var1.selected.hasTag(JCTree.Tag.IDENT) && ((JCTree.JCIdent)var1.selected).name == Flow.this.names._this && var1.sym.kind == 4) {
            this.checkInit(var1.pos(), (Symbol.VarSymbol)var1.sym);
         }

      }

      public void visitAssignop(JCTree.JCAssignOp var1) {
         this.scanExpr(var1.lhs);
         this.scanExpr(var1.rhs);
         this.letInit(var1.lhs);
      }

      public void visitUnary(JCTree.JCUnary var1) {
         switch (var1.getTag()) {
            case NOT:
               this.scanCond(var1.arg);
               Bits var2 = new Bits(this.initsWhenFalse);
               this.initsWhenFalse.assign(this.initsWhenTrue);
               this.initsWhenTrue.assign(var2);
               var2.assign(this.uninitsWhenFalse);
               this.uninitsWhenFalse.assign(this.uninitsWhenTrue);
               this.uninitsWhenTrue.assign(var2);
               break;
            case PREINC:
            case POSTINC:
            case PREDEC:
            case POSTDEC:
               this.scanExpr(var1.arg);
               this.letInit(var1.arg);
               break;
            default:
               this.scanExpr(var1.arg);
         }

      }

      public void visitBinary(JCTree.JCBinary var1) {
         switch (var1.getTag()) {
            case AND:
               this.scanCond(var1.lhs);
               Bits var2 = new Bits(this.initsWhenFalse);
               Bits var3 = new Bits(this.uninitsWhenFalse);
               this.inits.assign(this.initsWhenTrue);
               this.uninits.assign(this.uninitsWhenTrue);
               this.scanCond(var1.rhs);
               this.initsWhenFalse.andSet(var2);
               this.uninitsWhenFalse.andSet(var3);
               break;
            case OR:
               this.scanCond(var1.lhs);
               Bits var4 = new Bits(this.initsWhenTrue);
               Bits var5 = new Bits(this.uninitsWhenTrue);
               this.inits.assign(this.initsWhenFalse);
               this.uninits.assign(this.uninitsWhenFalse);
               this.scanCond(var1.rhs);
               this.initsWhenTrue.andSet(var4);
               this.uninitsWhenTrue.andSet(var5);
               break;
            default:
               this.scanExpr(var1.lhs);
               this.scanExpr(var1.rhs);
         }

      }

      public void visitIdent(JCTree.JCIdent var1) {
         if (var1.sym.kind == 4) {
            this.checkInit(var1.pos(), (Symbol.VarSymbol)var1.sym);
            this.referenced(var1.sym);
         }

      }

      void referenced(Symbol var1) {
         this.unrefdResources.remove(var1);
      }

      public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
         var1.underlyingType.accept(this);
      }

      public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      }

      public void analyzeTree(Env var1) {
         this.analyzeTree(var1, var1.tree);
      }

      public void analyzeTree(Env var1, JCTree var2) {
         boolean var7 = false;

         int var3;
         try {
            var7 = true;
            this.startPos = var2.pos().getStartPosition();
            if (this.vardecls == null) {
               this.vardecls = new JCTree.JCVariableDecl[32];
            } else {
               for(var3 = 0; var3 < this.vardecls.length; ++var3) {
                  this.vardecls[var3] = null;
               }
            }

            this.firstadr = 0;
            this.nextadr = 0;
            this.pendingExits = new ListBuffer();
            this.classDef = null;
            this.unrefdResources = new Scope(var1.enclClass.sym);
            this.scan(var2);
            var7 = false;
         } finally {
            if (var7) {
               this.startPos = -1;
               this.resetBits(this.inits, this.uninits, this.uninitsTry, this.initsWhenTrue, this.initsWhenFalse, this.uninitsWhenTrue, this.uninitsWhenFalse);
               if (this.vardecls != null) {
                  for(int var5 = 0; var5 < this.vardecls.length; ++var5) {
                     this.vardecls[var5] = null;
                  }
               }

               this.firstadr = 0;
               this.nextadr = 0;
               this.pendingExits = null;
               this.classDef = null;
               this.unrefdResources = null;
            }
         }

         this.startPos = -1;
         this.resetBits(this.inits, this.uninits, this.uninitsTry, this.initsWhenTrue, this.initsWhenFalse, this.uninitsWhenTrue, this.uninitsWhenFalse);
         if (this.vardecls != null) {
            for(var3 = 0; var3 < this.vardecls.length; ++var3) {
               this.vardecls[var3] = null;
            }
         }

         this.firstadr = 0;
         this.nextadr = 0;
         this.pendingExits = null;
         this.classDef = null;
         this.unrefdResources = null;
      }

      public class AssignPendingExit extends BaseAnalyzer.PendingExit {
         final Bits inits;
         final Bits uninits;
         final Bits exit_inits = new Bits(true);
         final Bits exit_uninits = new Bits(true);

         public AssignPendingExit(JCTree var2, Bits var3, Bits var4) {
            super(var2);
            this.inits = var3;
            this.uninits = var4;
            this.exit_inits.assign(var3);
            this.exit_uninits.assign(var4);
         }

         void resolveJump() {
            this.inits.andSet(this.exit_inits);
            this.uninits.andSet(this.exit_uninits);
         }
      }
   }

   class LambdaFlowAnalyzer extends FlowAnalyzer {
      List inferredThrownTypes;
      boolean inLambda;

      LambdaFlowAnalyzer() {
         super();
      }

      public void visitLambda(JCTree.JCLambda var1) {
         if ((var1.type == null || !var1.type.isErroneous()) && !this.inLambda) {
            List var2 = this.caught;
            List var3 = this.thrown;
            ListBuffer var4 = this.pendingExits;
            this.inLambda = true;

            try {
               this.pendingExits = new ListBuffer();
               this.caught = List.of(Flow.this.syms.throwableType);
               this.thrown = List.nil();
               this.scan(var1.body);
               this.inferredThrownTypes = this.thrown;
            } finally {
               this.pendingExits = var4;
               this.caught = var2;
               this.thrown = var3;
               this.inLambda = false;
            }

         }
      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
      }
   }

   class FlowAnalyzer extends BaseAnalyzer {
      HashMap preciseRethrowTypes;
      JCTree.JCClassDecl classDef;
      List thrown;
      List caught;

      void markDead() {
      }

      void errorUncaught() {
         for(FlowPendingExit var1 = (FlowPendingExit)this.pendingExits.next(); var1 != null; var1 = (FlowPendingExit)this.pendingExits.next()) {
            if (this.classDef != null && this.classDef.pos == var1.tree.pos) {
               Flow.this.log.error(var1.tree.pos(), "unreported.exception.default.constructor", new Object[]{var1.thrown});
            } else if (var1.tree.hasTag(JCTree.Tag.VARDEF) && ((JCTree.JCVariableDecl)var1.tree).sym.isResourceVariable()) {
               Flow.this.log.error(var1.tree.pos(), "unreported.exception.implicit.close", new Object[]{var1.thrown, ((JCTree.JCVariableDecl)var1.tree).sym.name});
            } else {
               Flow.this.log.error(var1.tree.pos(), "unreported.exception.need.to.catch.or.throw", new Object[]{var1.thrown});
            }
         }

      }

      void markThrown(JCTree var1, Type var2) {
         if (!Flow.this.chk.isUnchecked(var1.pos(), var2)) {
            if (!Flow.this.chk.isHandled(var2, this.caught)) {
               this.pendingExits.append(new FlowPendingExit(var1, var2));
            }

            this.thrown = Flow.this.chk.incl(var2, this.thrown);
         }

      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         if (var1.sym != null) {
            JCTree.JCClassDecl var2 = this.classDef;
            List var3 = this.thrown;
            List var4 = this.caught;
            ListBuffer var5 = this.pendingExits;
            Lint var6 = Flow.this.lint;
            this.pendingExits = new ListBuffer();
            if (var1.name != Flow.this.names.empty) {
               this.caught = List.nil();
            }

            this.classDef = var1;
            this.thrown = List.nil();
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

            try {
               List var7;
               for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                  if (!((JCTree)var7.head).hasTag(JCTree.Tag.METHODDEF) && (TreeInfo.flags((JCTree)var7.head) & 8L) != 0L) {
                     this.scan((JCTree)var7.head);
                     this.errorUncaught();
                  }
               }

               if (var1.name != Flow.this.names.empty) {
                  boolean var13 = true;

                  for(List var8 = var1.defs; var8.nonEmpty(); var8 = var8.tail) {
                     if (TreeInfo.isInitialConstructor((JCTree)var8.head)) {
                        List var9 = ((JCTree.JCMethodDecl)var8.head).sym.type.getThrownTypes();
                        if (var13) {
                           this.caught = var9;
                           var13 = false;
                        } else {
                           this.caught = Flow.this.chk.intersect(var9, this.caught);
                        }
                     }
                  }
               }

               for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                  if (!((JCTree)var7.head).hasTag(JCTree.Tag.METHODDEF) && (TreeInfo.flags((JCTree)var7.head) & 8L) == 0L) {
                     this.scan((JCTree)var7.head);
                     this.errorUncaught();
                  }
               }

               if (var1.name == Flow.this.names.empty) {
                  for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                     if (TreeInfo.isInitialConstructor((JCTree)var7.head)) {
                        JCTree.JCMethodDecl var14 = (JCTree.JCMethodDecl)var7.head;
                        var14.thrown = Flow.this.make.Types(this.thrown);
                        var14.sym.type = Flow.this.types.createMethodTypeWithThrown(var14.sym.type, this.thrown);
                     }
                  }

                  var3 = Flow.this.chk.union(this.thrown, var3);
               }

               for(var7 = var1.defs; var7.nonEmpty(); var7 = var7.tail) {
                  if (((JCTree)var7.head).hasTag(JCTree.Tag.METHODDEF)) {
                     this.scan((JCTree)var7.head);
                     this.errorUncaught();
                  }
               }

               this.thrown = var3;
            } finally {
               this.pendingExits = var5;
               this.caught = var4;
               this.classDef = var2;
               Flow.this.lint = var6;
            }

         }
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         if (var1.body != null) {
            List var2 = this.caught;
            List var3 = var1.sym.type.getThrownTypes();
            Lint var4 = Flow.this.lint;
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);
            Assert.check(this.pendingExits.isEmpty());

            try {
               List var5;
               for(var5 = var1.params; var5.nonEmpty(); var5 = var5.tail) {
                  JCTree.JCVariableDecl var6 = (JCTree.JCVariableDecl)var5.head;
                  this.scan(var6);
               }

               if (TreeInfo.isInitialConstructor(var1)) {
                  this.caught = Flow.this.chk.union(this.caught, var3);
               } else if ((var1.sym.flags() & 1048584L) != 1048576L) {
                  this.caught = var3;
               }

               this.scan(var1.body);
               var5 = this.pendingExits.toList();
               this.pendingExits = new ListBuffer();

               while(var5.nonEmpty()) {
                  FlowPendingExit var10 = (FlowPendingExit)var5.head;
                  var5 = var5.tail;
                  if (var10.thrown == null) {
                     Assert.check(var10.tree.hasTag(JCTree.Tag.RETURN));
                  } else {
                     this.pendingExits.append(var10);
                  }
               }

            } finally {
               this.caught = var2;
               Flow.this.lint = var4;
            }
         }
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         if (var1.init != null) {
            Lint var2 = Flow.this.lint;
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

            try {
               this.scan(var1.init);
            } finally {
               Flow.this.lint = var2;
            }
         }

      }

      public void visitBlock(JCTree.JCBlock var1) {
         this.scan(var1.stats);
      }

      public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.body);
         this.resolveContinues(var1);
         this.scan(var1.cond);
         this.resolveBreaks(var1, var2);
      }

      public void visitWhileLoop(JCTree.JCWhileLoop var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.cond);
         this.scan(var1.body);
         this.resolveContinues(var1);
         this.resolveBreaks(var1, var2);
      }

      public void visitForLoop(JCTree.JCForLoop var1) {
         ListBuffer var2 = this.pendingExits;
         this.scan(var1.init);
         this.pendingExits = new ListBuffer();
         if (var1.cond != null) {
            this.scan(var1.cond);
         }

         this.scan(var1.body);
         this.resolveContinues(var1);
         this.scan(var1.step);
         this.resolveBreaks(var1, var2);
      }

      public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
         this.visitVarDef(var1.var);
         ListBuffer var2 = this.pendingExits;
         this.scan(var1.expr);
         this.pendingExits = new ListBuffer();
         this.scan(var1.body);
         this.resolveContinues(var1);
         this.resolveBreaks(var1, var2);
      }

      public void visitLabelled(JCTree.JCLabeledStatement var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.body);
         this.resolveBreaks(var1, var2);
      }

      public void visitSwitch(JCTree.JCSwitch var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.selector);

         for(List var3 = var1.cases; var3.nonEmpty(); var3 = var3.tail) {
            JCTree.JCCase var4 = (JCTree.JCCase)var3.head;
            if (var4.pat != null) {
               this.scan(var4.pat);
            }

            this.scan(var4.stats);
         }

         this.resolveBreaks(var1, var2);
      }

      public void visitTry(JCTree.JCTry var1) {
         List var2 = this.caught;
         List var3 = this.thrown;
         this.thrown = List.nil();

         List var5;
         for(List var4 = var1.catchers; var4.nonEmpty(); var4 = var4.tail) {
            var5 = TreeInfo.isMultiCatch((JCTree.JCCatch)var4.head) ? ((JCTree.JCTypeUnion)((JCTree.JCCatch)var4.head).param.vartype).alternatives : List.of(((JCTree.JCCatch)var4.head).param.vartype);

            JCTree.JCExpression var7;
            for(Iterator var6 = var5.iterator(); var6.hasNext(); this.caught = Flow.this.chk.incl(var7.type, this.caught)) {
               var7 = (JCTree.JCExpression)var6.next();
            }
         }

         ListBuffer var15 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         Iterator var16 = var1.resources.iterator();

         JCTree var17;
         while(var16.hasNext()) {
            var17 = (JCTree)var16.next();
            if (var17 instanceof JCTree.JCVariableDecl) {
               JCTree.JCVariableDecl var19 = (JCTree.JCVariableDecl)var17;
               this.visitVarDef(var19);
            } else {
               if (!(var17 instanceof JCTree.JCExpression)) {
                  throw new AssertionError(var1);
               }

               this.scan((JCTree.JCExpression)var17);
            }
         }

         var16 = var1.resources.iterator();

         Iterator var12;
         List var20;
         label118:
         while(var16.hasNext()) {
            var17 = (JCTree)var16.next();
            var20 = var17.type.isCompound() ? Flow.this.types.interfaces(var17.type).prepend(Flow.this.types.supertype(var17.type)) : List.of(var17.type);
            Iterator var8 = var20.iterator();

            while(true) {
               Symbol var10;
               Type var11;
               do {
                  Type var9;
                  do {
                     if (!var8.hasNext()) {
                        continue label118;
                     }

                     var9 = (Type)var8.next();
                  } while(Flow.this.types.asSuper(var9, Flow.this.syms.autoCloseableType.tsym) == null);

                  var10 = Flow.this.rs.resolveQualifiedMethod(var1, Flow.this.attrEnv, var9, Flow.this.names.close, List.nil(), List.nil());
                  var11 = Flow.this.types.memberType(var17.type, var10);
               } while(var10.kind != 16);

               var12 = var11.getThrownTypes().iterator();

               while(var12.hasNext()) {
                  Type var13 = (Type)var12.next();
                  this.markThrown(var17, var13);
               }
            }
         }

         this.scan(var1.body);
         var5 = Flow.this.allowImprovedCatchAnalysis ? Flow.this.chk.union(this.thrown, List.of(Flow.this.syms.runtimeExceptionType, Flow.this.syms.errorType)) : this.thrown;
         this.thrown = var3;
         this.caught = var2;
         List var18 = List.nil();

         for(var20 = var1.catchers; var20.nonEmpty(); var20 = var20.tail) {
            JCTree.JCVariableDecl var21 = ((JCTree.JCCatch)var20.head).param;
            List var24 = TreeInfo.isMultiCatch((JCTree.JCCatch)var20.head) ? ((JCTree.JCTypeUnion)((JCTree.JCCatch)var20.head).param.vartype).alternatives : List.of(((JCTree.JCCatch)var20.head).param.vartype);
            List var25 = List.nil();
            List var26 = Flow.this.chk.diff(var5, var18);
            var12 = var24.iterator();

            while(var12.hasNext()) {
               JCTree.JCExpression var27 = (JCTree.JCExpression)var12.next();
               Type var14 = var27.type;
               if (var14 != Flow.this.syms.unknownType) {
                  var25 = var25.append(var14);
                  if (!Flow.this.types.isSameType(var14, Flow.this.syms.objectType)) {
                     this.checkCaughtType(((JCTree.JCCatch)var20.head).pos(), var14, var5, var18);
                     var18 = Flow.this.chk.incl(var14, var18);
                  }
               }
            }

            this.scan(var21);
            this.preciseRethrowTypes.put(var21.sym, Flow.this.chk.intersect(var25, var26));
            this.scan(((JCTree.JCCatch)var20.head).body);
            this.preciseRethrowTypes.remove(var21.sym);
         }

         if (var1.finalizer != null) {
            var20 = this.thrown;
            this.thrown = List.nil();
            ListBuffer var22 = this.pendingExits;
            this.pendingExits = var15;
            this.scan(var1.finalizer);
            if (!var1.finallyCanCompleteNormally) {
               this.thrown = Flow.this.chk.union(this.thrown, var3);
            } else {
               this.thrown = Flow.this.chk.union(this.thrown, Flow.this.chk.diff(var5, var18));
               this.thrown = Flow.this.chk.union(this.thrown, var20);

               while(var22.nonEmpty()) {
                  this.pendingExits.append(var22.next());
               }
            }
         } else {
            this.thrown = Flow.this.chk.union(this.thrown, Flow.this.chk.diff(var5, var18));
            ListBuffer var23 = this.pendingExits;
            this.pendingExits = var15;

            while(var23.nonEmpty()) {
               this.pendingExits.append(var23.next());
            }
         }

      }

      public void visitIf(JCTree.JCIf var1) {
         this.scan(var1.cond);
         this.scan(var1.thenpart);
         if (var1.elsepart != null) {
            this.scan(var1.elsepart);
         }

      }

      void checkCaughtType(JCDiagnostic.DiagnosticPosition var1, Type var2, List var3, List var4) {
         if (Flow.this.chk.subset(var2, var4)) {
            Flow.this.log.error(var1, "except.already.caught", new Object[]{var2});
         } else if (!Flow.this.chk.isUnchecked(var1, var2) && !this.isExceptionOrThrowable(var2) && !Flow.this.chk.intersects(var2, var3)) {
            Flow.this.log.error(var1, "except.never.thrown.in.try", new Object[]{var2});
         } else if (Flow.this.allowImprovedCatchAnalysis) {
            List var5 = Flow.this.chk.intersect(List.of(var2), var3);
            if (Flow.this.chk.diff(var5, var4).isEmpty() && !this.isExceptionOrThrowable(var2)) {
               String var6 = var5.length() == 1 ? "unreachable.catch" : "unreachable.catch.1";
               Flow.this.log.warning(var1, var6, new Object[]{var5});
            }
         }

      }

      private boolean isExceptionOrThrowable(Type var1) {
         return var1.tsym == Flow.this.syms.throwableType.tsym || var1.tsym == Flow.this.syms.exceptionType.tsym;
      }

      public void visitBreak(JCTree.JCBreak var1) {
         this.recordExit(new FlowPendingExit(var1, (Type)null));
      }

      public void visitContinue(JCTree.JCContinue var1) {
         this.recordExit(new FlowPendingExit(var1, (Type)null));
      }

      public void visitReturn(JCTree.JCReturn var1) {
         this.scan(var1.expr);
         this.recordExit(new FlowPendingExit(var1, (Type)null));
      }

      public void visitThrow(JCTree.JCThrow var1) {
         this.scan(var1.expr);
         Symbol var2 = TreeInfo.symbol(var1.expr);
         if (var2 != null && var2.kind == 4 && (var2.flags() & 2199023255568L) != 0L && this.preciseRethrowTypes.get(var2) != null && Flow.this.allowImprovedRethrowAnalysis) {
            Iterator var3 = ((List)this.preciseRethrowTypes.get(var2)).iterator();

            while(var3.hasNext()) {
               Type var4 = (Type)var3.next();
               this.markThrown(var1, var4);
            }
         } else {
            this.markThrown(var1, var1.expr.type);
         }

         this.markDead();
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         this.scan(var1.meth);
         this.scan(var1.args);

         for(List var2 = var1.meth.type.getThrownTypes(); var2.nonEmpty(); var2 = var2.tail) {
            this.markThrown(var1, (Type)var2.head);
         }

      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         this.scan(var1.encl);
         this.scan(var1.args);

         List var2;
         for(var2 = var1.constructorType.getThrownTypes(); var2.nonEmpty(); var2 = var2.tail) {
            this.markThrown(var1, (Type)var2.head);
         }

         var2 = this.caught;

         try {
            if (var1.def != null) {
               for(List var3 = var1.constructor.type.getThrownTypes(); var3.nonEmpty(); var3 = var3.tail) {
                  this.caught = Flow.this.chk.incl((Type)var3.head, this.caught);
               }
            }

            this.scan(var1.def);
         } finally {
            this.caught = var2;
         }

      }

      public void visitLambda(JCTree.JCLambda var1) {
         if (var1.type == null || !var1.type.isErroneous()) {
            List var2 = this.caught;
            List var3 = this.thrown;
            ListBuffer var4 = this.pendingExits;

            try {
               this.pendingExits = new ListBuffer();
               this.caught = var1.getDescriptorType(Flow.this.types).getThrownTypes();
               this.thrown = List.nil();
               this.scan(var1.body);
               List var5 = this.pendingExits.toList();
               this.pendingExits = new ListBuffer();

               while(var5.nonEmpty()) {
                  FlowPendingExit var6 = (FlowPendingExit)var5.head;
                  var5 = var5.tail;
                  if (var6.thrown == null) {
                     Assert.check(var6.tree.hasTag(JCTree.Tag.RETURN));
                  } else {
                     this.pendingExits.append(var6);
                  }
               }

               this.errorUncaught();
            } finally {
               this.pendingExits = var4;
               this.caught = var2;
               this.thrown = var3;
            }
         }
      }

      public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      }

      public void analyzeTree(Env var1, TreeMaker var2) {
         this.analyzeTree(var1, var1.tree, var2);
      }

      public void analyzeTree(Env var1, JCTree var2, TreeMaker var3) {
         try {
            Flow.this.attrEnv = var1;
            Flow.this.make = var3;
            this.pendingExits = new ListBuffer();
            this.preciseRethrowTypes = new HashMap();
            this.thrown = this.caught = null;
            this.classDef = null;
            this.scan(var2);
         } finally {
            this.pendingExits = null;
            Flow.this.make = null;
            this.thrown = this.caught = null;
            this.classDef = null;
         }

      }

      class FlowPendingExit extends BaseAnalyzer.PendingExit {
         Type thrown;

         FlowPendingExit(JCTree var2, Type var3) {
            super(var2);
            this.thrown = var3;
         }
      }
   }

   class AliveAnalyzer extends BaseAnalyzer {
      private boolean alive;

      void markDead() {
         this.alive = false;
      }

      void scanDef(JCTree var1) {
         this.scanStat(var1);
         if (var1 != null && var1.hasTag(JCTree.Tag.BLOCK) && !this.alive) {
            Flow.this.log.error(var1.pos(), "initializer.must.be.able.to.complete.normally", new Object[0]);
         }

      }

      void scanStat(JCTree var1) {
         if (!this.alive && var1 != null) {
            Flow.this.log.error(var1.pos(), "unreachable.stmt", new Object[0]);
            if (!var1.hasTag(JCTree.Tag.SKIP)) {
               this.alive = true;
            }
         }

         this.scan(var1);
      }

      void scanStats(List var1) {
         if (var1 != null) {
            for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
               this.scanStat((JCTree)var2.head);
            }
         }

      }

      public void visitClassDef(JCTree.JCClassDecl var1) {
         if (var1.sym != null) {
            boolean var2 = this.alive;
            ListBuffer var3 = this.pendingExits;
            Lint var4 = Flow.this.lint;
            this.pendingExits = new ListBuffer();
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

            try {
               List var5;
               for(var5 = var1.defs; var5.nonEmpty(); var5 = var5.tail) {
                  if (!((JCTree)var5.head).hasTag(JCTree.Tag.METHODDEF) && (TreeInfo.flags((JCTree)var5.head) & 8L) != 0L) {
                     this.scanDef((JCTree)var5.head);
                  }
               }

               for(var5 = var1.defs; var5.nonEmpty(); var5 = var5.tail) {
                  if (!((JCTree)var5.head).hasTag(JCTree.Tag.METHODDEF) && (TreeInfo.flags((JCTree)var5.head) & 8L) == 0L) {
                     this.scanDef((JCTree)var5.head);
                  }
               }

               for(var5 = var1.defs; var5.nonEmpty(); var5 = var5.tail) {
                  if (((JCTree)var5.head).hasTag(JCTree.Tag.METHODDEF)) {
                     this.scan((JCTree)var5.head);
                  }
               }
            } finally {
               this.pendingExits = var3;
               this.alive = var2;
               Flow.this.lint = var4;
            }

         }
      }

      public void visitMethodDef(JCTree.JCMethodDecl var1) {
         if (var1.body != null) {
            Lint var2 = Flow.this.lint;
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);
            Assert.check(this.pendingExits.isEmpty());

            try {
               this.alive = true;
               this.scanStat(var1.body);
               if (this.alive && !var1.sym.type.getReturnType().hasTag(TypeTag.VOID)) {
                  Flow.this.log.error(TreeInfo.diagEndPos(var1.body), "missing.ret.stmt", new Object[0]);
               }

               List var3 = this.pendingExits.toList();
               this.pendingExits = new ListBuffer();

               while(var3.nonEmpty()) {
                  BaseAnalyzer.PendingExit var4 = (BaseAnalyzer.PendingExit)var3.head;
                  var3 = var3.tail;
                  Assert.check(var4.tree.hasTag(JCTree.Tag.RETURN));
               }
            } finally {
               Flow.this.lint = var2;
            }

         }
      }

      public void visitVarDef(JCTree.JCVariableDecl var1) {
         if (var1.init != null) {
            Lint var2 = Flow.this.lint;
            Flow.this.lint = Flow.this.lint.augment((Symbol)var1.sym);

            try {
               this.scan(var1.init);
            } finally {
               Flow.this.lint = var2;
            }
         }

      }

      public void visitBlock(JCTree.JCBlock var1) {
         this.scanStats(var1.stats);
      }

      public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scanStat(var1.body);
         this.alive |= this.resolveContinues(var1);
         this.scan(var1.cond);
         this.alive = this.alive && !var1.cond.type.isTrue();
         this.alive |= this.resolveBreaks(var1, var2);
      }

      public void visitWhileLoop(JCTree.JCWhileLoop var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.cond);
         this.alive = !var1.cond.type.isFalse();
         this.scanStat(var1.body);
         this.alive |= this.resolveContinues(var1);
         this.alive = this.resolveBreaks(var1, var2) || !var1.cond.type.isTrue();
      }

      public void visitForLoop(JCTree.JCForLoop var1) {
         ListBuffer var2 = this.pendingExits;
         this.scanStats(var1.init);
         this.pendingExits = new ListBuffer();
         if (var1.cond != null) {
            this.scan(var1.cond);
            this.alive = !var1.cond.type.isFalse();
         } else {
            this.alive = true;
         }

         this.scanStat(var1.body);
         this.alive |= this.resolveContinues(var1);
         this.scan(var1.step);
         this.alive = this.resolveBreaks(var1, var2) || var1.cond != null && !var1.cond.type.isTrue();
      }

      public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
         this.visitVarDef(var1.var);
         ListBuffer var2 = this.pendingExits;
         this.scan(var1.expr);
         this.pendingExits = new ListBuffer();
         this.scanStat(var1.body);
         this.alive |= this.resolveContinues(var1);
         this.resolveBreaks(var1, var2);
         this.alive = true;
      }

      public void visitLabelled(JCTree.JCLabeledStatement var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scanStat(var1.body);
         this.alive |= this.resolveBreaks(var1, var2);
      }

      public void visitSwitch(JCTree.JCSwitch var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         this.scan(var1.selector);
         boolean var3 = false;

         for(List var4 = var1.cases; var4.nonEmpty(); var4 = var4.tail) {
            this.alive = true;
            JCTree.JCCase var5 = (JCTree.JCCase)var4.head;
            if (var5.pat == null) {
               var3 = true;
            } else {
               this.scan(var5.pat);
            }

            this.scanStats(var5.stats);
            if (this.alive && Flow.this.lint.isEnabled(Lint.LintCategory.FALLTHROUGH) && var5.stats.nonEmpty() && var4.tail.nonEmpty()) {
               Flow.this.log.warning(Lint.LintCategory.FALLTHROUGH, ((JCTree.JCCase)var4.tail.head).pos(), "possible.fall-through.into.case", new Object[0]);
            }
         }

         if (!var3) {
            this.alive = true;
         }

         this.alive |= this.resolveBreaks(var1, var2);
      }

      public void visitTry(JCTree.JCTry var1) {
         ListBuffer var2 = this.pendingExits;
         this.pendingExits = new ListBuffer();
         Iterator var3 = var1.resources.iterator();

         JCTree.JCVariableDecl var5;
         while(var3.hasNext()) {
            JCTree var4 = (JCTree)var3.next();
            if (var4 instanceof JCTree.JCVariableDecl) {
               var5 = (JCTree.JCVariableDecl)var4;
               this.visitVarDef(var5);
            } else {
               if (!(var4 instanceof JCTree.JCExpression)) {
                  throw new AssertionError(var1);
               }

               this.scan((JCTree.JCExpression)var4);
            }
         }

         this.scanStat(var1.body);
         boolean var6 = this.alive;

         for(List var7 = var1.catchers; var7.nonEmpty(); var7 = var7.tail) {
            this.alive = true;
            var5 = ((JCTree.JCCatch)var7.head).param;
            this.scan(var5);
            this.scanStat(((JCTree.JCCatch)var7.head).body);
            var6 |= this.alive;
         }

         ListBuffer var8;
         if (var1.finalizer != null) {
            var8 = this.pendingExits;
            this.pendingExits = var2;
            this.alive = true;
            this.scanStat(var1.finalizer);
            var1.finallyCanCompleteNormally = this.alive;
            if (!this.alive) {
               if (Flow.this.lint.isEnabled(Lint.LintCategory.FINALLY)) {
                  Flow.this.log.warning(Lint.LintCategory.FINALLY, TreeInfo.diagEndPos(var1.finalizer), "finally.cannot.complete", new Object[0]);
               }
            } else {
               while(var8.nonEmpty()) {
                  this.pendingExits.append(var8.next());
               }

               this.alive = var6;
            }
         } else {
            this.alive = var6;
            var8 = this.pendingExits;
            this.pendingExits = var2;

            while(var8.nonEmpty()) {
               this.pendingExits.append(var8.next());
            }
         }

      }

      public void visitIf(JCTree.JCIf var1) {
         this.scan(var1.cond);
         this.scanStat(var1.thenpart);
         if (var1.elsepart != null) {
            boolean var2 = this.alive;
            this.alive = true;
            this.scanStat(var1.elsepart);
            this.alive |= var2;
         } else {
            this.alive = true;
         }

      }

      public void visitBreak(JCTree.JCBreak var1) {
         this.recordExit(new BaseAnalyzer.PendingExit(var1));
      }

      public void visitContinue(JCTree.JCContinue var1) {
         this.recordExit(new BaseAnalyzer.PendingExit(var1));
      }

      public void visitReturn(JCTree.JCReturn var1) {
         this.scan(var1.expr);
         this.recordExit(new BaseAnalyzer.PendingExit(var1));
      }

      public void visitThrow(JCTree.JCThrow var1) {
         this.scan(var1.expr);
         this.markDead();
      }

      public void visitApply(JCTree.JCMethodInvocation var1) {
         this.scan(var1.meth);
         this.scan(var1.args);
      }

      public void visitNewClass(JCTree.JCNewClass var1) {
         this.scan(var1.encl);
         this.scan(var1.args);
         if (var1.def != null) {
            this.scan(var1.def);
         }

      }

      public void visitLambda(JCTree.JCLambda var1) {
         if (var1.type == null || !var1.type.isErroneous()) {
            ListBuffer var2 = this.pendingExits;
            boolean var3 = this.alive;

            try {
               this.pendingExits = new ListBuffer();
               this.alive = true;
               this.scanStat(var1.body);
               var1.canCompleteNormally = this.alive;
            } finally {
               this.pendingExits = var2;
               this.alive = var3;
            }

         }
      }

      public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      }

      public void analyzeTree(Env var1, TreeMaker var2) {
         this.analyzeTree(var1, var1.tree, var2);
      }

      public void analyzeTree(Env var1, JCTree var2, TreeMaker var3) {
         try {
            Flow.this.attrEnv = var1;
            Flow.this.make = var3;
            this.pendingExits = new ListBuffer();
            this.alive = true;
            this.scan(var2);
         } finally {
            this.pendingExits = null;
            Flow.this.make = null;
         }

      }
   }

   abstract static class BaseAnalyzer extends TreeScanner {
      ListBuffer pendingExits;

      abstract void markDead();

      void recordExit(PendingExit var1) {
         this.pendingExits.append(var1);
         this.markDead();
      }

      private boolean resolveJump(JCTree var1, ListBuffer var2, JumpKind var3) {
         boolean var4 = false;
         List var5 = this.pendingExits.toList();

         for(this.pendingExits = var2; var5.nonEmpty(); var5 = var5.tail) {
            PendingExit var6 = (PendingExit)var5.head;
            if (var6.tree.hasTag(var3.treeTag) && var3.getTarget(var6.tree) == var1) {
               var6.resolveJump();
               var4 = true;
            } else {
               this.pendingExits.append(var6);
            }
         }

         return var4;
      }

      boolean resolveContinues(JCTree var1) {
         return this.resolveJump(var1, new ListBuffer(), Flow.BaseAnalyzer.JumpKind.CONTINUE);
      }

      boolean resolveBreaks(JCTree var1, ListBuffer var2) {
         return this.resolveJump(var1, var2, Flow.BaseAnalyzer.JumpKind.BREAK);
      }

      public void scan(JCTree var1) {
         if (var1 != null && (var1.type == null || var1.type != Type.stuckType)) {
            super.scan(var1);
         }

      }

      static class PendingExit {
         JCTree tree;

         PendingExit(JCTree var1) {
            this.tree = var1;
         }

         void resolveJump() {
         }
      }

      static enum JumpKind {
         BREAK(JCTree.Tag.BREAK) {
            JCTree getTarget(JCTree var1) {
               return ((JCTree.JCBreak)var1).target;
            }
         },
         CONTINUE(JCTree.Tag.CONTINUE) {
            JCTree getTarget(JCTree var1) {
               return ((JCTree.JCContinue)var1).target;
            }
         };

         final JCTree.Tag treeTag;

         private JumpKind(JCTree.Tag var3) {
            this.treeTag = var3;
         }

         abstract JCTree getTarget(JCTree var1);

         // $FF: synthetic method
         JumpKind(JCTree.Tag var3, Object var4) {
            this(var3);
         }
      }
   }

   static enum FlowKind {
      NORMAL("var.might.already.be.assigned", false),
      SPECULATIVE_LOOP("var.might.be.assigned.in.loop", true);

      final String errKey;
      final boolean isFinal;

      private FlowKind(String var3, boolean var4) {
         this.errKey = var3;
         this.isFinal = var4;
      }

      boolean isFinal() {
         return this.isFinal;
      }
   }
}
