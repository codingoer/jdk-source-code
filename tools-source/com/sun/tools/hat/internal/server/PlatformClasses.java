package com.sun.tools.hat.internal.server;

import com.sun.tools.hat.internal.model.JavaClass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class PlatformClasses {
   static String[] names = null;

   public static synchronized String[] getNames() {
      if (names == null) {
         LinkedList var0 = new LinkedList();
         InputStream var1 = PlatformClasses.class.getResourceAsStream("/com/sun/tools/hat/resources/platform_names.txt");
         if (var1 != null) {
            try {
               BufferedReader var2 = new BufferedReader(new InputStreamReader(var1));

               while(true) {
                  String var3 = var2.readLine();
                  if (var3 == null) {
                     var2.close();
                     var1.close();
                     break;
                  }

                  if (var3.length() > 0) {
                     var0.add(var3);
                  }
               }
            } catch (IOException var4) {
               var4.printStackTrace();
            }
         }

         names = (String[])var0.toArray(new String[var0.size()]);
      }

      return names;
   }

   public static boolean isPlatformClass(JavaClass var0) {
      if (var0.isBootstrap()) {
         return true;
      } else {
         String var1 = var0.getName();
         if (var1.startsWith("[")) {
            int var2 = var1.lastIndexOf(91);
            if (var2 != -1) {
               if (var1.charAt(var2 + 1) != 'L') {
                  return true;
               }

               var1 = var1.substring(var2 + 2);
            }
         }

         String[] var4 = getNames();

         for(int var3 = 0; var3 < var4.length; ++var3) {
            if (var1.startsWith(var4[var3])) {
               return true;
            }
         }

         return false;
      }
   }
}
