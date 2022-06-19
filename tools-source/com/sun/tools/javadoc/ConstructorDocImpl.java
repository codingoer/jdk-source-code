package com.sun.tools.javadoc;

import com.sun.javadoc.ConstructorDoc;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.code.Symbol;

public class ConstructorDocImpl extends ExecutableMemberDocImpl implements ConstructorDoc {
   public ConstructorDocImpl(DocEnv var1, Symbol.MethodSymbol var2) {
      super(var1, var2);
   }

   public ConstructorDocImpl(DocEnv var1, Symbol.MethodSymbol var2, TreePath var3) {
      super(var1, var2, var3);
   }

   public boolean isConstructor() {
      return true;
   }

   public String name() {
      Symbol.ClassSymbol var1 = this.sym.enclClass();
      return var1.name.toString();
   }

   public String qualifiedName() {
      return this.sym.enclClass().getQualifiedName().toString();
   }

   public String toString() {
      return this.typeParametersString() + this.qualifiedName() + this.signature();
   }
}
