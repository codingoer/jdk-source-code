package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface CaseTree extends Tree {
   ExpressionTree getExpression();

   List getStatements();
}
