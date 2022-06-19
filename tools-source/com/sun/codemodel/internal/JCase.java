package com.sun.codemodel.internal;

public final class JCase implements JStatement {
   private JExpression label;
   private JBlock body;
   private boolean isDefaultCase;

   JCase(JExpression label) {
      this(label, false);
   }

   JCase(JExpression label, boolean isDefaultCase) {
      this.body = null;
      this.isDefaultCase = false;
      this.label = label;
      this.isDefaultCase = isDefaultCase;
   }

   public JExpression label() {
      return this.label;
   }

   public JBlock body() {
      if (this.body == null) {
         this.body = new JBlock(false, true);
      }

      return this.body;
   }

   public void state(JFormatter f) {
      f.i();
      if (!this.isDefaultCase) {
         f.p("case ").g((JGenerable)this.label).p(':').nl();
      } else {
         f.p("default:").nl();
      }

      if (this.body != null) {
         f.s(this.body);
      }

      f.o();
   }
}
