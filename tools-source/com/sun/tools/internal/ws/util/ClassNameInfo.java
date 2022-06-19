package com.sun.tools.internal.ws.util;

public final class ClassNameInfo {
   public static String getName(String className) {
      String qual = getQualifier(className);
      int len = className.length();
      int closingBracket = className.indexOf(62);
      if (closingBracket > 0) {
         len = closingBracket;
      }

      return qual != null ? className.substring(qual.length() + 1, len) : className;
   }

   public static String getGenericClass(String className) {
      int index = className.indexOf(60);
      if (index < 0) {
         return className;
      } else {
         return index > 0 ? className.substring(0, index) : className;
      }
   }

   public static String getQualifier(String className) {
      int idot = className.indexOf(32);
      if (idot <= 0) {
         idot = className.length();
      } else {
         --idot;
      }

      int index = className.lastIndexOf(46, idot - 1);
      return index < 0 ? null : className.substring(0, index);
   }

   public static String replaceInnerClassSym(String name) {
      return name.replace('$', '_');
   }
}
