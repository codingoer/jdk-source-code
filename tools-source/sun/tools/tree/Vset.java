package sun.tools.tree;

import sun.tools.java.Constants;

public final class Vset implements Constants {
   long vset;
   long uset;
   long[] x;
   static final long[] emptyX = new long[0];
   static final long[] fullX = new long[0];
   static final int VBITS = 64;
   static final Vset DEAD_END;

   public Vset() {
      this.x = emptyX;
   }

   private Vset(long var1, long var3, long[] var5) {
      this.vset = var1;
      this.uset = var3;
      this.x = var5;
   }

   public Vset copy() {
      if (this == DEAD_END) {
         return this;
      } else {
         Vset var1 = new Vset(this.vset, this.uset, this.x);
         if (this.x.length > 0) {
            var1.growX(this.x.length);
         }

         return var1;
      }
   }

   private void growX(int var1) {
      long[] var2 = new long[var1];
      long[] var3 = this.x;

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var2[var4] = var3[var4];
      }

      this.x = var2;
   }

   public boolean isDeadEnd() {
      return this == DEAD_END;
   }

   public boolean isReallyDeadEnd() {
      return this.x == fullX;
   }

   public Vset clearDeadEnd() {
      return this == DEAD_END ? new Vset(-1L, -1L, fullX) : this;
   }

   public boolean testVar(int var1) {
      long var2 = 1L << var1;
      if (var1 >= 64) {
         int var4 = (var1 / 64 - 1) * 2;
         if (var4 >= this.x.length) {
            return this.x == fullX;
         } else {
            return (this.x[var4] & var2) != 0L;
         }
      } else {
         return (this.vset & var2) != 0L;
      }
   }

   public boolean testVarUnassigned(int var1) {
      long var2 = 1L << var1;
      if (var1 >= 64) {
         int var4 = (var1 / 64 - 1) * 2 + 1;
         if (var4 >= this.x.length) {
            return this.x == fullX;
         } else {
            return (this.x[var4] & var2) != 0L;
         }
      } else {
         return (this.uset & var2) != 0L;
      }
   }

   public Vset addVar(int var1) {
      if (this.x == fullX) {
         return this;
      } else {
         long var2 = 1L << var1;
         if (var1 >= 64) {
            int var4 = (var1 / 64 - 1) * 2;
            if (var4 >= this.x.length) {
               this.growX(var4 + 1);
            }

            long[] var10000 = this.x;
            var10000[var4] |= var2;
            if (var4 + 1 < this.x.length) {
               var10000 = this.x;
               var10000[var4 + 1] &= ~var2;
            }
         } else {
            this.vset |= var2;
            this.uset &= ~var2;
         }

         return this;
      }
   }

   public Vset addVarUnassigned(int var1) {
      if (this.x == fullX) {
         return this;
      } else {
         long var2 = 1L << var1;
         if (var1 >= 64) {
            int var4 = (var1 / 64 - 1) * 2 + 1;
            if (var4 >= this.x.length) {
               this.growX(var4 + 1);
            }

            long[] var10000 = this.x;
            var10000[var4] |= var2;
            var10000 = this.x;
            var10000[var4 - 1] &= ~var2;
         } else {
            this.uset |= var2;
            this.vset &= ~var2;
         }

         return this;
      }
   }

   public Vset clearVar(int var1) {
      if (this.x == fullX) {
         return this;
      } else {
         long var2 = 1L << var1;
         if (var1 >= 64) {
            int var4 = (var1 / 64 - 1) * 2;
            if (var4 >= this.x.length) {
               return this;
            }

            long[] var10000 = this.x;
            var10000[var4] &= ~var2;
            if (var4 + 1 < this.x.length) {
               var10000 = this.x;
               var10000[var4 + 1] &= ~var2;
            }
         } else {
            this.vset &= ~var2;
            this.uset &= ~var2;
         }

         return this;
      }
   }

   public Vset join(Vset var1) {
      if (this == DEAD_END) {
         return var1.copy();
      } else if (var1 == DEAD_END) {
         return this;
      } else if (this.x == fullX) {
         return var1.copy();
      } else if (var1.x == fullX) {
         return this;
      } else {
         this.vset &= var1.vset;
         this.uset &= var1.uset;
         if (var1.x == emptyX) {
            this.x = emptyX;
         } else {
            long[] var2 = var1.x;
            int var3 = this.x.length;
            int var4 = var2.length < var3 ? var2.length : var3;

            int var5;
            for(var5 = 0; var5 < var4; ++var5) {
               long[] var10000 = this.x;
               var10000[var5] &= var2[var5];
            }

            for(var5 = var4; var5 < var3; ++var5) {
               this.x[var5] = 0L;
            }
         }

         return this;
      }
   }

   public Vset addDAandJoinDU(Vset var1) {
      if (this == DEAD_END) {
         return this;
      } else if (var1 == DEAD_END) {
         return var1;
      } else if (this.x == fullX) {
         return this;
      } else if (var1.x == fullX) {
         return var1.copy();
      } else {
         this.vset |= var1.vset;
         this.uset = this.uset & var1.uset & ~var1.vset;
         int var2 = this.x.length;
         long[] var3 = var1.x;
         int var4 = var3.length;
         int var5;
         if (var3 != emptyX) {
            if (var4 > var2) {
               this.growX(var4);
            }

            for(var5 = 0; var5 < var4; ++var5) {
               long[] var10000 = this.x;
               var10000[var5] |= var3[var5];
               ++var5;
               if (var5 == var4) {
                  break;
               }

               this.x[var5] = this.x[var5] & var3[var5] & ~var3[var5 - 1];
            }
         }

         for(var5 = var4 | 1; var5 < var2; var5 += 2) {
            this.x[var5] = 0L;
         }

         return this;
      }
   }

   public static Vset firstDAandSecondDU(Vset var0, Vset var1) {
      if (var0.x == fullX) {
         return var0.copy();
      } else {
         long[] var2 = var0.x;
         int var3 = var2.length;
         long[] var4 = var1.x;
         int var5 = var4.length;
         int var6 = var3 > var5 ? var3 : var5;
         long[] var7 = emptyX;
         if (var6 > 0) {
            var7 = new long[var6];

            int var8;
            for(var8 = 0; var8 < var3; var8 += 2) {
               var7[var8] = var2[var8];
            }

            for(var8 = 1; var8 < var5; var8 += 2) {
               var7[var8] = var4[var8];
            }
         }

         return new Vset(var0.vset, var1.uset, var7);
      }
   }

   public Vset removeAdditionalVars(int var1) {
      if (this.x == fullX) {
         return this;
      } else {
         long var2 = 1L << var1;
         if (var1 >= 64) {
            int var4 = (var1 / 64 - 1) * 2;
            if (var4 < this.x.length) {
               long[] var10000 = this.x;
               var10000[var4] &= var2 - 1L;
               ++var4;
               if (var4 < this.x.length) {
                  var10000 = this.x;
                  var10000[var4] &= var2 - 1L;
               }

               while(true) {
                  ++var4;
                  if (var4 >= this.x.length) {
                     break;
                  }

                  this.x[var4] = 0L;
               }
            }
         } else {
            if (this.x.length > 0) {
               this.x = emptyX;
            }

            this.vset &= var2 - 1L;
            this.uset &= var2 - 1L;
         }

         return this;
      }
   }

   public int varLimit() {
      int var4 = this.x.length / 2 * 2;

      long var1;
      int var3;
      while(true) {
         if (var4 < 0) {
            var1 = this.vset;
            var1 |= this.uset;
            if (var1 == 0L) {
               return 0;
            }

            var3 = 0;
            break;
         }

         if (var4 != this.x.length) {
            var1 = this.x[var4];
            if (var4 + 1 < this.x.length) {
               var1 |= this.x[var4 + 1];
            }

            if (var1 != 0L) {
               var3 = (var4 / 2 + 1) * 64;
               break;
            }
         }

         var4 -= 2;
      }

      while(var1 != 0L) {
         ++var3;
         var1 >>>= 1;
      }

      return var3;
   }

   public String toString() {
      if (this == DEAD_END) {
         return "{DEAD_END}";
      } else {
         StringBuffer var1 = new StringBuffer("{");
         int var2 = 64 * (1 + (this.x.length + 1) / 2);

         for(int var3 = 0; var3 < var2; ++var3) {
            if (!this.testVarUnassigned(var3)) {
               if (var1.length() > 1) {
                  var1.append(' ');
               }

               var1.append(var3);
               if (!this.testVar(var3)) {
                  var1.append('?');
               }
            }
         }

         if (this.x == fullX) {
            var1.append("...DEAD_END");
         }

         var1.append('}');
         return var1.toString();
      }
   }

   static {
      DEAD_END = new Vset(-1L, -1L, fullX);
   }
}
