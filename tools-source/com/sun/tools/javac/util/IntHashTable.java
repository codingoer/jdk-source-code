package com.sun.tools.javac.util;

public class IntHashTable {
   private static final int DEFAULT_INITIAL_SIZE = 64;
   protected Object[] objs;
   protected int[] ints;
   protected int mask;
   protected int num_bindings;
   private static final Object DELETED = new Object();

   public IntHashTable() {
      this.objs = new Object[64];
      this.ints = new int[64];
      this.mask = 63;
   }

   public IntHashTable(int var1) {
      int var2;
      for(var2 = 4; var1 > 1 << var2; ++var2) {
      }

      var1 = 1 << var2;
      this.objs = new Object[var1];
      this.ints = new int[var1];
      this.mask = var1 - 1;
   }

   public int hash(Object var1) {
      return System.identityHashCode(var1);
   }

   public int lookup(Object var1, int var2) {
      int var4 = var2 ^ var2 >>> 15;
      int var5 = var2 ^ var2 << 6 | 1;
      int var6 = -1;
      int var7 = var4 & this.mask;

      while(true) {
         Object var3 = this.objs[var7];
         if (var3 == var1) {
            return var7;
         }

         if (var3 == null) {
            return var6 >= 0 ? var6 : var7;
         }

         if (var3 == DELETED && var6 < 0) {
            var6 = var7;
         }

         var7 = var7 + var5 & this.mask;
      }
   }

   public int lookup(Object var1) {
      return this.lookup(var1, this.hash(var1));
   }

   public int getFromIndex(int var1) {
      Object var2 = this.objs[var1];
      return var2 != null && var2 != DELETED ? this.ints[var1] : -1;
   }

   public int putAtIndex(Object var1, int var2, int var3) {
      Object var4 = this.objs[var3];
      if (var4 != null && var4 != DELETED) {
         int var5 = this.ints[var3];
         this.ints[var3] = var2;
         return var5;
      } else {
         this.objs[var3] = var1;
         this.ints[var3] = var2;
         if (var4 != DELETED) {
            ++this.num_bindings;
         }

         if (3 * this.num_bindings >= 2 * this.objs.length) {
            this.rehash();
         }

         return -1;
      }
   }

   public int remove(Object var1) {
      int var2 = this.lookup(var1);
      Object var3 = this.objs[var2];
      if (var3 != null && var3 != DELETED) {
         this.objs[var2] = DELETED;
         return this.ints[var2];
      } else {
         return -1;
      }
   }

   protected void rehash() {
      Object[] var1 = this.objs;
      int[] var2 = this.ints;
      int var3 = var1.length;
      int var4 = var3 << 1;
      Object[] var5 = new Object[var4];
      int[] var6 = new int[var4];
      int var7 = var4 - 1;
      this.objs = var5;
      this.ints = var6;
      this.mask = var7;
      this.num_bindings = 0;
      int var9 = var2.length;

      while(true) {
         --var9;
         if (var9 < 0) {
            return;
         }

         Object var8 = var1[var9];
         if (var8 != null && var8 != DELETED) {
            this.putAtIndex(var8, var2[var9], this.lookup(var8, this.hash(var8)));
         }
      }
   }

   public void clear() {
      int var1 = this.objs.length;

      while(true) {
         --var1;
         if (var1 < 0) {
            this.num_bindings = 0;
            return;
         }

         this.objs[var1] = null;
      }
   }
}
