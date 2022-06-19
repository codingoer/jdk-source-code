package com.sun.codemodel.internal;

class JThrow implements JStatement {
   private JExpression expr;

   JThrow(JExpression expr) {
      this.expr = expr;
   }

   public void state(JFormatter f) {
      f.p("throw");
      f.g((JGenerable)this.expr);
      f.p(';').nl();
   }
}
