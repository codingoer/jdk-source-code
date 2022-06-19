package com.sun.codemodel.internal;

public class JWhileLoop implements JStatement {
   private JExpression test;
   private JBlock body = null;

   JWhileLoop(JExpression test) {
      this.test = test;
   }

   public JExpression test() {
      return this.test;
   }

   public JBlock body() {
      if (this.body == null) {
         this.body = new JBlock();
      }

      return this.body;
   }

   public void state(JFormatter f) {
      if (JOp.hasTopOp(this.test)) {
         f.p("while ").g((JGenerable)this.test);
      } else {
         f.p("while (").g((JGenerable)this.test).p(')');
      }

      if (this.body != null) {
         f.s(this.body);
      } else {
         f.p(';').nl();
      }

   }
}
