package com.sun.source.util;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CompilationUnitTree;
import jdk.Exported;

@Exported
public interface DocSourcePositions extends SourcePositions {
   long getStartPosition(CompilationUnitTree var1, DocCommentTree var2, DocTree var3);

   long getEndPosition(CompilationUnitTree var1, DocCommentTree var2, DocTree var3);
}
