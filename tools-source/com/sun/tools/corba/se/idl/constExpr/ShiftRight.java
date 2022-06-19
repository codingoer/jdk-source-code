package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class ShiftRight extends BinaryExpr {
   protected ShiftRight(Expression var1, Expression var2) {
      super(">>", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Number var1 = (Number)this.left().evaluate();
         Number var6 = (Number)this.right().evaluate();
         if (var1 instanceof Float || var1 instanceof Double || var6 instanceof Float || var6 instanceof Double) {
            String[] var7 = new String[]{Util.getMessage("EvaluationException.right"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
            throw new EvaluationException(Util.getMessage("EvaluationException.1", var7));
         }

         BigInteger var3 = (BigInteger)this.coerceToTarget((BigInteger)var1);
         BigInteger var4 = (BigInteger)var6;
         if (var3.signum() == -1) {
            if (this.type().equals("short")) {
               var3 = var3.add(twoPow16);
            } else if (this.type().equals("long")) {
               var3 = var3.add(twoPow32);
            } else if (this.type().equals("long long")) {
               var3 = var3.add(twoPow64);
            }
         }

         this.value(var3.shiftRight(var4.intValue()));
      } catch (ClassCastException var5) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.right"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
