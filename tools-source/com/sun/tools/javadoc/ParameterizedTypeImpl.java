package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;

public class ParameterizedTypeImpl extends AbstractTypeImpl implements ParameterizedType {
   ParameterizedTypeImpl(DocEnv var1, Type var2) {
      super(var1, var2);
   }

   public ClassDoc asClassDoc() {
      return this.env.getClassDoc((Symbol.ClassSymbol)this.type.tsym);
   }

   public com.sun.javadoc.Type[] typeArguments() {
      return TypeMaker.getTypes(this.env, this.type.getTypeArguments());
   }

   public com.sun.javadoc.Type superclassType() {
      if (this.asClassDoc().isInterface()) {
         return null;
      } else {
         Type var1 = this.env.types.supertype(this.type);
         return TypeMaker.getType(this.env, var1 != this.type ? var1 : this.env.syms.objectType);
      }
   }

   public com.sun.javadoc.Type[] interfaceTypes() {
      return TypeMaker.getTypes(this.env, this.env.types.interfaces(this.type));
   }

   public com.sun.javadoc.Type containingType() {
      if (this.type.getEnclosingType().hasTag(TypeTag.CLASS)) {
         return TypeMaker.getType(this.env, this.type.getEnclosingType());
      } else {
         Symbol.ClassSymbol var1 = this.type.tsym.owner.enclClass();
         return var1 != null ? this.env.getClassDoc(var1) : null;
      }
   }

   public String typeName() {
      return TypeMaker.getTypeName(this.type, false);
   }

   public ParameterizedType asParameterizedType() {
      return this;
   }

   public String toString() {
      return parameterizedTypeToString(this.env, (Type.ClassType)this.type, true);
   }

   static String parameterizedTypeToString(DocEnv var0, Type.ClassType var1, boolean var2) {
      if (var0.legacyDoclet) {
         return TypeMaker.getTypeName(var1, var2);
      } else {
         StringBuilder var3 = new StringBuilder();
         if (!var1.getEnclosingType().hasTag(TypeTag.CLASS)) {
            var3.append(TypeMaker.getTypeName(var1, var2));
         } else {
            Type.ClassType var4 = (Type.ClassType)var1.getEnclosingType();
            var3.append(parameterizedTypeToString(var0, var4, var2)).append('.').append(var1.tsym.name.toString());
         }

         var3.append(TypeMaker.typeArgumentsString(var0, var1, var2));
         return var3.toString();
      }
   }
}
