package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class Not extends UnaryExpr {
   protected Not(Expression var1) {
      super("~", var1);
   }

   public Object evaluate() throws EvaluationException {
      String[] var2;
      try {
         Number var1 = (Number)this.operand().evaluate();
         if (var1 instanceof Float || var1 instanceof Double) {
            var2 = new String[]{Util.getMessage("EvaluationException.not"), this.operand().value().getClass().getName()};
            throw new EvaluationException(Util.getMessage("EvaluationException.2", var2));
         }

         BigInteger var4 = (BigInteger)this.coerceToTarget((BigInteger)var1);
         if (!this.type().equals("short") && !this.type().equals("long") && !this.type().equals("long long")) {
            if (this.type().equals("unsigned short")) {
               this.value(twoPow16.subtract(one).subtract(var4));
            } else if (this.type().equals("unsigned long")) {
               this.value(twoPow32.subtract(one).subtract(var4));
            } else if (this.type().equals("unsigned long long")) {
               this.value(twoPow64.subtract(one).subtract(var4));
            } else {
               this.value(var4.not());
            }
         } else {
            this.value(var4.add(one).multiply(negOne));
         }
      } catch (ClassCastException var3) {
         var2 = new String[]{Util.getMessage("EvaluationException.not"), this.operand().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.2", var2));
      }

      return this.value();
   }
}
