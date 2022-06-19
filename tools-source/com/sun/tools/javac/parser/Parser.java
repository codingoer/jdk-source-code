package com.sun.tools.javac.parser;

import com.sun.tools.javac.tree.JCTree;

public interface Parser {
   JCTree.JCCompilationUnit parseCompilationUnit();

   JCTree.JCExpression parseExpression();

   JCTree.JCStatement parseStatement();

   JCTree.JCExpression parseType();
}
