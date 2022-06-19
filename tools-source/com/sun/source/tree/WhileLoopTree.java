package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface WhileLoopTree extends StatementTree {
   ExpressionTree getCondition();

   StatementTree getStatement();
}
