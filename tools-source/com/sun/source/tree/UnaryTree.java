package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface UnaryTree extends ExpressionTree {
   ExpressionTree getExpression();
}
