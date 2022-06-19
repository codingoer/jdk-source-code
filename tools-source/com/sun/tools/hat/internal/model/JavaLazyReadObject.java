package com.sun.tools.hat.internal.model;

import com.sun.tools.hat.internal.parser.ReadBuffer;
import java.io.IOException;

public abstract class JavaLazyReadObject extends JavaHeapObject {
   private final long offset;

   protected JavaLazyReadObject(long var1) {
      this.offset = var1;
   }

   public final int getSize() {
      return this.getValueLength() + this.getClazz().getMinimumObjectSize();
   }

   protected final long getOffset() {
      return this.offset;
   }

   protected final int getValueLength() {
      try {
         return this.readValueLength();
      } catch (IOException var2) {
         System.err.println("lazy read failed at offset " + this.offset);
         var2.printStackTrace();
         return 0;
      }
   }

   protected final byte[] getValue() {
      try {
         return this.readValue();
      } catch (IOException var2) {
         System.err.println("lazy read failed at offset " + this.offset);
         var2.printStackTrace();
         return Snapshot.EMPTY_BYTE_ARRAY;
      }
   }

   public final long getId() {
      try {
         ReadBuffer var1 = this.getClazz().getReadBuffer();
         int var2 = this.getClazz().getIdentifierSize();
         return var2 == 4 ? (long)var1.getInt(this.offset) & Snapshot.SMALL_ID_MASK : var1.getLong(this.offset);
      } catch (IOException var3) {
         System.err.println("lazy read failed at offset " + this.offset);
         var3.printStackTrace();
         return -1L;
      }
   }

   protected abstract int readValueLength() throws IOException;

   protected abstract byte[] readValue() throws IOException;

   protected static Number makeId(long var0) {
      return (Number)((var0 & ~Snapshot.SMALL_ID_MASK) == 0L ? new Integer((int)var0) : new Long(var0));
   }

   protected static long getIdValue(Number var0) {
      long var1 = var0.longValue();
      if (var0 instanceof Integer) {
         var1 &= Snapshot.SMALL_ID_MASK;
      }

      return var1;
   }

   protected final long objectIdAt(int var1, byte[] var2) {
      int var3 = this.getClazz().getIdentifierSize();
      return var3 == 4 ? (long)intAt(var1, var2) & Snapshot.SMALL_ID_MASK : longAt(var1, var2);
   }

   protected static byte byteAt(int var0, byte[] var1) {
      return var1[var0];
   }

   protected static boolean booleanAt(int var0, byte[] var1) {
      return (var1[var0] & 255) != 0;
   }

   protected static char charAt(int var0, byte[] var1) {
      int var2 = var1[var0++] & 255;
      int var3 = var1[var0++] & 255;
      return (char)((var2 << 8) + var3);
   }

   protected static short shortAt(int var0, byte[] var1) {
      int var2 = var1[var0++] & 255;
      int var3 = var1[var0++] & 255;
      return (short)((var2 << 8) + var3);
   }

   protected static int intAt(int var0, byte[] var1) {
      int var2 = var1[var0++] & 255;
      int var3 = var1[var0++] & 255;
      int var4 = var1[var0++] & 255;
      int var5 = var1[var0++] & 255;
      return (var2 << 24) + (var3 << 16) + (var4 << 8) + var5;
   }

   protected static long longAt(int var0, byte[] var1) {
      long var2 = 0L;

      for(int var4 = 0; var4 < 8; ++var4) {
         var2 <<= 8;
         int var5 = var1[var0++] & 255;
         var2 |= (long)var5;
      }

      return var2;
   }

   protected static float floatAt(int var0, byte[] var1) {
      int var2 = intAt(var0, var1);
      return Float.intBitsToFloat(var2);
   }

   protected static double doubleAt(int var0, byte[] var1) {
      long var2 = longAt(var0, var1);
      return Double.longBitsToDouble(var2);
   }
}
