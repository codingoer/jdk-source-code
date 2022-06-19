package com.sun.tools.javac.util;

import java.lang.ref.WeakReference;

public class UnsharedNameTable extends Name.Table {
   private HashEntry[] hashes;
   private int hashMask;
   public int index;

   public static Name.Table create(Names var0) {
      return new UnsharedNameTable(var0);
   }

   public UnsharedNameTable(Names var1, int var2) {
      super(var1);
      this.hashes = null;
      this.hashMask = var2 - 1;
      this.hashes = new HashEntry[var2];
   }

   public UnsharedNameTable(Names var1) {
      this(var1, 32768);
   }

   public Name fromChars(char[] var1, int var2, int var3) {
      byte[] var4 = new byte[var3 * 3];
      int var5 = Convert.chars2utf(var1, var2, var4, 0, var3);
      return this.fromUtf(var4, 0, var5);
   }

   public Name fromUtf(byte[] var1, int var2, int var3) {
      int var4 = hashValue(var1, var2, var3) & this.hashMask;
      HashEntry var5 = this.hashes[var4];
      NameImpl var6 = null;
      HashEntry var7 = null;

      for(HashEntry var8 = var5; var5 != null && var5 != null; var5 = var5.next) {
         var6 = (NameImpl)var5.get();
         if (var6 == null) {
            if (var8 == var5) {
               this.hashes[var4] = var8 = var5.next;
            } else {
               Assert.checkNonNull(var7, "previousNonNullTableEntry cannot be null here.");
               var7.next = var5.next;
            }
         } else {
            if (var6.getByteLength() == var3 && equals(var6.bytes, 0, var1, var2, var3)) {
               return var6;
            }

            var7 = var5;
         }
      }

      byte[] var9 = new byte[var3];
      System.arraycopy(var1, var2, var9, 0, var3);
      var6 = new NameImpl(this, var9, this.index++);
      HashEntry var10 = new HashEntry(var6);
      if (var7 == null) {
         this.hashes[var4] = var10;
      } else {
         Assert.checkNull(var7.next, (String)"previousNonNullTableEntry.next must be null.");
         var7.next = var10;
      }

      return var6;
   }

   public void dispose() {
      this.hashes = null;
   }

   static class NameImpl extends Name {
      final byte[] bytes;
      final int index;

      NameImpl(UnsharedNameTable var1, byte[] var2, int var3) {
         super(var1);
         this.bytes = var2;
         this.index = var3;
      }

      public int getIndex() {
         return this.index;
      }

      public int getByteLength() {
         return this.bytes.length;
      }

      public byte getByteAt(int var1) {
         return this.bytes[var1];
      }

      public byte[] getByteArray() {
         return this.bytes;
      }

      public int getByteOffset() {
         return 0;
      }
   }

   static class HashEntry extends WeakReference {
      HashEntry next;

      HashEntry(NameImpl var1) {
         super(var1);
      }
   }
}
