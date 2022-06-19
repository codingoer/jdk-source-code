package com.sun.codemodel.internal;

final class JAnnotationStringValue extends JAnnotationValue {
   private final JExpression value;

   JAnnotationStringValue(JExpression value) {
      this.value = value;
   }

   public void generate(JFormatter f) {
      f.g((JGenerable)this.value);
   }
}
