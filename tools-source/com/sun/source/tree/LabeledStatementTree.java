package com.sun.source.tree;

import javax.lang.model.element.Name;
import jdk.Exported;

@Exported
public interface LabeledStatementTree extends StatementTree {
   Name getLabel();

   StatementTree getStatement();
}
