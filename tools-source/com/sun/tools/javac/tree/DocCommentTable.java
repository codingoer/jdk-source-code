package com.sun.tools.javac.tree;

import com.sun.tools.javac.parser.Tokens;

public interface DocCommentTable {
   boolean hasComment(JCTree var1);

   Tokens.Comment getComment(JCTree var1);

   String getCommentText(JCTree var1);

   DCTree.DCDocComment getCommentTree(JCTree var1);

   void putComment(JCTree var1, Tokens.Comment var2);
}
