package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;

public class AnnotationTypeElementDocImpl extends MethodDocImpl implements AnnotationTypeElementDoc {
   public AnnotationTypeElementDocImpl(DocEnv var1, Symbol.MethodSymbol var2) {
      super(var1, var2);
   }

   public AnnotationTypeElementDocImpl(DocEnv var1, Symbol.MethodSymbol var2, TreePath var3) {
      super(var1, var2, var3);
   }

   public boolean isAnnotationTypeElement() {
      return !this.isMethod();
   }

   public boolean isMethod() {
      return this.env.legacyDoclet;
   }

   public boolean isAbstract() {
      return false;
   }

   public AnnotationValue defaultValue() {
      return this.sym.defaultValue == null ? null : new AnnotationValueImpl(this.env, this.sym.defaultValue);
   }
}
