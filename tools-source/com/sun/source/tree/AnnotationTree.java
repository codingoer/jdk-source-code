package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface AnnotationTree extends ExpressionTree {
   Tree getAnnotationType();

   List getArguments();
}
