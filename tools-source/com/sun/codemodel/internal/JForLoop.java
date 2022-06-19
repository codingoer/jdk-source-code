package com.sun.codemodel.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JForLoop implements JStatement {
   private List inits = new ArrayList();
   private JExpression test = null;
   private List updates = new ArrayList();
   private JBlock body = null;

   public JVar init(int mods, JType type, String var, JExpression e) {
      JVar v = new JVar(JMods.forVar(mods), type, var, e);
      this.inits.add(v);
      return v;
   }

   public JVar init(JType type, String var, JExpression e) {
      return this.init(0, type, var, e);
   }

   public void init(JVar v, JExpression e) {
      this.inits.add(JExpr.assign(v, e));
   }

   public void test(JExpression e) {
      this.test = e;
   }

   public void update(JExpression e) {
      this.updates.add(e);
   }

   public JBlock body() {
      if (this.body == null) {
         this.body = new JBlock();
      }

      return this.body;
   }

   public void state(JFormatter f) {
      f.p("for (");
      boolean first = true;

      for(Iterator var3 = this.inits.iterator(); var3.hasNext(); first = false) {
         Object o = var3.next();
         if (!first) {
            f.p(',');
         }

         if (o instanceof JVar) {
            f.b((JVar)o);
         } else {
            f.g((JGenerable)((JExpression)o));
         }
      }

      f.p(';').g((JGenerable)this.test).p(';').g((Collection)this.updates).p(')');
      if (this.body != null) {
         f.g((JGenerable)this.body).nl();
      } else {
         f.p(';').nl();
      }

   }
}
