package com.sun.tools.internal.jxc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SchemaGeneratorFacade {
   public static void main(String[] args) throws Throwable {
      try {
         ClassLoader cl = SecureLoader.getClassClassLoader(SchemaGeneratorFacade.class);
         if (cl == null) {
            cl = SecureLoader.getSystemClassLoader();
         }

         Class driver = cl.loadClass("com.sun.tools.internal.jxc.SchemaGenerator");
         Method mainMethod = driver.getDeclaredMethod("main", String[].class);

         try {
            mainMethod.invoke((Object)null, args);
         } catch (IllegalAccessException var5) {
            throw var5;
         } catch (InvocationTargetException var6) {
            if (var6.getTargetException() != null) {
               throw var6.getTargetException();
            }
         }
      } catch (UnsupportedClassVersionError var7) {
         System.err.println("schemagen requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
      }

   }
}
