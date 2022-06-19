package com.sun.tools.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService;
import com.sun.tools.attach.VirtualMachine;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class ProcessAttachingConnector extends ConnectorImpl implements AttachingConnector {
   static final String ARG_PID = "pid";
   static final String ARG_TIMEOUT = "timeout";
   VirtualMachine vm;
   Transport transport;

   public ProcessAttachingConnector() {
      this.addStringArgument("pid", this.getString("process_attaching.pid.label"), this.getString("process_attaching.pid"), "", true);
      this.addIntegerArgument("timeout", this.getString("generic_attaching.timeout.label"), this.getString("generic_attaching.timeout"), "", false, 0, Integer.MAX_VALUE);
      this.transport = new Transport() {
         public String name() {
            return "local";
         }
      };
   }

   public com.sun.jdi.VirtualMachine attach(Map var1) throws IOException, IllegalConnectorArgumentsException {
      String var2 = this.argument("pid", var1).value();
      String var3 = this.argument("timeout", var1).value();
      int var4 = 0;
      if (var3.length() > 0) {
         var4 = Integer.decode(var3);
      }

      String var5 = null;
      VirtualMachine var6 = null;

      try {
         var6 = VirtualMachine.attach(var2);
         Properties var7 = var6.getAgentProperties();
         var5 = var7.getProperty("sun.jdwp.listenerAddress");
      } catch (Exception var15) {
         throw new IOException(var15.getMessage());
      } finally {
         if (var6 != null) {
            var6.detach();
         }

      }

      if (var5 == null) {
         throw new IOException("Not a debuggee, or not listening for debugger to attach");
      } else {
         int var17 = var5.indexOf(58);
         if (var17 < 1) {
            throw new IOException("Unable to determine transport endpoint");
         } else {
            String var8 = var5.substring(0, var17);
            var5 = var5.substring(var17 + 1, var5.length());
            Object var9 = null;
            if (var8.equals("dt_socket")) {
               var9 = new SocketTransportService();
            } else if (var8.equals("dt_shmem")) {
               try {
                  Class var10 = Class.forName("com.sun.tools.jdi.SharedMemoryTransportService");
                  var9 = (TransportService)var10.newInstance();
               } catch (Exception var14) {
               }
            }

            if (var9 == null) {
               throw new IOException("Transport " + var8 + " not recognized");
            } else {
               Connection var18 = ((TransportService)var9).attach(var5, (long)var4, 0L);
               return Bootstrap.virtualMachineManager().createVirtualMachine(var18);
            }
         }
      }
   }

   public String name() {
      return "com.sun.jdi.ProcessAttach";
   }

   public String description() {
      return this.getString("process_attaching.description");
   }

   public Transport transport() {
      return this.transport == null ? new Transport() {
         public String name() {
            return "local";
         }
      } : this.transport;
   }
}
