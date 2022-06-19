package com.sun.source.tree;

import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface VariableTree extends StatementTree {
   ModifiersTree getModifiers();

   Name getName();

   ExpressionTree getNameExpression();

   Tree getType();

   ExpressionTree getInitializer();
}
