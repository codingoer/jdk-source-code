package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface ParameterizedTypeTree extends Tree {
   Tree getType();

   List getTypeArguments();
}
