package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class Minus extends BinaryExpr {
   protected Minus(Expression var1, Expression var2) {
      super("-", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Number var1 = (Number)this.left().evaluate();
         Number var8 = (Number)this.right().evaluate();
         boolean var3 = var1 instanceof Float || var1 instanceof Double;
         boolean var4 = var8 instanceof Float || var8 instanceof Double;
         if (var3 && var4) {
            this.value(new Double(var1.doubleValue() - var8.doubleValue()));
         } else {
            if (var3 || var4) {
               String[] var9 = new String[]{Util.getMessage("EvaluationException.minus"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
               throw new EvaluationException(Util.getMessage("EvaluationException.1", var9));
            }

            BigInteger var5 = (BigInteger)var1;
            BigInteger var6 = (BigInteger)var8;
            this.value(var5.subtract(var6));
         }
      } catch (ClassCastException var7) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.minus"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
