package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class LessEqual extends BinaryExpr {
   protected LessEqual(Expression var1, Expression var2) {
      super("<=", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Object var1 = this.left().evaluate();
         Object var6 = this.right().evaluate();
         if (var1 instanceof Boolean) {
            String[] var7 = new String[]{Util.getMessage("EvaluationException.lessEqual"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
            throw new EvaluationException(Util.getMessage("EvaluationException.1", var7));
         }

         Number var3 = (Number)var1;
         Number var4 = (Number)this.right().evaluate();
         if (!(var3 instanceof Float) && !(var3 instanceof Double) && !(var4 instanceof Float) && !(var4 instanceof Double)) {
            this.value(new Boolean(((BigInteger)var3).compareTo((BigInteger)var4) <= 0));
         } else {
            this.value(new Boolean(var3.doubleValue() <= var4.doubleValue()));
         }
      } catch (ClassCastException var5) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.lessEqual"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
