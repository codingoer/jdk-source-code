package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ParenthesizedTree extends ExpressionTree {
   ExpressionTree getExpression();
}
