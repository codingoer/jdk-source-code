package com.sun.tools.javac.util;

import java.lang.reflect.Array;

public class ArrayUtils {
   private static int calculateNewLength(int var0, int var1) {
      while(var0 < var1 + 1) {
         var0 *= 2;
      }

      return var0;
   }

   public static Object[] ensureCapacity(Object[] var0, int var1) {
      if (var1 < var0.length) {
         return var0;
      } else {
         int var2 = calculateNewLength(var0.length, var1);
         Object[] var3 = (Object[])((Object[])Array.newInstance(var0.getClass().getComponentType(), var2));
         System.arraycopy(var0, 0, var3, 0, var0.length);
         return var3;
      }
   }

   public static byte[] ensureCapacity(byte[] var0, int var1) {
      if (var1 < var0.length) {
         return var0;
      } else {
         int var2 = calculateNewLength(var0.length, var1);
         byte[] var3 = new byte[var2];
         System.arraycopy(var0, 0, var3, 0, var0.length);
         return var3;
      }
   }

   public static char[] ensureCapacity(char[] var0, int var1) {
      if (var1 < var0.length) {
         return var0;
      } else {
         int var2 = calculateNewLength(var0.length, var1);
         char[] var3 = new char[var2];
         System.arraycopy(var0, 0, var3, 0, var0.length);
         return var3;
      }
   }

   public static int[] ensureCapacity(int[] var0, int var1) {
      if (var1 < var0.length) {
         return var0;
      } else {
         int var2 = calculateNewLength(var0.length, var1);
         int[] var3 = new int[var2];
         System.arraycopy(var0, 0, var3, 0, var0.length);
         return var3;
      }
   }
}
