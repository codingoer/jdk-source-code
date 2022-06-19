package com.sun.tools.javac.comp;

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
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Filter;
import com.sun.tools.javac.util.JCDiagnostic;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Names;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TransTypes extends TreeTranslator {
   protected static final Context.Key transTypesKey = new Context.Key();
   private Names names;
   private Log log;
   private Symtab syms;
   private TreeMaker make;
   private Enter enter;
   private boolean allowEnums;
   private boolean allowInterfaceBridges;
   private Types types;
   private final Resolve resolve;
   private final boolean addBridges;
   private final CompileStates compileStates;
   Map overridden;
   private Filter overrideBridgeFilter = new Filter() {
      public boolean accepts(Symbol var1) {
         return (var1.flags() & 1099511631872L) != 4096L;
      }
   };
   private Type pt;
   JCTree currentMethod = null;
   private Env env;
   private static final String statePreviousToFlowAssertMsg = "The current compile state [%s] of class %s is previous to FLOW";

   public static TransTypes instance(Context var0) {
      TransTypes var1 = (TransTypes)var0.get(transTypesKey);
      if (var1 == null) {
         var1 = new TransTypes(var0);
      }

      return var1;
   }

   protected TransTypes(Context var1) {
      var1.put((Context.Key)transTypesKey, (Object)this);
      this.compileStates = CompileStates.instance(var1);
      this.names = Names.instance(var1);
      this.log = Log.instance(var1);
      this.syms = Symtab.instance(var1);
      this.enter = Enter.instance(var1);
      this.overridden = new HashMap();
      Source var2 = Source.instance(var1);
      this.allowEnums = var2.allowEnums();
      this.addBridges = var2.addBridges();
      this.allowInterfaceBridges = var2.allowDefaultMethods();
      this.types = Types.instance(var1);
      this.make = TreeMaker.instance(var1);
      this.resolve = Resolve.instance(var1);
   }

   JCTree.JCExpression cast(JCTree.JCExpression var1, Type var2) {
      int var3 = this.make.pos;
      this.make.at(var1.pos);
      if (!this.types.isSameType(var1.type, var2)) {
         if (!this.resolve.isAccessible(this.env, var2.tsym)) {
            this.resolve.logAccessErrorInternal(this.env, var1, var2);
         }

         var1 = this.make.TypeCast((JCTree)this.make.Type(var2), var1).setType(var2);
      }

      this.make.pos = var3;
      return var1;
   }

   public JCTree.JCExpression coerce(Env var1, JCTree.JCExpression var2, Type var3) {
      Env var4 = this.env;

      JCTree.JCExpression var5;
      try {
         this.env = var1;
         var5 = this.coerce(var2, var3);
      } finally {
         this.env = var4;
      }

      return var5;
   }

   JCTree.JCExpression coerce(JCTree.JCExpression var1, Type var2) {
      Type var3 = var2.baseType();
      if (var1.type.isPrimitive() == var2.isPrimitive()) {
         return this.types.isAssignable(var1.type, var3, this.types.noWarnings) ? var1 : this.cast(var1, var3);
      } else {
         return var1;
      }
   }

   JCTree.JCExpression retype(JCTree.JCExpression var1, Type var2, Type var3) {
      if (!var2.isPrimitive()) {
         if (var3 != null && var3.isPrimitive()) {
            var3 = this.erasure(var1.type);
         }

         var1.type = var2;
         if (var3 != null) {
            return this.coerce(var1, var3);
         }
      }

      return var1;
   }

   List translateArgs(List var1, List var2, Type var3) {
      if (var2.isEmpty()) {
         return var1;
      } else {
         List var4;
         for(var4 = var1; var2.tail.nonEmpty(); var2 = var2.tail) {
            var4.head = this.translate((JCTree)var4.head, (Type)var2.head);
            var4 = var4.tail;
         }

         Type var5 = (Type)var2.head;
         Assert.check(var3 != null || var4.length() == 1);
         if (var3 != null) {
            while(var4.nonEmpty()) {
               var4.head = this.translate((JCTree)var4.head, var3);
               var4 = var4.tail;
            }
         } else {
            var4.head = this.translate((JCTree)var4.head, var5);
         }

         return var1;
      }
   }

   public List translateArgs(List var1, List var2, Type var3, Env var4) {
      Env var5 = this.env;

      List var6;
      try {
         this.env = var4;
         var6 = this.translateArgs(var1, var2, var3);
      } finally {
         this.env = var5;
      }

      return var6;
   }

   void addBridge(JCDiagnostic.DiagnosticPosition var1, Symbol.MethodSymbol var2, Symbol.MethodSymbol var3, Symbol.ClassSymbol var4, boolean var5, ListBuffer var6) {
      this.make.at(var1);
      Type var7 = this.types.memberType(var4.type, var2);
      Type var8 = this.erasure(var7);
      Type var9 = var2.erasure(this.types);
      long var10 = var3.flags() & 7L | 4096L | 2147483648L | (var4.isInterface() ? 8796093022208L : 0L);
      if (var5) {
         var10 |= 137438953472L;
      }

      Symbol.MethodSymbol var12 = new Symbol.MethodSymbol(var10, var2.name, var9, var4);
      var12.params = this.createBridgeParams(var3, var12, var9);
      var12.setAttributes(var3);
      if (!var5) {
         JCTree.JCMethodDecl var13 = this.make.MethodDef(var12, (JCTree.JCBlock)null);
         Object var14 = var3.owner == var4 ? this.make.This(var4.erasure(this.types)) : this.make.Super(this.types.supertype(var4.type).tsym.erasure(this.types), var4);
         Type var15 = this.erasure(var3.type.getReturnType());
         JCTree.JCMethodInvocation var16 = this.make.Apply((List)null, this.make.Select((JCTree.JCExpression)var14, (Symbol)var3).setType(var15), this.translateArgs(this.make.Idents(var13.params), var8.getParameterTypes(), (Type)null)).setType(var15);
         Object var17 = var8.getReturnType().hasTag(TypeTag.VOID) ? this.make.Exec(var16) : this.make.Return(this.coerce(var16, var9.getReturnType()));
         var13.body = this.make.Block(0L, List.of(var17));
         var6.append(var13);
      }

      var4.members().enter(var12);
      this.overridden.put(var12, var2);
   }

   private List createBridgeParams(Symbol.MethodSymbol var1, Symbol.MethodSymbol var2, Type var3) {
      List var4 = null;
      if (var1.params != null) {
         var4 = List.nil();
         List var5 = var1.params;
         Type.MethodType var6 = (Type.MethodType)var3;

         for(List var7 = var6.argtypes; var5.nonEmpty() && var7.nonEmpty(); var7 = var7.tail) {
            Symbol.VarSymbol var8 = new Symbol.VarSymbol(((Symbol.VarSymbol)var5.head).flags() | 4096L | 8589934592L, ((Symbol.VarSymbol)var5.head).name, (Type)var7.head, var2);
            var8.setAttributes((Symbol)var5.head);
            var4 = var4.append(var8);
            var5 = var5.tail;
         }
      }

      return var4;
   }

   void addBridgeIfNeeded(JCDiagnostic.DiagnosticPosition var1, Symbol var2, Symbol.ClassSymbol var3, ListBuffer var4) {
      if (var2.kind == 16 && var2.name != this.names.init && (var2.flags() & 10L) == 0L && (var2.flags() & 1099511631872L) != 4096L && var2.isMemberOf(var3, this.types)) {
         Symbol.MethodSymbol var5 = (Symbol.MethodSymbol)var2;
         Symbol.MethodSymbol var6 = var5.binaryImplementation(var3, this.types);
         Symbol.MethodSymbol var7 = var5.implementation(var3, this.types, true, this.overrideBridgeFilter);
         if (var6 == null || var6 == var5 || var7 != null && !var6.owner.isSubClass(var7.owner, this.types)) {
            if (var7 != null && this.isBridgeNeeded(var5, var7, var3.type)) {
               this.addBridge(var1, var5, var7, var3, var6 == var7, var4);
            } else if (var7 == var5 && var7.owner != var3 && (var7.flags() & 16L) == 0L && (var5.flags() & 1025L) == 1L && (var3.flags() & 1L) > (var7.owner.flags() & 1L)) {
               this.addBridge(var1, var5, var7, var3, false, var4);
            }
         } else if ((var6.flags() & 1099511631872L) == 4096L) {
            Symbol.MethodSymbol var8 = (Symbol.MethodSymbol)this.overridden.get(var6);
            if (var8 != null && var8 != var5 && (var7 == null || !var7.overrides(var8, var3, this.types, true))) {
               this.log.error(var1, "name.clash.same.erasure.no.override", new Object[]{var8, var8.location(var3.type, this.types), var5, var5.location(var3.type, this.types)});
            }
         } else if (!var6.overrides(var5, var3, this.types, true) && (var6.owner == var3 || this.types.asSuper(var6.owner.type, var5.owner) == null)) {
            this.log.error(var1, "name.clash.same.erasure.no.override", new Object[]{var6, var6.location(var3.type, this.types), var5, var5.location(var3.type, this.types)});
         }
      }

   }

   private boolean isBridgeNeeded(Symbol.MethodSymbol var1, Symbol.MethodSymbol var2, Type var3) {
      if (var2 != var1) {
         Type var4 = var1.erasure(this.types);
         if (!this.isSameMemberWhenErased(var3, var1, var4)) {
            return true;
         } else {
            Type var5 = var2.erasure(this.types);
            if (!this.isSameMemberWhenErased(var3, var2, var5)) {
               return true;
            } else {
               return !this.types.isSameType(var5.getReturnType(), var4.getReturnType());
            }
         }
      } else if ((var1.flags() & 1024L) != 0L) {
         return false;
      } else {
         return !this.isSameMemberWhenErased(var3, var1, var1.erasure(this.types));
      }
   }

   private boolean isSameMemberWhenErased(Type var1, Symbol.MethodSymbol var2, Type var3) {
      return this.types.isSameType(this.erasure(this.types.memberType(var1, var2)), var3);
   }

   void addBridges(JCDiagnostic.DiagnosticPosition var1, Symbol.TypeSymbol var2, Symbol.ClassSymbol var3, ListBuffer var4) {
      for(Scope.Entry var5 = var2.members().elems; var5 != null; var5 = var5.sibling) {
         this.addBridgeIfNeeded(var1, var5.sym, var3, var4);
      }

      for(List var6 = this.types.interfaces(var2.type); var6.nonEmpty(); var6 = var6.tail) {
         this.addBridges(var1, ((Type)var6.head).tsym, var3, var4);
      }

   }

   void addBridges(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2, ListBuffer var3) {
      for(Type var4 = this.types.supertype(var2.type); var4.hasTag(TypeTag.CLASS); var4 = this.types.supertype(var4)) {
         this.addBridges(var1, var4.tsym, var2, var3);
      }

      for(List var5 = this.types.interfaces(var2.type); var5.nonEmpty(); var5 = var5.tail) {
         this.addBridges(var1, ((Type)var5.head).tsym, var2, var3);
      }

   }

   public JCTree translate(JCTree var1, Type var2) {
      Type var3 = this.pt;

      JCTree var4;
      try {
         this.pt = var2;
         var4 = this.translate(var1);
      } finally {
         this.pt = var3;
      }

      return var4;
   }

   public List translate(List var1, Type var2) {
      Type var3 = this.pt;

      List var4;
      try {
         this.pt = var2;
         var4 = this.translate(var1);
      } finally {
         this.pt = var3;
      }

      return var4;
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      this.translateClass(var1.sym);
      this.result = var1;
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      JCTree var2 = this.currentMethod;

      try {
         this.currentMethod = var1;
         var1.restype = (JCTree.JCExpression)this.translate((JCTree)var1.restype, (Type)null);
         var1.typarams = List.nil();
         var1.params = this.translateVarDefs(var1.params);
         var1.recvparam = (JCTree.JCVariableDecl)this.translate((JCTree)var1.recvparam, (Type)null);
         var1.thrown = this.translate((List)var1.thrown, (Type)null);
         var1.body = (JCTree.JCBlock)this.translate((JCTree)var1.body, var1.sym.erasure(this.types).getReturnType());
         var1.type = this.erasure(var1.type);
         this.result = var1;
      } finally {
         this.currentMethod = var2;
      }

      for(Scope.Entry var3 = var1.sym.owner.members().lookup(var1.name); var3.sym != null; var3 = var3.next()) {
         if (var3.sym != var1.sym && this.types.isSameType(this.erasure(var3.sym.type), var1.type)) {
            this.log.error(var1.pos(), "name.clash.same.erasure", new Object[]{var1.sym, var3.sym});
            return;
         }
      }

   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      var1.vartype = (JCTree.JCExpression)this.translate((JCTree)var1.vartype, (Type)null);
      var1.init = (JCTree.JCExpression)this.translate((JCTree)var1.init, var1.sym.erasure(this.types));
      var1.type = this.erasure(var1.type);
      this.result = var1;
   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, this.syms.booleanType);
      this.result = var1;
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, this.syms.booleanType);
      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      this.result = var1;
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      var1.init = this.translate((List)var1.init, (Type)null);
      if (var1.cond != null) {
         var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, this.syms.booleanType);
      }

      var1.step = this.translate((List)var1.step, (Type)null);
      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      this.result = var1;
   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      var1.var = (JCTree.JCVariableDecl)this.translate((JCTree)var1.var, (Type)null);
      Type var2 = var1.expr.type;
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, this.erasure(var1.expr.type));
      if (this.types.elemtype(var1.expr.type) == null) {
         var1.expr.type = var2;
      }

      var1.body = (JCTree.JCStatement)this.translate(var1.body);
      this.result = var1;
   }

   public void visitLambda(JCTree.JCLambda var1) {
      JCTree var2 = this.currentMethod;

      try {
         this.currentMethod = null;
         var1.params = this.translate(var1.params);
         var1.body = this.translate(var1.body, var1.body.type == null ? null : this.erasure(var1.body.type));
         var1.type = this.erasure(var1.type);
         this.result = var1;
      } finally {
         this.currentMethod = var2;
      }

   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      Type var2 = this.types.supertype(var1.selector.type);
      boolean var3 = var2 != null && var2.tsym == this.syms.enumSym;
      Object var4 = var3 ? this.erasure(var1.selector.type) : this.syms.intType;
      var1.selector = (JCTree.JCExpression)this.translate((JCTree)var1.selector, (Type)var4);
      var1.cases = this.translateCases(var1.cases);
      this.result = var1;
   }

   public void visitCase(JCTree.JCCase var1) {
      var1.pat = (JCTree.JCExpression)this.translate((JCTree)var1.pat, (Type)null);
      var1.stats = this.translate(var1.stats);
      this.result = var1;
   }

   public void visitSynchronized(JCTree.JCSynchronized var1) {
      var1.lock = (JCTree.JCExpression)this.translate((JCTree)var1.lock, this.erasure(var1.lock.type));
      var1.body = (JCTree.JCBlock)this.translate(var1.body);
      this.result = var1;
   }

   public void visitTry(JCTree.JCTry var1) {
      var1.resources = this.translate(var1.resources, this.syms.autoCloseableType);
      var1.body = (JCTree.JCBlock)this.translate(var1.body);
      var1.catchers = this.translateCatchers(var1.catchers);
      var1.finalizer = (JCTree.JCBlock)this.translate(var1.finalizer);
      this.result = var1;
   }

   public void visitConditional(JCTree.JCConditional var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, this.syms.booleanType);
      var1.truepart = (JCTree.JCExpression)this.translate((JCTree)var1.truepart, this.erasure(var1.type));
      var1.falsepart = (JCTree.JCExpression)this.translate((JCTree)var1.falsepart, this.erasure(var1.type));
      var1.type = this.erasure(var1.type);
      this.result = this.retype(var1, var1.type, this.pt);
   }

   public void visitIf(JCTree.JCIf var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, this.syms.booleanType);
      var1.thenpart = (JCTree.JCStatement)this.translate(var1.thenpart);
      var1.elsepart = (JCTree.JCStatement)this.translate(var1.elsepart);
      this.result = var1;
   }

   public void visitExec(JCTree.JCExpressionStatement var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, (Type)null);
      this.result = var1;
   }

   public void visitReturn(JCTree.JCReturn var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, this.currentMethod != null ? this.types.erasure(this.currentMethod.type).getReturnType() : null);
      this.result = var1;
   }

   public void visitThrow(JCTree.JCThrow var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, this.erasure(var1.expr.type));
      this.result = var1;
   }

   public void visitAssert(JCTree.JCAssert var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond, this.syms.booleanType);
      if (var1.detail != null) {
         var1.detail = (JCTree.JCExpression)this.translate((JCTree)var1.detail, this.erasure(var1.detail.type));
      }

      this.result = var1;
   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      var1.meth = (JCTree.JCExpression)this.translate((JCTree)var1.meth, (Type)null);
      Symbol var2 = TreeInfo.symbol(var1.meth);
      Type var3 = var2.erasure(this.types);
      List var4 = var3.getParameterTypes();
      if (this.allowEnums && var2.name == this.names.init && var2.owner == this.syms.enumSym) {
         var4 = var4.tail.tail;
      }

      if (var1.varargsElement != null) {
         var1.varargsElement = this.types.erasure(var1.varargsElement);
      } else if (var1.args.length() != var4.length()) {
         this.log.error(var1.pos(), "method.invoked.with.incorrect.number.arguments", new Object[]{var1.args.length(), var4.length()});
      }

      var1.args = this.translateArgs(var1.args, var4, var1.varargsElement);
      var1.type = this.types.erasure(var1.type);
      this.result = this.retype(var1, var3.getReturnType(), this.pt);
   }

   public void visitNewClass(JCTree.JCNewClass var1) {
      if (var1.encl != null) {
         var1.encl = (JCTree.JCExpression)this.translate((JCTree)var1.encl, this.erasure(var1.encl.type));
      }

      var1.clazz = (JCTree.JCExpression)this.translate((JCTree)var1.clazz, (Type)null);
      if (var1.varargsElement != null) {
         var1.varargsElement = this.types.erasure(var1.varargsElement);
      }

      var1.args = this.translateArgs(var1.args, var1.constructor.erasure(this.types).getParameterTypes(), var1.varargsElement);
      var1.def = (JCTree.JCClassDecl)this.translate((JCTree)var1.def, (Type)null);
      if (var1.constructorType != null) {
         var1.constructorType = this.erasure(var1.constructorType);
      }

      var1.type = this.erasure(var1.type);
      this.result = var1;
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      var1.elemtype = (JCTree.JCExpression)this.translate((JCTree)var1.elemtype, (Type)null);
      this.translate((List)var1.dims, this.syms.intType);
      if (var1.type != null) {
         var1.elems = this.translate(var1.elems, this.erasure(this.types.elemtype(var1.type)));
         var1.type = this.erasure(var1.type);
      } else {
         var1.elems = this.translate((List)var1.elems, (Type)null);
      }

      this.result = var1;
   }

   public void visitParens(JCTree.JCParens var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, this.pt);
      var1.type = this.erasure(var1.type);
      this.result = var1;
   }

   public void visitAssign(JCTree.JCAssign var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs, (Type)null);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs, this.erasure(var1.lhs.type));
      var1.type = this.erasure(var1.lhs.type);
      this.result = this.retype(var1, var1.type, this.pt);
   }

   public void visitAssignop(JCTree.JCAssignOp var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs, (Type)null);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs, (Type)var1.operator.type.getParameterTypes().tail.head);
      var1.type = this.erasure(var1.type);
      this.result = var1;
   }

   public void visitUnary(JCTree.JCUnary var1) {
      var1.arg = (JCTree.JCExpression)this.translate((JCTree)var1.arg, (Type)var1.operator.type.getParameterTypes().head);
      this.result = var1;
   }

   public void visitBinary(JCTree.JCBinary var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs, (Type)var1.operator.type.getParameterTypes().head);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs, (Type)var1.operator.type.getParameterTypes().tail.head);
      this.result = var1;
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      var1.clazz = this.translate((JCTree)var1.clazz, (Type)null);
      Type var2 = var1.type;
      var1.type = this.erasure(var1.type);
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, var1.type);
      if (var2.isIntersection()) {
         Type.IntersectionClassType var3 = (Type.IntersectionClassType)var2;
         Iterator var4 = var3.getExplicitComponents().iterator();

         while(var4.hasNext()) {
            Type var5 = (Type)var4.next();
            Type var6 = this.erasure(var5);
            if (!this.types.isSameType(var6, var1.type)) {
               var1.expr = this.coerce(var1.expr, var6);
            }
         }
      }

      this.result = var1;
   }

   public void visitTypeTest(JCTree.JCInstanceOf var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, (Type)null);
      var1.clazz = this.translate((JCTree)var1.clazz, (Type)null);
      this.result = var1;
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      var1.indexed = (JCTree.JCExpression)this.translate((JCTree)var1.indexed, this.erasure(var1.indexed.type));
      var1.index = (JCTree.JCExpression)this.translate((JCTree)var1.index, this.syms.intType);
      this.result = this.retype(var1, this.types.elemtype(var1.indexed.type), this.pt);
   }

   public void visitAnnotation(JCTree.JCAnnotation var1) {
      this.result = var1;
   }

   public void visitIdent(JCTree.JCIdent var1) {
      Type var2 = var1.sym.erasure(this.types);
      if (var1.sym.kind == 2 && var1.sym.type.hasTag(TypeTag.TYPEVAR)) {
         this.result = this.make.at(var1.pos).Type(var2);
      } else if (var1.type.constValue() != null) {
         this.result = var1;
      } else if (var1.sym.kind == 4) {
         this.result = this.retype(var1, var2, this.pt);
      } else {
         var1.type = this.erasure(var1.type);
         this.result = var1;
      }

   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      Type var2;
      for(var2 = var1.selected.type; var2.hasTag(TypeTag.TYPEVAR); var2 = var2.getUpperBound()) {
      }

      if (var2.isCompound()) {
         if ((var1.sym.flags() & 2097152L) != 0L) {
            var1.sym = ((Symbol.MethodSymbol)var1.sym).implemented((Symbol.TypeSymbol)var1.sym.owner, this.types);
         }

         var1.selected = this.coerce((JCTree.JCExpression)this.translate((JCTree)var1.selected, this.erasure(var1.selected.type)), this.erasure(var1.sym.owner.type));
      } else {
         var1.selected = (JCTree.JCExpression)this.translate((JCTree)var1.selected, this.erasure(var2));
      }

      if (var1.type.constValue() != null) {
         this.result = var1;
      } else if (var1.sym.kind == 4) {
         this.result = this.retype(var1, var1.sym.erasure(this.types), this.pt);
      } else {
         var1.type = this.erasure(var1.type);
         this.result = var1;
      }

   }

   public void visitReference(JCTree.JCMemberReference var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr, this.erasure(var1.expr.type));
      var1.type = this.erasure(var1.type);
      if (var1.varargsElement != null) {
         var1.varargsElement = this.erasure(var1.varargsElement);
      }

      this.result = var1;
   }

   public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
      var1.elemtype = (JCTree.JCExpression)this.translate((JCTree)var1.elemtype, (Type)null);
      var1.type = this.erasure(var1.type);
      this.result = var1;
   }

   public void visitTypeApply(JCTree.JCTypeApply var1) {
      JCTree var2 = this.translate((JCTree)var1.clazz, (Type)null);
      this.result = var2;
   }

   public void visitTypeIntersection(JCTree.JCTypeIntersection var1) {
      var1.bounds = this.translate((List)var1.bounds, (Type)null);
      var1.type = this.erasure(var1.type);
      this.result = var1;
   }

   private Type erasure(Type var1) {
      return this.types.erasure(var1);
   }

   private boolean boundsRestricted(Symbol.ClassSymbol var1) {
      Type var2 = this.types.supertype(var1.type);
      if (var2.isParameterized()) {
         List var3 = var2.allparams();

         for(List var4 = var2.tsym.type.allparams(); !var3.isEmpty() && !var4.isEmpty(); var4 = var4.tail) {
            Type var5 = (Type)var3.head;
            Type var6 = (Type)var4.head;
            if (!this.types.isSameType(this.types.erasure(var5), this.types.erasure(var6))) {
               return true;
            }

            var3 = var3.tail;
         }
      }

      return false;
   }

   private List addOverrideBridgesIfNeeded(JCDiagnostic.DiagnosticPosition var1, Symbol.ClassSymbol var2) {
      ListBuffer var3 = new ListBuffer();
      if (!var2.isInterface() && this.boundsRestricted(var2)) {
         Type var4 = this.types.supertype(var2.type);
         Scope var5 = var4.tsym.members();
         if (var5.elems != null) {
            Iterator var6 = var5.getElements(new NeedsOverridBridgeFilter(var2)).iterator();

            while(true) {
               Symbol.MethodSymbol var8;
               Symbol.MethodSymbol var9;
               Symbol.MethodSymbol var10;
               do {
                  if (!var6.hasNext()) {
                     return var3.toList();
                  }

                  Symbol var7 = (Symbol)var6.next();
                  var8 = (Symbol.MethodSymbol)var7;
                  var9 = (Symbol.MethodSymbol)var8.asMemberOf(var2.type, this.types);
                  var10 = var8.implementation(var2, this.types, false);
               } while(var10 != null && var10.owner == var2);

               if (!this.types.isSameType(var9.erasure(this.types), var8.erasure(this.types))) {
                  this.addOverrideBridges(var1, var8, var9, var2, var3);
               }
            }
         } else {
            return var3.toList();
         }
      } else {
         return var3.toList();
      }
   }

   private void addOverrideBridges(JCDiagnostic.DiagnosticPosition var1, Symbol.MethodSymbol var2, Symbol.MethodSymbol var3, Symbol.ClassSymbol var4, ListBuffer var5) {
      Type var6 = var2.erasure(this.types);
      long var7 = var2.flags() & 7L | 4096L | 2147483648L | 1099511627776L;
      var3 = new Symbol.MethodSymbol(var7, var3.name, var3.type, var4);
      JCTree.JCMethodDecl var9 = this.make.MethodDef(var3, (JCTree.JCBlock)null);
      JCTree.JCIdent var10 = this.make.Super(this.types.supertype(var4.type).tsym.erasure(this.types), var4);
      Type var11 = this.erasure(var2.type.getReturnType());
      JCTree.JCMethodInvocation var12 = this.make.Apply((List)null, this.make.Select(var10, (Symbol)var2).setType(var11), this.translateArgs(this.make.Idents(var9.params), var6.getParameterTypes(), (Type)null)).setType(var11);
      Object var13 = var3.getReturnType().hasTag(TypeTag.VOID) ? this.make.Exec(var12) : this.make.Return(this.coerce(var12, var3.erasure(this.types).getReturnType()));
      var9.body = this.make.Block(0L, List.of(var13));
      var4.members().enter(var3);
      var5.append(var9);
   }

   void translateClass(Symbol.ClassSymbol var1) {
      Type var2 = this.types.supertype(var1.type);
      if (var2.hasTag(TypeTag.CLASS)) {
         this.translateClass((Symbol.ClassSymbol)var2.tsym);
      }

      Env var3 = this.enter.getEnv(var1);
      if (var3 != null && (var1.flags_field & 1125899906842624L) == 0L) {
         var1.flags_field |= 1125899906842624L;
         boolean var4 = this.compileStates.get(var3) != null;
         if (!var4 && var1.outermostClass() == var1) {
            Assert.error("No info for outermost class: " + var3.enclClass.sym);
         }

         if (var4 && CompileStates.CompileState.FLOW.isAfter((CompileStates.CompileState)this.compileStates.get(var3))) {
            Assert.error(String.format("The current compile state [%s] of class %s is previous to FLOW", this.compileStates.get(var3), var3.enclClass.sym));
         }

         Env var5 = this.env;

         try {
            this.env = var3;
            TreeMaker var6 = this.make;
            Type var7 = this.pt;
            this.make = this.make.forToplevel(this.env.toplevel);
            this.pt = null;

            try {
               JCTree.JCClassDecl var8 = (JCTree.JCClassDecl)this.env.tree;
               var8.typarams = List.nil();
               super.visitClassDef(var8);
               this.make.at(var8.pos);
               if (this.addBridges) {
                  ListBuffer var9 = new ListBuffer();
                  if (this.allowInterfaceBridges || (var8.sym.flags() & 512L) == 0L) {
                     this.addBridges(var8.pos(), var1, var9);
                  }

                  var8.defs = var9.toList().prependList(var8.defs);
               }

               var8.type = this.erasure(var8.type);
            } finally {
               this.make = var6;
               this.pt = var7;
            }
         } finally {
            this.env = var5;
         }

      }
   }

   public JCTree translateTopLevelClass(JCTree var1, TreeMaker var2) {
      this.make = var2;
      this.pt = null;
      return this.translate((JCTree)var1, (Type)null);
   }

   class NeedsOverridBridgeFilter implements Filter {
      Symbol.ClassSymbol c;

      NeedsOverridBridgeFilter(Symbol.ClassSymbol var2) {
         this.c = var2;
      }

      public boolean accepts(Symbol var1) {
         return var1.kind == 16 && !var1.isConstructor() && var1.isInheritedIn(this.c, TransTypes.this.types) && (var1.flags() & 16L) == 0L && (var1.flags() & 1099511631872L) != 4096L;
      }
   }
}
