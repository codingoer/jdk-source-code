package com.sun.tools.internal.xjc.util;

import org.xml.sax.Locator;

public final class Util {
   private Util() {
   }

   public static String getSystemProperty(String name) {
      try {
         return System.getProperty(name);
      } catch (SecurityException var2) {
         return null;
      }
   }

   public static boolean equals(Locator lhs, Locator rhs) {
      return lhs.getLineNumber() == rhs.getLineNumber() && lhs.getColumnNumber() == rhs.getColumnNumber() && equals(lhs.getSystemId(), rhs.getSystemId()) && equals(lhs.getPublicId(), rhs.getPublicId());
   }

   private static boolean equals(String lhs, String rhs) {
      if (lhs == null && rhs == null) {
         return true;
      } else {
         return lhs != null && rhs != null ? lhs.equals(rhs) : false;
      }
   }

   public static String getSystemProperty(Class clazz, String name) {
      return getSystemProperty(clazz.getName() + '.' + name);
   }
}
