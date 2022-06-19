package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class Modulo extends BinaryExpr {
   protected Modulo(Expression var1, Expression var2) {
      super("%", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Number var1 = (Number)this.left().evaluate();
         Number var6 = (Number)this.right().evaluate();
         if (var1 instanceof Float || var1 instanceof Double || var6 instanceof Float || var6 instanceof Double) {
            String[] var7 = new String[]{Util.getMessage("EvaluationException.mod"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
            throw new EvaluationException(Util.getMessage("EvaluationException.1", var7));
         }

         BigInteger var3 = (BigInteger)var1;
         BigInteger var4 = (BigInteger)var6;
         this.value(var3.remainder(var4));
      } catch (ClassCastException var5) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.mod"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
