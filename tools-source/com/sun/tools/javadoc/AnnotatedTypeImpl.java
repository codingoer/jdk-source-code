package com.sun.tools.javadoc;

import com.sun.javadoc.AnnotatedType;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import java.util.Iterator;

public class AnnotatedTypeImpl extends AbstractTypeImpl implements AnnotatedType {
   AnnotatedTypeImpl(DocEnv var1, Type var2) {
      super(var1, var2);
   }

   public AnnotationDesc[] annotations() {
      List var1 = this.type.getAnnotationMirrors();
      if (var1 != null && !var1.isEmpty()) {
         AnnotationDesc[] var2 = new AnnotationDesc[var1.length()];
         int var3 = 0;

         Attribute.Compound var5;
         for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = new AnnotationDescImpl(this.env, var5)) {
            var5 = (Attribute.Compound)var4.next();
         }

         return var2;
      } else {
         return new AnnotationDesc[0];
      }
   }

   public com.sun.javadoc.Type underlyingType() {
      return TypeMaker.getType(this.env, this.type.unannotatedType(), true, false);
   }

   public AnnotatedType asAnnotatedType() {
      return this;
   }

   public String toString() {
      return this.typeName();
   }

   public String typeName() {
      return this.underlyingType().typeName();
   }

   public String qualifiedTypeName() {
      return this.underlyingType().qualifiedTypeName();
   }

   public String simpleTypeName() {
      return this.underlyingType().simpleTypeName();
   }

   public String dimension() {
      return this.underlyingType().dimension();
   }

   public boolean isPrimitive() {
      return this.underlyingType().isPrimitive();
   }

   public ClassDoc asClassDoc() {
      return this.underlyingType().asClassDoc();
   }

   public TypeVariable asTypeVariable() {
      return this.underlyingType().asTypeVariable();
   }

   public WildcardType asWildcardType() {
      return this.underlyingType().asWildcardType();
   }

   public ParameterizedType asParameterizedType() {
      return this.underlyingType().asParameterizedType();
   }
}
