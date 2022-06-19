package com.sun.tools.hat.internal.parser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

class MappedReadBuffer implements ReadBuffer {
   private MappedByteBuffer buf;

   MappedReadBuffer(MappedByteBuffer var1) {
      this.buf = var1;
   }

   static ReadBuffer create(RandomAccessFile var0) throws IOException {
      FileChannel var1 = var0.getChannel();
      long var2 = var1.size();
      if (canUseFileMap() && var2 <= 2147483647L) {
         try {
            MappedByteBuffer var4 = var1.map(MapMode.READ_ONLY, 0L, var2);
            var1.close();
            return new MappedReadBuffer(var4);
         } catch (IOException var6) {
            var6.printStackTrace();
            System.err.println("File mapping failed, will use direct read");
         }
      }

      return new FileReadBuffer(var0);
   }

   private static boolean canUseFileMap() {
      String var0 = System.getProperty("jhat.disableFileMap");
      return var0 == null || var0.equals("false");
   }

   private void seek(long var1) throws IOException {
      assert var1 <= 2147483647L : "position overflow";

      this.buf.position((int)var1);
   }

   public synchronized void get(long var1, byte[] var3) throws IOException {
      this.seek(var1);
      this.buf.get(var3);
   }

   public synchronized char getChar(long var1) throws IOException {
      this.seek(var1);
      return this.buf.getChar();
   }

   public synchronized byte getByte(long var1) throws IOException {
      this.seek(var1);
      return this.buf.get();
   }

   public synchronized short getShort(long var1) throws IOException {
      this.seek(var1);
      return this.buf.getShort();
   }

   public synchronized int getInt(long var1) throws IOException {
      this.seek(var1);
      return this.buf.getInt();
   }

   public synchronized long getLong(long var1) throws IOException {
      this.seek(var1);
      return this.buf.getLong();
   }
}
