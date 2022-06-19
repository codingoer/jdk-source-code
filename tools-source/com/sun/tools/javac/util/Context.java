package com.sun.tools.javac.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Context {
   private Map ht = new HashMap();
   private Map ft = new HashMap();
   private Map kt = new HashMap();

   public void put(Key var1, Factory var2) {
      checkState(this.ht);
      Object var3 = this.ht.put(var1, var2);
      if (var3 != null) {
         throw new AssertionError("duplicate context value");
      } else {
         checkState(this.ft);
         this.ft.put(var1, var2);
      }
   }

   public void put(Key var1, Object var2) {
      if (var2 instanceof Factory) {
         throw new AssertionError("T extends Context.Factory");
      } else {
         checkState(this.ht);
         Object var3 = this.ht.put(var1, var2);
         if (var3 != null && !(var3 instanceof Factory) && var3 != var2 && var2 != null) {
            throw new AssertionError("duplicate context value");
         }
      }
   }

   public Object get(Key var1) {
      checkState(this.ht);
      Object var2 = this.ht.get(var1);
      if (var2 instanceof Factory) {
         Factory var3 = (Factory)var2;
         var2 = var3.make(this);
         if (var2 instanceof Factory) {
            throw new AssertionError("T extends Context.Factory");
         }

         Assert.check(this.ht.get(var1) == var2);
      }

      return uncheckedCast(var2);
   }

   public Context() {
   }

   public Context(Context var1) {
      this.kt.putAll(var1.kt);
      this.ft.putAll(var1.ft);
      this.ht.putAll(var1.ft);
   }

   private Key key(Class var1) {
      checkState(this.kt);
      Key var2 = (Key)uncheckedCast(this.kt.get(var1));
      if (var2 == null) {
         var2 = new Key();
         this.kt.put(var1, var2);
      }

      return var2;
   }

   public Object get(Class var1) {
      return this.get(this.key(var1));
   }

   public void put(Class var1, Object var2) {
      this.put(this.key(var1), var2);
   }

   public void put(Class var1, Factory var2) {
      this.put(this.key(var1), var2);
   }

   private static Object uncheckedCast(Object var0) {
      return var0;
   }

   public void dump() {
      Iterator var1 = this.ht.values().iterator();

      while(var1.hasNext()) {
         Object var2 = var1.next();
         System.err.println(var2 == null ? null : var2.getClass());
      }

   }

   public void clear() {
      this.ht = null;
      this.kt = null;
      this.ft = null;
   }

   private static void checkState(Map var0) {
      if (var0 == null) {
         throw new IllegalStateException();
      }
   }

   public interface Factory {
      Object make(Context var1);
   }

   public static class Key {
   }
}
