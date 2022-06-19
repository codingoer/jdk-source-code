package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface InstanceOfTree extends ExpressionTree {
   ExpressionTree getExpression();

   Tree getType();
}
