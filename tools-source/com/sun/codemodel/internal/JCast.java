package com.sun.codemodel.internal;

final class JCast extends JExpressionImpl {
   private final JType type;
   private final JExpression object;

   JCast(JType type, JExpression object) {
      this.type = type;
      this.object = object;
   }

   public void generate(JFormatter f) {
      f.p("((").g((JGenerable)this.type).p(')').g((JGenerable)this.object).p(')');
   }
}
