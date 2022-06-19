package com.sun.source.util;

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
import java.util.Iterator;
import jdk.Exported;

@Exported
public class SimpleTreeVisitor implements TreeVisitor {
   protected final Object DEFAULT_VALUE;

   protected SimpleTreeVisitor() {
      this.DEFAULT_VALUE = null;
   }

   protected SimpleTreeVisitor(Object var1) {
      this.DEFAULT_VALUE = var1;
   }

   protected Object defaultAction(Tree var1, Object var2) {
      return this.DEFAULT_VALUE;
   }

   public final Object visit(Tree var1, Object var2) {
      return var1 == null ? null : var1.accept(this, var2);
   }

   public final Object visit(Iterable var1, Object var2) {
      Object var3 = null;
      Tree var5;
      if (var1 != null) {
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = this.visit(var5, var2)) {
            var5 = (Tree)var4.next();
         }
      }

      return var3;
   }

   public Object visitCompilationUnit(CompilationUnitTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitImport(ImportTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitClass(ClassTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitMethod(MethodTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitVariable(VariableTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitEmptyStatement(EmptyStatementTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitBlock(BlockTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitDoWhileLoop(DoWhileLoopTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitWhileLoop(WhileLoopTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitForLoop(ForLoopTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitEnhancedForLoop(EnhancedForLoopTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitLabeledStatement(LabeledStatementTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSwitch(SwitchTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitCase(CaseTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitSynchronized(SynchronizedTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitTry(TryTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitCatch(CatchTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitConditionalExpression(ConditionalExpressionTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitIf(IfTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitExpressionStatement(ExpressionStatementTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitBreak(BreakTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitContinue(ContinueTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitReturn(ReturnTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitThrow(ThrowTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitAssert(AssertTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitMethodInvocation(MethodInvocationTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitNewClass(NewClassTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitNewArray(NewArrayTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitLambdaExpression(LambdaExpressionTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitParenthesized(ParenthesizedTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitAssignment(AssignmentTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitCompoundAssignment(CompoundAssignmentTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitUnary(UnaryTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitBinary(BinaryTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitTypeCast(TypeCastTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitInstanceOf(InstanceOfTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitArrayAccess(ArrayAccessTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitMemberSelect(MemberSelectTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitMemberReference(MemberReferenceTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitIdentifier(IdentifierTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitLiteral(LiteralTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitPrimitiveType(PrimitiveTypeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitArrayType(ArrayTypeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitParameterizedType(ParameterizedTypeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitUnionType(UnionTypeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitIntersectionType(IntersectionTypeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitTypeParameter(TypeParameterTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitWildcard(WildcardTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitModifiers(ModifiersTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitAnnotation(AnnotationTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitAnnotatedType(AnnotatedTypeTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitErroneous(ErroneousTree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }

   public Object visitOther(Tree var1, Object var2) {
      return this.defaultAction(var1, var2);
   }
}
