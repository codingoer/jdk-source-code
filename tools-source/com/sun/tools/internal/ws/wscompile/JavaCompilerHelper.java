package com.sun.tools.internal.ws.wscompile;

import com.sun.istack.internal.tools.ParallelWorldClassLoader;
import com.sun.tools.internal.ws.resources.JavacompilerMessages;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

class JavaCompilerHelper {
   private static final Class[] compileMethodSignature = new Class[]{String[].class, PrintWriter.class};

   static File getJarFile(Class clazz) {
      URL url = null;

      try {
         url = ParallelWorldClassLoader.toJarUrl(clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class"));
         return new File(url.toURI());
      } catch (ClassNotFoundException var3) {
         throw new Error(var3);
      } catch (MalformedURLException var4) {
         throw new Error(var4);
      } catch (URISyntaxException var5) {
         return new File(url.getPath());
      }
   }

   static boolean compile(String[] args, OutputStream out, ErrorReceiver receiver) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();

      try {
         Class comSunToolsJavacMainClass = cl.loadClass("com.sun.tools.javac.Main");

         try {
            Method compileMethod = comSunToolsJavacMainClass.getMethod("compile", compileMethodSignature);
            Object result = compileMethod.invoke((Object)null, args, new PrintWriter(out));
            return result instanceof Integer && (Integer)result == 0;
         } catch (NoSuchMethodException var7) {
            receiver.error((String)JavacompilerMessages.JAVACOMPILER_NOSUCHMETHOD_ERROR("getMethod(\"compile\", Class[])"), (Exception)var7);
         } catch (IllegalAccessException var8) {
            receiver.error((Exception)var8);
         } catch (InvocationTargetException var9) {
            receiver.error((Exception)var9);
         }
      } catch (ClassNotFoundException var10) {
         receiver.error((String)JavacompilerMessages.JAVACOMPILER_CLASSPATH_ERROR("com.sun.tools.javac.Main"), (Exception)var10);
      } catch (SecurityException var11) {
         receiver.error((Exception)var11);
      }

      return false;
   }
}
