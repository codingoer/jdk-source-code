package com.sun.tools.javac.util;

import java.lang.ref.SoftReference;

public class SharedNameTable extends Name.Table {
   private static List freelist = List.nil();
   private NameImpl[] hashes;
   public byte[] bytes;
   private int hashMask;
   private int nc;

   public static synchronized SharedNameTable create(Names var0) {
      while(true) {
         if (freelist.nonEmpty()) {
            SharedNameTable var1 = (SharedNameTable)((SoftReference)freelist.head).get();
            freelist = freelist.tail;
            if (var1 == null) {
               continue;
            }

            return var1;
         }

         return new SharedNameTable(var0);
      }
   }

   private static synchronized void dispose(SharedNameTable var0) {
      freelist = freelist.prepend(new SoftReference(var0));
   }

   public SharedNameTable(Names var1, int var2, int var3) {
      super(var1);
      this.nc = 0;
      this.hashMask = var2 - 1;
      this.hashes = new NameImpl[var2];
      this.bytes = new byte[var3];
   }

   public SharedNameTable(Names var1) {
      this(var1, 32768, 131072);
   }

   public Name fromChars(char[] var1, int var2, int var3) {
      int var4 = this.nc;
      byte[] var5 = this.bytes = ArrayUtils.ensureCapacity(this.bytes, var4 + var3 * 3);
      int var6 = Convert.chars2utf(var1, var2, var5, var4, var3) - var4;
      int var7 = hashValue(var5, var4, var6) & this.hashMask;

      NameImpl var8;
      for(var8 = this.hashes[var7]; var8 != null && (var8.getByteLength() != var6 || !equals(var5, var8.index, var5, var4, var6)); var8 = var8.next) {
      }

      if (var8 == null) {
         var8 = new NameImpl(this);
         var8.index = var4;
         var8.length = var6;
         var8.next = this.hashes[var7];
         this.hashes[var7] = var8;
         this.nc = var4 + var6;
         if (var6 == 0) {
            ++this.nc;
         }
      }

      return var8;
   }

   public Name fromUtf(byte[] var1, int var2, int var3) {
      int var4 = hashValue(var1, var2, var3) & this.hashMask;
      NameImpl var5 = this.hashes[var4];

      byte[] var6;
      for(var6 = this.bytes; var5 != null && (var5.getByteLength() != var3 || !equals(var6, var5.index, var1, var2, var3)); var5 = var5.next) {
      }

      if (var5 == null) {
         int var7 = this.nc;
         var6 = this.bytes = ArrayUtils.ensureCapacity(var6, var7 + var3);
         System.arraycopy(var1, var2, var6, var7, var3);
         var5 = new NameImpl(this);
         var5.index = var7;
         var5.length = var3;
         var5.next = this.hashes[var4];
         this.hashes[var4] = var5;
         this.nc = var7 + var3;
         if (var3 == 0) {
            ++this.nc;
         }
      }

      return var5;
   }

   public void dispose() {
      dispose(this);
   }

   static class NameImpl extends Name {
      NameImpl next;
      int index;
      int length;

      NameImpl(SharedNameTable var1) {
         super(var1);
      }

      public int getIndex() {
         return this.index;
      }

      public int getByteLength() {
         return this.length;
      }

      public byte getByteAt(int var1) {
         return this.getByteArray()[this.index + var1];
      }

      public byte[] getByteArray() {
         return ((SharedNameTable)this.table).bytes;
      }

      public int getByteOffset() {
         return this.index;
      }

      public int hashCode() {
         return this.index;
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Name)) {
            return false;
         } else {
            return this.table == ((Name)var1).table && this.index == ((Name)var1).getIndex();
         }
      }
   }
}
