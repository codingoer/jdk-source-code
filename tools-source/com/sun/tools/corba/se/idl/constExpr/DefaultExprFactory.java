package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.ConstEntry;
import java.math.BigInteger;

public class DefaultExprFactory implements ExprFactory {
   public And and(Expression var1, Expression var2) {
      return new And(var1, var2);
   }

   public BooleanAnd booleanAnd(Expression var1, Expression var2) {
      return new BooleanAnd(var1, var2);
   }

   public BooleanNot booleanNot(Expression var1) {
      return new BooleanNot(var1);
   }

   public BooleanOr booleanOr(Expression var1, Expression var2) {
      return new BooleanOr(var1, var2);
   }

   public Divide divide(Expression var1, Expression var2) {
      return new Divide(var1, var2);
   }

   public Equal equal(Expression var1, Expression var2) {
      return new Equal(var1, var2);
   }

   public GreaterEqual greaterEqual(Expression var1, Expression var2) {
      return new GreaterEqual(var1, var2);
   }

   public GreaterThan greaterThan(Expression var1, Expression var2) {
      return new GreaterThan(var1, var2);
   }

   public LessEqual lessEqual(Expression var1, Expression var2) {
      return new LessEqual(var1, var2);
   }

   public LessThan lessThan(Expression var1, Expression var2) {
      return new LessThan(var1, var2);
   }

   public Minus minus(Expression var1, Expression var2) {
      return new Minus(var1, var2);
   }

   public Modulo modulo(Expression var1, Expression var2) {
      return new Modulo(var1, var2);
   }

   public Negative negative(Expression var1) {
      return new Negative(var1);
   }

   public Not not(Expression var1) {
      return new Not(var1);
   }

   public NotEqual notEqual(Expression var1, Expression var2) {
      return new NotEqual(var1, var2);
   }

   public Or or(Expression var1, Expression var2) {
      return new Or(var1, var2);
   }

   public Plus plus(Expression var1, Expression var2) {
      return new Plus(var1, var2);
   }

   public Positive positive(Expression var1) {
      return new Positive(var1);
   }

   public ShiftLeft shiftLeft(Expression var1, Expression var2) {
      return new ShiftLeft(var1, var2);
   }

   public ShiftRight shiftRight(Expression var1, Expression var2) {
      return new ShiftRight(var1, var2);
   }

   public Terminal terminal(String var1, Character var2, boolean var3) {
      return new Terminal(var1, var2, var3);
   }

   public Terminal terminal(String var1, Boolean var2) {
      return new Terminal(var1, var2);
   }

   public Terminal terminal(String var1, BigInteger var2) {
      return new Terminal(var1, var2);
   }

   public Terminal terminal(String var1, Double var2) {
      return new Terminal(var1, var2);
   }

   public Terminal terminal(String var1, boolean var2) {
      return new Terminal(var1, var2);
   }

   public Terminal terminal(ConstEntry var1) {
      return new Terminal(var1);
   }

   public Times times(Expression var1, Expression var2) {
      return new Times(var1, var2);
   }

   public Xor xor(Expression var1, Expression var2) {
      return new Xor(var1, var2);
   }
}
