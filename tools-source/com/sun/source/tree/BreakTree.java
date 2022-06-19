package com.sun.source.tree;

import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface BreakTree extends StatementTree {
   Name getLabel();
}
