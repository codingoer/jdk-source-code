package com.sun.source.tree;

import java.util.List;
import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface MemberReferenceTree extends ExpressionTree {
   ReferenceMode getMode();

   ExpressionTree getQualifierExpression();

   Name getName();

   List getTypeArguments();

   @Exported
   public static enum ReferenceMode {
      INVOKE,
      NEW;
   }
}
