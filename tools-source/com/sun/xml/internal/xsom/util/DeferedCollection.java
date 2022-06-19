package com.sun.xml.internal.xsom.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DeferedCollection implements Collection {
   private final Iterator result;
   private final List archive = new ArrayList();

   public DeferedCollection(Iterator result) {
      this.result = result;
   }

   public boolean isEmpty() {
      if (this.archive.isEmpty()) {
         this.fetch();
      }

      return this.archive.isEmpty();
   }

   public int size() {
      this.fetchAll();
      return this.archive.size();
   }

   public boolean contains(Object o) {
      if (this.archive.contains(o)) {
         return true;
      } else {
         Object value;
         do {
            if (!this.result.hasNext()) {
               return false;
            }

            value = this.result.next();
            this.archive.add(value);
         } while(!value.equals(o));

         return true;
      }
   }

   public boolean containsAll(Collection c) {
      Iterator var2 = c.iterator();

      Object o;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         o = var2.next();
      } while(this.contains(o));

      return false;
   }

   public Iterator iterator() {
      return new Iterator() {
         int idx = 0;

         public boolean hasNext() {
            return this.idx < DeferedCollection.this.archive.size() ? true : DeferedCollection.this.result.hasNext();
         }

         public Object next() {
            if (this.idx == DeferedCollection.this.archive.size()) {
               DeferedCollection.this.fetch();
            }

            if (this.idx == DeferedCollection.this.archive.size()) {
               throw new NoSuchElementException();
            } else {
               return DeferedCollection.this.archive.get(this.idx++);
            }
         }

         public void remove() {
         }
      };
   }

   public Object[] toArray() {
      this.fetchAll();
      return this.archive.toArray();
   }

   public Object[] toArray(Object[] a) {
      this.fetchAll();
      return this.archive.toArray(a);
   }

   private void fetchAll() {
      while(this.result.hasNext()) {
         this.archive.add(this.result.next());
      }

   }

   private void fetch() {
      if (this.result.hasNext()) {
         this.archive.add(this.result.next());
      }

   }

   public boolean add(Object o) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(Collection c) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection c) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection c) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }
}
