package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface DoWhileLoopTree extends StatementTree {
   ExpressionTree getCondition();

   StatementTree getStatement();
}
