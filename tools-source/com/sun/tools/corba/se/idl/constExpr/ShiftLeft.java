package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class ShiftLeft extends BinaryExpr {
   protected ShiftLeft(Expression var1, Expression var2) {
      super("<<", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Number var1 = (Number)this.left().evaluate();
         Number var7 = (Number)this.right().evaluate();
         if (var1 instanceof Float || var1 instanceof Double || var7 instanceof Float || var7 instanceof Double) {
            String[] var8 = new String[]{Util.getMessage("EvaluationException.left"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
            throw new EvaluationException(Util.getMessage("EvaluationException.1", var8));
         }

         BigInteger var3 = (BigInteger)this.coerceToTarget(var1);
         BigInteger var4 = (BigInteger)var7;
         BigInteger var5 = var3.shiftLeft(var4.intValue());
         if (this.type().indexOf("short") >= 0) {
            var5 = var5.mod(twoPow16);
         } else if (this.type().indexOf("long") >= 0) {
            var5 = var5.mod(twoPow32);
         } else if (this.type().indexOf("long long") >= 0) {
            var5 = var5.mod(twoPow64);
         }

         this.value(this.coerceToTarget(var5));
      } catch (ClassCastException var6) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.left"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
