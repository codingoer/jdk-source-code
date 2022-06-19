package com.sun.tools.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenericListeningConnector extends ConnectorImpl implements ListeningConnector {
   static final String ARG_ADDRESS = "address";
   static final String ARG_TIMEOUT = "timeout";
   Map listenMap;
   TransportService transportService;
   Transport transport;

   private GenericListeningConnector(TransportService var1, boolean var2) {
      this.transportService = var1;
      this.transport = new Transport() {
         public String name() {
            return GenericListeningConnector.this.transportService.name();
         }
      };
      if (var2) {
         this.addStringArgument("address", this.getString("generic_listening.address.label"), this.getString("generic_listening.address"), "", false);
      }

      this.addIntegerArgument("timeout", this.getString("generic_listening.timeout.label"), this.getString("generic_listening.timeout"), "", false, 0, Integer.MAX_VALUE);
      this.listenMap = new HashMap(10);
   }

   protected GenericListeningConnector(TransportService var1) {
      this(var1, false);
   }

   public static GenericListeningConnector create(TransportService var0) {
      return new GenericListeningConnector(var0, true);
   }

   public String startListening(String var1, Map var2) throws IOException, IllegalConnectorArgumentsException {
      TransportService.ListenKey var3 = (TransportService.ListenKey)this.listenMap.get(var2);
      if (var3 != null) {
         throw new IllegalConnectorArgumentsException("Already listening", new ArrayList(var2.keySet()));
      } else {
         var3 = this.transportService.startListening(var1);
         this.updateArgumentMapIfRequired(var2, var3);
         this.listenMap.put(var2, var3);
         return var3.address();
      }
   }

   public String startListening(Map var1) throws IOException, IllegalConnectorArgumentsException {
      String var2 = this.argument("address", var1).value();
      return this.startListening(var2, var1);
   }

   public void stopListening(Map var1) throws IOException, IllegalConnectorArgumentsException {
      TransportService.ListenKey var2 = (TransportService.ListenKey)this.listenMap.get(var1);
      if (var2 == null) {
         throw new IllegalConnectorArgumentsException("Not listening", new ArrayList(var1.keySet()));
      } else {
         this.transportService.stopListening(var2);
         this.listenMap.remove(var1);
      }
   }

   public VirtualMachine accept(Map var1) throws IOException, IllegalConnectorArgumentsException {
      String var2 = this.argument("timeout", var1).value();
      int var3 = 0;
      if (var2.length() > 0) {
         var3 = Integer.decode(var2);
      }

      TransportService.ListenKey var4 = (TransportService.ListenKey)this.listenMap.get(var1);
      Connection var5;
      if (var4 != null) {
         var5 = this.transportService.accept(var4, (long)var3, 0L);
      } else {
         this.startListening(var1);
         var4 = (TransportService.ListenKey)this.listenMap.get(var1);

         assert var4 != null;

         var5 = this.transportService.accept(var4, (long)var3, 0L);
         this.stopListening(var1);
      }

      return Bootstrap.virtualMachineManager().createVirtualMachine(var5);
   }

   public boolean supportsMultipleConnections() {
      return this.transportService.capabilities().supportsMultipleConnections();
   }

   public String name() {
      return this.transport.name() + "Listen";
   }

   public String description() {
      return this.transportService.description();
   }

   public Transport transport() {
      return this.transport;
   }

   protected void updateArgumentMapIfRequired(Map var1, TransportService.ListenKey var2) {
   }
}
