package com.sun.tools.javac.tree;

import com.sun.tools.javac.util.List;
import java.util.Iterator;

public class TreeTranslator extends JCTree.Visitor {
   protected JCTree result;

   public JCTree translate(JCTree var1) {
      if (var1 == null) {
         return null;
      } else {
         var1.accept(this);
         JCTree var2 = this.result;
         this.result = null;
         return var2;
      }
   }

   public List translate(List var1) {
      if (var1 == null) {
         return null;
      } else {
         for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
            var2.head = this.translate((JCTree)var2.head);
         }

         return var1;
      }
   }

   public List translateVarDefs(List var1) {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         var2.head = this.translate((JCTree)var2.head);
      }

      return var1;
   }

   public List translateTypeParams(List var1) {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         var2.head = this.translate((JCTree)var2.head);
      }

      return var1;
   }

   public List translateCases(List var1) {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         var2.head = this.translate((JCTree)var2.head);
      }

      return var1;
   }

   public List translateCatchers(List var1) {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         var2.head = this.translate((JCTree)var2.head);
      }

      return var1;
   }

   public List translateAnnotations(List var1) {
      for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
         var2.head = this.translate((JCTree)var2.head);
      }

      return var1;
   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      var1.pid = (JCTree.JCExpression)this.translate((JCTree)var1.pid);
      var1.defs = this.translate(var1.defs);
      this.result = var1;
   }

   public void visitImport(JCTree.JCImport var1) {
      var1.qualid = this.translate(var1.qualid);
      this.result = var1;
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      var1.mods = (JCTree.JCModifiers)this.translate((JCTree)var1.mods);
      var1.typarams = this.translateTypeParams(var1.typarams);
      var1.extending = (JCTree.JCExpression)this.translate((JCTree)var1.extending);
      var1.implementing = this.translate(var1.implementing);
      var1.defs = this.translate(var1.defs);
      this.result = var1;
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      var1.mods = (JCTree.JCModifiers)this.translate((JCTree)var1.mods);
      var1.restype = (JCTree.JCExpression)this.translate((JCTree)var1.restype);
      var1.typarams = this.translateTypeParams(var1.typarams);
      var1.recvparam = (JCTree.JCVariableDecl)this.translate((JCTree)var1.recvparam);
      var1.params = this.translateVarDefs(var1.params);
      var1.thrown = this.translate(var1.thrown);
      var1.body = (JCTree.JCBlock)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      var1.mods = (JCTree.JCModifiers)this.translate((JCTree)var1.mods);
      var1.nameexpr = (JCTree.JCExpression)this.translate((JCTree)var1.nameexpr);
      var1.vartype = (JCTree.JCExpression)this.translate((JCTree)var1.vartype);
      var1.init = (JCTree.JCExpression)this.translate((JCTree)var1.init);
      this.result = var1;
   }

   public void visitSkip(JCTree.JCSkip var1) {
      this.result = var1;
   }

   public void visitBlock(JCTree.JCBlock var1) {
      var1.stats = this.translate(var1.stats);
      this.result = var1;
   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      var1.body = (JCTree.JCStatement)this.translate((JCTree)var1.body);
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond);
      this.result = var1;
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond);
      var1.body = (JCTree.JCStatement)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      var1.init = this.translate(var1.init);
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond);
      var1.step = this.translate(var1.step);
      var1.body = (JCTree.JCStatement)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      var1.var = (JCTree.JCVariableDecl)this.translate((JCTree)var1.var);
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      var1.body = (JCTree.JCStatement)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitLabelled(JCTree.JCLabeledStatement var1) {
      var1.body = (JCTree.JCStatement)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      var1.selector = (JCTree.JCExpression)this.translate((JCTree)var1.selector);
      var1.cases = this.translateCases(var1.cases);
      this.result = var1;
   }

   public void visitCase(JCTree.JCCase var1) {
      var1.pat = (JCTree.JCExpression)this.translate((JCTree)var1.pat);
      var1.stats = this.translate(var1.stats);
      this.result = var1;
   }

   public void visitSynchronized(JCTree.JCSynchronized var1) {
      var1.lock = (JCTree.JCExpression)this.translate((JCTree)var1.lock);
      var1.body = (JCTree.JCBlock)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitTry(JCTree.JCTry var1) {
      var1.resources = this.translate(var1.resources);
      var1.body = (JCTree.JCBlock)this.translate((JCTree)var1.body);
      var1.catchers = this.translateCatchers(var1.catchers);
      var1.finalizer = (JCTree.JCBlock)this.translate((JCTree)var1.finalizer);
      this.result = var1;
   }

   public void visitCatch(JCTree.JCCatch var1) {
      var1.param = (JCTree.JCVariableDecl)this.translate((JCTree)var1.param);
      var1.body = (JCTree.JCBlock)this.translate((JCTree)var1.body);
      this.result = var1;
   }

   public void visitConditional(JCTree.JCConditional var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond);
      var1.truepart = (JCTree.JCExpression)this.translate((JCTree)var1.truepart);
      var1.falsepart = (JCTree.JCExpression)this.translate((JCTree)var1.falsepart);
      this.result = var1;
   }

   public void visitIf(JCTree.JCIf var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond);
      var1.thenpart = (JCTree.JCStatement)this.translate((JCTree)var1.thenpart);
      var1.elsepart = (JCTree.JCStatement)this.translate((JCTree)var1.elsepart);
      this.result = var1;
   }

   public void visitExec(JCTree.JCExpressionStatement var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      this.result = var1;
   }

   public void visitBreak(JCTree.JCBreak var1) {
      this.result = var1;
   }

   public void visitContinue(JCTree.JCContinue var1) {
      this.result = var1;
   }

   public void visitReturn(JCTree.JCReturn var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      this.result = var1;
   }

   public void visitThrow(JCTree.JCThrow var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      this.result = var1;
   }

   public void visitAssert(JCTree.JCAssert var1) {
      var1.cond = (JCTree.JCExpression)this.translate((JCTree)var1.cond);
      var1.detail = (JCTree.JCExpression)this.translate((JCTree)var1.detail);
      this.result = var1;
   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      var1.meth = (JCTree.JCExpression)this.translate((JCTree)var1.meth);
      var1.args = this.translate(var1.args);
      this.result = var1;
   }

   public void visitNewClass(JCTree.JCNewClass var1) {
      var1.encl = (JCTree.JCExpression)this.translate((JCTree)var1.encl);
      var1.clazz = (JCTree.JCExpression)this.translate((JCTree)var1.clazz);
      var1.args = this.translate(var1.args);
      var1.def = (JCTree.JCClassDecl)this.translate((JCTree)var1.def);
      this.result = var1;
   }

   public void visitLambda(JCTree.JCLambda var1) {
      var1.params = this.translate(var1.params);
      var1.body = this.translate(var1.body);
      this.result = var1;
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      var1.annotations = this.translate(var1.annotations);
      List var2 = List.nil();

      List var4;
      for(Iterator var3 = var1.dimAnnotations.iterator(); var3.hasNext(); var2 = var2.append(this.translate(var4))) {
         var4 = (List)var3.next();
      }

      var1.dimAnnotations = var2;
      var1.elemtype = (JCTree.JCExpression)this.translate((JCTree)var1.elemtype);
      var1.dims = this.translate(var1.dims);
      var1.elems = this.translate(var1.elems);
      this.result = var1;
   }

   public void visitParens(JCTree.JCParens var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      this.result = var1;
   }

   public void visitAssign(JCTree.JCAssign var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs);
      this.result = var1;
   }

   public void visitAssignop(JCTree.JCAssignOp var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs);
      this.result = var1;
   }

   public void visitUnary(JCTree.JCUnary var1) {
      var1.arg = (JCTree.JCExpression)this.translate((JCTree)var1.arg);
      this.result = var1;
   }

   public void visitBinary(JCTree.JCBinary var1) {
      var1.lhs = (JCTree.JCExpression)this.translate((JCTree)var1.lhs);
      var1.rhs = (JCTree.JCExpression)this.translate((JCTree)var1.rhs);
      this.result = var1;
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      var1.clazz = this.translate(var1.clazz);
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      this.result = var1;
   }

   public void visitTypeTest(JCTree.JCInstanceOf var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      var1.clazz = this.translate(var1.clazz);
      this.result = var1;
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      var1.indexed = (JCTree.JCExpression)this.translate((JCTree)var1.indexed);
      var1.index = (JCTree.JCExpression)this.translate((JCTree)var1.index);
      this.result = var1;
   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      var1.selected = (JCTree.JCExpression)this.translate((JCTree)var1.selected);
      this.result = var1;
   }

   public void visitReference(JCTree.JCMemberReference var1) {
      var1.expr = (JCTree.JCExpression)this.translate((JCTree)var1.expr);
      this.result = var1;
   }

   public void visitIdent(JCTree.JCIdent var1) {
      this.result = var1;
   }

   public void visitLiteral(JCTree.JCLiteral var1) {
      this.result = var1;
   }

   public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
      this.result = var1;
   }

   public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
      var1.elemtype = (JCTree.JCExpression)this.translate((JCTree)var1.elemtype);
      this.result = var1;
   }

   public void visitTypeApply(JCTree.JCTypeApply var1) {
      var1.clazz = (JCTree.JCExpression)this.translate((JCTree)var1.clazz);
      var1.arguments = this.translate(var1.arguments);
      this.result = var1;
   }

   public void visitTypeUnion(JCTree.JCTypeUnion var1) {
      var1.alternatives = this.translate(var1.alternatives);
      this.result = var1;
   }

   public void visitTypeIntersection(JCTree.JCTypeIntersection var1) {
      var1.bounds = this.translate(var1.bounds);
      this.result = var1;
   }

   public void visitTypeParameter(JCTree.JCTypeParameter var1) {
      var1.annotations = this.translate(var1.annotations);
      var1.bounds = this.translate(var1.bounds);
      this.result = var1;
   }

   public void visitWildcard(JCTree.JCWildcard var1) {
      var1.kind = (JCTree.TypeBoundKind)this.translate((JCTree)var1.kind);
      var1.inner = this.translate(var1.inner);
      this.result = var1;
   }

   public void visitTypeBoundKind(JCTree.TypeBoundKind var1) {
      this.result = var1;
   }

   public void visitErroneous(JCTree.JCErroneous var1) {
      this.result = var1;
   }

   public void visitLetExpr(JCTree.LetExpr var1) {
      var1.defs = this.translateVarDefs(var1.defs);
      var1.expr = this.translate(var1.expr);
      this.result = var1;
   }

   public void visitModifiers(JCTree.JCModifiers var1) {
      var1.annotations = this.translateAnnotations(var1.annotations);
      this.result = var1;
   }

   public void visitAnnotation(JCTree.JCAnnotation var1) {
      var1.annotationType = this.translate(var1.annotationType);
      var1.args = this.translate(var1.args);
      this.result = var1;
   }

   public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
      var1.annotations = this.translate(var1.annotations);
      var1.underlyingType = (JCTree.JCExpression)this.translate((JCTree)var1.underlyingType);
      this.result = var1;
   }

   public void visitTree(JCTree var1) {
      throw new AssertionError(var1);
   }
}
