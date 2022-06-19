package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ArrayAccessTree extends ExpressionTree {
   ExpressionTree getExpression();

   ExpressionTree getIndex();
}
