package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface TypeCastTree extends ExpressionTree {
   Tree getType();

   ExpressionTree getExpression();
}
