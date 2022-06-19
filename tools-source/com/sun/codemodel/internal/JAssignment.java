package com.sun.codemodel.internal;

public class JAssignment extends JExpressionImpl implements JStatement {
   JAssignmentTarget lhs;
   JExpression rhs;
   String op = "";

   JAssignment(JAssignmentTarget lhs, JExpression rhs) {
      this.lhs = lhs;
      this.rhs = rhs;
   }

   JAssignment(JAssignmentTarget lhs, JExpression rhs, String op) {
      this.lhs = lhs;
      this.rhs = rhs;
      this.op = op;
   }

   public void generate(JFormatter f) {
      f.g((JGenerable)this.lhs).p(this.op + '=').g((JGenerable)this.rhs);
   }

   public void state(JFormatter f) {
      f.g((JGenerable)this).p(';').nl();
   }
}
