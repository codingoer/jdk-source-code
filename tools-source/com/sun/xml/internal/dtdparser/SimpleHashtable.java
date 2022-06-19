package com.sun.xml.internal.dtdparser;

import java.util.Enumeration;

final class SimpleHashtable implements Enumeration {
   private Entry[] table;
   private Entry current;
   private int currentBucket;
   private int count;
   private int threshold;
   private static final float loadFactor = 0.75F;

   public SimpleHashtable(int initialCapacity) {
      this.current = null;
      this.currentBucket = 0;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      } else {
         if (initialCapacity == 0) {
            initialCapacity = 1;
         }

         this.table = new Entry[initialCapacity];
         this.threshold = (int)((float)initialCapacity * 0.75F);
      }
   }

   public SimpleHashtable() {
      this(11);
   }

   public void clear() {
      this.count = 0;
      this.currentBucket = 0;
      this.current = null;

      for(int i = 0; i < this.table.length; ++i) {
         this.table[i] = null;
      }

   }

   public int size() {
      return this.count;
   }

   public Enumeration keys() {
      this.currentBucket = 0;
      this.current = null;
      return this;
   }

   public boolean hasMoreElements() {
      if (this.current != null) {
         return true;
      } else {
         do {
            if (this.currentBucket >= this.table.length) {
               return false;
            }

            this.current = this.table[this.currentBucket++];
         } while(this.current == null);

         return true;
      }
   }

   public Object nextElement() {
      if (this.current == null) {
         throw new IllegalStateException();
      } else {
         Object retval = this.current.key;
         this.current = this.current.next;
         return retval;
      }
   }

   public Object get(String key) {
      Entry[] tab = this.table;
      int hash = key.hashCode();
      int index = (hash & Integer.MAX_VALUE) % tab.length;

      for(Entry e = tab[index]; e != null; e = e.next) {
         if (e.hash == hash && e.key == key) {
            return e.value;
         }
      }

      return null;
   }

   public Object getNonInterned(String key) {
      Entry[] tab = this.table;
      int hash = key.hashCode();
      int index = (hash & Integer.MAX_VALUE) % tab.length;

      for(Entry e = tab[index]; e != null; e = e.next) {
         if (e.hash == hash && e.key.equals(key)) {
            return e.value;
         }
      }

      return null;
   }

   private void rehash() {
      int oldCapacity = this.table.length;
      Entry[] oldMap = this.table;
      int newCapacity = oldCapacity * 2 + 1;
      Entry[] newMap = new Entry[newCapacity];
      this.threshold = (int)((float)newCapacity * 0.75F);
      this.table = newMap;
      int i = oldCapacity;

      Entry e;
      int index;
      while(i-- > 0) {
         for(Entry old = oldMap[i]; old != null; newMap[index] = e) {
            e = old;
            old = old.next;
            index = (e.hash & Integer.MAX_VALUE) % newCapacity;
            e.next = newMap[index];
         }
      }

   }

   public Object put(Object key, Object value) {
      if (value == null) {
         throw new NullPointerException();
      } else {
         Entry[] tab = this.table;
         int hash = key.hashCode();
         int index = (hash & Integer.MAX_VALUE) % tab.length;

         Entry e;
         for(e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
               Object old = e.value;
               e.value = value;
               return old;
            }
         }

         if (this.count >= this.threshold) {
            this.rehash();
            tab = this.table;
            index = (hash & Integer.MAX_VALUE) % tab.length;
         }

         e = new Entry(hash, key, value, tab[index]);
         tab[index] = e;
         ++this.count;
         return null;
      }
   }

   private static class Entry {
      int hash;
      Object key;
      Object value;
      Entry next;

      protected Entry(int hash, Object key, Object value, Entry next) {
         this.hash = hash;
         this.key = key;
         this.value = value;
         this.next = next;
      }
   }
}
