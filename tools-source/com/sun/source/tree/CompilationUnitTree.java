package com.sun.source.tree;

import java.util.List;
import javax.tools.JavaFileObject;
import jdk.Exported;

@Exported
public interface CompilationUnitTree extends Tree {
   List getPackageAnnotations();

   ExpressionTree getPackageName();

   List getImports();

   List getTypeDecls();

   JavaFileObject getSourceFile();

   LineMap getLineMap();
}
