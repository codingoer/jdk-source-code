package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface ErroneousTree extends ExpressionTree {
   List getErrorTrees();
}
