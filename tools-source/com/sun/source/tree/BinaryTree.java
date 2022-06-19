package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface BinaryTree extends ExpressionTree {
   ExpressionTree getLeftOperand();

   ExpressionTree getRightOperand();
}
