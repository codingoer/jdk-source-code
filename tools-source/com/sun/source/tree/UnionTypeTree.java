package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface UnionTypeTree extends Tree {
   List getTypeAlternatives();
}
