package com.sun.xml.internal.dtdparser;

public class XmlNames {
   private XmlNames() {
   }

   public static boolean isName(String value) {
      if (value == null) {
         return false;
      } else {
         char c = value.charAt(0);
         if (!XmlChars.isLetter(c) && c != '_' && c != ':') {
            return false;
         } else {
            for(int i = 1; i < value.length(); ++i) {
               if (!XmlChars.isNameChar(value.charAt(i))) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static boolean isUnqualifiedName(String value) {
      if (value != null && value.length() != 0) {
         char c = value.charAt(0);
         if (!XmlChars.isLetter(c) && c != '_') {
            return false;
         } else {
            for(int i = 1; i < value.length(); ++i) {
               if (!XmlChars.isNCNameChar(value.charAt(i))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isQualifiedName(String value) {
      if (value == null) {
         return false;
      } else {
         int first = value.indexOf(58);
         if (first <= 0) {
            return isUnqualifiedName(value);
         } else {
            int last = value.lastIndexOf(58);
            if (last != first) {
               return false;
            } else {
               return isUnqualifiedName(value.substring(0, first)) && isUnqualifiedName(value.substring(first + 1));
            }
         }
      }
   }

   public static boolean isNmtoken(String token) {
      int length = token.length();

      for(int i = 0; i < length; ++i) {
         if (!XmlChars.isNameChar(token.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public static boolean isNCNmtoken(String token) {
      return isNmtoken(token) && token.indexOf(58) < 0;
   }
}
