package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface AnnotatedTypeTree extends ExpressionTree {
   List getAnnotations();

   ExpressionTree getUnderlyingType();
}
