package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface EnhancedForLoopTree extends StatementTree {
   VariableTree getVariable();

   ExpressionTree getExpression();

   StatementTree getStatement();
}
