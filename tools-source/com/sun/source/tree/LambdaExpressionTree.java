package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface LambdaExpressionTree extends ExpressionTree {
   List getParameters();

   Tree getBody();

   BodyKind getBodyKind();

   @Exported
   public static enum BodyKind {
      EXPRESSION,
      STATEMENT;
   }
}
