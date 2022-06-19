package com.sun.tools.internal.xjc;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class XJCFacade {
   private static final String JDK6_REQUIRED = "XJC requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads";

   public static void main(String[] args) throws Throwable {
      String v = "2.0";

      for(int i = 0; i < args.length; ++i) {
         if (args[i].equals("-source") && i + 1 < args.length) {
            v = parseVersion(args[i + 1]);
         }
      }

      ClassLoader oldContextCl = SecureLoader.getContextClassLoader();
      boolean var17 = false;

      ClassLoader cl;
      Class clUtil;
      Method release;
      label240: {
         try {
            var17 = true;
            cl = ClassLoaderBuilder.createProtectiveClassLoader(SecureLoader.getClassClassLoader(XJCFacade.class), v);
            SecureLoader.setContextClassLoader(cl);
            clUtil = cl.loadClass("com.sun.tools.internal.xjc.Driver");
            release = clUtil.getDeclaredMethod("main", String[].class);

            try {
               release.invoke((Object)null, args);
               var17 = false;
            } catch (InvocationTargetException var21) {
               if (var21.getTargetException() != null) {
                  throw var21.getTargetException();
               }

               var17 = false;
            }
            break label240;
         } catch (UnsupportedClassVersionError var22) {
            System.err.println("XJC requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
            var17 = false;
         } finally {
            if (var17) {
               ClassLoader cl = SecureLoader.getContextClassLoader();
               SecureLoader.setContextClassLoader(oldContextCl);

               for(; cl != null && !oldContextCl.equals(cl); cl = SecureLoader.getParentClassLoader(cl)) {
                  if (cl instanceof Closeable) {
                     ((Closeable)cl).close();
                  } else if (cl instanceof URLClassLoader) {
                     try {
                        Class clUtil = oldContextCl.loadClass("sun.misc.ClassLoaderUtil");
                        Method release = clUtil.getDeclaredMethod("releaseLoader", URLClassLoader.class);
                        release.invoke((Object)null, cl);
                     } catch (ClassNotFoundException var18) {
                        System.err.println("XJC requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
                     }
                  }
               }

            }
         }

         cl = SecureLoader.getContextClassLoader();
         SecureLoader.setContextClassLoader(oldContextCl);

         for(; cl != null && !oldContextCl.equals(cl); cl = SecureLoader.getParentClassLoader(cl)) {
            if (cl instanceof Closeable) {
               ((Closeable)cl).close();
            } else if (cl instanceof URLClassLoader) {
               try {
                  clUtil = oldContextCl.loadClass("sun.misc.ClassLoaderUtil");
                  release = clUtil.getDeclaredMethod("releaseLoader", URLClassLoader.class);
                  release.invoke((Object)null, cl);
               } catch (ClassNotFoundException var19) {
                  System.err.println("XJC requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
               }
            }
         }

         return;
      }

      cl = SecureLoader.getContextClassLoader();
      SecureLoader.setContextClassLoader(oldContextCl);

      for(; cl != null && !oldContextCl.equals(cl); cl = SecureLoader.getParentClassLoader(cl)) {
         if (cl instanceof Closeable) {
            ((Closeable)cl).close();
         } else if (cl instanceof URLClassLoader) {
            try {
               clUtil = oldContextCl.loadClass("sun.misc.ClassLoaderUtil");
               release = clUtil.getDeclaredMethod("releaseLoader", URLClassLoader.class);
               release.invoke((Object)null, cl);
            } catch (ClassNotFoundException var20) {
               System.err.println("XJC requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
            }
         }
      }

   }

   public static String parseVersion(String version) {
      return "2.0";
   }
}
