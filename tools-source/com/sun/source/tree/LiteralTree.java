package com.sun.source.tree;

import jdk.Exported;

@Exported
public interface LiteralTree extends ExpressionTree {
   Object getValue();
}
