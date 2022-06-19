package com.sun.tools.corba.se.idl.constExpr;

public abstract class BinaryExpr extends Expression {
   private String _op = "";
   private Expression _left = null;
   private Expression _right = null;

   public BinaryExpr(String var1, Expression var2, Expression var3) {
      this._op = var1;
      this._left = var2;
      this._right = var3;
   }

   public void op(String var1) {
      this._op = var1 == null ? "" : var1;
   }

   public String op() {
      return this._op;
   }

   public void left(Expression var1) {
      this._left = var1;
   }

   public Expression left() {
      return this._left;
   }

   public void right(Expression var1) {
      this._right = var1;
   }

   public Expression right() {
      return this._right;
   }
}
