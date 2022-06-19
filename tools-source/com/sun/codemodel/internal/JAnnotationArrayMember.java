package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class JAnnotationArrayMember extends JAnnotationValue implements JAnnotatable {
   private final List values = new ArrayList();
   private final JCodeModel owner;

   JAnnotationArrayMember(JCodeModel owner) {
      this.owner = owner;
   }

   public JAnnotationArrayMember param(String value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(boolean value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(byte value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit((int)value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(char value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(double value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(long value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(short value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit((int)value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(int value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(float value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(JExpr.lit(value));
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(final Enum value) {
      JAnnotationValue annotationValue = new JAnnotationValue() {
         public void generate(JFormatter f) {
            f.t(JAnnotationArrayMember.this.owner.ref(value.getDeclaringClass())).p('.').p(value.name());
         }
      };
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(JEnumConstant value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(value);
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(JExpression value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(value);
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(final Class value) {
      JAnnotationValue annotationValue = new JAnnotationStringValue(new JExpressionImpl() {
         public void generate(JFormatter f) {
            f.p(value.getName().replace('$', '.'));
            f.p(".class");
         }
      });
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationArrayMember param(JType type) {
      JClass clazz = type.boxify();
      JAnnotationValue annotationValue = new JAnnotationStringValue(clazz.dotclass());
      this.values.add(annotationValue);
      return this;
   }

   public JAnnotationUse annotate(Class clazz) {
      return this.annotate(this.owner.ref(clazz));
   }

   public JAnnotationUse annotate(JClass clazz) {
      JAnnotationUse a = new JAnnotationUse(clazz);
      this.values.add(a);
      return a;
   }

   public JAnnotationWriter annotate2(Class clazz) {
      return TypedAnnotationWriter.create(clazz, this);
   }

   public Collection annotations() {
      return Collections.unmodifiableList(this.values);
   }

   /** @deprecated */
   public JAnnotationArrayMember param(JAnnotationUse value) {
      this.values.add(value);
      return this;
   }

   public void generate(JFormatter f) {
      f.p('{').nl().i();
      boolean first = true;

      for(Iterator var3 = this.values.iterator(); var3.hasNext(); first = false) {
         JAnnotationValue aValue = (JAnnotationValue)var3.next();
         if (!first) {
            f.p(',').nl();
         }

         f.g((JGenerable)aValue);
      }

      f.nl().o().p('}');
   }
}
