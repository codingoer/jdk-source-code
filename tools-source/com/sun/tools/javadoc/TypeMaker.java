package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.util.List;
import java.util.Iterator;

public class TypeMaker {
   public static Type getType(DocEnv var0, com.sun.tools.javac.code.Type var1) {
      return getType(var0, var1, true);
   }

   public static Type getType(DocEnv var0, com.sun.tools.javac.code.Type var1, boolean var2) {
      return getType(var0, var1, var2, true);
   }

   public static Type getType(DocEnv var0, com.sun.tools.javac.code.Type var1, boolean var2, boolean var3) {
      try {
         return getTypeImpl(var0, var1, var2, var3);
      } catch (Symbol.CompletionFailure var5) {
         return getType(var0, var1, var2, var3);
      }
   }

   private static Type getTypeImpl(DocEnv var0, com.sun.tools.javac.code.Type var1, boolean var2, boolean var3) {
      if (var0.legacyDoclet) {
         var1 = var0.types.erasure(var1);
      }

      if (var3 && var1.isAnnotated()) {
         return new AnnotatedTypeImpl(var0, var1);
      } else {
         switch (var1.getTag()) {
            case CLASS:
               if (ClassDocImpl.isGeneric((Symbol.ClassSymbol)var1.tsym)) {
                  return var0.getParameterizedType((com.sun.tools.javac.code.Type.ClassType)var1);
               }

               return var0.getClassDoc((Symbol.ClassSymbol)var1.tsym);
            case WILDCARD:
               com.sun.tools.javac.code.Type.WildcardType var4 = (com.sun.tools.javac.code.Type.WildcardType)var1;
               return new WildcardTypeImpl(var0, var4);
            case TYPEVAR:
               return new TypeVariableImpl(var0, (com.sun.tools.javac.code.Type.TypeVar)var1);
            case ARRAY:
               return new ArrayTypeImpl(var0, var1);
            case BYTE:
               return PrimitiveType.byteType;
            case CHAR:
               return PrimitiveType.charType;
            case SHORT:
               return PrimitiveType.shortType;
            case INT:
               return PrimitiveType.intType;
            case LONG:
               return PrimitiveType.longType;
            case FLOAT:
               return PrimitiveType.floatType;
            case DOUBLE:
               return PrimitiveType.doubleType;
            case BOOLEAN:
               return PrimitiveType.booleanType;
            case VOID:
               return PrimitiveType.voidType;
            case ERROR:
               if (var2) {
                  return var0.getClassDoc((Symbol.ClassSymbol)var1.tsym);
               }
            default:
               return new PrimitiveType(var1.tsym.getQualifiedName().toString());
         }
      }
   }

   public static Type[] getTypes(DocEnv var0, List var1) {
      return getTypes(var0, var1, new Type[var1.length()]);
   }

   public static Type[] getTypes(DocEnv var0, List var1, Type[] var2) {
      int var3 = 0;

      com.sun.tools.javac.code.Type var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = getType(var0, var5)) {
         var5 = (com.sun.tools.javac.code.Type)var4.next();
      }

      return var2;
   }

   public static String getTypeName(com.sun.tools.javac.code.Type var0, boolean var1) {
      switch (var0.getTag()) {
         case CLASS:
            return ClassDocImpl.getClassName((Symbol.ClassSymbol)var0.tsym, var1);
         case ARRAY:
            StringBuilder var2;
            for(var2 = new StringBuilder(); var0.hasTag(TypeTag.ARRAY); var0 = ((com.sun.tools.javac.code.Type.ArrayType)var0).elemtype) {
               var2.append("[]");
            }

            var2.insert(0, getTypeName(var0, var1));
            return var2.toString();
         default:
            return var0.tsym.getQualifiedName().toString();
      }
   }

   static String getTypeString(DocEnv var0, com.sun.tools.javac.code.Type var1, boolean var2) {
      if (var1.isAnnotated()) {
         var1 = var1.unannotatedType();
      }

      switch (var1.getTag()) {
         case CLASS:
            return ParameterizedTypeImpl.parameterizedTypeToString(var0, (com.sun.tools.javac.code.Type.ClassType)var1, var2);
         case WILDCARD:
            com.sun.tools.javac.code.Type.WildcardType var4 = (com.sun.tools.javac.code.Type.WildcardType)var1;
            return WildcardTypeImpl.wildcardTypeToString(var0, var4, var2);
         case TYPEVAR:
         default:
            return var1.tsym.getQualifiedName().toString();
         case ARRAY:
            StringBuilder var3;
            for(var3 = new StringBuilder(); var1.hasTag(TypeTag.ARRAY); var1 = var0.types.elemtype(var1)) {
               var3.append("[]");
            }

            var3.insert(0, getTypeString(var0, var1, var2));
            return var3.toString();
      }
   }

   static String typeParametersString(DocEnv var0, Symbol var1, boolean var2) {
      if (!var0.legacyDoclet && !var1.type.getTypeArguments().isEmpty()) {
         StringBuilder var3 = new StringBuilder();
         Iterator var4 = var1.type.getTypeArguments().iterator();

         while(var4.hasNext()) {
            com.sun.tools.javac.code.Type var5 = (com.sun.tools.javac.code.Type)var4.next();
            var3.append(var3.length() == 0 ? "<" : ", ");
            var3.append(TypeVariableImpl.typeVarToString(var0, (com.sun.tools.javac.code.Type.TypeVar)var5, var2));
         }

         var3.append(">");
         return var3.toString();
      } else {
         return "";
      }
   }

   static String typeArgumentsString(DocEnv var0, com.sun.tools.javac.code.Type.ClassType var1, boolean var2) {
      if (!var0.legacyDoclet && !var1.getTypeArguments().isEmpty()) {
         StringBuilder var3 = new StringBuilder();
         Iterator var4 = var1.getTypeArguments().iterator();

         while(var4.hasNext()) {
            com.sun.tools.javac.code.Type var5 = (com.sun.tools.javac.code.Type)var4.next();
            var3.append(var3.length() == 0 ? "<" : ", ");
            var3.append(getTypeString(var0, var5, var2));
         }

         var3.append(">");
         return var3.toString();
      } else {
         return "";
      }
   }

   private static class ArrayTypeImpl implements Type {
      com.sun.tools.javac.code.Type arrayType;
      DocEnv env;
      private Type skipArraysCache = null;

      ArrayTypeImpl(DocEnv var1, com.sun.tools.javac.code.Type var2) {
         this.env = var1;
         this.arrayType = var2;
      }

      public Type getElementType() {
         return TypeMaker.getType(this.env, this.env.types.elemtype(this.arrayType));
      }

      private Type skipArrays() {
         if (this.skipArraysCache == null) {
            com.sun.tools.javac.code.Type var1;
            for(var1 = this.arrayType; var1.hasTag(TypeTag.ARRAY); var1 = this.env.types.elemtype(var1)) {
            }

            this.skipArraysCache = TypeMaker.getType(this.env, var1);
         }

         return this.skipArraysCache;
      }

      public String dimension() {
         StringBuilder var1 = new StringBuilder();

         for(com.sun.tools.javac.code.Type var2 = this.arrayType; var2.hasTag(TypeTag.ARRAY); var2 = this.env.types.elemtype(var2)) {
            var1.append("[]");
         }

         return var1.toString();
      }

      public String typeName() {
         return this.skipArrays().typeName();
      }

      public String qualifiedTypeName() {
         return this.skipArrays().qualifiedTypeName();
      }

      public String simpleTypeName() {
         return this.skipArrays().simpleTypeName();
      }

      public ClassDoc asClassDoc() {
         return this.skipArrays().asClassDoc();
      }

      public ParameterizedType asParameterizedType() {
         return this.skipArrays().asParameterizedType();
      }

      public TypeVariable asTypeVariable() {
         return this.skipArrays().asTypeVariable();
      }

      public WildcardType asWildcardType() {
         return null;
      }

      public AnnotatedType asAnnotatedType() {
         return null;
      }

      public AnnotationTypeDoc asAnnotationTypeDoc() {
         return this.skipArrays().asAnnotationTypeDoc();
      }

      public boolean isPrimitive() {
         return this.skipArrays().isPrimitive();
      }

      public String toString() {
         return this.qualifiedTypeName() + this.dimension();
      }
   }
}
