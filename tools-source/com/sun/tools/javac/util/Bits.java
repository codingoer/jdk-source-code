package com.sun.tools.javac.util;

import java.util.Arrays;
import java.util.Random;

public class Bits {
   private static final int wordlen = 32;
   private static final int wordshift = 5;
   private static final int wordmask = 31;
   public int[] bits;
   private static final int[] unassignedBits = new int[0];
   protected BitsState currentState;

   public Bits() {
      this(false);
   }

   public Bits(Bits var1) {
      this(var1.dup().bits, Bits.BitsState.getState(var1.bits, false));
   }

   public Bits(boolean var1) {
      this(unassignedBits, Bits.BitsState.getState(unassignedBits, var1));
   }

   protected Bits(int[] var1, BitsState var2) {
      this.bits = null;
      this.bits = var1;
      this.currentState = var2;
      switch (var2) {
         case UNKNOWN:
            this.bits = null;
            break;
         case NORMAL:
            Assert.check(var1 != unassignedBits);
      }

   }

   protected void sizeTo(int var1) {
      if (this.bits.length < var1) {
         this.bits = Arrays.copyOf(this.bits, var1);
      }

   }

   public void clear() {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);

      for(int var1 = 0; var1 < this.bits.length; ++var1) {
         this.bits[var1] = 0;
      }

      this.currentState = Bits.BitsState.NORMAL;
   }

   public void reset() {
      this.internalReset();
   }

   protected void internalReset() {
      this.bits = null;
      this.currentState = Bits.BitsState.UNKNOWN;
   }

   public boolean isReset() {
      return this.currentState == Bits.BitsState.UNKNOWN;
   }

   public Bits assign(Bits var1) {
      this.bits = var1.dup().bits;
      this.currentState = Bits.BitsState.NORMAL;
      return this;
   }

   public Bits dup() {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      Bits var1 = new Bits();
      var1.bits = this.dupBits();
      this.currentState = Bits.BitsState.NORMAL;
      return var1;
   }

   protected int[] dupBits() {
      int[] var1;
      if (this.currentState != Bits.BitsState.NORMAL) {
         var1 = this.bits;
      } else {
         var1 = new int[this.bits.length];
         System.arraycopy(this.bits, 0, var1, 0, this.bits.length);
      }

      return var1;
   }

   public void incl(int var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      Assert.check(var1 >= 0, "Value of x " + var1);
      this.sizeTo((var1 >>> 5) + 1);
      this.bits[var1 >>> 5] |= 1 << (var1 & 31);
      this.currentState = Bits.BitsState.NORMAL;
   }

   public void inclRange(int var1, int var2) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      this.sizeTo((var2 >>> 5) + 1);

      for(int var3 = var1; var3 < var2; ++var3) {
         this.bits[var3 >>> 5] |= 1 << (var3 & 31);
      }

      this.currentState = Bits.BitsState.NORMAL;
   }

   public void excludeFrom(int var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      Bits var2 = new Bits();
      var2.sizeTo(this.bits.length);
      var2.inclRange(0, var1);
      this.internalAndSet(var2);
      this.currentState = Bits.BitsState.NORMAL;
   }

   public void excl(int var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      Assert.check(var1 >= 0);
      this.sizeTo((var1 >>> 5) + 1);
      this.bits[var1 >>> 5] &= ~(1 << (var1 & 31));
      this.currentState = Bits.BitsState.NORMAL;
   }

   public boolean isMember(int var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      return 0 <= var1 && var1 < this.bits.length << 5 && (this.bits[var1 >>> 5] & 1 << (var1 & 31)) != 0;
   }

   public Bits andSet(Bits var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      this.internalAndSet(var1);
      this.currentState = Bits.BitsState.NORMAL;
      return this;
   }

   protected void internalAndSet(Bits var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      this.sizeTo(var1.bits.length);

      for(int var2 = 0; var2 < var1.bits.length; ++var2) {
         this.bits[var2] &= var1.bits[var2];
      }

   }

   public Bits orSet(Bits var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      this.sizeTo(var1.bits.length);

      for(int var2 = 0; var2 < var1.bits.length; ++var2) {
         this.bits[var2] |= var1.bits[var2];
      }

      this.currentState = Bits.BitsState.NORMAL;
      return this;
   }

   public Bits diffSet(Bits var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);

      for(int var2 = 0; var2 < this.bits.length; ++var2) {
         if (var2 < var1.bits.length) {
            this.bits[var2] &= ~var1.bits[var2];
         }
      }

      this.currentState = Bits.BitsState.NORMAL;
      return this;
   }

   public Bits xorSet(Bits var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      this.sizeTo(var1.bits.length);

      for(int var2 = 0; var2 < var1.bits.length; ++var2) {
         this.bits[var2] ^= var1.bits[var2];
      }

      this.currentState = Bits.BitsState.NORMAL;
      return this;
   }

   private static int trailingZeroBits(int var0) {
      Assert.check(true);
      if (var0 == 0) {
         return 32;
      } else {
         int var1 = 1;
         if ((var0 & '\uffff') == 0) {
            var1 += 16;
            var0 >>>= 16;
         }

         if ((var0 & 255) == 0) {
            var1 += 8;
            var0 >>>= 8;
         }

         if ((var0 & 15) == 0) {
            var1 += 4;
            var0 >>>= 4;
         }

         if ((var0 & 3) == 0) {
            var1 += 2;
            var0 >>>= 2;
         }

         return var1 - (var0 & 1);
      }
   }

   public int nextBit(int var1) {
      Assert.check(this.currentState != Bits.BitsState.UNKNOWN);
      int var2 = var1 >>> 5;
      if (var2 >= this.bits.length) {
         return -1;
      } else {
         int var3;
         for(var3 = this.bits[var2] & ~((1 << (var1 & 31)) - 1); var3 == 0; var3 = this.bits[var2]) {
            ++var2;
            if (var2 >= this.bits.length) {
               return -1;
            }
         }

         return (var2 << 5) + trailingZeroBits(var3);
      }
   }

   public String toString() {
      if (this.bits != null && this.bits.length > 0) {
         char[] var1 = new char[this.bits.length * 32];

         for(int var2 = 0; var2 < this.bits.length * 32; ++var2) {
            var1[var2] = (char)(this.isMember(var2) ? 49 : 48);
         }

         return new String(var1);
      } else {
         return "[]";
      }
   }

   public static void main(String[] var0) {
      Random var1 = new Random();
      Bits var2 = new Bits();

      int var3;
      int var4;
      for(var3 = 0; var3 < 125; ++var3) {
         do {
            var4 = var1.nextInt(250);
         } while(var2.isMember(var4));

         System.out.println("adding " + var4);
         var2.incl(var4);
      }

      var3 = 0;

      for(var4 = var2.nextBit(0); var4 >= 0; var4 = var2.nextBit(var4 + 1)) {
         System.out.println("found " + var4);
         ++var3;
      }

      if (var3 != 125) {
         throw new Error();
      }
   }

   protected static enum BitsState {
      UNKNOWN,
      UNINIT,
      NORMAL;

      static BitsState getState(int[] var0, boolean var1) {
         if (var1) {
            return UNKNOWN;
         } else {
            return var0 != Bits.unassignedBits ? NORMAL : UNINIT;
         }
      }
   }
}
