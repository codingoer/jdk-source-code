package com.sun.xml.internal.rngom.util;

public abstract class Utf16 {
   public static boolean isSurrogate(char c) {
      return (c & '\uf800') == 55296;
   }

   public static boolean isSurrogate1(char c) {
      return (c & 'ﰀ') == 55296;
   }

   public static boolean isSurrogate2(char c) {
      return (c & 'ﰀ') == 56320;
   }

   public static int scalarValue(char c1, char c2) {
      return ((c1 & 1023) << 10 | c2 & 1023) + 65536;
   }

   public static char surrogate1(int c) {
      return (char)(c - 65536 >> 10 | '\ud800');
   }

   public static char surrogate2(int c) {
      return (char)(c - 65536 & 1023 | '\udc00');
   }
}
