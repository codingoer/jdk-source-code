package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface TryTree extends StatementTree {
   BlockTree getBlock();

   List getCatches();

   BlockTree getFinallyBlock();

   List getResources();
}
