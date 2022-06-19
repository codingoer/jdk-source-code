package com.sun.tools.javac.tree;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import java.util.Iterator;

public class TreeCopier implements TreeVisitor {
   private TreeMaker M;

   public TreeCopier(TreeMaker var1) {
      this.M = var1;
   }

   public JCTree copy(JCTree var1) {
      return this.copy((JCTree)var1, (Object)null);
   }

   public JCTree copy(JCTree var1, Object var2) {
      return var1 == null ? null : (JCTree)var1.accept(this, var2);
   }

   public List copy(List var1) {
      return this.copy((List)var1, (Object)null);
   }

   public List copy(List var1, Object var2) {
      if (var1 == null) {
         return null;
      } else {
         ListBuffer var3 = new ListBuffer();
         Iterator var4 = var1.iterator();

         while(var4.hasNext()) {
            JCTree var5 = (JCTree)var4.next();
            var3.append(this.copy(var5, var2));
         }

         return var3.toList();
      }
   }

   public JCTree visitAnnotatedType(AnnotatedTypeTree var1, Object var2) {
      JCTree.JCAnnotatedType var3 = (JCTree.JCAnnotatedType)var1;
      List var4 = this.copy(var3.annotations, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.underlyingType, var2);
      return this.M.at(var3.pos).AnnotatedType(var4, var5);
   }

   public JCTree visitAnnotation(AnnotationTree var1, Object var2) {
      JCTree.JCAnnotation var3 = (JCTree.JCAnnotation)var1;
      JCTree var4 = this.copy(var3.annotationType, var2);
      List var5 = this.copy(var3.args, var2);
      JCTree.JCAnnotation var6;
      if (var3.getKind() == Tree.Kind.TYPE_ANNOTATION) {
         var6 = this.M.at(var3.pos).TypeAnnotation(var4, var5);
         var6.attribute = var3.attribute;
         return var6;
      } else {
         var6 = this.M.at(var3.pos).Annotation(var4, var5);
         var6.attribute = var3.attribute;
         return var6;
      }
   }

   public JCTree visitAssert(AssertTree var1, Object var2) {
      JCTree.JCAssert var3 = (JCTree.JCAssert)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.cond, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.detail, var2);
      return this.M.at(var3.pos).Assert(var4, var5);
   }

   public JCTree visitAssignment(AssignmentTree var1, Object var2) {
      JCTree.JCAssign var3 = (JCTree.JCAssign)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.lhs, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.rhs, var2);
      return this.M.at(var3.pos).Assign(var4, var5);
   }

   public JCTree visitCompoundAssignment(CompoundAssignmentTree var1, Object var2) {
      JCTree.JCAssignOp var3 = (JCTree.JCAssignOp)var1;
      JCTree var4 = this.copy((JCTree)var3.lhs, var2);
      JCTree var5 = this.copy((JCTree)var3.rhs, var2);
      return this.M.at(var3.pos).Assignop(var3.getTag(), var4, var5);
   }

   public JCTree visitBinary(BinaryTree var1, Object var2) {
      JCTree.JCBinary var3 = (JCTree.JCBinary)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.lhs, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.rhs, var2);
      return this.M.at(var3.pos).Binary(var3.getTag(), var4, var5);
   }

   public JCTree visitBlock(BlockTree var1, Object var2) {
      JCTree.JCBlock var3 = (JCTree.JCBlock)var1;
      List var4 = this.copy(var3.stats, var2);
      return this.M.at(var3.pos).Block(var3.flags, var4);
   }

   public JCTree visitBreak(BreakTree var1, Object var2) {
      JCTree.JCBreak var3 = (JCTree.JCBreak)var1;
      return this.M.at(var3.pos).Break(var3.label);
   }

   public JCTree visitCase(CaseTree var1, Object var2) {
      JCTree.JCCase var3 = (JCTree.JCCase)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.pat, var2);
      List var5 = this.copy(var3.stats, var2);
      return this.M.at(var3.pos).Case(var4, var5);
   }

   public JCTree visitCatch(CatchTree var1, Object var2) {
      JCTree.JCCatch var3 = (JCTree.JCCatch)var1;
      JCTree.JCVariableDecl var4 = (JCTree.JCVariableDecl)this.copy((JCTree)var3.param, var2);
      JCTree.JCBlock var5 = (JCTree.JCBlock)this.copy((JCTree)var3.body, var2);
      return this.M.at(var3.pos).Catch(var4, var5);
   }

   public JCTree visitClass(ClassTree var1, Object var2) {
      JCTree.JCClassDecl var3 = (JCTree.JCClassDecl)var1;
      JCTree.JCModifiers var4 = (JCTree.JCModifiers)this.copy((JCTree)var3.mods, var2);
      List var5 = this.copy(var3.typarams, var2);
      JCTree.JCExpression var6 = (JCTree.JCExpression)this.copy((JCTree)var3.extending, var2);
      List var7 = this.copy(var3.implementing, var2);
      List var8 = this.copy(var3.defs, var2);
      return this.M.at(var3.pos).ClassDef(var4, var3.name, var5, var6, var7, var8);
   }

   public JCTree visitConditionalExpression(ConditionalExpressionTree var1, Object var2) {
      JCTree.JCConditional var3 = (JCTree.JCConditional)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.cond, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.truepart, var2);
      JCTree.JCExpression var6 = (JCTree.JCExpression)this.copy((JCTree)var3.falsepart, var2);
      return this.M.at(var3.pos).Conditional(var4, var5, var6);
   }

   public JCTree visitContinue(ContinueTree var1, Object var2) {
      JCTree.JCContinue var3 = (JCTree.JCContinue)var1;
      return this.M.at(var3.pos).Continue(var3.label);
   }

   public JCTree visitDoWhileLoop(DoWhileLoopTree var1, Object var2) {
      JCTree.JCDoWhileLoop var3 = (JCTree.JCDoWhileLoop)var1;
      JCTree.JCStatement var4 = (JCTree.JCStatement)this.copy((JCTree)var3.body, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.cond, var2);
      return this.M.at(var3.pos).DoLoop(var4, var5);
   }

   public JCTree visitErroneous(ErroneousTree var1, Object var2) {
      JCTree.JCErroneous var3 = (JCTree.JCErroneous)var1;
      List var4 = this.copy(var3.errs, var2);
      return this.M.at(var3.pos).Erroneous(var4);
   }

   public JCTree visitExpressionStatement(ExpressionStatementTree var1, Object var2) {
      JCTree.JCExpressionStatement var3 = (JCTree.JCExpressionStatement)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      return this.M.at(var3.pos).Exec(var4);
   }

   public JCTree visitEnhancedForLoop(EnhancedForLoopTree var1, Object var2) {
      JCTree.JCEnhancedForLoop var3 = (JCTree.JCEnhancedForLoop)var1;
      JCTree.JCVariableDecl var4 = (JCTree.JCVariableDecl)this.copy((JCTree)var3.var, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      JCTree.JCStatement var6 = (JCTree.JCStatement)this.copy((JCTree)var3.body, var2);
      return this.M.at(var3.pos).ForeachLoop(var4, var5, var6);
   }

   public JCTree visitForLoop(ForLoopTree var1, Object var2) {
      JCTree.JCForLoop var3 = (JCTree.JCForLoop)var1;
      List var4 = this.copy(var3.init, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.cond, var2);
      List var6 = this.copy(var3.step, var2);
      JCTree.JCStatement var7 = (JCTree.JCStatement)this.copy((JCTree)var3.body, var2);
      return this.M.at(var3.pos).ForLoop(var4, var5, var6, var7);
   }

   public JCTree visitIdentifier(IdentifierTree var1, Object var2) {
      JCTree.JCIdent var3 = (JCTree.JCIdent)var1;
      return this.M.at(var3.pos).Ident(var3.name);
   }

   public JCTree visitIf(IfTree var1, Object var2) {
      JCTree.JCIf var3 = (JCTree.JCIf)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.cond, var2);
      JCTree.JCStatement var5 = (JCTree.JCStatement)this.copy((JCTree)var3.thenpart, var2);
      JCTree.JCStatement var6 = (JCTree.JCStatement)this.copy((JCTree)var3.elsepart, var2);
      return this.M.at(var3.pos).If(var4, var5, var6);
   }

   public JCTree visitImport(ImportTree var1, Object var2) {
      JCTree.JCImport var3 = (JCTree.JCImport)var1;
      JCTree var4 = this.copy(var3.qualid, var2);
      return this.M.at(var3.pos).Import(var4, var3.staticImport);
   }

   public JCTree visitArrayAccess(ArrayAccessTree var1, Object var2) {
      JCTree.JCArrayAccess var3 = (JCTree.JCArrayAccess)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.indexed, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.index, var2);
      return this.M.at(var3.pos).Indexed(var4, var5);
   }

   public JCTree visitLabeledStatement(LabeledStatementTree var1, Object var2) {
      JCTree.JCLabeledStatement var3 = (JCTree.JCLabeledStatement)var1;
      JCTree.JCStatement var4 = (JCTree.JCStatement)this.copy((JCTree)var3.body, var2);
      return this.M.at(var3.pos).Labelled(var3.label, var4);
   }

   public JCTree visitLiteral(LiteralTree var1, Object var2) {
      JCTree.JCLiteral var3 = (JCTree.JCLiteral)var1;
      return this.M.at(var3.pos).Literal(var3.typetag, var3.value);
   }

   public JCTree visitMethod(MethodTree var1, Object var2) {
      JCTree.JCMethodDecl var3 = (JCTree.JCMethodDecl)var1;
      JCTree.JCModifiers var4 = (JCTree.JCModifiers)this.copy((JCTree)var3.mods, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.restype, var2);
      List var6 = this.copy(var3.typarams, var2);
      List var7 = this.copy(var3.params, var2);
      JCTree.JCVariableDecl var8 = (JCTree.JCVariableDecl)this.copy((JCTree)var3.recvparam, var2);
      List var9 = this.copy(var3.thrown, var2);
      JCTree.JCBlock var10 = (JCTree.JCBlock)this.copy((JCTree)var3.body, var2);
      JCTree.JCExpression var11 = (JCTree.JCExpression)this.copy((JCTree)var3.defaultValue, var2);
      return this.M.at(var3.pos).MethodDef(var4, var3.name, var5, var6, var8, var7, var9, var10, var11);
   }

   public JCTree visitMethodInvocation(MethodInvocationTree var1, Object var2) {
      JCTree.JCMethodInvocation var3 = (JCTree.JCMethodInvocation)var1;
      List var4 = this.copy(var3.typeargs, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.meth, var2);
      List var6 = this.copy(var3.args, var2);
      return this.M.at(var3.pos).Apply(var4, var5, var6);
   }

   public JCTree visitModifiers(ModifiersTree var1, Object var2) {
      JCTree.JCModifiers var3 = (JCTree.JCModifiers)var1;
      List var4 = this.copy(var3.annotations, var2);
      return this.M.at(var3.pos).Modifiers(var3.flags, var4);
   }

   public JCTree visitNewArray(NewArrayTree var1, Object var2) {
      JCTree.JCNewArray var3 = (JCTree.JCNewArray)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.elemtype, var2);
      List var5 = this.copy(var3.dims, var2);
      List var6 = this.copy(var3.elems, var2);
      return this.M.at(var3.pos).NewArray(var4, var5, var6);
   }

   public JCTree visitNewClass(NewClassTree var1, Object var2) {
      JCTree.JCNewClass var3 = (JCTree.JCNewClass)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.encl, var2);
      List var5 = this.copy(var3.typeargs, var2);
      JCTree.JCExpression var6 = (JCTree.JCExpression)this.copy((JCTree)var3.clazz, var2);
      List var7 = this.copy(var3.args, var2);
      JCTree.JCClassDecl var8 = (JCTree.JCClassDecl)this.copy((JCTree)var3.def, var2);
      return this.M.at(var3.pos).NewClass(var4, var5, var6, var7, var8);
   }

   public JCTree visitLambdaExpression(LambdaExpressionTree var1, Object var2) {
      JCTree.JCLambda var3 = (JCTree.JCLambda)var1;
      List var4 = this.copy(var3.params, var2);
      JCTree var5 = this.copy(var3.body, var2);
      return this.M.at(var3.pos).Lambda(var4, var5);
   }

   public JCTree visitParenthesized(ParenthesizedTree var1, Object var2) {
      JCTree.JCParens var3 = (JCTree.JCParens)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      return this.M.at(var3.pos).Parens(var4);
   }

   public JCTree visitReturn(ReturnTree var1, Object var2) {
      JCTree.JCReturn var3 = (JCTree.JCReturn)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      return this.M.at(var3.pos).Return(var4);
   }

   public JCTree visitMemberSelect(MemberSelectTree var1, Object var2) {
      JCTree.JCFieldAccess var3 = (JCTree.JCFieldAccess)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.selected, var2);
      return this.M.at(var3.pos).Select(var4, var3.name);
   }

   public JCTree visitMemberReference(MemberReferenceTree var1, Object var2) {
      JCTree.JCMemberReference var3 = (JCTree.JCMemberReference)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      List var5 = this.copy(var3.typeargs, var2);
      return this.M.at(var3.pos).Reference(var3.mode, var3.name, var4, var5);
   }

   public JCTree visitEmptyStatement(EmptyStatementTree var1, Object var2) {
      JCTree.JCSkip var3 = (JCTree.JCSkip)var1;
      return this.M.at(var3.pos).Skip();
   }

   public JCTree visitSwitch(SwitchTree var1, Object var2) {
      JCTree.JCSwitch var3 = (JCTree.JCSwitch)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.selector, var2);
      List var5 = this.copy(var3.cases, var2);
      return this.M.at(var3.pos).Switch(var4, var5);
   }

   public JCTree visitSynchronized(SynchronizedTree var1, Object var2) {
      JCTree.JCSynchronized var3 = (JCTree.JCSynchronized)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.lock, var2);
      JCTree.JCBlock var5 = (JCTree.JCBlock)this.copy((JCTree)var3.body, var2);
      return this.M.at(var3.pos).Synchronized(var4, var5);
   }

   public JCTree visitThrow(ThrowTree var1, Object var2) {
      JCTree.JCThrow var3 = (JCTree.JCThrow)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      return this.M.at(var3.pos).Throw(var4);
   }

   public JCTree visitCompilationUnit(CompilationUnitTree var1, Object var2) {
      JCTree.JCCompilationUnit var3 = (JCTree.JCCompilationUnit)var1;
      List var4 = this.copy(var3.packageAnnotations, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.pid, var2);
      List var6 = this.copy(var3.defs, var2);
      return this.M.at(var3.pos).TopLevel(var4, var5, var6);
   }

   public JCTree visitTry(TryTree var1, Object var2) {
      JCTree.JCTry var3 = (JCTree.JCTry)var1;
      List var4 = this.copy(var3.resources, var2);
      JCTree.JCBlock var5 = (JCTree.JCBlock)this.copy((JCTree)var3.body, var2);
      List var6 = this.copy(var3.catchers, var2);
      JCTree.JCBlock var7 = (JCTree.JCBlock)this.copy((JCTree)var3.finalizer, var2);
      return this.M.at(var3.pos).Try(var4, var5, var6, var7);
   }

   public JCTree visitParameterizedType(ParameterizedTypeTree var1, Object var2) {
      JCTree.JCTypeApply var3 = (JCTree.JCTypeApply)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.clazz, var2);
      List var5 = this.copy(var3.arguments, var2);
      return this.M.at(var3.pos).TypeApply(var4, var5);
   }

   public JCTree visitUnionType(UnionTypeTree var1, Object var2) {
      JCTree.JCTypeUnion var3 = (JCTree.JCTypeUnion)var1;
      List var4 = this.copy(var3.alternatives, var2);
      return this.M.at(var3.pos).TypeUnion(var4);
   }

   public JCTree visitIntersectionType(IntersectionTypeTree var1, Object var2) {
      JCTree.JCTypeIntersection var3 = (JCTree.JCTypeIntersection)var1;
      List var4 = this.copy(var3.bounds, var2);
      return this.M.at(var3.pos).TypeIntersection(var4);
   }

   public JCTree visitArrayType(ArrayTypeTree var1, Object var2) {
      JCTree.JCArrayTypeTree var3 = (JCTree.JCArrayTypeTree)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.elemtype, var2);
      return this.M.at(var3.pos).TypeArray(var4);
   }

   public JCTree visitTypeCast(TypeCastTree var1, Object var2) {
      JCTree.JCTypeCast var3 = (JCTree.JCTypeCast)var1;
      JCTree var4 = this.copy(var3.clazz, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      return this.M.at(var3.pos).TypeCast(var4, var5);
   }

   public JCTree visitPrimitiveType(PrimitiveTypeTree var1, Object var2) {
      JCTree.JCPrimitiveTypeTree var3 = (JCTree.JCPrimitiveTypeTree)var1;
      return this.M.at(var3.pos).TypeIdent(var3.typetag);
   }

   public JCTree visitTypeParameter(TypeParameterTree var1, Object var2) {
      JCTree.JCTypeParameter var3 = (JCTree.JCTypeParameter)var1;
      List var4 = this.copy(var3.annotations, var2);
      List var5 = this.copy(var3.bounds, var2);
      return this.M.at(var3.pos).TypeParameter(var3.name, var5, var4);
   }

   public JCTree visitInstanceOf(InstanceOfTree var1, Object var2) {
      JCTree.JCInstanceOf var3 = (JCTree.JCInstanceOf)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.expr, var2);
      JCTree var5 = this.copy(var3.clazz, var2);
      return this.M.at(var3.pos).TypeTest(var4, var5);
   }

   public JCTree visitUnary(UnaryTree var1, Object var2) {
      JCTree.JCUnary var3 = (JCTree.JCUnary)var1;
      JCTree.JCExpression var4 = (JCTree.JCExpression)this.copy((JCTree)var3.arg, var2);
      return this.M.at(var3.pos).Unary(var3.getTag(), var4);
   }

   public JCTree visitVariable(VariableTree var1, Object var2) {
      JCTree.JCVariableDecl var3 = (JCTree.JCVariableDecl)var1;
      JCTree.JCModifiers var4 = (JCTree.JCModifiers)this.copy((JCTree)var3.mods, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.vartype, var2);
      JCTree.JCExpression var6;
      if (var3.nameexpr == null) {
         var6 = (JCTree.JCExpression)this.copy((JCTree)var3.init, var2);
         return this.M.at(var3.pos).VarDef(var4, var3.name, var5, var6);
      } else {
         var6 = (JCTree.JCExpression)this.copy((JCTree)var3.nameexpr, var2);
         return this.M.at(var3.pos).ReceiverVarDef(var4, var6, var5);
      }
   }

   public JCTree visitWhileLoop(WhileLoopTree var1, Object var2) {
      JCTree.JCWhileLoop var3 = (JCTree.JCWhileLoop)var1;
      JCTree.JCStatement var4 = (JCTree.JCStatement)this.copy((JCTree)var3.body, var2);
      JCTree.JCExpression var5 = (JCTree.JCExpression)this.copy((JCTree)var3.cond, var2);
      return this.M.at(var3.pos).WhileLoop(var5, var4);
   }

   public JCTree visitWildcard(WildcardTree var1, Object var2) {
      JCTree.JCWildcard var3 = (JCTree.JCWildcard)var1;
      JCTree.TypeBoundKind var4 = this.M.at(var3.kind.pos).TypeBoundKind(var3.kind.kind);
      JCTree var5 = this.copy(var3.inner, var2);
      return this.M.at(var3.pos).Wildcard(var4, var5);
   }

   public JCTree visitOther(Tree var1, Object var2) {
      JCTree var3 = (JCTree)var1;
      switch (var3.getTag()) {
         case LETEXPR:
            JCTree.LetExpr var4 = (JCTree.LetExpr)var1;
            List var5 = this.copy(var4.defs, var2);
            JCTree var6 = this.copy(var4.expr, var2);
            return this.M.at(var4.pos).LetExpr(var5, var6);
         default:
            throw new AssertionError("unknown tree tag: " + var3.getTag());
      }
   }
}
