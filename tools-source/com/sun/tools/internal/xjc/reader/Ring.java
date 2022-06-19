package com.sun.tools.internal.xjc.reader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class Ring {
   private final Map components = new HashMap();
   private static final ThreadLocal instances = new ThreadLocal();

   private Ring() {
   }

   public static void add(Class clazz, Object instance) {
      assert !get().components.containsKey(clazz);

      get().components.put(clazz, instance);
   }

   public static void add(Object o) {
      add(o.getClass(), o);
   }

   public static Object get(Class key) {
      Object t = get().components.get(key);
      if (t == null) {
         try {
            Constructor c = key.getDeclaredConstructor();
            c.setAccessible(true);
            t = c.newInstance();
            if (!get().components.containsKey(key)) {
               add(key, t);
            }
         } catch (InstantiationException var3) {
            throw new Error(var3);
         } catch (IllegalAccessException var4) {
            throw new Error(var4);
         } catch (NoSuchMethodException var5) {
            throw new Error(var5);
         } catch (InvocationTargetException var6) {
            throw new Error(var6);
         }
      }

      assert t != null;

      return t;
   }

   public static Ring get() {
      return (Ring)instances.get();
   }

   public static Ring begin() {
      Ring r = null;
      synchronized(instances) {
         r = (Ring)instances.get();
         instances.set(new Ring());
         return r;
      }
   }

   public static void end(Ring old) {
      synchronized(instances) {
         instances.remove();
         instances.set(old);
      }
   }
}
