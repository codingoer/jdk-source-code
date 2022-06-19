package com.sun.tools.internal.ws.processor.generator;

import com.sun.tools.internal.ws.wscompile.Options;

public class GeneratorUtil {
   public static boolean classExists(Options options, String className) {
      try {
         getLoadableClassName(className, options.getClassLoader());
         return true;
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }

   private static String getLoadableClassName(String className, ClassLoader classLoader) throws ClassNotFoundException {
      try {
         Class.forName(className, true, classLoader);
         return className;
      } catch (ClassNotFoundException var5) {
         int idx = className.lastIndexOf(GeneratorConstants.DOTC.getValue());
         if (idx > -1) {
            String tmp = className.substring(0, idx) + GeneratorConstants.SIG_INNERCLASS.getValue();
            tmp = tmp + className.substring(idx + 1);
            return getLoadableClassName(tmp, classLoader);
         } else {
            throw var5;
         }
      }
   }
}
