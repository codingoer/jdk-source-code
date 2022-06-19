package com.sun.source.util;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.tree.CompilationUnitTree;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;
import jdk.Exported;

@Exported
public abstract class DocTrees extends Trees {
   public static DocTrees instance(JavaCompiler.CompilationTask var0) {
      return (DocTrees)Trees.instance(var0);
   }

   public static DocTrees instance(ProcessingEnvironment var0) {
      if (!var0.getClass().getName().equals("com.sun.tools.javac.processing.JavacProcessingEnvironment")) {
         throw new IllegalArgumentException();
      } else {
         return (DocTrees)getJavacTrees(ProcessingEnvironment.class, var0);
      }
   }

   public abstract DocCommentTree getDocCommentTree(TreePath var1);

   public abstract Element getElement(DocTreePath var1);

   public abstract DocSourcePositions getSourcePositions();

   public abstract void printMessage(Diagnostic.Kind var1, CharSequence var2, DocTree var3, DocCommentTree var4, CompilationUnitTree var5);
}
