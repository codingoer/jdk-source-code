package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.TypeVariable;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import java.util.Iterator;

public class TypeVariableImpl extends AbstractTypeImpl implements TypeVariable {
   TypeVariableImpl(DocEnv var1, Type.TypeVar var2) {
      super(var1, var2);
   }

   public com.sun.javadoc.Type[] bounds() {
      return TypeMaker.getTypes(this.env, getBounds((Type.TypeVar)this.type, this.env));
   }

   public ProgramElementDoc owner() {
      Symbol var1 = this.type.tsym.owner;
      if ((var1.kind & 2) != 0) {
         return this.env.getClassDoc((Symbol.ClassSymbol)var1);
      } else {
         Names var2 = var1.name.table.names;
         return (ProgramElementDoc)(var1.name == var2.init ? this.env.getConstructorDoc((Symbol.MethodSymbol)var1) : this.env.getMethodDoc((Symbol.MethodSymbol)var1));
      }
   }

   public ClassDoc asClassDoc() {
      return this.env.getClassDoc((Symbol.ClassSymbol)this.env.types.erasure(this.type).tsym);
   }

   public TypeVariable asTypeVariable() {
      return this;
   }

   public String toString() {
      return typeVarToString(this.env, (Type.TypeVar)this.type, true);
   }

   static String typeVarToString(DocEnv var0, Type.TypeVar var1, boolean var2) {
      StringBuilder var3 = new StringBuilder(var1.toString());
      List var4 = getBounds(var1, var0);
      if (var4.nonEmpty()) {
         boolean var5 = true;

         for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 = false) {
            Type var7 = (Type)var6.next();
            var3.append(var5 ? " extends " : " & ");
            var3.append(TypeMaker.getTypeString(var0, var7, var2));
         }
      }

      return var3.toString();
   }

   private static List getBounds(Type.TypeVar var0, DocEnv var1) {
      Type var2 = var0.getUpperBound();
      Name var3 = var2.tsym.getQualifiedName();
      return var3 == var3.table.names.java_lang_Object && !var2.isAnnotated() ? List.nil() : var1.types.getBounds(var0);
   }

   public AnnotationDesc[] annotations() {
      if (!this.type.isAnnotated()) {
         return new AnnotationDesc[0];
      } else {
         List var1 = this.type.getAnnotationMirrors();
         AnnotationDesc[] var2 = new AnnotationDesc[var1.length()];
         int var3 = 0;

         Attribute.Compound var5;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = new AnnotationDescImpl(this.env, var5)) {
            var5 = (Attribute.Compound)var4.next();
         }

         return var2;
      }
   }
}
