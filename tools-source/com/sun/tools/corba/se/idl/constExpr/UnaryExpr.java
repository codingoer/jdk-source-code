package com.sun.tools.corba.se.idl.constExpr;

public abstract class UnaryExpr extends Expression {
   private String _op = "";
   private Expression _operand = null;

   public UnaryExpr(String var1, Expression var2) {
      this._op = var1;
      this._operand = var2;
   }

   public void op(String var1) {
      this._op = var1 == null ? "" : var1;
   }

   public String op() {
      return this._op;
   }

   public void operand(Expression var1) {
      this._operand = var1;
   }

   public Expression operand() {
      return this._operand;
   }
}
