package com.sun.tools.jdi;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.Transport;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.connect.spi.TransportService;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class SunCommandLineLauncher extends AbstractLauncher implements LaunchingConnector {
   private static final String ARG_HOME = "home";
   private static final String ARG_OPTIONS = "options";
   private static final String ARG_MAIN = "main";
   private static final String ARG_INIT_SUSPEND = "suspend";
   private static final String ARG_QUOTE = "quote";
   private static final String ARG_VM_EXEC = "vmexec";
   TransportService transportService;
   Transport transport;
   boolean usingSharedMemory = false;

   TransportService transportService() {
      return this.transportService;
   }

   public Transport transport() {
      return this.transport;
   }

   public SunCommandLineLauncher() {
      try {
         Class var1 = Class.forName("com.sun.tools.jdi.SharedMemoryTransportService");
         this.transportService = (TransportService)var1.newInstance();
         this.transport = new Transport() {
            public String name() {
               return "dt_shmem";
            }
         };
         this.usingSharedMemory = true;
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

      this.addStringArgument("home", this.getString("sun.home.label"), this.getString("sun.home"), System.getProperty("java.home"), false);
      this.addStringArgument("options", this.getString("sun.options.label"), this.getString("sun.options"), "", false);
      this.addStringArgument("main", this.getString("sun.main.label"), this.getString("sun.main"), "", true);
      this.addBooleanArgument("suspend", this.getString("sun.init_suspend.label"), this.getString("sun.init_suspend"), true, false);
      this.addStringArgument("quote", this.getString("sun.quote.label"), this.getString("sun.quote"), "\"", true);
      this.addStringArgument("vmexec", this.getString("sun.vm_exec.label"), this.getString("sun.vm_exec"), "java", true);
   }

   static boolean hasWhitespace(String var0) {
      int var1 = var0.length();

      for(int var2 = 0; var2 < var1; ++var2) {
         if (Character.isWhitespace(var0.charAt(var2))) {
            return true;
         }
      }

      return false;
   }

   public VirtualMachine launch(Map var1) throws IOException, IllegalConnectorArgumentsException, VMStartException {
      String var3 = this.argument("home", var1).value();
      String var4 = this.argument("options", var1).value();
      String var5 = this.argument("main", var1).value();
      boolean var6 = ((ConnectorImpl.BooleanArgumentImpl)this.argument("suspend", var1)).booleanValue();
      String var7 = this.argument("quote", var1).value();
      String var8 = this.argument("vmexec", var1).value();
      String var9 = null;
      if (var7.length() > 1) {
         throw new IllegalConnectorArgumentsException("Invalid length", "quote");
      } else if (var4.indexOf("-Djava.compiler=") != -1 && var4.toLowerCase().indexOf("-djava.compiler=none") == -1) {
         throw new IllegalConnectorArgumentsException("Cannot debug with a JIT compiler", "options");
      } else {
         TransportService.ListenKey var10;
         String var13;
         if (!this.usingSharedMemory) {
            var10 = this.transportService().startListening();
         } else {
            Random var11 = new Random();
            int var12 = 0;

            while(true) {
               try {
                  var13 = "javadebug" + String.valueOf(var11.nextInt(100000));
                  var10 = this.transportService().startListening(var13);
                  break;
               } catch (IOException var18) {
                  ++var12;
                  if (var12 > 5) {
                     throw var18;
                  }
               }
            }
         }

         String var19 = var10.address();

         VirtualMachine var2;
         try {
            if (var3.length() > 0) {
               var9 = var3 + File.separator + "bin" + File.separator + var8;
            } else {
               var9 = var8;
            }

            if (hasWhitespace(var9)) {
               var9 = var7 + var9 + var7;
            }

            String var20 = "transport=" + this.transport().name() + ",address=" + var19 + ",suspend=" + (var6 ? 'y' : 'n');
            if (hasWhitespace(var20)) {
               var20 = var7 + var20 + var7;
            }

            var13 = var9 + ' ' + var4 + ' ' + "-Xdebug -Xrunjdwp:" + var20 + ' ' + var5;
            var2 = this.launch(this.tokenizeCommand(var13, var7.charAt(0)), var19, var10, this.transportService());
         } finally {
            this.transportService().stopListening(var10);
         }

         return var2;
      }
   }

   public String name() {
      return "com.sun.jdi.CommandLineLaunch";
   }

   public String description() {
      return this.getString("sun.description");
   }
}
