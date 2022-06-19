package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.util.List;

public class AnnotationTypeDocImpl extends ClassDocImpl implements AnnotationTypeDoc {
   public AnnotationTypeDocImpl(DocEnv var1, Symbol.ClassSymbol var2) {
      this(var1, var2, (TreePath)null);
   }

   public AnnotationTypeDocImpl(DocEnv var1, Symbol.ClassSymbol var2, TreePath var3) {
      super(var1, var2, var3);
   }

   public boolean isAnnotationType() {
      return !this.isInterface();
   }

   public boolean isInterface() {
      return this.env.legacyDoclet;
   }

   public MethodDoc[] methods(boolean var1) {
      return this.env.legacyDoclet ? (MethodDoc[])this.elements() : new MethodDoc[0];
   }

   public AnnotationTypeElementDoc[] elements() {
      List var1 = List.nil();

      for(Scope.Entry var2 = this.tsym.members().elems; var2 != null; var2 = var2.sibling) {
         if (var2.sym != null && var2.sym.kind == 16) {
            Symbol.MethodSymbol var3 = (Symbol.MethodSymbol)var2.sym;
            var1 = var1.prepend(this.env.getAnnotationTypeElementDoc(var3));
         }
      }

      return (AnnotationTypeElementDoc[])var1.toArray(new AnnotationTypeElementDoc[var1.length()]);
   }
}
