package com.sun.tools.jdi;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.util.Map;

public class RawCommandLineLauncher extends AbstractLauncher implements LaunchingConnector {
   private static final String ARG_COMMAND = "command";
   private static final String ARG_ADDRESS = "address";
   private static final String ARG_QUOTE = "quote";
   TransportService transportService;
   Transport transport;

   public TransportService transportService() {
      return this.transportService;
   }

   public Transport transport() {
      return this.transport;
   }

   public RawCommandLineLauncher() {
      try {
         Class var1 = Class.forName("com.sun.tools.jdi.SharedMemoryTransportService");
         this.transportService = (TransportService)var1.newInstance();
         this.transport = new Transport() {
            public String name() {
               return "dt_shmem";
            }
         };
      } catch (ClassNotFoundException var2) {
      } catch (UnsatisfiedLinkError var3) {
      } catch (InstantiationException var4) {
      } catch (IllegalAccessException var5) {
      }

      if (this.transportService == null) {
         this.transportService = new SocketTransportService();
         this.transport = new Transport() {
            public String name() {
               return "dt_socket";
            }
         };
      }

      this.addStringArgument("command", this.getString("raw.command.label"), this.getString("raw.command"), "", true);
      this.addStringArgument("quote", this.getString("raw.quote.label"), this.getString("raw.quote"), "\"", true);
      this.addStringArgument("address", this.getString("raw.address.label"), this.getString("raw.address"), "", true);
   }

   public VirtualMachine launch(Map var1) throws IOException, IllegalConnectorArgumentsException, VMStartException {
      String var2 = this.argument("command", var1).value();
      String var3 = this.argument("address", var1).value();
      String var4 = this.argument("quote", var1).value();
      if (var4.length() > 1) {
         throw new IllegalConnectorArgumentsException("Invalid length", "quote");
      } else {
         TransportService.ListenKey var5 = this.transportService.startListening(var3);

         VirtualMachine var6;
         try {
            var6 = this.launch(this.tokenizeCommand(var2, var4.charAt(0)), var3, var5, this.transportService);
         } finally {
            this.transportService.stopListening(var5);
         }

         return var6;
      }
   }

   public String name() {
      return "com.sun.jdi.RawCommandLineLaunch";
   }

   public String description() {
      return this.getString("raw.description");
   }
}
