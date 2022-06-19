package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface BlockTree extends StatementTree {
   boolean isStatic();

   List getStatements();
}
