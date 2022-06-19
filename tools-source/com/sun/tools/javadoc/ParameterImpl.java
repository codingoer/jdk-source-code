package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import java.util.Iterator;

class ParameterImpl implements Parameter {
   private final DocEnv env;
   private final Symbol.VarSymbol sym;
   private final Type type;

   ParameterImpl(DocEnv var1, Symbol.VarSymbol var2) {
      this.env = var1;
      this.sym = var2;
      this.type = TypeMaker.getType(var1, var2.type, false);
   }

   public Type type() {
      return this.type;
   }

   public String name() {
      return this.sym.toString();
   }

   public String typeName() {
      return !(this.type instanceof ClassDoc) && !(this.type instanceof TypeVariable) ? this.type.toString() : this.type.typeName();
   }

   public String toString() {
      return this.typeName() + " " + this.sym;
   }

   public AnnotationDesc[] annotations() {
      AnnotationDesc[] var1 = new AnnotationDesc[this.sym.getRawAttributes().length()];
      int var2 = 0;

      Attribute.Compound var4;
      for(Iterator var3 = this.sym.getRawAttributes().iterator(); var3.hasNext(); var1[var2++] = new AnnotationDescImpl(this.env, var4)) {
         var4 = (Attribute.Compound)var3.next();
      }

      return var1;
   }
}
