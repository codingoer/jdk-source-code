package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

abstract class AbstractTypeImpl implements Type {
   protected final DocEnv env;
   protected final com.sun.tools.javac.code.Type type;

   protected AbstractTypeImpl(DocEnv var1, com.sun.tools.javac.code.Type var2) {
      this.env = var1;
      this.type = var2;
   }

   public String typeName() {
      return this.type.tsym.name.toString();
   }

   public String qualifiedTypeName() {
      return this.type.tsym.getQualifiedName().toString();
   }

   public Type getElementType() {
      return null;
   }

   public String simpleTypeName() {
      return this.type.tsym.name.toString();
   }

   public String name() {
      return this.typeName();
   }

   public String qualifiedName() {
      return this.qualifiedTypeName();
   }

   public String toString() {
      return this.qualifiedTypeName();
   }

   public String dimension() {
      return "";
   }

   public boolean isPrimitive() {
      return false;
   }

   public ClassDoc asClassDoc() {
      return null;
   }

   public TypeVariable asTypeVariable() {
      return null;
   }

   public WildcardType asWildcardType() {
      return null;
   }

   public ParameterizedType asParameterizedType() {
      return null;
   }

   public AnnotationTypeDoc asAnnotationTypeDoc() {
      return null;
   }

   public AnnotatedType asAnnotatedType() {
      return null;
   }
}
