package com.sun.source.util;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import jdk.Exported;

@Exported
public interface SourcePositions {
   long getStartPosition(CompilationUnitTree var1, Tree var2);

   long getEndPosition(CompilationUnitTree var1, Tree var2);
}
