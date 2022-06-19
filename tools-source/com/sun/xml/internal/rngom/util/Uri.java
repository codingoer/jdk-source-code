package com.sun.xml.internal.rngom.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

public class Uri {
   private static String utf8 = "UTF-8";
   private static final String HEX_DIGITS = "0123456789abcdef";
   private static final String excluded = "<>\"{}|\\^`";

   public static boolean isValid(String s) {
      return isValidPercent(s) && isValidFragment(s) && isValidScheme(s);
   }

   public static String escapeDisallowedChars(String s) {
      StringBuffer buf = null;
      int len = s.length();
      int done = 0;

      while(true) {
         int i = done;

         while(true) {
            if (i == len) {
               if (done == 0) {
                  return s;
               }
               break;
            }

            if (isExcluded(s.charAt(i))) {
               break;
            }

            ++i;
         }

         if (buf == null) {
            buf = new StringBuffer();
         }

         if (i > done) {
            buf.append(s.substring(done, i));
            done = i;
         }

         if (i == len) {
            return buf.toString();
         }

         ++i;

         while(i < len && isExcluded(s.charAt(i))) {
            ++i;
         }

         String tem = s.substring(done, i);

         byte[] bytes;
         try {
            bytes = tem.getBytes(utf8);
         } catch (UnsupportedEncodingException var10) {
            utf8 = "UTF8";

            try {
               bytes = tem.getBytes(utf8);
            } catch (UnsupportedEncodingException var9) {
               return s;
            }
         }

         for(int j = 0; j < bytes.length; ++j) {
            buf.append('%');
            buf.append("0123456789abcdef".charAt((bytes[j] & 255) >> 4));
            buf.append("0123456789abcdef".charAt(bytes[j] & 15));
         }

         done = i;
      }
   }

   private static boolean isExcluded(char c) {
      return c <= ' ' || c >= 127 || "<>\"{}|\\^`".indexOf(c) >= 0;
   }

   private static boolean isAlpha(char c) {
      return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
   }

   private static boolean isHexDigit(char c) {
      return 'a' <= c && c <= 'f' || 'A' <= c && c <= 'F' || isDigit(c);
   }

   private static boolean isDigit(char c) {
      return '0' <= c && c <= '9';
   }

   private static boolean isSchemeChar(char c) {
      return isAlpha(c) || isDigit(c) || c == '+' || c == '-' || c == '.';
   }

   private static boolean isValidPercent(String s) {
      int len = s.length();

      for(int i = 0; i < len; ++i) {
         if (s.charAt(i) == '%') {
            if (i + 2 >= len) {
               return false;
            }

            if (!isHexDigit(s.charAt(i + 1)) || !isHexDigit(s.charAt(i + 2))) {
               return false;
            }
         }
      }

      return true;
   }

   private static boolean isValidFragment(String s) {
      int i = s.indexOf(35);
      return i < 0 || s.indexOf(35, i + 1) < 0;
   }

   private static boolean isValidScheme(String s) {
      if (!isAbsolute(s)) {
         return true;
      } else {
         int i = s.indexOf(58);
         if (i != 0 && i + 1 != s.length() && isAlpha(s.charAt(0))) {
            do {
               --i;
               if (i <= 0) {
                  return true;
               }
            } while(isSchemeChar(s.charAt(i)));

            return false;
         } else {
            return false;
         }
      }
   }

   public static String resolve(String baseUri, String uriReference) {
      if (!isAbsolute(uriReference) && baseUri != null && isAbsolute(baseUri)) {
         try {
            return (new URL(new URL(baseUri), uriReference)).toString();
         } catch (MalformedURLException var3) {
         }
      }

      return uriReference;
   }

   public static boolean hasFragmentId(String uri) {
      return uri.indexOf(35) >= 0;
   }

   public static boolean isAbsolute(String uri) {
      int i = uri.indexOf(58);
      if (i < 0) {
         return false;
      } else {
         while(true) {
            --i;
            if (i < 0) {
               return true;
            }

            switch (uri.charAt(i)) {
               case '#':
               case '/':
               case '?':
                  return false;
            }
         }
      }
   }
}
