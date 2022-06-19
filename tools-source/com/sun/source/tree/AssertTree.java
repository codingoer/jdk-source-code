package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface AssertTree extends StatementTree {
   ExpressionTree getCondition();

   ExpressionTree getDetail();
}
