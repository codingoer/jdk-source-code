package com.sun.tools.javac.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
   public static String toLowerCase(String var0) {
      return var0.toLowerCase(Locale.US);
   }

   public static String toUpperCase(String var0) {
      return var0.toUpperCase(Locale.US);
   }

   public static int indexOfIgnoreCase(String var0, String var1) {
      return indexOfIgnoreCase(var0, var1, 0);
   }

   public static int indexOfIgnoreCase(String var0, String var1, int var2) {
      Matcher var3 = Pattern.compile(Pattern.quote(var1), 2).matcher(var0);
      return var3.find(var2) ? var3.start() : -1;
   }
}
