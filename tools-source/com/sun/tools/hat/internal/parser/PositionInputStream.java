package com.sun.tools.hat.internal.parser;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PositionInputStream extends FilterInputStream {
   private long position = 0L;

   public PositionInputStream(InputStream var1) {
      super(var1);
   }

   public int read() throws IOException {
      int var1 = super.read();
      if (var1 != -1) {
         ++this.position;
      }

      return var1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      int var4 = super.read(var1, var2, var3);
      if (var4 != -1) {
         this.position += (long)var4;
      }

      return var4;
   }

   public long skip(long var1) throws IOException {
      long var3 = super.skip(var1);
      this.position += var3;
      return var3;
   }

   public boolean markSupported() {
      return false;
   }

   public void mark(int var1) {
      throw new UnsupportedOperationException("mark");
   }

   public void reset() {
      throw new UnsupportedOperationException("reset");
   }

   public long position() {
      return this.position;
   }
}
