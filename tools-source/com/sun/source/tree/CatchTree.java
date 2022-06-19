package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface CatchTree extends Tree {
   VariableTree getParameter();

   BlockTree getBlock();
}
