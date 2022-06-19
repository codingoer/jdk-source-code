package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class Positive extends UnaryExpr {
   protected Positive(Expression var1) {
      super("+", var1);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Number var1 = (Number)this.operand().evaluate();
         if (!(var1 instanceof Float) && !(var1 instanceof Double)) {
            this.value(((BigInteger)var1).multiply(BigInteger.valueOf((long)((BigInteger)var1).signum())));
         } else {
            this.value(new Double(var1.doubleValue()));
         }
      } catch (ClassCastException var3) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.pos"), this.operand().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.2", var2));
      }

      return this.value();
   }
}
