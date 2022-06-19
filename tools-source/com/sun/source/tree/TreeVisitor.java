package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface TreeVisitor {
   Object visitAnnotatedType(AnnotatedTypeTree var1, Object var2);

   Object visitAnnotation(AnnotationTree var1, Object var2);

   Object visitMethodInvocation(MethodInvocationTree var1, Object var2);

   Object visitAssert(AssertTree var1, Object var2);

   Object visitAssignment(AssignmentTree var1, Object var2);

   Object visitCompoundAssignment(CompoundAssignmentTree var1, Object var2);

   Object visitBinary(BinaryTree var1, Object var2);

   Object visitBlock(BlockTree var1, Object var2);

   Object visitBreak(BreakTree var1, Object var2);

   Object visitCase(CaseTree var1, Object var2);

   Object visitCatch(CatchTree var1, Object var2);

   Object visitClass(ClassTree var1, Object var2);

   Object visitConditionalExpression(ConditionalExpressionTree var1, Object var2);

   Object visitContinue(ContinueTree var1, Object var2);

   Object visitDoWhileLoop(DoWhileLoopTree var1, Object var2);

   Object visitErroneous(ErroneousTree var1, Object var2);

   Object visitExpressionStatement(ExpressionStatementTree var1, Object var2);

   Object visitEnhancedForLoop(EnhancedForLoopTree var1, Object var2);

   Object visitForLoop(ForLoopTree var1, Object var2);

   Object visitIdentifier(IdentifierTree var1, Object var2);

   Object visitIf(IfTree var1, Object var2);

   Object visitImport(ImportTree var1, Object var2);

   Object visitArrayAccess(ArrayAccessTree var1, Object var2);

   Object visitLabeledStatement(LabeledStatementTree var1, Object var2);

   Object visitLiteral(LiteralTree var1, Object var2);

   Object visitMethod(MethodTree var1, Object var2);

   Object visitModifiers(ModifiersTree var1, Object var2);

   Object visitNewArray(NewArrayTree var1, Object var2);

   Object visitNewClass(NewClassTree var1, Object var2);

   Object visitLambdaExpression(LambdaExpressionTree var1, Object var2);

   Object visitParenthesized(ParenthesizedTree var1, Object var2);

   Object visitReturn(ReturnTree var1, Object var2);

   Object visitMemberSelect(MemberSelectTree var1, Object var2);

   Object visitMemberReference(MemberReferenceTree var1, Object var2);

   Object visitEmptyStatement(EmptyStatementTree var1, Object var2);

   Object visitSwitch(SwitchTree var1, Object var2);

   Object visitSynchronized(SynchronizedTree var1, Object var2);

   Object visitThrow(ThrowTree var1, Object var2);

   Object visitCompilationUnit(CompilationUnitTree var1, Object var2);

   Object visitTry(TryTree var1, Object var2);

   Object visitParameterizedType(ParameterizedTypeTree var1, Object var2);

   Object visitUnionType(UnionTypeTree var1, Object var2);

   Object visitIntersectionType(IntersectionTypeTree var1, Object var2);

   Object visitArrayType(ArrayTypeTree var1, Object var2);

   Object visitTypeCast(TypeCastTree var1, Object var2);

   Object visitPrimitiveType(PrimitiveTypeTree var1, Object var2);

   Object visitTypeParameter(TypeParameterTree var1, Object var2);

   Object visitInstanceOf(InstanceOfTree var1, Object var2);

   Object visitUnary(UnaryTree var1, Object var2);

   Object visitVariable(VariableTree var1, Object var2);

   Object visitWhileLoop(WhileLoopTree var1, Object var2);

   Object visitWildcard(WildcardTree var1, Object var2);

   Object visitOther(Tree var1, Object var2);
}
