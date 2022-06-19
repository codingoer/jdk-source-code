package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ArrayTypeTree extends Tree {
   Tree getType();
}
