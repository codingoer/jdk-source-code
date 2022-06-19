package com.sun.codemodel.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JAnnotationUse extends JAnnotationValue {
   private final JClass clazz;
   private Map memberValues;

   JAnnotationUse(JClass clazz) {
      this.clazz = clazz;
   }

   public JClass getAnnotationClass() {
      return this.clazz;
   }

   public Map getAnnotationMembers() {
      return Collections.unmodifiableMap(this.memberValues);
   }

   private JCodeModel owner() {
      return this.clazz.owner();
   }

   private void addValue(String name, JAnnotationValue annotationValue) {
      if (this.memberValues == null) {
         this.memberValues = new LinkedHashMap();
      }

      this.memberValues.put(name, annotationValue);
   }

   public JAnnotationUse param(String name, boolean value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse param(String name, byte value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit((int)value)));
      return this;
   }

   public JAnnotationUse param(String name, char value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse param(String name, double value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse param(String name, float value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse param(String name, long value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse param(String name, short value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit((int)value)));
      return this;
   }

   public JAnnotationUse param(String name, int value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse param(String name, String value) {
      this.addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
      return this;
   }

   public JAnnotationUse annotationParam(String name, Class value) {
      JAnnotationUse annotationUse = new JAnnotationUse(this.owner().ref(value));
      this.addValue(name, annotationUse);
      return annotationUse;
   }

   public JAnnotationUse param(String name, final Enum value) {
      this.addValue(name, new JAnnotationValue() {
         public void generate(JFormatter f) {
            f.t(JAnnotationUse.this.owner().ref(value.getDeclaringClass())).p('.').p(value.name());
         }
      });
      return this;
   }

   public JAnnotationUse param(String name, JEnumConstant value) {
      this.addValue(name, new JAnnotationStringValue(value));
      return this;
   }

   public JAnnotationUse param(String name, final Class value) {
      this.addValue(name, new JAnnotationStringValue(new JExpressionImpl() {
         public void generate(JFormatter f) {
            f.p(value.getName().replace('$', '.'));
            f.p(".class");
         }
      }));
      return this;
   }

   public JAnnotationUse param(String name, JType type) {
      JClass c = type.boxify();
      this.addValue(name, new JAnnotationStringValue(c.dotclass()));
      return this;
   }

   public JAnnotationUse param(String name, JExpression value) {
      this.addValue(name, new JAnnotationStringValue(value));
      return this;
   }

   public JAnnotationArrayMember paramArray(String name) {
      JAnnotationArrayMember arrayMember = new JAnnotationArrayMember(this.owner());
      this.addValue(name, arrayMember);
      return arrayMember;
   }

   /** @deprecated */
   public JAnnotationUse annotate(Class clazz) {
      JAnnotationUse annotationUse = new JAnnotationUse(this.owner().ref(clazz));
      return annotationUse;
   }

   public void generate(JFormatter f) {
      f.p('@').g((JGenerable)this.clazz);
      if (this.memberValues != null) {
         f.p('(');
         boolean first = true;
         if (this.isOptimizable()) {
            f.g((JGenerable)this.memberValues.get("value"));
         } else {
            for(Iterator var3 = this.memberValues.entrySet().iterator(); var3.hasNext(); first = false) {
               Map.Entry mapEntry = (Map.Entry)var3.next();
               if (!first) {
                  f.p(',');
               }

               f.p((String)mapEntry.getKey()).p('=').g((JGenerable)mapEntry.getValue());
            }
         }

         f.p(')');
      }

   }

   private boolean isOptimizable() {
      return this.memberValues.size() == 1 && this.memberValues.containsKey("value");
   }
}
