package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.ConstEntry;
import java.math.BigInteger;

public interface ExprFactory {
   And and(Expression var1, Expression var2);

   BooleanAnd booleanAnd(Expression var1, Expression var2);

   BooleanNot booleanNot(Expression var1);

   BooleanOr booleanOr(Expression var1, Expression var2);

   Divide divide(Expression var1, Expression var2);

   Equal equal(Expression var1, Expression var2);

   GreaterEqual greaterEqual(Expression var1, Expression var2);

   GreaterThan greaterThan(Expression var1, Expression var2);

   LessEqual lessEqual(Expression var1, Expression var2);

   LessThan lessThan(Expression var1, Expression var2);

   Minus minus(Expression var1, Expression var2);

   Modulo modulo(Expression var1, Expression var2);

   Negative negative(Expression var1);

   Not not(Expression var1);

   NotEqual notEqual(Expression var1, Expression var2);

   Or or(Expression var1, Expression var2);

   Plus plus(Expression var1, Expression var2);

   Positive positive(Expression var1);

   ShiftLeft shiftLeft(Expression var1, Expression var2);

   ShiftRight shiftRight(Expression var1, Expression var2);

   Terminal terminal(String var1, Character var2, boolean var3);

   Terminal terminal(String var1, Boolean var2);

   Terminal terminal(String var1, Double var2);

   Terminal terminal(String var1, BigInteger var2);

   Terminal terminal(String var1, boolean var2);

   Terminal terminal(ConstEntry var1);

   Times times(Expression var1, Expression var2);

   Xor xor(Expression var1, Expression var2);
}
