package com.sun.jdi.connect.spi;

import java.io.IOException;
import jdk.Exported;

@Exported
public abstract class TransportService {
   public abstract String name();

   public abstract String description();

   public abstract Capabilities capabilities();

   public abstract Connection attach(String var1, long var2, long var4) throws IOException;

   public abstract ListenKey startListening(String var1) throws IOException;

   public abstract ListenKey startListening() throws IOException;

   public abstract void stopListening(ListenKey var1) throws IOException;

   public abstract Connection accept(ListenKey var1, long var2, long var4) throws IOException;

   @Exported
   public abstract static class ListenKey {
      public abstract String address();
   }

   @Exported
   public abstract static class Capabilities {
      public abstract boolean supportsMultipleConnections();

      public abstract boolean supportsAttachTimeout();

      public abstract boolean supportsAcceptTimeout();

      public abstract boolean supportsHandshakeTimeout();
   }
}
