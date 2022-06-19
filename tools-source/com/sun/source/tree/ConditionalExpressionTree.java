package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ConditionalExpressionTree extends ExpressionTree {
   ExpressionTree getCondition();

   ExpressionTree getTrueExpression();

   ExpressionTree getFalseExpression();
}
