package com.sun.tools.javac.processing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class ServiceProxy {
   private static final String prefix = "META-INF/services/";

   private static void fail(Class var0, String var1) throws ServiceConfigurationError {
      throw new ServiceConfigurationError(var0.getName() + ": " + var1);
   }

   private static void fail(Class var0, URL var1, int var2, String var3) throws ServiceConfigurationError {
      fail(var0, var1 + ":" + var2 + ": " + var3);
   }

   private static boolean parse(Class var0, URL var1) throws ServiceConfigurationError {
      InputStream var2 = null;
      BufferedReader var3 = null;

      boolean var5;
      try {
         var2 = var1.openStream();
         var3 = new BufferedReader(new InputStreamReader(var2, "utf-8"));
         byte var4 = 1;

         int var7;
         String var31;
         do {
            if ((var31 = var3.readLine()) == null) {
               return false;
            }

            int var6 = var31.indexOf(35);
            if (var6 >= 0) {
               var31 = var31.substring(0, var6);
            }

            var31 = var31.trim();
            var7 = var31.length();
         } while(var7 == 0);

         if (var31.indexOf(32) >= 0 || var31.indexOf(9) >= 0) {
            fail(var0, var1, var4, "Illegal configuration-file syntax");
         }

         int var8 = var31.codePointAt(0);
         if (!Character.isJavaIdentifierStart(var8)) {
            fail(var0, var1, var4, "Illegal provider-class name: " + var31);
         }

         for(int var9 = Character.charCount(var8); var9 < var7; var9 += Character.charCount(var8)) {
            var8 = var31.codePointAt(var9);
            if (!Character.isJavaIdentifierPart(var8) && var8 != 46) {
               fail(var0, var1, var4, "Illegal provider-class name: " + var31);
            }
         }

         boolean var32 = true;
         return var32;
      } catch (FileNotFoundException var28) {
         var5 = false;
      } catch (IOException var29) {
         fail(var0, ": " + var29);
         return false;
      } finally {
         try {
            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var27) {
            fail(var0, ": " + var27);
         }

         try {
            if (var2 != null) {
               var2.close();
            }
         } catch (IOException var26) {
            fail(var0, ": " + var26);
         }

      }

      return var5;
   }

   public static boolean hasService(Class var0, URL[] var1) throws ServiceConfigurationError {
      URL[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         URL var5 = var2[var4];

         try {
            String var6 = "META-INF/services/" + var0.getName();
            URL var7 = new URL(var5, var6);
            boolean var8 = parse(var0, var7);
            if (var8) {
               return true;
            }
         } catch (MalformedURLException var9) {
         }
      }

      return false;
   }

   static class ServiceConfigurationError extends Error {
      static final long serialVersionUID = 7732091036771098303L;

      ServiceConfigurationError(String var1) {
         super(var1);
      }
   }
}
