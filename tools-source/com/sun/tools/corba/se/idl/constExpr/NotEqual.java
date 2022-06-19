package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.Util;
import java.math.BigInteger;

public class NotEqual extends BinaryExpr {
   protected NotEqual(Expression var1, Expression var2) {
      super("!=", var1, var2);
   }

   public Object evaluate() throws EvaluationException {
      try {
         Object var1 = this.left().evaluate();
         if (var1 instanceof Boolean) {
            Boolean var5 = (Boolean)var1;
            Boolean var3 = (Boolean)this.right().evaluate();
            this.value(new Boolean(var5 != var3));
         } else {
            Number var6 = (Number)var1;
            Number var7 = (Number)this.right().evaluate();
            if (!(var6 instanceof Float) && !(var6 instanceof Double) && !(var7 instanceof Float) && !(var7 instanceof Double)) {
               this.value(new Boolean(!((BigInteger)var6).equals((BigInteger)var7)));
            } else {
               this.value(new Boolean(var6.doubleValue() != var7.doubleValue()));
            }
         }
      } catch (ClassCastException var4) {
         String[] var2 = new String[]{Util.getMessage("EvaluationException.notEqual"), this.left().value().getClass().getName(), this.right().value().getClass().getName()};
         throw new EvaluationException(Util.getMessage("EvaluationException.1", var2));
      }

      return this.value();
   }
}
