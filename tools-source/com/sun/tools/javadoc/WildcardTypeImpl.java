package com.sun.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.WildcardType;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import java.util.Iterator;

public class WildcardTypeImpl extends AbstractTypeImpl implements WildcardType {
   WildcardTypeImpl(DocEnv var1, Type.WildcardType var2) {
      super(var1, var2);
   }

   public com.sun.javadoc.Type[] extendsBounds() {
      return TypeMaker.getTypes(this.env, getExtendsBounds((Type.WildcardType)this.type));
   }

   public com.sun.javadoc.Type[] superBounds() {
      return TypeMaker.getTypes(this.env, getSuperBounds((Type.WildcardType)this.type));
   }

   public ClassDoc asClassDoc() {
      return this.env.getClassDoc((Symbol.ClassSymbol)this.env.types.erasure(this.type).tsym);
   }

   public WildcardType asWildcardType() {
      return this;
   }

   public String typeName() {
      return "?";
   }

   public String qualifiedTypeName() {
      return "?";
   }

   public String simpleTypeName() {
      return "?";
   }

   public String toString() {
      return wildcardTypeToString(this.env, (Type.WildcardType)this.type, true);
   }

   static String wildcardTypeToString(DocEnv var0, Type.WildcardType var1, boolean var2) {
      if (var0.legacyDoclet) {
         return TypeMaker.getTypeName(var0.types.erasure((Type)var1), var2);
      } else {
         StringBuilder var3 = new StringBuilder("?");
         List var4 = getExtendsBounds(var1);
         if (var4.nonEmpty()) {
            var3.append(" extends ");
         } else {
            var4 = getSuperBounds(var1);
            if (var4.nonEmpty()) {
               var3.append(" super ");
            }
         }

         boolean var5 = true;

         for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 = false) {
            Type var7 = (Type)var6.next();
            if (!var5) {
               var3.append(" & ");
            }

            var3.append(TypeMaker.getTypeString(var0, var7, var2));
         }

         return var3.toString();
      }
   }

   private static List getExtendsBounds(Type.WildcardType var0) {
      return var0.isSuperBound() ? List.nil() : List.of(var0.type);
   }

   private static List getSuperBounds(Type.WildcardType var0) {
      return var0.isExtendsBound() ? List.nil() : List.of(var0.type);
   }
}
