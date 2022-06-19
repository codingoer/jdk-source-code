package com.sun.tools.jdi;

import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.util.Map;

public class SocketListeningConnector extends GenericListeningConnector {
   static final String ARG_PORT = "port";
   static final String ARG_LOCALADDR = "localAddress";

   public SocketListeningConnector() {
      super(new SocketTransportService());
      this.addIntegerArgument("port", this.getString("socket_listening.port.label"), this.getString("socket_listening.port"), "", false, 0, Integer.MAX_VALUE);
      this.addStringArgument("localAddress", this.getString("socket_listening.localaddr.label"), this.getString("socket_listening.localaddr"), "", false);
      this.transport = new Transport() {
         public String name() {
            return "dt_socket";
         }
      };
   }

   public String startListening(Map var1) throws IOException, IllegalConnectorArgumentsException {
      String var2 = this.argument("port", var1).value();
      String var3 = this.argument("localAddress", var1).value();
      if (var2.length() == 0) {
         var2 = "0";
      }

      if (var3.length() > 0) {
         var3 = var3 + ":" + var2;
      } else {
         var3 = var2;
      }

      return super.startListening(var3, var1);
   }

   public String name() {
      return "com.sun.jdi.SocketListen";
   }

   public String description() {
      return this.getString("socket_listening.description");
   }

   protected void updateArgumentMapIfRequired(Map var1, TransportService.ListenKey var2) {
      if (this.isWildcardPort(var1)) {
         String[] var3 = var2.address().split(":");
         if (var3.length > 1) {
            ((Connector.Argument)var1.get("port")).setValue(var3[1]);
         }
      }

   }

   private boolean isWildcardPort(Map var1) {
      String var2 = ((Connector.Argument)var1.get("port")).value();
      return var2.isEmpty() || Integer.valueOf(var2) == 0;
   }
}
