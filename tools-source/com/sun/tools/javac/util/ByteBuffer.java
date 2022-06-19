package com.sun.tools.javac.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteBuffer {
   public byte[] elems;
   public int length;

   public ByteBuffer() {
      this(64);
   }

   public ByteBuffer(int var1) {
      this.elems = new byte[var1];
      this.length = 0;
   }

   public void appendByte(int var1) {
      this.elems = ArrayUtils.ensureCapacity(this.elems, this.length);
      this.elems[this.length++] = (byte)var1;
   }

   public void appendBytes(byte[] var1, int var2, int var3) {
      this.elems = ArrayUtils.ensureCapacity(this.elems, this.length + var3);
      System.arraycopy(var1, var2, this.elems, this.length, var3);
      this.length += var3;
   }

   public void appendBytes(byte[] var1) {
      this.appendBytes(var1, 0, var1.length);
   }

   public void appendChar(int var1) {
      this.elems = ArrayUtils.ensureCapacity(this.elems, this.length + 1);
      this.elems[this.length] = (byte)(var1 >> 8 & 255);
      this.elems[this.length + 1] = (byte)(var1 & 255);
      this.length += 2;
   }

   public void appendInt(int var1) {
      this.elems = ArrayUtils.ensureCapacity(this.elems, this.length + 3);
      this.elems[this.length] = (byte)(var1 >> 24 & 255);
      this.elems[this.length + 1] = (byte)(var1 >> 16 & 255);
      this.elems[this.length + 2] = (byte)(var1 >> 8 & 255);
      this.elems[this.length + 3] = (byte)(var1 & 255);
      this.length += 4;
   }

   public void appendLong(long var1) {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream(8);
      DataOutputStream var4 = new DataOutputStream(var3);

      try {
         var4.writeLong(var1);
         this.appendBytes(var3.toByteArray(), 0, 8);
      } catch (IOException var6) {
         throw new AssertionError("write");
      }
   }

   public void appendFloat(float var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(4);
      DataOutputStream var3 = new DataOutputStream(var2);

      try {
         var3.writeFloat(var1);
         this.appendBytes(var2.toByteArray(), 0, 4);
      } catch (IOException var5) {
         throw new AssertionError("write");
      }
   }

   public void appendDouble(double var1) {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream(8);
      DataOutputStream var4 = new DataOutputStream(var3);

      try {
         var4.writeDouble(var1);
         this.appendBytes(var3.toByteArray(), 0, 8);
      } catch (IOException var6) {
         throw new AssertionError("write");
      }
   }

   public void appendName(Name var1) {
      this.appendBytes(var1.getByteArray(), var1.getByteOffset(), var1.getByteLength());
   }

   public void reset() {
      this.length = 0;
   }

   public Name toName(Names var1) {
      return var1.fromUtf(this.elems, 0, this.length);
   }
}
