package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface SwitchTree extends StatementTree {
   ExpressionTree getExpression();

   List getCases();
}
