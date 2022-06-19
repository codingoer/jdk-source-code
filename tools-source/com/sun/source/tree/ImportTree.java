package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface ImportTree extends Tree {
   boolean isStatic();

   Tree getQualifiedIdentifier();
}
