package com.sun.codemodel.internal;

public class JFieldRef extends JExpressionImpl implements JAssignmentTarget {
   private JGenerable object;
   private String name;
   private JVar var;
   private boolean explicitThis;

   JFieldRef(JExpression object, String name) {
      this(object, (String)name, false);
   }

   JFieldRef(JExpression object, JVar v) {
      this(object, (JVar)v, false);
   }

   JFieldRef(JType type, String name) {
      this(type, (String)name, false);
   }

   JFieldRef(JType type, JVar v) {
      this(type, (JVar)v, false);
   }

   JFieldRef(JGenerable object, String name, boolean explicitThis) {
      this.explicitThis = explicitThis;
      this.object = object;
      if (name.indexOf(46) >= 0) {
         throw new IllegalArgumentException("Field name contains '.': " + name);
      } else {
         this.name = name;
      }
   }

   JFieldRef(JGenerable object, JVar var, boolean explicitThis) {
      this.explicitThis = explicitThis;
      this.object = object;
      this.var = var;
   }

   public void generate(JFormatter f) {
      String name = this.name;
      if (name == null) {
         name = this.var.name();
      }

      if (this.object != null) {
         f.g(this.object).p('.').p(name);
      } else if (this.explicitThis) {
         f.p("this.").p(name);
      } else {
         f.id(name);
      }

   }

   public JExpression assign(JExpression rhs) {
      return JExpr.assign(this, rhs);
   }

   public JExpression assignPlus(JExpression rhs) {
      return JExpr.assignPlus(this, rhs);
   }
}
