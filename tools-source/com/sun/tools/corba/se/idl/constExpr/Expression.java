package com.sun.tools.corba.se.idl.constExpr;

import java.math.BigInteger;

public abstract class Expression {
   public static final BigInteger negOne = BigInteger.valueOf(-1L);
   public static final BigInteger zero = BigInteger.valueOf(0L);
   public static final BigInteger one = BigInteger.valueOf(1L);
   public static final BigInteger two = BigInteger.valueOf(2L);
   public static final BigInteger twoPow15;
   public static final BigInteger twoPow16;
   public static final BigInteger twoPow31;
   public static final BigInteger twoPow32;
   public static final BigInteger twoPow63;
   public static final BigInteger twoPow64;
   public static final BigInteger sMax;
   public static final BigInteger sMin;
   public static final BigInteger usMax;
   public static final BigInteger usMin;
   public static final BigInteger lMax;
   public static final BigInteger lMin;
   public static final BigInteger ulMax;
   public static final BigInteger ulMin;
   public static final BigInteger llMax;
   public static final BigInteger llMin;
   public static final BigInteger ullMax;
   public static final BigInteger ullMin;
   private Object _value = null;
   private String _rep = null;
   private String _type = null;

   public abstract Object evaluate() throws EvaluationException;

   public void value(Object var1) {
      this._value = var1;
   }

   public Object value() {
      return this._value;
   }

   public void rep(String var1) {
      this._rep = var1;
   }

   public String rep() {
      return this._rep;
   }

   public void type(String var1) {
      this._type = var1;
   }

   public String type() {
      return this._type;
   }

   protected static String defaultType(String var0) {
      return var0 == null ? new String("") : var0;
   }

   public Object coerceToTarget(Object var1) {
      if (var1 instanceof BigInteger) {
         return this.type().indexOf("unsigned") >= 0 ? this.toUnsignedTarget((BigInteger)var1) : this.toSignedTarget((BigInteger)var1);
      } else {
         return var1;
      }
   }

   protected BigInteger toUnsignedTarget(BigInteger var1) {
      if (this.type().equals("unsigned short")) {
         if (var1 != null && var1.compareTo(zero) < 0) {
            return var1.add(twoPow16);
         }
      } else if (this.type().equals("unsigned long")) {
         if (var1 != null && var1.compareTo(zero) < 0) {
            return var1.add(twoPow32);
         }
      } else if (this.type().equals("unsigned long long") && var1 != null && var1.compareTo(zero) < 0) {
         return var1.add(twoPow64);
      }

      return var1;
   }

   protected BigInteger toSignedTarget(BigInteger var1) {
      if (this.type().equals("short")) {
         if (var1 != null && var1.compareTo(sMax) > 0) {
            return var1.subtract(twoPow16);
         }
      } else if (this.type().equals("long")) {
         if (var1 != null && var1.compareTo(lMax) > 0) {
            return var1.subtract(twoPow32);
         }
      } else if (this.type().equals("long long") && var1 != null && var1.compareTo(llMax) > 0) {
         return var1.subtract(twoPow64);
      }

      return var1;
   }

   protected BigInteger toUnsigned(BigInteger var1) {
      if (var1 != null && var1.signum() == -1) {
         if (this.type().equals("short")) {
            return var1.add(twoPow16);
         }

         if (this.type().equals("long")) {
            return var1.add(twoPow32);
         }

         if (this.type().equals("long long")) {
            return var1.add(twoPow64);
         }
      }

      return var1;
   }

   static {
      twoPow15 = two.pow(15);
      twoPow16 = two.pow(16);
      twoPow31 = two.pow(31);
      twoPow32 = two.pow(32);
      twoPow63 = two.pow(63);
      twoPow64 = two.pow(64);
      sMax = BigInteger.valueOf(32767L);
      sMin = BigInteger.valueOf(32767L);
      usMax = sMax.multiply(two).add(one);
      usMin = zero;
      lMax = BigInteger.valueOf(2147483647L);
      lMin = BigInteger.valueOf(2147483647L);
      ulMax = lMax.multiply(two).add(one);
      ulMin = zero;
      llMax = BigInteger.valueOf(Long.MAX_VALUE);
      llMin = BigInteger.valueOf(Long.MIN_VALUE);
      ullMax = llMax.multiply(two).add(one);
      ullMin = zero;
   }
}
