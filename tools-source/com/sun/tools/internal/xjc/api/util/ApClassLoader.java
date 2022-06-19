package com.sun.tools.internal.xjc.api.util;

import com.sun.istack.internal.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public final class ApClassLoader extends URLClassLoader {
   private final String[] packagePrefixes;

   public ApClassLoader(@Nullable ClassLoader parent, String[] packagePrefixes) throws ToolsJarNotFoundException {
      super(getToolsJar(parent), parent);
      if (this.getURLs().length == 0) {
         this.packagePrefixes = new String[0];
      } else {
         this.packagePrefixes = packagePrefixes;
      }

   }

   public Class loadClass(String className) throws ClassNotFoundException {
      String[] var2 = this.packagePrefixes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String prefix = var2[var4];
         if (className.startsWith(prefix)) {
            return this.findClass(className);
         }
      }

      return super.loadClass(className);
   }

   protected Class findClass(String name) throws ClassNotFoundException {
      StringBuilder sb = new StringBuilder(name.length() + 6);
      sb.append(name.replace('.', '/')).append(".class");
      InputStream is = this.getResourceAsStream(sb.toString());
      if (is == null) {
         throw new ClassNotFoundException("Class not found" + sb);
      } else {
         Class var19;
         try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            int len;
            while((len = is.read(buf)) >= 0) {
               baos.write(buf, 0, len);
            }

            buf = baos.toByteArray();
            int i = name.lastIndexOf(46);
            if (i != -1) {
               String pkgname = name.substring(0, i);
               Package pkg = this.getPackage(pkgname);
               if (pkg == null) {
                  this.definePackage(pkgname, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
               }
            }

            var19 = this.defineClass(name, buf, 0, buf.length);
         } catch (IOException var17) {
            throw new ClassNotFoundException(name, var17);
         } finally {
            try {
               is.close();
            } catch (IOException var16) {
            }

         }

         return var19;
      }
   }

   private static URL[] getToolsJar(@Nullable ClassLoader parent) throws ToolsJarNotFoundException {
      try {
         Class.forName("com.sun.tools.javac.Main", false, parent);
         return new URL[0];
      } catch (ClassNotFoundException var5) {
         File jreHome = new File(System.getProperty("java.home"));
         File toolsJar = new File(jreHome.getParent(), "lib/tools.jar");
         if (!toolsJar.exists()) {
            throw new ToolsJarNotFoundException(toolsJar);
         } else {
            try {
               return new URL[]{toolsJar.toURL()};
            } catch (MalformedURLException var4) {
               throw new AssertionError(var4);
            }
         }
      }
   }
}
