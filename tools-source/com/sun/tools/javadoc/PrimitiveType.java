package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

class PrimitiveType implements Type {
   private final String name;
   static final PrimitiveType voidType = new PrimitiveType("void");
   static final PrimitiveType booleanType = new PrimitiveType("boolean");
   static final PrimitiveType byteType = new PrimitiveType("byte");
   static final PrimitiveType charType = new PrimitiveType("char");
   static final PrimitiveType shortType = new PrimitiveType("short");
   static final PrimitiveType intType = new PrimitiveType("int");
   static final PrimitiveType longType = new PrimitiveType("long");
   static final PrimitiveType floatType = new PrimitiveType("float");
   static final PrimitiveType doubleType = new PrimitiveType("double");
   static final PrimitiveType errorType = new PrimitiveType("");

   PrimitiveType(String var1) {
      this.name = var1;
   }

   public String typeName() {
      return this.name;
   }

   public Type getElementType() {
      return null;
   }

   public String qualifiedTypeName() {
      return this.name;
   }

   public String simpleTypeName() {
      return this.name;
   }

   public String dimension() {
      return "";
   }

   public ClassDoc asClassDoc() {
      return null;
   }

   public AnnotationTypeDoc asAnnotationTypeDoc() {
      return null;
   }

   public ParameterizedType asParameterizedType() {
      return null;
   }

   public TypeVariable asTypeVariable() {
      return null;
   }

   public WildcardType asWildcardType() {
      return null;
   }

   public AnnotatedType asAnnotatedType() {
      return null;
   }

   public String toString() {
      return this.qualifiedTypeName();
   }

   public boolean isPrimitive() {
      return true;
   }
}
