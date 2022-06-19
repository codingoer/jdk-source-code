package com.sun.tools.corba.se.idl.som.cff;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public abstract class Messages {
   private static final String LTB = "%B";
   private static final char NL = '\n';
   private static final String lineSeparator = System.getProperty("line.separator");
   private static final Properties m = new Properties();
   private static boolean loadNeeded = true;

   private static final synchronized void loadDefaultProperties() {
      if (loadNeeded) {
         try {
            m.load(FileLocator.locateLocaleSpecificFileInClassPath("com/sun/tools/corba/se/idl/som/cff/cff.properties"));
         } catch (IOException var1) {
         }

         fixMessages(m);
         loadNeeded = false;
      }
   }

   private static final void fixMessages(Properties var0) {
      Enumeration var1 = var0.keys();
      Enumeration var2 = var0.elements();

      while(var1.hasMoreElements()) {
         String var3 = (String)var1.nextElement();
         String var4 = (String)var2.nextElement();
         int var5 = var4.indexOf("%B");

         boolean var6;
         for(var6 = false; var5 != -1; var5 = var4.indexOf("%B")) {
            if (var5 == 0) {
               var4 = " " + var4.substring(2);
            } else {
               var4 = var4.substring(0, var5) + " " + var4.substring(var5 + 2);
            }

            var6 = true;
         }

         int var7 = lineSeparator.length() - 1;

         for(var5 = 0; var5 < var4.length(); ++var5) {
            if (var4.charAt(var5) == '\n') {
               var4 = var4.substring(0, var5) + lineSeparator + var4.substring(var5 + 1);
               var5 += var7;
               var6 = true;
            }
         }

         if (var6) {
            var0.put(var3, var4);
         }
      }

   }

   public static final synchronized void msgLoad(String var0) throws IOException {
      m.load(FileLocator.locateLocaleSpecificFileInClassPath(var0));
      fixMessages(m);
      loadNeeded = false;
   }

   public static final String msg(String var0) {
      if (loadNeeded) {
         loadDefaultProperties();
      }

      String var1 = m.getProperty(var0, var0);
      return var1;
   }

   public static final String msg(String var0, String var1) {
      if (loadNeeded) {
         loadDefaultProperties();
      }

      String var2 = m.getProperty(var0, var0);
      int var3 = var2.indexOf("%1");
      if (var3 >= 0) {
         String var4 = "";
         if (var3 + 2 < var2.length()) {
            var4 = var2.substring(var3 + 2);
         }

         return var2.substring(0, var3) + var1 + var4;
      } else {
         var2 = var2 + " " + var1;
         return var2;
      }
   }

   public static final String msg(String var0, int var1) {
      return msg(var0, String.valueOf(var1));
   }

   public static final String msg(String var0, String var1, String var2) {
      if (loadNeeded) {
         loadDefaultProperties();
      }

      String var3 = m.getProperty(var0, var0);
      String var4 = "";
      int var5 = var3.indexOf("%1");
      if (var5 >= 0) {
         if (var5 + 2 < var3.length()) {
            var4 = var3.substring(var5 + 2);
         }

         var3 = var3.substring(0, var5) + var1 + var4;
      } else {
         var3 = var3 + " " + var1;
      }

      var5 = var3.indexOf("%2");
      if (var5 >= 0) {
         var4 = "";
         if (var5 + 2 < var3.length()) {
            var4 = var3.substring(var5 + 2);
         }

         var3 = var3.substring(0, var5) + var2 + var4;
      } else {
         var3 = var3 + " " + var2;
      }

      return var3;
   }

   public static final String msg(String var0, int var1, String var2) {
      return msg(var0, String.valueOf(var1), var2);
   }

   public static final String msg(String var0, String var1, int var2) {
      return msg(var0, var1, String.valueOf(var2));
   }

   public static final String msg(String var0, int var1, int var2) {
      return msg(var0, String.valueOf(var1), String.valueOf(var2));
   }

   public static final String msg(String var0, String var1, String var2, String var3) {
      if (loadNeeded) {
         loadDefaultProperties();
      }

      String var4 = m.getProperty(var0, var0);
      var4 = substituteString(var4, 1, var1);
      var4 = substituteString(var4, 2, var2);
      var4 = substituteString(var4, 3, var3);
      return var4;
   }

   private static String substituteString(String var0, int var1, String var2) {
      String var4 = "%" + var1;
      int var5 = var4.length();
      int var6 = var0.indexOf(var4);
      String var7 = "";
      String var3;
      if (var6 >= 0) {
         if (var6 + var5 < var0.length()) {
            var7 = var0.substring(var6 + var5);
         }

         var3 = var0.substring(0, var6) + var2 + var7;
      } else {
         var3 = var0 + " " + var2;
      }

      return var3;
   }
}
