package com.sun.jdi.connect.spi;

import java.io.IOException;
import jdk.Exported;

@Exported
public abstract class Connection {
   public abstract byte[] readPacket() throws IOException;

   public abstract void writePacket(byte[] var1) throws IOException;

   public abstract void close() throws IOException;

   public abstract boolean isOpen();
}
