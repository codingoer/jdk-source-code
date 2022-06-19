package com.sun.tools.javac.tree;

public interface EndPosTable {
   int getEndPos(JCTree var1);

   void storeEnd(JCTree var1, int var2);

   int replaceTree(JCTree var1, JCTree var2);
}
