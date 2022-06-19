package com.sun.tools.internal.xjc.model;

import java.math.BigInteger;

public final class Multiplicity {
   public final BigInteger min;
   public final BigInteger max;
   public static final Multiplicity ZERO = new Multiplicity(0, 0);
   public static final Multiplicity ONE = new Multiplicity(1, 1);
   public static final Multiplicity OPTIONAL = new Multiplicity(0, 1);
   public static final Multiplicity STAR = new Multiplicity(0, (Integer)null);
   public static final Multiplicity PLUS = new Multiplicity(1, (Integer)null);

   public static Multiplicity create(BigInteger min, BigInteger max) {
      if (BigInteger.ZERO.equals(min) && max == null) {
         return STAR;
      } else if (BigInteger.ONE.equals(min) && max == null) {
         return PLUS;
      } else {
         if (max != null) {
            if (BigInteger.ZERO.equals(min) && BigInteger.ZERO.equals(max)) {
               return ZERO;
            }

            if (BigInteger.ZERO.equals(min) && BigInteger.ONE.equals(max)) {
               return OPTIONAL;
            }

            if (BigInteger.ONE.equals(min) && BigInteger.ONE.equals(max)) {
               return ONE;
            }
         }

         return new Multiplicity(min, max);
      }
   }

   public static Multiplicity create(int min, Integer max) {
      return create(BigInteger.valueOf((long)min), BigInteger.valueOf((long)max));
   }

   private Multiplicity(BigInteger min, BigInteger max) {
      this.min = min;
      this.max = max;
   }

   private Multiplicity(int min, int max) {
      this(BigInteger.valueOf((long)min), BigInteger.valueOf((long)max));
   }

   private Multiplicity(int min, Integer max) {
      this(BigInteger.valueOf((long)min), max == null ? null : BigInteger.valueOf((long)max));
   }

   public boolean equals(Object o) {
      if (!(o instanceof Multiplicity)) {
         return false;
      } else {
         Multiplicity that = (Multiplicity)o;
         if (!this.min.equals(that.min)) {
            return false;
         } else {
            if (this.max != null) {
               if (!this.max.equals(that.max)) {
                  return false;
               }
            } else if (that.max != null) {
               return false;
            }

            return true;
         }
      }
   }

   public int hashCode() {
      return this.min.add(this.max).intValue();
   }

   public boolean isUnique() {
      if (this.max == null) {
         return false;
      } else {
         return BigInteger.ONE.equals(this.min) && BigInteger.ONE.equals(this.max);
      }
   }

   public boolean isOptional() {
      if (this.max == null) {
         return false;
      } else {
         return BigInteger.ZERO.equals(this.min) && BigInteger.ONE.equals(this.max);
      }
   }

   public boolean isAtMostOnce() {
      if (this.max == null) {
         return false;
      } else {
         return this.max.compareTo(BigInteger.ONE) <= 0;
      }
   }

   public boolean isZero() {
      return this.max == null ? false : BigInteger.ZERO.equals(this.max);
   }

   public boolean includes(Multiplicity rhs) {
      if (rhs.min.compareTo(this.min) == -1) {
         return false;
      } else if (this.max == null) {
         return true;
      } else if (rhs.max == null) {
         return false;
      } else {
         return rhs.max.compareTo(this.max) <= 0;
      }
   }

   public String getMaxString() {
      return this.max == null ? "unbounded" : this.max.toString();
   }

   public String toString() {
      return "(" + this.min + ',' + this.getMaxString() + ')';
   }

   public static Multiplicity choice(Multiplicity lhs, Multiplicity rhs) {
      return create(lhs.min.min(rhs.min), lhs.max != null && rhs.max != null ? lhs.max.max(rhs.max) : null);
   }

   public static Multiplicity group(Multiplicity lhs, Multiplicity rhs) {
      return create(lhs.min.add(rhs.min), lhs.max != null && rhs.max != null ? lhs.max.add(rhs.max) : null);
   }

   public static Multiplicity multiply(Multiplicity lhs, Multiplicity rhs) {
      BigInteger min = lhs.min.multiply(rhs.min);
      BigInteger max;
      if (!isZero(lhs.max) && !isZero(rhs.max)) {
         if (lhs.max != null && rhs.max != null) {
            max = lhs.max.multiply(rhs.max);
         } else {
            max = null;
         }
      } else {
         max = BigInteger.ZERO;
      }

      return create(min, max);
   }

   private static boolean isZero(BigInteger i) {
      return i != null && BigInteger.ZERO.equals(i);
   }

   public static Multiplicity oneOrMore(Multiplicity c) {
      if (c.max == null) {
         return c;
      } else {
         return BigInteger.ZERO.equals(c.max) ? c : create(c.min, (BigInteger)null);
      }
   }

   public Multiplicity makeOptional() {
      return BigInteger.ZERO.equals(this.min) ? this : create(BigInteger.ZERO, this.max);
   }

   public Multiplicity makeRepeated() {
      return this.max != null && !BigInteger.ZERO.equals(this.max) ? create(this.min, (BigInteger)null) : this;
   }
}
