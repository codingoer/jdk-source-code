package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface SynchronizedTree extends StatementTree {
   ExpressionTree getExpression();

   BlockTree getBlock();
}
