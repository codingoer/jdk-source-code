package com.sun.codemodel.internal;

public class JDoLoop implements JStatement {
   private JExpression test;
   private JBlock body = null;

   JDoLoop(JExpression test) {
      this.test = test;
   }

   public JBlock body() {
      if (this.body == null) {
         this.body = new JBlock();
      }

      return this.body;
   }

   public void state(JFormatter f) {
      f.p("do");
      if (this.body != null) {
         f.g((JGenerable)this.body);
      } else {
         f.p("{ }");
      }

      if (JOp.hasTopOp(this.test)) {
         f.p("while ").g((JGenerable)this.test);
      } else {
         f.p("while (").g((JGenerable)this.test).p(')');
      }

      f.p(';').nl();
   }
}
