package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ExpressionStatementTree extends StatementTree {
   ExpressionTree getExpression();
}
