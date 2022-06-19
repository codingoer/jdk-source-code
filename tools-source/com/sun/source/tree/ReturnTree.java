package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ReturnTree extends StatementTree {
   ExpressionTree getExpression();
}
