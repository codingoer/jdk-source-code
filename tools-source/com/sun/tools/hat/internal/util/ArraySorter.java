package com.sun.tools.hat.internal.util;

public class ArraySorter {
   public static void sort(Object[] var0, Comparer var1) {
      quickSort(var0, var1, 0, var0.length - 1);
   }

   public static void sortArrayOfStrings(Object[] var0) {
      sort(var0, new Comparer() {
         public int compare(Object var1, Object var2) {
            return ((String)var1).compareTo((String)var2);
         }
      });
   }

   private static void swap(Object[] var0, int var1, int var2) {
      Object var3 = var0[var1];
      var0[var1] = var0[var2];
      var0[var2] = var3;
   }

   private static void quickSort(Object[] var0, Comparer var1, int var2, int var3) {
      if (var3 > var2) {
         int var4 = (var2 + var3) / 2;
         if (var4 != var2) {
            swap(var0, var4, var2);
         }

         Object var5 = var0[var2];
         int var6 = var2 - 1;
         int var7 = var2 + 1;
         int var8 = var3;

         while(true) {
            while(var7 <= var8) {
               int var9 = var1.compare(var0[var7], var5);
               if (var9 <= 0) {
                  if (var9 < 0) {
                     var6 = var7;
                  }

                  ++var7;
               } else {
                  int var10;
                  do {
                     var10 = var1.compare(var0[var8], var5);
                     if (var10 <= 0) {
                        break;
                     }

                     --var8;
                  } while(var7 <= var8);

                  if (var7 <= var8) {
                     swap(var0, var7, var8);
                     if (var10 < 0) {
                        var6 = var7;
                     }

                     ++var7;
                     --var8;
                  }
               }
            }

            if (var6 > var2) {
               swap(var0, var2, var6);
               quickSort(var0, var1, var2, var6 - 1);
            }

            quickSort(var0, var1, var8 + 1, var3);
            return;
         }
      }
   }
}
