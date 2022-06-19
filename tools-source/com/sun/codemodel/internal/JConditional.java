package com.sun.codemodel.internal;

public class JConditional implements JStatement {
   private JExpression test = null;
   private JBlock _then = new JBlock();
   private JBlock _else = null;

   JConditional(JExpression test) {
      this.test = test;
   }

   public JBlock _then() {
      return this._then;
   }

   public JBlock _else() {
      if (this._else == null) {
         this._else = new JBlock();
      }

      return this._else;
   }

   public JConditional _elseif(JExpression boolExp) {
      return this._else()._if(boolExp);
   }

   public void state(JFormatter f) {
      if (this.test == JExpr.TRUE) {
         this._then.generateBody(f);
      } else if (this.test == JExpr.FALSE) {
         this._else.generateBody(f);
      } else {
         if (JOp.hasTopOp(this.test)) {
            f.p("if ").g((JGenerable)this.test);
         } else {
            f.p("if (").g((JGenerable)this.test).p(')');
         }

         f.g((JGenerable)this._then);
         if (this._else != null) {
            f.p("else").g((JGenerable)this._else);
         }

         f.nl();
      }
   }
}
