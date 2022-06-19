package com.sun.tools.hat.internal.parser;

import java.io.IOException;
import java.io.RandomAccessFile;

class FileReadBuffer implements ReadBuffer {
   private RandomAccessFile file;

   FileReadBuffer(RandomAccessFile var1) {
      this.file = var1;
   }

   private void seek(long var1) throws IOException {
      this.file.getChannel().position(var1);
   }

   public synchronized void get(long var1, byte[] var3) throws IOException {
      this.seek(var1);
      this.file.read(var3);
   }

   public synchronized char getChar(long var1) throws IOException {
      this.seek(var1);
      return this.file.readChar();
   }

   public synchronized byte getByte(long var1) throws IOException {
      this.seek(var1);
      return (byte)this.file.read();
   }

   public synchronized short getShort(long var1) throws IOException {
      this.seek(var1);
      return this.file.readShort();
   }

   public synchronized int getInt(long var1) throws IOException {
      this.seek(var1);
      return this.file.readInt();
   }

   public synchronized long getLong(long var1) throws IOException {
      this.seek(var1);
      return this.file.readLong();
   }
}
