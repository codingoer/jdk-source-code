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
public class TreeScanner implements TreeVisitor {
   public Object scan(Tree var1, Object var2) {
      return var1 == null ? null : var1.accept(this, var2);
   }

   private Object scanAndReduce(Tree var1, Object var2, Object var3) {
      return this.reduce(this.scan(var1, var2), var3);
   }

   public Object scan(Iterable var1, Object var2) {
      Object var3 = null;
      if (var1 != null) {
         boolean var4 = true;

         for(Iterator var5 = var1.iterator(); var5.hasNext(); var4 = false) {
            Tree var6 = (Tree)var5.next();
            var3 = var4 ? this.scan(var6, var2) : this.scanAndReduce(var6, var2, var3);
         }
      }

      return var3;
   }

   private Object scanAndReduce(Iterable var1, Object var2, Object var3) {
      return this.reduce(this.scan(var1, var2), var3);
   }

   public Object reduce(Object var1, Object var2) {
      return var1;
   }

   public Object visitCompilationUnit(CompilationUnitTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getPackageAnnotations(), var2);
      var3 = this.scanAndReduce((Tree)var1.getPackageName(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getImports(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getTypeDecls(), var2, var3);
      return var3;
   }

   public Object visitImport(ImportTree var1, Object var2) {
      return this.scan(var1.getQualifiedIdentifier(), var2);
   }

   public Object visitClass(ClassTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getModifiers(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getTypeParameters(), var2, var3);
      var3 = this.scanAndReduce(var1.getExtendsClause(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getImplementsClause(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getMembers(), var2, var3);
      return var3;
   }

   public Object visitMethod(MethodTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getModifiers(), var2);
      var3 = this.scanAndReduce(var1.getReturnType(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getTypeParameters(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getParameters(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getReceiverParameter(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getThrows(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getBody(), var2, var3);
      var3 = this.scanAndReduce(var1.getDefaultValue(), var2, var3);
      return var3;
   }

   public Object visitVariable(VariableTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getModifiers(), var2);
      var3 = this.scanAndReduce(var1.getType(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getNameExpression(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getInitializer(), var2, var3);
      return var3;
   }

   public Object visitEmptyStatement(EmptyStatementTree var1, Object var2) {
      return null;
   }

   public Object visitBlock(BlockTree var1, Object var2) {
      return this.scan((Iterable)var1.getStatements(), var2);
   }

   public Object visitDoWhileLoop(DoWhileLoopTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getStatement(), var2);
      var3 = this.scanAndReduce((Tree)var1.getCondition(), var2, var3);
      return var3;
   }

   public Object visitWhileLoop(WhileLoopTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getCondition(), var2);
      var3 = this.scanAndReduce((Tree)var1.getStatement(), var2, var3);
      return var3;
   }

   public Object visitForLoop(ForLoopTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getInitializer(), var2);
      var3 = this.scanAndReduce((Tree)var1.getCondition(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getUpdate(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getStatement(), var2, var3);
      return var3;
   }

   public Object visitEnhancedForLoop(EnhancedForLoopTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getVariable(), var2);
      var3 = this.scanAndReduce((Tree)var1.getExpression(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getStatement(), var2, var3);
      return var3;
   }

   public Object visitLabeledStatement(LabeledStatementTree var1, Object var2) {
      return this.scan((Tree)var1.getStatement(), var2);
   }

   public Object visitSwitch(SwitchTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getExpression(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getCases(), var2, var3);
      return var3;
   }

   public Object visitCase(CaseTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getExpression(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getStatements(), var2, var3);
      return var3;
   }

   public Object visitSynchronized(SynchronizedTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getExpression(), var2);
      var3 = this.scanAndReduce((Tree)var1.getBlock(), var2, var3);
      return var3;
   }

   public Object visitTry(TryTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getResources(), var2);
      var3 = this.scanAndReduce((Tree)var1.getBlock(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getCatches(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getFinallyBlock(), var2, var3);
      return var3;
   }

   public Object visitCatch(CatchTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getParameter(), var2);
      var3 = this.scanAndReduce((Tree)var1.getBlock(), var2, var3);
      return var3;
   }

   public Object visitConditionalExpression(ConditionalExpressionTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getCondition(), var2);
      var3 = this.scanAndReduce((Tree)var1.getTrueExpression(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getFalseExpression(), var2, var3);
      return var3;
   }

   public Object visitIf(IfTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getCondition(), var2);
      var3 = this.scanAndReduce((Tree)var1.getThenStatement(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getElseStatement(), var2, var3);
      return var3;
   }

   public Object visitExpressionStatement(ExpressionStatementTree var1, Object var2) {
      return this.scan((Tree)var1.getExpression(), var2);
   }

   public Object visitBreak(BreakTree var1, Object var2) {
      return null;
   }

   public Object visitContinue(ContinueTree var1, Object var2) {
      return null;
   }

   public Object visitReturn(ReturnTree var1, Object var2) {
      return this.scan((Tree)var1.getExpression(), var2);
   }

   public Object visitThrow(ThrowTree var1, Object var2) {
      return this.scan((Tree)var1.getExpression(), var2);
   }

   public Object visitAssert(AssertTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getCondition(), var2);
      var3 = this.scanAndReduce((Tree)var1.getDetail(), var2, var3);
      return var3;
   }

   public Object visitMethodInvocation(MethodInvocationTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getTypeArguments(), var2);
      var3 = this.scanAndReduce((Tree)var1.getMethodSelect(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getArguments(), var2, var3);
      return var3;
   }

   public Object visitNewClass(NewClassTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getEnclosingExpression(), var2);
      var3 = this.scanAndReduce((Tree)var1.getIdentifier(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getTypeArguments(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getArguments(), var2, var3);
      var3 = this.scanAndReduce((Tree)var1.getClassBody(), var2, var3);
      return var3;
   }

   public Object visitNewArray(NewArrayTree var1, Object var2) {
      Object var3 = this.scan(var1.getType(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getDimensions(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getInitializers(), var2, var3);
      var3 = this.scanAndReduce((Iterable)var1.getAnnotations(), var2, var3);

      Iterable var5;
      for(Iterator var4 = var1.getDimAnnotations().iterator(); var4.hasNext(); var3 = this.scanAndReduce(var5, var2, var3)) {
         var5 = (Iterable)var4.next();
      }

      return var3;
   }

   public Object visitLambdaExpression(LambdaExpressionTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getParameters(), var2);
      var3 = this.scanAndReduce(var1.getBody(), var2, var3);
      return var3;
   }

   public Object visitParenthesized(ParenthesizedTree var1, Object var2) {
      return this.scan((Tree)var1.getExpression(), var2);
   }

   public Object visitAssignment(AssignmentTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getVariable(), var2);
      var3 = this.scanAndReduce((Tree)var1.getExpression(), var2, var3);
      return var3;
   }

   public Object visitCompoundAssignment(CompoundAssignmentTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getVariable(), var2);
      var3 = this.scanAndReduce((Tree)var1.getExpression(), var2, var3);
      return var3;
   }

   public Object visitUnary(UnaryTree var1, Object var2) {
      return this.scan((Tree)var1.getExpression(), var2);
   }

   public Object visitBinary(BinaryTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getLeftOperand(), var2);
      var3 = this.scanAndReduce((Tree)var1.getRightOperand(), var2, var3);
      return var3;
   }

   public Object visitTypeCast(TypeCastTree var1, Object var2) {
      Object var3 = this.scan(var1.getType(), var2);
      var3 = this.scanAndReduce((Tree)var1.getExpression(), var2, var3);
      return var3;
   }

   public Object visitInstanceOf(InstanceOfTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getExpression(), var2);
      var3 = this.scanAndReduce(var1.getType(), var2, var3);
      return var3;
   }

   public Object visitArrayAccess(ArrayAccessTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getExpression(), var2);
      var3 = this.scanAndReduce((Tree)var1.getIndex(), var2, var3);
      return var3;
   }

   public Object visitMemberSelect(MemberSelectTree var1, Object var2) {
      return this.scan((Tree)var1.getExpression(), var2);
   }

   public Object visitMemberReference(MemberReferenceTree var1, Object var2) {
      Object var3 = this.scan((Tree)var1.getQualifierExpression(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getTypeArguments(), var2, var3);
      return var3;
   }

   public Object visitIdentifier(IdentifierTree var1, Object var2) {
      return null;
   }

   public Object visitLiteral(LiteralTree var1, Object var2) {
      return null;
   }

   public Object visitPrimitiveType(PrimitiveTypeTree var1, Object var2) {
      return null;
   }

   public Object visitArrayType(ArrayTypeTree var1, Object var2) {
      return this.scan(var1.getType(), var2);
   }

   public Object visitParameterizedType(ParameterizedTypeTree var1, Object var2) {
      Object var3 = this.scan(var1.getType(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getTypeArguments(), var2, var3);
      return var3;
   }

   public Object visitUnionType(UnionTypeTree var1, Object var2) {
      return this.scan((Iterable)var1.getTypeAlternatives(), var2);
   }

   public Object visitIntersectionType(IntersectionTypeTree var1, Object var2) {
      return this.scan((Iterable)var1.getBounds(), var2);
   }

   public Object visitTypeParameter(TypeParameterTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getAnnotations(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getBounds(), var2, var3);
      return var3;
   }

   public Object visitWildcard(WildcardTree var1, Object var2) {
      return this.scan(var1.getBound(), var2);
   }

   public Object visitModifiers(ModifiersTree var1, Object var2) {
      return this.scan((Iterable)var1.getAnnotations(), var2);
   }

   public Object visitAnnotation(AnnotationTree var1, Object var2) {
      Object var3 = this.scan(var1.getAnnotationType(), var2);
      var3 = this.scanAndReduce((Iterable)var1.getArguments(), var2, var3);
      return var3;
   }

   public Object visitAnnotatedType(AnnotatedTypeTree var1, Object var2) {
      Object var3 = this.scan((Iterable)var1.getAnnotations(), var2);
      var3 = this.scanAndReduce((Tree)var1.getUnderlyingType(), var2, var3);
      return var3;
   }

   public Object visitOther(Tree var1, Object var2) {
      return null;
   }

   public Object visitErroneous(ErroneousTree var1, Object var2) {
      return null;
   }
}
