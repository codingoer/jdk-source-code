package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ThrowTree extends StatementTree {
   ExpressionTree getExpression();
}
