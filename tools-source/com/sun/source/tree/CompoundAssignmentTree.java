package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface CompoundAssignmentTree extends ExpressionTree {
   ExpressionTree getVariable();

   ExpressionTree getExpression();
}
