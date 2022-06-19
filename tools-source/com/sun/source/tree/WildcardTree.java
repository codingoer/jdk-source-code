package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface WildcardTree extends Tree {
   Tree getBound();
}
