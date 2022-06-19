package com.sun.tools.javac.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ServiceConfigurationError;

public final class ServiceLoader implements Iterable {
   private static final String PREFIX = "META-INF/services/";
   private Class service;
   private ClassLoader loader;
   private LinkedHashMap providers = new LinkedHashMap();
   private LazyIterator lookupIterator;

   public void reload() {
      this.providers.clear();
      this.lookupIterator = new LazyIterator(this.service, this.loader);
   }

   private ServiceLoader(Class var1, ClassLoader var2) {
      this.service = (Class)Objects.requireNonNull(var1, "Service interface cannot be null");
      this.loader = var2 == null ? ClassLoader.getSystemClassLoader() : var2;
      this.reload();
   }

   private static void fail(Class var0, String var1, Throwable var2) throws ServiceConfigurationError {
      throw new ServiceConfigurationError(var0.getName() + ": " + var1, var2);
   }

   private static void fail(Class var0, String var1) throws ServiceConfigurationError {
      throw new ServiceConfigurationError(var0.getName() + ": " + var1);
   }

   private static void fail(Class var0, URL var1, int var2, String var3) throws ServiceConfigurationError {
      fail(var0, var1 + ":" + var2 + ": " + var3);
   }

   private int parseLine(Class var1, URL var2, BufferedReader var3, int var4, java.util.List var5) throws IOException, ServiceConfigurationError {
      String var6 = var3.readLine();
      if (var6 == null) {
         return -1;
      } else {
         int var7 = var6.indexOf(35);
         if (var7 >= 0) {
            var6 = var6.substring(0, var7);
         }

         var6 = var6.trim();
         int var8 = var6.length();
         if (var8 != 0) {
            if (var6.indexOf(32) >= 0 || var6.indexOf(9) >= 0) {
               fail(var1, var2, var4, "Illegal configuration-file syntax");
            }

            int var9 = var6.codePointAt(0);
            if (!Character.isJavaIdentifierStart(var9)) {
               fail(var1, var2, var4, "Illegal provider-class name: " + var6);
            }

            for(int var10 = Character.charCount(var9); var10 < var8; var10 += Character.charCount(var9)) {
               var9 = var6.codePointAt(var10);
               if (!Character.isJavaIdentifierPart(var9) && var9 != 46) {
                  fail(var1, var2, var4, "Illegal provider-class name: " + var6);
               }
            }

            if (!this.providers.containsKey(var6) && !var5.contains(var6)) {
               var5.add(var6);
            }
         }

         return var4 + 1;
      }
   }

   private Iterator parse(Class var1, URL var2) throws ServiceConfigurationError {
      InputStream var3 = null;
      BufferedReader var4 = null;
      ArrayList var5 = new ArrayList();

      try {
         URLConnection var6 = var2.openConnection();
         var6.setUseCaches(false);
         var3 = var6.getInputStream();
         var4 = new BufferedReader(new InputStreamReader(var3, "utf-8"));
         int var7 = 1;

         while(true) {
            if ((var7 = this.parseLine(var1, var2, var4, var7, var5)) >= 0) {
               continue;
            }
         }
      } catch (IOException var16) {
         fail(var1, "Error reading configuration file", var16);
      } finally {
         try {
            if (var4 != null) {
               var4.close();
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (IOException var15) {
            fail(var1, "Error closing configuration file", var15);
         }

      }

      return var5.iterator();
   }

   public Iterator iterator() {
      return new Iterator() {
         Iterator knownProviders;

         {
            this.knownProviders = ServiceLoader.this.providers.entrySet().iterator();
         }

         public boolean hasNext() {
            return this.knownProviders.hasNext() ? true : ServiceLoader.this.lookupIterator.hasNext();
         }

         public Object next() {
            return this.knownProviders.hasNext() ? ((Map.Entry)this.knownProviders.next()).getValue() : ServiceLoader.this.lookupIterator.next();
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public static ServiceLoader load(Class var0, ClassLoader var1) {
      return new ServiceLoader(var0, var1);
   }

   public static ServiceLoader load(Class var0) {
      ClassLoader var1 = Thread.currentThread().getContextClassLoader();
      return load(var0, var1);
   }

   public static ServiceLoader loadInstalled(Class var0) {
      ClassLoader var1 = ClassLoader.getSystemClassLoader();

      ClassLoader var2;
      for(var2 = null; var1 != null; var1 = var1.getParent()) {
         var2 = var1;
      }

      return load(var0, var2);
   }

   public String toString() {
      return "java.util.ServiceLoader[" + this.service.getName() + "]";
   }

   private class LazyIterator implements Iterator {
      Class service;
      ClassLoader loader;
      Enumeration configs;
      Iterator pending;
      String nextName;

      private LazyIterator(Class var2, ClassLoader var3) {
         this.configs = null;
         this.pending = null;
         this.nextName = null;
         this.service = var2;
         this.loader = var3;
      }

      public boolean hasNext() {
         if (this.nextName != null) {
            return true;
         } else {
            if (this.configs == null) {
               try {
                  String var1 = "META-INF/services/" + this.service.getName();
                  if (this.loader == null) {
                     this.configs = ClassLoader.getSystemResources(var1);
                  } else {
                     this.configs = this.loader.getResources(var1);
                  }
               } catch (IOException var2) {
                  ServiceLoader.fail(this.service, "Error locating configuration files", var2);
               }
            }

            while(this.pending == null || !this.pending.hasNext()) {
               if (!this.configs.hasMoreElements()) {
                  return false;
               }

               this.pending = ServiceLoader.this.parse(this.service, (URL)this.configs.nextElement());
            }

            this.nextName = (String)this.pending.next();
            return true;
         }
      }

      public Object next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            String var1 = this.nextName;
            this.nextName = null;
            Class var2 = null;

            try {
               var2 = Class.forName(var1, false, this.loader);
            } catch (ClassNotFoundException var5) {
               ServiceLoader.fail(this.service, "Provider " + var1 + " not found");
            }

            if (!this.service.isAssignableFrom(var2)) {
               ServiceLoader.fail(this.service, "Provider " + var1 + " not a subtype");
            }

            try {
               Object var3 = this.service.cast(var2.newInstance());
               ServiceLoader.this.providers.put(var1, var3);
               return var3;
            } catch (Throwable var4) {
               ServiceLoader.fail(this.service, "Provider " + var1 + " could not be instantiated: " + var4, var4);
               throw new Error();
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      // $FF: synthetic method
      LazyIterator(Class var2, ClassLoader var3, Object var4) {
         this(var2, var3);
      }
   }
}
