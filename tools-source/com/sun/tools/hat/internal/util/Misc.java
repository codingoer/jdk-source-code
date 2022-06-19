package com.sun.tools.hat.internal.util;

public class Misc {
   private static char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static final String toHex(int var0) {
      char[] var1 = new char[8];
      int var2 = 0;

      for(int var3 = 28; var3 >= 0; var3 -= 4) {
         var1[var2++] = digits[var0 >> var3 & 15];
      }

      return "0x" + new String(var1);
   }

   public static final String toHex(long var0) {
      return "0x" + Long.toHexString(var0);
   }

   public static final long parseHex(String var0) {
      long var1 = 0L;
      if (var0.length() >= 2 && var0.charAt(0) == '0' && var0.charAt(1) == 'x') {
         for(int var3 = 2; var3 < var0.length(); ++var3) {
            var1 *= 16L;
            char var4 = var0.charAt(var3);
            if (var4 >= '0' && var4 <= '9') {
               var1 += (long)(var4 - 48);
            } else if (var4 >= 'a' && var4 <= 'f') {
               var1 += (long)(var4 - 97 + 10);
            } else {
               if (var4 < 'A' || var4 > 'F') {
                  throw new NumberFormatException("" + var4 + " is not a valid hex digit");
               }

               var1 += (long)(var4 - 65 + 10);
            }
         }

         return var1;
      } else {
         return -1L;
      }
   }

   public static String encodeHtml(String var0) {
      int var1 = var0.length();
      StringBuffer var2 = new StringBuffer();

      for(int var3 = 0; var3 < var1; ++var3) {
         char var4 = var0.charAt(var3);
         if (var4 == '<') {
            var2.append("&lt;");
         } else if (var4 == '>') {
            var2.append("&gt;");
         } else if (var4 == '"') {
            var2.append("&quot;");
         } else if (var4 == '\'') {
            var2.append("&#039;");
         } else if (var4 == '&') {
            var2.append("&amp;");
         } else if (var4 < ' ') {
            var2.append("&#" + Integer.toString(var4) + ";");
         } else {
            int var5 = var4 & '\uffff';
            if (var5 > 127) {
               var2.append("&#" + Integer.toString(var5) + ";");
            } else {
               var2.append(var4);
            }
         }
      }

      return var2.toString();
   }
}
