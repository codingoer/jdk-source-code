package com.sun.tools.javac.util;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class List extends AbstractCollection implements java.util.List {
   public Object head;
   public List tail;
   private static final List EMPTY_LIST = new List((Object)null, (List)null) {
      public List setTail(List var1) {
         throw new UnsupportedOperationException();
      }

      public boolean isEmpty() {
         return true;
      }
   };
   private static final Iterator EMPTYITERATOR = new Iterator() {
      public boolean hasNext() {
         return false;
      }

      public Object next() {
         throw new NoSuchElementException();
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   };

   List(Object var1, List var2) {
      this.tail = var2;
      this.head = var1;
   }

   public static List nil() {
      return EMPTY_LIST;
   }

   public static List filter(List var0, Object var1) {
      Assert.checkNonNull(var1);
      List var2 = nil();
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (var4 != null && !var4.equals(var1)) {
            var2 = var2.prepend(var4);
         }
      }

      return var2.reverse();
   }

   public List intersect(List var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (var1.contains(var4)) {
            var2.append(var4);
         }
      }

      return var2.toList();
   }

   public List diff(List var1) {
      ListBuffer var2 = new ListBuffer();
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (!var1.contains(var4)) {
            var2.append(var4);
         }
      }

      return var2.toList();
   }

   public List take(int var1) {
      ListBuffer var2 = new ListBuffer();
      int var3 = 0;
      Iterator var4 = this.iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         if (var3++ == var1) {
            break;
         }

         var2.append(var5);
      }

      return var2.toList();
   }

   public static List of(Object var0) {
      return new List(var0, nil());
   }

   public static List of(Object var0, Object var1) {
      return new List(var0, of(var1));
   }

   public static List of(Object var0, Object var1, Object var2) {
      return new List(var0, of(var1, var2));
   }

   public static List of(Object var0, Object var1, Object var2, Object... var3) {
      return new List(var0, new List(var1, new List(var2, from(var3))));
   }

   public static List from(Object[] var0) {
      List var1 = nil();
      if (var0 != null) {
         for(int var2 = var0.length - 1; var2 >= 0; --var2) {
            var1 = new List(var0[var2], var1);
         }
      }

      return var1;
   }

   public static List from(Iterable var0) {
      ListBuffer var1 = new ListBuffer();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         var1.append(var3);
      }

      return var1.toList();
   }

   /** @deprecated */
   @Deprecated
   public static List fill(int var0, Object var1) {
      List var2 = nil();

      for(int var3 = 0; var3 < var0; ++var3) {
         var2 = new List(var1, var2);
      }

      return var2;
   }

   public boolean isEmpty() {
      return this.tail == null;
   }

   public boolean nonEmpty() {
      return this.tail != null;
   }

   public int length() {
      List var1 = this;

      int var2;
      for(var2 = 0; var1.tail != null; ++var2) {
         var1 = var1.tail;
      }

      return var2;
   }

   public int size() {
      return this.length();
   }

   public List setTail(List var1) {
      this.tail = var1;
      return var1;
   }

   public List prepend(Object var1) {
      return new List(var1, this);
   }

   public List prependList(List var1) {
      if (this.isEmpty()) {
         return var1;
      } else if (var1.isEmpty()) {
         return this;
      } else if (var1.tail.isEmpty()) {
         return this.prepend(var1.head);
      } else {
         List var2 = this;
         List var3 = var1.reverse();
         Assert.check(var3 != var1);

         while(var3.nonEmpty()) {
            List var4 = var3;
            var3 = var3.tail;
            var4.setTail(var2);
            var2 = var4;
         }

         return var2;
      }
   }

   public List reverse() {
      if (!this.isEmpty() && !this.tail.isEmpty()) {
         List var1 = nil();

         for(List var2 = this; var2.nonEmpty(); var2 = var2.tail) {
            var1 = new List(var2.head, var1);
         }

         return var1;
      } else {
         return this;
      }
   }

   public List append(Object var1) {
      return of(var1).prependList(this);
   }

   public List appendList(List var1) {
      return var1.prependList(this);
   }

   public List appendList(ListBuffer var1) {
      return this.appendList(var1.toList());
   }

   public Object[] toArray(Object[] var1) {
      int var2 = 0;
      List var3 = this;

      for(Object[] var4 = var1; var3.nonEmpty() && var2 < var1.length; ++var2) {
         var4[var2] = var3.head;
         var3 = var3.tail;
      }

      if (var3.isEmpty()) {
         if (var2 < var1.length) {
            var1[var2] = null;
         }

         return var1;
      } else {
         var1 = (Object[])((Object[])Array.newInstance(var1.getClass().getComponentType(), this.size()));
         return this.toArray(var1);
      }
   }

   public Object[] toArray() {
      return this.toArray(new Object[this.size()]);
   }

   public String toString(String var1) {
      if (this.isEmpty()) {
         return "";
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append(this.head);

         for(List var3 = this.tail; var3.nonEmpty(); var3 = var3.tail) {
            var2.append(var1);
            var2.append(var3.head);
         }

         return var2.toString();
      }
   }

   public String toString() {
      return this.toString(",");
   }

   public int hashCode() {
      List var1 = this;

      int var2;
      for(var2 = 1; var1.tail != null; var1 = var1.tail) {
         var2 = var2 * 31 + (var1.head == null ? 0 : var1.head.hashCode());
      }

      return var2;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof List) {
         return equals(this, (List)var1);
      } else if (!(var1 instanceof java.util.List)) {
         return false;
      } else {
         List var2 = this;

         Iterator var3;
         for(var3 = ((java.util.List)var1).iterator(); var2.tail != null && var3.hasNext(); var2 = var2.tail) {
            Object var4 = var3.next();
            if (var2.head == null) {
               if (var4 != null) {
                  return false;
               }
            } else if (!var2.head.equals(var4)) {
               return false;
            }
         }

         return var2.isEmpty() && !var3.hasNext();
      }
   }

   public static boolean equals(List var0, List var1) {
      while(var0.tail != null && var1.tail != null) {
         if (var0.head == null) {
            if (var1.head != null) {
               return false;
            }
         } else if (!var0.head.equals(var1.head)) {
            return false;
         }

         var0 = var0.tail;
         var1 = var1.tail;
      }

      return var0.tail == null && var1.tail == null;
   }

   public boolean contains(Object var1) {
      for(List var2 = this; var2.tail != null; var2 = var2.tail) {
         if (var1 == null) {
            if (var2.head == null) {
               return true;
            }
         } else if (var2.head.equals(var1)) {
            return true;
         }
      }

      return false;
   }

   public Object last() {
      Object var1 = null;

      for(List var2 = this; var2.tail != null; var2 = var2.tail) {
         var1 = var2.head;
      }

      return var1;
   }

   public static List convert(Class var0, List var1) {
      if (var1 == null) {
         return null;
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            var0.cast(var3);
         }

         return var1;
      }
   }

   private static Iterator emptyIterator() {
      return EMPTYITERATOR;
   }

   public Iterator iterator() {
      return this.tail == null ? emptyIterator() : new Iterator() {
         List elems = List.this;

         public boolean hasNext() {
            return this.elems.tail != null;
         }

         public Object next() {
            if (this.elems.tail == null) {
               throw new NoSuchElementException();
            } else {
               Object var1 = this.elems.head;
               this.elems = this.elems.tail;
               return var1;
            }
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public Object get(int var1) {
      if (var1 < 0) {
         throw new IndexOutOfBoundsException(String.valueOf(var1));
      } else {
         List var2 = this;

         for(int var3 = var1; var3-- > 0 && !var2.isEmpty(); var2 = var2.tail) {
         }

         if (var2.isEmpty()) {
            throw new IndexOutOfBoundsException("Index: " + var1 + ", Size: " + this.size());
         } else {
            return var2.head;
         }
      }
   }

   public boolean addAll(int var1, Collection var2) {
      if (var2.isEmpty()) {
         return false;
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public Object set(int var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   public void add(int var1, Object var2) {
      throw new UnsupportedOperationException();
   }

   public Object remove(int var1) {
      throw new UnsupportedOperationException();
   }

   public int indexOf(Object var1) {
      int var2 = 0;
      List var3 = this;

      while(true) {
         if (var3.tail == null) {
            return -1;
         }

         if (var3.head == null) {
            if (var1 == null) {
               break;
            }
         } else if (var3.head.equals(var1)) {
            break;
         }

         var3 = var3.tail;
         ++var2;
      }

      return var2;
   }

   public int lastIndexOf(Object var1) {
      int var2 = -1;
      int var3 = 0;

      for(List var4 = this; var4.tail != null; ++var3) {
         label18: {
            if (var4.head == null) {
               if (var1 != null) {
                  break label18;
               }
            } else if (!var4.head.equals(var1)) {
               break label18;
            }

            var2 = var3;
         }

         var4 = var4.tail;
      }

      return var2;
   }

   public ListIterator listIterator() {
      return Collections.unmodifiableList(new ArrayList(this)).listIterator();
   }

   public ListIterator listIterator(int var1) {
      return Collections.unmodifiableList(new ArrayList(this)).listIterator(var1);
   }

   public java.util.List subList(int var1, int var2) {
      if (var1 >= 0 && var2 <= this.size() && var1 <= var2) {
         ArrayList var3 = new ArrayList(var2 - var1);
         int var4 = 0;

         for(List var5 = this; var5.tail != null && var4 != var2; ++var4) {
            if (var4 >= var1) {
               var3.add(var5.head);
            }

            var5 = var5.tail;
         }

         return Collections.unmodifiableList(var3);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
