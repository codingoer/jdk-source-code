package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface AssignmentTree extends ExpressionTree {
   ExpressionTree getVariable();

   ExpressionTree getExpression();
}
