package com.sun.tools.javadoc;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import com.sun.tools.javac.file.Locations;
import com.sun.tools.javac.util.ClientCodeException;
import com.sun.tools.javac.util.List;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import javax.tools.JavaFileManager;
import javax.tools.DocumentationTool.Location;

public class DocletInvoker {
   private final Class docletClass;
   private final String docletClassName;
   private final ClassLoader appClassLoader;
   private final Messager messager;
   private final boolean apiMode;

   private String appendPath(String var1, String var2) {
      if (var1 != null && var1.length() != 0) {
         return var2 != null && var2.length() != 0 ? var1 + File.pathSeparator + var2 : var1;
      } else {
         return var2 == null ? "." : var2;
      }
   }

   public DocletInvoker(Messager var1, Class var2, boolean var3) {
      this.messager = var1;
      this.docletClass = var2;
      this.docletClassName = var2.getName();
      this.appClassLoader = null;
      this.apiMode = var3;
   }

   public DocletInvoker(Messager var1, JavaFileManager var2, String var3, String var4, ClassLoader var5, boolean var6) {
      this.messager = var1;
      this.docletClassName = var3;
      this.apiMode = var6;
      if (var2 != null && var2.hasLocation(Location.DOCLET_PATH)) {
         this.appClassLoader = var2.getClassLoader(Location.DOCLET_PATH);
      } else {
         String var7 = null;
         var7 = this.appendPath(System.getProperty("env.class.path"), var7);
         var7 = this.appendPath(System.getProperty("java.class.path"), var7);
         var7 = this.appendPath(var4, var7);
         URL[] var8 = Locations.pathToURLs(var7);
         if (var5 == null) {
            this.appClassLoader = new URLClassLoader(var8, this.getDelegationClassLoader(var3));
         } else {
            this.appClassLoader = new URLClassLoader(var8, var5);
         }
      }

      Class var10 = null;

      try {
         var10 = this.appClassLoader.loadClass(var3);
      } catch (ClassNotFoundException var9) {
         var1.error(Messager.NOPOS, "main.doclet_class_not_found", var3);
         var1.exit();
      }

      this.docletClass = var10;
   }

   private ClassLoader getDelegationClassLoader(String var1) {
      ClassLoader var2 = Thread.currentThread().getContextClassLoader();
      ClassLoader var3 = ClassLoader.getSystemClassLoader();
      if (var3 == null) {
         return var2;
      } else if (var2 == null) {
         return var3;
      } else {
         try {
            var3.loadClass(var1);

            try {
               var2.loadClass(var1);
            } catch (ClassNotFoundException var7) {
               return var3;
            }
         } catch (ClassNotFoundException var8) {
         }

         try {
            if (this.getClass() == var3.loadClass(this.getClass().getName())) {
               try {
                  if (this.getClass() != var2.loadClass(this.getClass().getName())) {
                     return var3;
                  }
               } catch (ClassNotFoundException var5) {
                  return var3;
               }
            }
         } catch (ClassNotFoundException var6) {
         }

         return var2;
      }
   }

   public boolean start(RootDoc var1) {
      String var3 = "start";
      Class[] var4 = new Class[]{RootDoc.class};
      Object[] var5 = new Object[]{var1};

      Object var2;
      try {
         var2 = this.invoke(var3, (Object)null, var4, var5);
      } catch (DocletInvokeException var7) {
         return false;
      }

      if (var2 instanceof Boolean) {
         return (Boolean)var2;
      } else {
         this.messager.error(Messager.NOPOS, "main.must_return_boolean", this.docletClassName, var3);
         return false;
      }
   }

   public int optionLength(String var1) {
      String var3 = "optionLength";
      Class[] var4 = new Class[]{String.class};
      Object[] var5 = new Object[]{var1};

      Object var2;
      try {
         var2 = this.invoke(var3, new Integer(0), var4, var5);
      } catch (DocletInvokeException var7) {
         return -1;
      }

      if (var2 instanceof Integer) {
         return (Integer)var2;
      } else {
         this.messager.error(Messager.NOPOS, "main.must_return_int", this.docletClassName, var3);
         return -1;
      }
   }

   public boolean validOptions(List var1) {
      String[][] var3 = (String[][])var1.toArray(new String[var1.length()][]);
      String var4 = "validOptions";
      Messager var5 = this.messager;
      Class[] var6 = new Class[]{String[][].class, DocErrorReporter.class};
      Object[] var7 = new Object[]{var3, var5};

      Object var2;
      try {
         var2 = this.invoke(var4, Boolean.TRUE, var6, var7);
      } catch (DocletInvokeException var9) {
         return false;
      }

      if (var2 instanceof Boolean) {
         return (Boolean)var2;
      } else {
         this.messager.error(Messager.NOPOS, "main.must_return_boolean", this.docletClassName, var4);
         return false;
      }
   }

   public LanguageVersion languageVersion() {
      try {
         String var2 = "languageVersion";
         Class[] var3 = new Class[0];
         Object[] var4 = new Object[0];

         Object var1;
         try {
            var1 = this.invoke(var2, LanguageVersion.JAVA_1_1, var3, var4);
         } catch (DocletInvokeException var6) {
            return LanguageVersion.JAVA_1_1;
         }

         if (var1 instanceof LanguageVersion) {
            return (LanguageVersion)var1;
         } else {
            this.messager.error(Messager.NOPOS, "main.must_return_languageversion", this.docletClassName, var2);
            return LanguageVersion.JAVA_1_1;
         }
      } catch (NoClassDefFoundError var7) {
         return null;
      }
   }

   private Object invoke(String var1, Object var2, Class[] var3, Object[] var4) throws DocletInvokeException {
      Method var5;
      try {
         var5 = this.docletClass.getMethod(var1, var3);
      } catch (NoSuchMethodException var17) {
         if (var2 == null) {
            this.messager.error(Messager.NOPOS, "main.doclet_method_not_found", this.docletClassName, var1);
            throw new DocletInvokeException();
         }

         return var2;
      } catch (SecurityException var18) {
         this.messager.error(Messager.NOPOS, "main.doclet_method_not_accessible", this.docletClassName, var1);
         throw new DocletInvokeException();
      }

      if (!Modifier.isStatic(var5.getModifiers())) {
         this.messager.error(Messager.NOPOS, "main.doclet_method_must_be_static", this.docletClassName, var1);
         throw new DocletInvokeException();
      } else {
         ClassLoader var6 = Thread.currentThread().getContextClassLoader();

         Object var7;
         try {
            if (this.appClassLoader != null) {
               Thread.currentThread().setContextClassLoader(this.appClassLoader);
            }

            var7 = var5.invoke((Object)null, var4);
         } catch (IllegalArgumentException var19) {
            this.messager.error(Messager.NOPOS, "main.internal_error_exception_thrown", this.docletClassName, var1, var19.toString());
            throw new DocletInvokeException();
         } catch (IllegalAccessException var20) {
            this.messager.error(Messager.NOPOS, "main.doclet_method_not_accessible", this.docletClassName, var1);
            throw new DocletInvokeException();
         } catch (NullPointerException var21) {
            this.messager.error(Messager.NOPOS, "main.internal_error_exception_thrown", this.docletClassName, var1, var21.toString());
            throw new DocletInvokeException();
         } catch (InvocationTargetException var22) {
            Throwable var8 = var22.getTargetException();
            if (this.apiMode) {
               throw new ClientCodeException(var8);
            }

            if (var8 instanceof OutOfMemoryError) {
               this.messager.error(Messager.NOPOS, "main.out.of.memory");
            } else {
               this.messager.error(Messager.NOPOS, "main.exception_thrown", this.docletClassName, var1, var22.toString());
               var22.getTargetException().printStackTrace();
            }

            throw new DocletInvokeException();
         } finally {
            Thread.currentThread().setContextClassLoader(var6);
         }

         return var7;
      }
   }

   private static class DocletInvokeException extends Exception {
      private static final long serialVersionUID = 0L;

      private DocletInvokeException() {
      }

      // $FF: synthetic method
      DocletInvokeException(Object var1) {
         this();
      }
   }
}
