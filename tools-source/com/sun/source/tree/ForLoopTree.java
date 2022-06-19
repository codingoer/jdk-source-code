package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface ForLoopTree extends StatementTree {
   List getInitializer();

   ExpressionTree getCondition();

   List getUpdate();

   StatementTree getStatement();
}
