package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface NewClassTree extends ExpressionTree {
   ExpressionTree getEnclosingExpression();

   List getTypeArguments();

   ExpressionTree getIdentifier();

   List getArguments();

   ClassTree getClassBody();
}
