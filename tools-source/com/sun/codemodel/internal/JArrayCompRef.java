package com.sun.codemodel.internal;

final class JArrayCompRef extends JExpressionImpl implements JAssignmentTarget {
   private final JExpression array;
   private final JExpression index;

   JArrayCompRef(JExpression array, JExpression index) {
      if (array != null && index != null) {
         this.array = array;
         this.index = index;
      } else {
         throw new NullPointerException();
      }
   }

   public void generate(JFormatter f) {
      f.g((JGenerable)this.array).p('[').g((JGenerable)this.index).p(']');
   }

   public JExpression assign(JExpression rhs) {
      return JExpr.assign(this, rhs);
   }

   public JExpression assignPlus(JExpression rhs) {
      return JExpr.assignPlus(this, rhs);
   }
}
