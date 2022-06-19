package com.sun.tools.javac.util;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListBuffer extends AbstractQueue {
   private List elems;
   private List last;
   private int count;
   private boolean shared;

   public static ListBuffer of(Object var0) {
      ListBuffer var1 = new ListBuffer();
      var1.add(var0);
      return var1;
   }

   public ListBuffer() {
      this.clear();
   }

   public final void clear() {
      this.elems = List.nil();
      this.last = null;
      this.count = 0;
      this.shared = false;
   }

   public int length() {
      return this.count;
   }

   public int size() {
      return this.count;
   }

   public boolean isEmpty() {
      return this.count == 0;
   }

   public boolean nonEmpty() {
      return this.count != 0;
   }

   private void copy() {
      if (this.elems.nonEmpty()) {
         List var1 = this.elems;

         for(this.elems = this.last = List.of(var1.head); (var1 = var1.tail).nonEmpty(); this.last = this.last.tail) {
            this.last.tail = List.of(var1.head);
         }
      }

   }

   public ListBuffer prepend(Object var1) {
      this.elems = this.elems.prepend(var1);
      if (this.last == null) {
         this.last = this.elems;
      }

      ++this.count;
      return this;
   }

   public ListBuffer append(Object var1) {
      var1.getClass();
      if (this.shared) {
         this.copy();
      }

      List var2 = List.of(var1);
      if (this.last != null) {
         this.last.tail = var2;
         this.last = var2;
      } else {
         this.elems = this.last = var2;
      }

      ++this.count;
      return this;
   }

   public ListBuffer appendList(List var1) {
      while(var1.nonEmpty()) {
         this.append(var1.head);
         var1 = var1.tail;
      }

      return this;
   }

   public ListBuffer appendList(ListBuffer var1) {
      return this.appendList(var1.toList());
   }

   public ListBuffer appendArray(Object[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         this.append(var1[var2]);
      }

      return this;
   }

   public List toList() {
      this.shared = true;
      return this.elems;
   }

   public boolean contains(Object var1) {
      return this.elems.contains(var1);
   }

   public Object[] toArray(Object[] var1) {
      return this.elems.toArray(var1);
   }

   public Object[] toArray() {
      return this.toArray(new Object[this.size()]);
   }

   public Object first() {
      return this.elems.head;
   }

   public Object next() {
      Object var1 = this.elems.head;
      if (!this.elems.isEmpty()) {
         this.elems = this.elems.tail;
         if (this.elems.isEmpty()) {
            this.last = null;
         }

         --this.count;
      }

      return var1;
   }

   public Iterator iterator() {
      return new Iterator() {
         List elems;

         {
            this.elems = ListBuffer.this.elems;
         }

         public boolean hasNext() {
            return !this.elems.isEmpty();
         }

         public Object next() {
            if (this.elems.isEmpty()) {
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

   public boolean add(Object var1) {
      this.append(var1);
      return true;
   }

   public boolean remove(Object var1) {
      throw new UnsupportedOperationException();
   }

   public boolean containsAll(Collection var1) {
      Iterator var2 = var1.iterator();

      Object var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = var2.next();
      } while(this.contains(var3));

      return false;
   }

   public boolean addAll(Collection var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         this.append(var3);
      }

      return true;
   }

   public boolean removeAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection var1) {
      throw new UnsupportedOperationException();
   }

   public boolean offer(Object var1) {
      this.append(var1);
      return true;
   }

   public Object poll() {
      return this.next();
   }

   public Object peek() {
      return this.first();
   }

   public Object last() {
      return this.last != null ? this.last.head : null;
   }
}
