package com.sun.tools.jdi;

import com.sun.jdi.connect.spi.TransportService;

class SocketTransportServiceCapabilities extends TransportService.Capabilities {
   public boolean supportsMultipleConnections() {
      return true;
   }

   public boolean supportsAttachTimeout() {
      return true;
   }

   public boolean supportsAcceptTimeout() {
      return true;
   }

   public boolean supportsHandshakeTimeout() {
      return true;
   }
}
