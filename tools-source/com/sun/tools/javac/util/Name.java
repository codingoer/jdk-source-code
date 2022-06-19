package com.sun.tools.javac.util;

public abstract class Name implements javax.lang.model.element.Name {
   public final Table table;

   protected Name(Table var1) {
      this.table = var1;
   }

   public boolean contentEquals(CharSequence var1) {
      return this.toString().equals(var1.toString());
   }

   public int length() {
      return this.toString().length();
   }

   public char charAt(int var1) {
      return this.toString().charAt(var1);
   }

   public CharSequence subSequence(int var1, int var2) {
      return this.toString().subSequence(var1, var2);
   }

   public Name append(Name var1) {
      int var2 = this.getByteLength();
      byte[] var3 = new byte[var2 + var1.getByteLength()];
      this.getBytes(var3, 0);
      var1.getBytes(var3, var2);
      return this.table.fromUtf(var3, 0, var3.length);
   }

   public Name append(char var1, Name var2) {
      int var3 = this.getByteLength();
      byte[] var4 = new byte[var3 + 1 + var2.getByteLength()];
      this.getBytes(var4, 0);
      var4[var3] = (byte)var1;
      var2.getBytes(var4, var3 + 1);
      return this.table.fromUtf(var4, 0, var4.length);
   }

   public int compareTo(Name var1) {
      return var1.getIndex() - this.getIndex();
   }

   public boolean isEmpty() {
      return this.getByteLength() == 0;
   }

   public int lastIndexOf(byte var1) {
      byte[] var2 = this.getByteArray();
      int var3 = this.getByteOffset();

      int var4;
      for(var4 = this.getByteLength() - 1; var4 >= 0 && var2[var3 + var4] != var1; --var4) {
      }

      return var4;
   }

   public boolean startsWith(Name var1) {
      byte[] var2 = this.getByteArray();
      int var3 = this.getByteOffset();
      int var4 = this.getByteLength();
      byte[] var5 = var1.getByteArray();
      int var6 = var1.getByteOffset();
      int var7 = var1.getByteLength();

      int var8;
      for(var8 = 0; var8 < var7 && var8 < var4 && var2[var3 + var8] == var5[var6 + var8]; ++var8) {
      }

      return var8 == var7;
   }

   public Name subName(int var1, int var2) {
      if (var2 < var1) {
         var2 = var1;
      }

      return this.table.fromUtf(this.getByteArray(), this.getByteOffset() + var1, var2 - var1);
   }

   public String toString() {
      return Convert.utf2string(this.getByteArray(), this.getByteOffset(), this.getByteLength());
   }

   public byte[] toUtf() {
      byte[] var1 = new byte[this.getByteLength()];
      this.getBytes(var1, 0);
      return var1;
   }

   public abstract int getIndex();

   public abstract int getByteLength();

   public abstract byte getByteAt(int var1);

   public void getBytes(byte[] var1, int var2) {
      System.arraycopy(this.getByteArray(), this.getByteOffset(), var1, var2, this.getByteLength());
   }

   public abstract byte[] getByteArray();

   public abstract int getByteOffset();

   public abstract static class Table {
      public final Names names;

      Table(Names var1) {
         this.names = var1;
      }

      public abstract Name fromChars(char[] var1, int var2, int var3);

      public Name fromString(String var1) {
         char[] var2 = var1.toCharArray();
         return this.fromChars(var2, 0, var2.length);
      }

      public Name fromUtf(byte[] var1) {
         return this.fromUtf(var1, 0, var1.length);
      }

      public abstract Name fromUtf(byte[] var1, int var2, int var3);

      public abstract void dispose();

      protected static int hashValue(byte[] var0, int var1, int var2) {
         int var3 = 0;
         int var4 = var1;

         for(int var5 = 0; var5 < var2; ++var5) {
            var3 = (var3 << 5) - var3 + var0[var4++];
         }

         return var3;
      }

      protected static boolean equals(byte[] var0, int var1, byte[] var2, int var3, int var4) {
         int var5;
         for(var5 = 0; var5 < var4 && var0[var1 + var5] == var2[var3 + var5]; ++var5) {
         }

         return var5 == var4;
      }
   }
}
