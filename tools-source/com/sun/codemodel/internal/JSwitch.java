package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JSwitch implements JStatement {
   private JExpression test;
   private List cases = new ArrayList();
   private JCase defaultCase = null;

   JSwitch(JExpression test) {
      this.test = test;
   }

   public JExpression test() {
      return this.test;
   }

   public Iterator cases() {
      return this.cases.iterator();
   }

   public JCase _case(JExpression label) {
      JCase c = new JCase(label);
      this.cases.add(c);
      return c;
   }

   public JCase _default() {
      this.defaultCase = new JCase((JExpression)null, true);
      return this.defaultCase;
   }

   public void state(JFormatter f) {
      if (JOp.hasTopOp(this.test)) {
         f.p("switch ").g((JGenerable)this.test).p(" {").nl();
      } else {
         f.p("switch (").g((JGenerable)this.test).p(')').p(" {").nl();
      }

      Iterator var2 = this.cases.iterator();

      while(var2.hasNext()) {
         JCase c = (JCase)var2.next();
         f.s(c);
      }

      if (this.defaultCase != null) {
         f.s(this.defaultCase);
      }

      f.p('}').nl();
   }
}
