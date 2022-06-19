package com.sun.tools.jdi;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.Transport;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

public class SocketAttachingConnector extends GenericAttachingConnector {
   static final String ARG_PORT = "port";
   static final String ARG_HOST = "hostname";

   public SocketAttachingConnector() {
      super(new SocketTransportService());

      String var1;
      try {
         var1 = InetAddress.getLocalHost().getHostName();
      } catch (UnknownHostException var3) {
         var1 = "";
      }

      this.addStringArgument("hostname", this.getString("socket_attaching.host.label"), this.getString("socket_attaching.host"), var1, false);
      this.addIntegerArgument("port", this.getString("socket_attaching.port.label"), this.getString("socket_attaching.port"), "", true, 0, Integer.MAX_VALUE);
      this.transport = new Transport() {
         public String name() {
            return "dt_socket";
         }
      };
   }

   public VirtualMachine attach(Map var1) throws IOException, IllegalConnectorArgumentsException {
      String var2 = this.argument("hostname", var1).value();
      if (var2.length() > 0) {
         var2 = var2 + ":";
      }

      String var3 = var2 + this.argument("port", var1).value();
      return super.attach(var3, var1);
   }

   public String name() {
      return "com.sun.jdi.SocketAttach";
   }

   public String description() {
      return this.getString("socket_attaching.description");
   }
}
