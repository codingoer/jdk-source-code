package com.sun.codemodel.internal;

class JReturn implements JStatement {
   private JExpression expr;

   JReturn(JExpression expr) {
      this.expr = expr;
   }

   public void state(JFormatter f) {
      f.p("return ");
      if (this.expr != null) {
         f.g((JGenerable)this.expr);
      }

      f.p(';').nl();
   }
}
