package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface IfTree extends StatementTree {
   ExpressionTree getCondition();

   StatementTree getThenStatement();

   StatementTree getElseStatement();
}
