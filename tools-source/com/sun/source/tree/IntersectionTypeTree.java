package com.sun.source.tree;

import java.util.List;
import jdk.Exported;

@Exported
public interface IntersectionTypeTree extends Tree {
   List getBounds();
}
