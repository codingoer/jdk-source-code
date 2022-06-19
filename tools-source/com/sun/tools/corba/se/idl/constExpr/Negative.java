package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class Negative extends UnaryExpr {
   protected Negative(Expression var1) {
      super("-", var1);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Number var1 = (Number)this.operand().evaluate();
         if (!(var1 instanceof Float) && !(var1 instanceof Double)) {
            BigInteger var4 = (BigInteger)var1;
            this.value(var4.multiply(BigInteger.valueOf(-1L)));
         } else {
            this.value(new Double(-var1.doubleValue()));
         }
      } catch (ClassCastException var3) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.neg"), this.operand().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.2", var2));
      }

      return this.value();
   }
}
