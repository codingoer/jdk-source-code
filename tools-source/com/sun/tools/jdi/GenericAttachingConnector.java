package com.sun.tools.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.util.Map;

public class GenericAttachingConnector extends ConnectorImpl implements AttachingConnector {
   static final String ARG_ADDRESS = "address";
   static final String ARG_TIMEOUT = "timeout";
   TransportService transportService;
   Transport transport;

   private GenericAttachingConnector(TransportService var1, boolean var2) {
      this.transportService = var1;
      this.transport = new Transport() {
         public String name() {
            return GenericAttachingConnector.this.transportService.name();
         }
      };
      if (var2) {
         this.addStringArgument("address", this.getString("generic_attaching.address.label"), this.getString("generic_attaching.address"), "", true);
      }

      this.addIntegerArgument("timeout", this.getString("generic_attaching.timeout.label"), this.getString("generic_attaching.timeout"), "", false, 0, Integer.MAX_VALUE);
   }

   protected GenericAttachingConnector(TransportService var1) {
      this(var1, false);
   }

   public static GenericAttachingConnector create(TransportService var0) {
      return new GenericAttachingConnector(var0, true);
   }

   public VirtualMachine attach(String var1, Map var2) throws IOException, IllegalConnectorArgumentsException {
      String var3 = this.argument("timeout", var2).value();
      int var4 = 0;
      if (var3.length() > 0) {
         var4 = Integer.decode(var3);
      }

      Connection var5 = this.transportService.attach(var1, (long)var4, 0L);
      return Bootstrap.virtualMachineManager().createVirtualMachine(var5);
   }

   public VirtualMachine attach(Map var1) throws IOException, IllegalConnectorArgumentsException {
      String var2 = this.argument("address", var1).value();
      return this.attach(var2, var1);
   }

   public String name() {
      return this.transport.name() + "Attach";
   }

   public String description() {
      return this.transportService.description();
   }

   public Transport transport() {
      return this.transport;
   }
}
