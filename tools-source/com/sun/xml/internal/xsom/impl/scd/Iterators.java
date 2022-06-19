package com.sun.xml.internal.xsom.impl.scd;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class Iterators {
   private static final Iterator EMPTY;

   public static Iterator empty() {
      return EMPTY;
   }

   public static Iterator singleton(Object value) {
      return new Singleton(value);
   }

   static {
      EMPTY = Collections.EMPTY_LIST.iterator();
   }

   public static final class Array extends ReadOnly {
      private final Object[] items;
      private int index = 0;

      public Array(Object[] items) {
         this.items = items;
      }

      public boolean hasNext() {
         return this.index < this.items.length;
      }

      public Object next() {
         return this.items[this.index++];
      }
   }

   public static final class Union extends ReadOnly {
      private final Iterator first;
      private final Iterator second;

      public Union(Iterator first, Iterator second) {
         this.first = first;
         this.second = second;
      }

      public boolean hasNext() {
         return this.first.hasNext() || this.second.hasNext();
      }

      public Object next() {
         return this.first.hasNext() ? this.first.next() : this.second.next();
      }
   }

   static final class Unique extends Filter {
      private Set values = new HashSet();

      public Unique(Iterator core) {
         super(core);
      }

      protected boolean matches(Object value) {
         return this.values.add(value);
      }
   }

   public abstract static class Filter extends ReadOnly {
      private final Iterator core;
      private Object next;

      protected Filter(Iterator core) {
         this.core = core;
      }

      protected abstract boolean matches(Object var1);

      public boolean hasNext() {
         while(this.core.hasNext() && this.next == null) {
            this.next = this.core.next();
            if (!this.matches(this.next)) {
               this.next = null;
            }
         }

         return this.next != null;
      }

      public Object next() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            Object r = this.next;
            this.next = null;
            return r;
         }
      }
   }

   public abstract static class Map extends ReadOnly {
      private final Iterator core;
      private Iterator current;

      protected Map(Iterator core) {
         this.core = core;
      }

      public boolean hasNext() {
         while(this.current == null || !this.current.hasNext()) {
            if (!this.core.hasNext()) {
               return false;
            }

            this.current = this.apply(this.core.next());
         }

         return true;
      }

      public Object next() {
         return this.current.next();
      }

      protected abstract Iterator apply(Object var1);
   }

   public abstract static class Adapter extends ReadOnly {
      private final Iterator core;

      public Adapter(Iterator core) {
         this.core = core;
      }

      public boolean hasNext() {
         return this.core.hasNext();
      }

      public Object next() {
         return this.filter(this.core.next());
      }

      protected abstract Object filter(Object var1);
   }

   static final class Singleton extends ReadOnly {
      private Object next;

      Singleton(Object next) {
         this.next = next;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      public Object next() {
         Object r = this.next;
         this.next = null;
         return r;
      }
   }

   abstract static class ReadOnly implements Iterator {
      public final void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
