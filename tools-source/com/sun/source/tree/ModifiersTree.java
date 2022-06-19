package com.sun.source.tree;

import java.util.List;
import java.util.Set;
import jdk.Exported;

@Exported
public interface ModifiersTree extends Tree {
   Set getFlags();

   List getAnnotations();
}
