package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface MethodInvocationTree extends ExpressionTree {
   List getTypeArguments();

   ExpressionTree getMethodSelect();

   List getArguments();
}
