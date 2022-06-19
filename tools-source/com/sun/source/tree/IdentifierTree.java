package com.sun.source.tree;

import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface IdentifierTree extends ExpressionTree {
   Name getName();
}
