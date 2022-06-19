package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class BooleanNot extends UnaryExpr {
   protected BooleanNot(Expression var1) {
      super("!", var1);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Object var1 = this.operand().evaluate();
         Boolean var4;
         if (var1 instanceof Number) {
            if (var1 instanceof BigInteger) {
               var4 = new Boolean(((BigInteger)var1).compareTo(zero) != 0);
            } else {
               var4 = new Boolean(((Number)var1).longValue() != 0L);
            }
         } else {
            var4 = (Boolean)var1;
         }

         this.value(new Boolean(!var4));
      } catch (ClassCastException var3) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.booleanNot"), this.operand().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.2", var2));
      }

      return this.value();
   }
}
