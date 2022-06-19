package com.sun.tools.corba.se.idl.constExpr;

import com.sun.tools.corba.se.idl.ConstEntry;
import java.math.BigInteger;

public class Terminal extends Expression {
   protected Terminal(String var1, Character var2, boolean var3) {
      this.rep(var1);
      this.value(var2);
      if (var3) {
         this.type("wchar");
      } else {
         this.type("char");
      }

   }

   protected Terminal(String var1, Boolean var2) {
      this.rep(var1);
      this.value(var2);
   }

   protected Terminal(String var1, BigInteger var2) {
      this.rep(var1);
      this.value(var2);
   }

   protected Terminal(String var1, Long var2) {
      long var3 = var2;
      this.rep(var1);
      if (var3 <= 2147483647L && var3 >= -2147483648L) {
         this.value(new Integer(var2.intValue()));
      } else {
         this.value(var2);
      }

   }

   protected Terminal(String var1, Double var2) {
      this.rep(var1);
      this.value(var2);
   }

   protected Terminal(String var1, boolean var2) {
      this.rep(var1);
      this.value(var1);
      if (var2) {
         this.type("wstring");
      } else {
         this.type("string");
      }

   }

   protected Terminal(ConstEntry var1) {
      this.rep(var1.fullName());
      this.value(var1);
   }

   public Object evaluate() throws EvaluationException {
      return this.value() instanceof ConstEntry ? ((ConstEntry)this.value()).value().evaluate() : this.value();
   }
}
