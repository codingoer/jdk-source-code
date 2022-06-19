package com.sun.tools.javac.util;

public class Convert {
   public static int string2int(String var0, int var1) throws NumberFormatException {
      if (var1 == 10) {
         return Integer.parseInt(var0, var1);
      } else {
         char[] var2 = var0.toCharArray();
         int var3 = Integer.MAX_VALUE / (var1 / 2);
         int var4 = 0;

         for(int var5 = 0; var5 < var2.length; ++var5) {
            int var6 = Character.digit(var2[var5], var1);
            if (var4 < 0 || var4 > var3 || var4 * var1 > Integer.MAX_VALUE - var6) {
               throw new NumberFormatException();
            }

            var4 = var4 * var1 + var6;
         }

         return var4;
      }
   }

   public static long string2long(String var0, int var1) throws NumberFormatException {
      if (var1 == 10) {
         return Long.parseLong(var0, var1);
      } else {
         char[] var2 = var0.toCharArray();
         long var3 = Long.MAX_VALUE / (long)(var1 / 2);
         long var5 = 0L;

         for(int var7 = 0; var7 < var2.length; ++var7) {
            int var8 = Character.digit(var2[var7], var1);
            if (var5 < 0L || var5 > var3 || var5 * (long)var1 > Long.MAX_VALUE - (long)var8) {
               throw new NumberFormatException();
            }

            var5 = var5 * (long)var1 + (long)var8;
         }

         return var5;
      }
   }

   public static int utf2chars(byte[] var0, int var1, char[] var2, int var3, int var4) {
      int var5 = var1;
      int var6 = var3;

      int var8;
      for(int var7 = var1 + var4; var5 < var7; var2[var6++] = (char)var8) {
         var8 = var0[var5++] & 255;
         if (var8 >= 224) {
            var8 = (var8 & 15) << 12;
            var8 |= (var0[var5++] & 63) << 6;
            var8 |= var0[var5++] & 63;
         } else if (var8 >= 192) {
            var8 = (var8 & 31) << 6;
            var8 |= var0[var5++] & 63;
         }
      }

      return var6;
   }

   public static char[] utf2chars(byte[] var0, int var1, int var2) {
      char[] var3 = new char[var2];
      int var4 = utf2chars(var0, var1, var3, 0, var2);
      char[] var5 = new char[var4];
      System.arraycopy(var3, 0, var5, 0, var4);
      return var5;
   }

   public static char[] utf2chars(byte[] var0) {
      return utf2chars(var0, 0, var0.length);
   }

   public static String utf2string(byte[] var0, int var1, int var2) {
      char[] var3 = new char[var2];
      int var4 = utf2chars(var0, var1, var3, 0, var2);
      return new String(var3, 0, var4);
   }

   public static String utf2string(byte[] var0) {
      return utf2string(var0, 0, var0.length);
   }

   public static int chars2utf(char[] var0, int var1, byte[] var2, int var3, int var4) {
      int var5 = var3;
      int var6 = var1 + var4;

      for(int var7 = var1; var7 < var6; ++var7) {
         char var8 = var0[var7];
         if (1 <= var8 && var8 <= 127) {
            var2[var5++] = (byte)var8;
         } else if (var8 <= 2047) {
            var2[var5++] = (byte)(192 | var8 >> 6);
            var2[var5++] = (byte)(128 | var8 & 63);
         } else {
            var2[var5++] = (byte)(224 | var8 >> 12);
            var2[var5++] = (byte)(128 | var8 >> 6 & 63);
            var2[var5++] = (byte)(128 | var8 & 63);
         }
      }

      return var5;
   }

   public static byte[] chars2utf(char[] var0, int var1, int var2) {
      byte[] var3 = new byte[var2 * 3];
      int var4 = chars2utf(var0, var1, var3, 0, var2);
      byte[] var5 = new byte[var4];
      System.arraycopy(var3, 0, var5, 0, var4);
      return var5;
   }

   public static byte[] chars2utf(char[] var0) {
      return chars2utf(var0, 0, var0.length);
   }

   public static byte[] string2utf(String var0) {
      return chars2utf(var0.toCharArray());
   }

   public static String quote(String var0) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < var0.length(); ++var2) {
         var1.append(quote(var0.charAt(var2)));
      }

      return var1.toString();
   }

   public static String quote(char var0) {
      switch (var0) {
         case '\b':
            return "\\b";
         case '\t':
            return "\\t";
         case '\n':
            return "\\n";
         case '\f':
            return "\\f";
         case '\r':
            return "\\r";
         case '"':
            return "\\\"";
         case '\'':
            return "\\'";
         case '\\':
            return "\\\\";
         default:
            return isPrintableAscii(var0) ? String.valueOf(var0) : String.format("\\u%04x", Integer.valueOf(var0));
      }
   }

   private static boolean isPrintableAscii(char var0) {
      return var0 >= ' ' && var0 <= '~';
   }

   public static String escapeUnicode(String var0) {
      int var1 = var0.length();
      int var2 = 0;

      while(true) {
         while(var2 < var1) {
            char var3 = var0.charAt(var2);
            if (var3 > 255) {
               StringBuilder var4 = new StringBuilder();
               var4.append(var0.substring(0, var2));

               for(; var2 < var1; ++var2) {
                  var3 = var0.charAt(var2);
                  if (var3 > 255) {
                     var4.append("\\u");
                     var4.append(Character.forDigit((var3 >> 12) % 16, 16));
                     var4.append(Character.forDigit((var3 >> 8) % 16, 16));
                     var4.append(Character.forDigit((var3 >> 4) % 16, 16));
                     var4.append(Character.forDigit(var3 % 16, 16));
                  } else {
                     var4.append(var3);
                  }
               }

               var0 = var4.toString();
            } else {
               ++var2;
            }
         }

         return var0;
      }
   }

   public static Name shortName(Name var0) {
      return var0.subName(var0.lastIndexOf((byte)46) + 1, var0.getByteLength());
   }

   public static String shortName(String var0) {
      return var0.substring(var0.lastIndexOf(46) + 1);
   }

   public static Name packagePart(Name var0) {
      return var0.subName(0, var0.lastIndexOf((byte)46));
   }

   public static String packagePart(String var0) {
      int var1 = var0.lastIndexOf(46);
      return var1 < 0 ? "" : var0.substring(0, var1);
   }

   public static List enclosingCandidates(Name var0) {
      List var1;
      int var2;
      for(var1 = List.nil(); (var2 = var0.lastIndexOf((byte)36)) > 0; var1 = var1.prepend(var0)) {
         var0 = var0.subName(0, var2);
      }

      return var1;
   }
}
