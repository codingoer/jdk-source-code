package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class BooleanOr extends BinaryExpr {
   protected BooleanOr(Expression var1, Expression var2) {
      super("||", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Object var1 = this.left().evaluate();
         Object var6 = this.right().evaluate();
         Boolean var3;
         if (var1 instanceof Number) {
            if (var1 instanceof BigInteger) {
               var3 = new Boolean(((BigInteger)var1).compareTo(zero) != 0);
            } else {
               var3 = new Boolean(((Number)var1).longValue() != 0L);
            }
         } else {
            var3 = (Boolean)var1;
         }

         Boolean var4;
         if (var6 instanceof Number) {
            if (var6 instanceof BigInteger) {
               var4 = new Boolean(((BigInteger)var6).compareTo(BigInteger.valueOf(0L)) != 0);
            } else {
               var4 = new Boolean(((Number)var6).longValue() != 0L);
            }
         } else {
            var4 = (Boolean)var6;
         }

         this.value(new Boolean(var3 || var4));
      } catch (ClassCastException var5) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.booleanOr"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
