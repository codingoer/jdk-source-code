package com.sun.tools.javac.tree;

import com.sun.tools.javac.util.Assert;
import com.sun.tools.javac.util.List;
import java.util.Iterator;

public class TreeScanner extends JCTree.Visitor {
   public void scan(JCTree var1) {
      if (var1 != null) {
         var1.accept(this);
      }

   }

   public void scan(List var1) {
      if (var1 != null) {
         for(List var2 = var1; var2.nonEmpty(); var2 = var2.tail) {
            this.scan((JCTree)var2.head);
         }
      }

   }

   public void visitTopLevel(JCTree.JCCompilationUnit var1) {
      this.scan(var1.packageAnnotations);
      this.scan((JCTree)var1.pid);
      this.scan(var1.defs);
   }

   public void visitImport(JCTree.JCImport var1) {
      this.scan(var1.qualid);
   }

   public void visitClassDef(JCTree.JCClassDecl var1) {
      this.scan((JCTree)var1.mods);
      this.scan(var1.typarams);
      this.scan((JCTree)var1.extending);
      this.scan(var1.implementing);
      this.scan(var1.defs);
   }

   public void visitMethodDef(JCTree.JCMethodDecl var1) {
      this.scan((JCTree)var1.mods);
      this.scan((JCTree)var1.restype);
      this.scan(var1.typarams);
      this.scan((JCTree)var1.recvparam);
      this.scan(var1.params);
      this.scan(var1.thrown);
      this.scan((JCTree)var1.defaultValue);
      this.scan((JCTree)var1.body);
   }

   public void visitVarDef(JCTree.JCVariableDecl var1) {
      this.scan((JCTree)var1.mods);
      this.scan((JCTree)var1.vartype);
      this.scan((JCTree)var1.nameexpr);
      this.scan((JCTree)var1.init);
   }

   public void visitSkip(JCTree.JCSkip var1) {
   }

   public void visitBlock(JCTree.JCBlock var1) {
      this.scan(var1.stats);
   }

   public void visitDoLoop(JCTree.JCDoWhileLoop var1) {
      this.scan((JCTree)var1.body);
      this.scan((JCTree)var1.cond);
   }

   public void visitWhileLoop(JCTree.JCWhileLoop var1) {
      this.scan((JCTree)var1.cond);
      this.scan((JCTree)var1.body);
   }

   public void visitForLoop(JCTree.JCForLoop var1) {
      this.scan(var1.init);
      this.scan((JCTree)var1.cond);
      this.scan(var1.step);
      this.scan((JCTree)var1.body);
   }

   public void visitForeachLoop(JCTree.JCEnhancedForLoop var1) {
      this.scan((JCTree)var1.var);
      this.scan((JCTree)var1.expr);
      this.scan((JCTree)var1.body);
   }

   public void visitLabelled(JCTree.JCLabeledStatement var1) {
      this.scan((JCTree)var1.body);
   }

   public void visitSwitch(JCTree.JCSwitch var1) {
      this.scan((JCTree)var1.selector);
      this.scan(var1.cases);
   }

   public void visitCase(JCTree.JCCase var1) {
      this.scan((JCTree)var1.pat);
      this.scan(var1.stats);
   }

   public void visitSynchronized(JCTree.JCSynchronized var1) {
      this.scan((JCTree)var1.lock);
      this.scan((JCTree)var1.body);
   }

   public void visitTry(JCTree.JCTry var1) {
      this.scan(var1.resources);
      this.scan((JCTree)var1.body);
      this.scan(var1.catchers);
      this.scan((JCTree)var1.finalizer);
   }

   public void visitCatch(JCTree.JCCatch var1) {
      this.scan((JCTree)var1.param);
      this.scan((JCTree)var1.body);
   }

   public void visitConditional(JCTree.JCConditional var1) {
      this.scan((JCTree)var1.cond);
      this.scan((JCTree)var1.truepart);
      this.scan((JCTree)var1.falsepart);
   }

   public void visitIf(JCTree.JCIf var1) {
      this.scan((JCTree)var1.cond);
      this.scan((JCTree)var1.thenpart);
      this.scan((JCTree)var1.elsepart);
   }

   public void visitExec(JCTree.JCExpressionStatement var1) {
      this.scan((JCTree)var1.expr);
   }

   public void visitBreak(JCTree.JCBreak var1) {
   }

   public void visitContinue(JCTree.JCContinue var1) {
   }

   public void visitReturn(JCTree.JCReturn var1) {
      this.scan((JCTree)var1.expr);
   }

   public void visitThrow(JCTree.JCThrow var1) {
      this.scan((JCTree)var1.expr);
   }

   public void visitAssert(JCTree.JCAssert var1) {
      this.scan((JCTree)var1.cond);
      this.scan((JCTree)var1.detail);
   }

   public void visitApply(JCTree.JCMethodInvocation var1) {
      this.scan(var1.typeargs);
      this.scan((JCTree)var1.meth);
      this.scan(var1.args);
   }

   public void visitNewClass(JCTree.JCNewClass var1) {
      this.scan((JCTree)var1.encl);
      this.scan(var1.typeargs);
      this.scan((JCTree)var1.clazz);
      this.scan(var1.args);
      this.scan((JCTree)var1.def);
   }

   public void visitNewArray(JCTree.JCNewArray var1) {
      this.scan(var1.annotations);
      this.scan((JCTree)var1.elemtype);
      this.scan(var1.dims);
      Iterator var2 = var1.dimAnnotations.iterator();

      while(var2.hasNext()) {
         List var3 = (List)var2.next();
         this.scan(var3);
      }

      this.scan(var1.elems);
   }

   public void visitLambda(JCTree.JCLambda var1) {
      this.scan(var1.body);
      this.scan(var1.params);
   }

   public void visitParens(JCTree.JCParens var1) {
      this.scan((JCTree)var1.expr);
   }

   public void visitAssign(JCTree.JCAssign var1) {
      this.scan((JCTree)var1.lhs);
      this.scan((JCTree)var1.rhs);
   }

   public void visitAssignop(JCTree.JCAssignOp var1) {
      this.scan((JCTree)var1.lhs);
      this.scan((JCTree)var1.rhs);
   }

   public void visitUnary(JCTree.JCUnary var1) {
      this.scan((JCTree)var1.arg);
   }

   public void visitBinary(JCTree.JCBinary var1) {
      this.scan((JCTree)var1.lhs);
      this.scan((JCTree)var1.rhs);
   }

   public void visitTypeCast(JCTree.JCTypeCast var1) {
      this.scan(var1.clazz);
      this.scan((JCTree)var1.expr);
   }

   public void visitTypeTest(JCTree.JCInstanceOf var1) {
      this.scan((JCTree)var1.expr);
      this.scan(var1.clazz);
   }

   public void visitIndexed(JCTree.JCArrayAccess var1) {
      this.scan((JCTree)var1.indexed);
      this.scan((JCTree)var1.index);
   }

   public void visitSelect(JCTree.JCFieldAccess var1) {
      this.scan((JCTree)var1.selected);
   }

   public void visitReference(JCTree.JCMemberReference var1) {
      this.scan((JCTree)var1.expr);
      this.scan(var1.typeargs);
   }

   public void visitIdent(JCTree.JCIdent var1) {
   }

   public void visitLiteral(JCTree.JCLiteral var1) {
   }

   public void visitTypeIdent(JCTree.JCPrimitiveTypeTree var1) {
   }

   public void visitTypeArray(JCTree.JCArrayTypeTree var1) {
      this.scan((JCTree)var1.elemtype);
   }

   public void visitTypeApply(JCTree.JCTypeApply var1) {
      this.scan((JCTree)var1.clazz);
      this.scan(var1.arguments);
   }

   public void visitTypeUnion(JCTree.JCTypeUnion var1) {
      this.scan(var1.alternatives);
   }

   public void visitTypeIntersection(JCTree.JCTypeIntersection var1) {
      this.scan(var1.bounds);
   }

   public void visitTypeParameter(JCTree.JCTypeParameter var1) {
      this.scan(var1.annotations);
      this.scan(var1.bounds);
   }

   public void visitWildcard(JCTree.JCWildcard var1) {
      this.scan((JCTree)var1.kind);
      if (var1.inner != null) {
         this.scan(var1.inner);
      }

   }

   public void visitTypeBoundKind(JCTree.TypeBoundKind var1) {
   }

   public void visitModifiers(JCTree.JCModifiers var1) {
      this.scan(var1.annotations);
   }

   public void visitAnnotation(JCTree.JCAnnotation var1) {
      this.scan(var1.annotationType);
      this.scan(var1.args);
   }

   public void visitAnnotatedType(JCTree.JCAnnotatedType var1) {
      this.scan(var1.annotations);
      this.scan((JCTree)var1.underlyingType);
   }

   public void visitErroneous(JCTree.JCErroneous var1) {
   }

   public void visitLetExpr(JCTree.LetExpr var1) {
      this.scan(var1.defs);
      this.scan(var1.expr);
   }

   public void visitTree(JCTree var1) {
      Assert.error();
   }
}
