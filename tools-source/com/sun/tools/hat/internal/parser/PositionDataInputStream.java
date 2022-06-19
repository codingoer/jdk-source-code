package com.sun.tools.hat.internal.parser;

import java.io.DataInputStream;
import java.io.InputStream;

public class PositionDataInputStream extends DataInputStream {
   public PositionDataInputStream(InputStream var1) {
      super((InputStream)(var1 instanceof PositionInputStream ? var1 : new PositionInputStream(var1)));
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
      return ((PositionInputStream)this.in).position();
   }
}
