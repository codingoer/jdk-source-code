package com.sun.tools.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.InternalException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

abstract class AbstractLauncher extends ConnectorImpl implements LaunchingConnector {
   ThreadGroup grp = Thread.currentThread().getThreadGroup();

   public abstract VirtualMachine launch(Map var1) throws IOException, IllegalConnectorArgumentsException, VMStartException;

   public abstract String name();

   public abstract String description();

   AbstractLauncher() {
      for(ThreadGroup var1 = null; (var1 = this.grp.getParent()) != null; this.grp = var1) {
      }

   }

   String[] tokenizeCommand(String var1, char var2) {
      String var3 = String.valueOf(var2);
      StringTokenizer var4 = new StringTokenizer(var1, var2 + " \t\r\n\f", true);
      String var5 = null;
      String var6 = null;
      ArrayList var7 = new ArrayList();

      while(true) {
         while(true) {
            while(var4.hasMoreTokens()) {
               String var8 = var4.nextToken();
               if (var5 == null) {
                  if (var6 != null) {
                     if (var8.equals(var3)) {
                        var5 = var6;
                     } else {
                        if (var8.length() != 1 || !Character.isWhitespace(var8.charAt(0))) {
                           throw new InternalException("Unexpected token: " + var8);
                        }

                        var7.add(var6);
                     }

                     var6 = null;
                  } else if (var8.equals(var3)) {
                     var5 = "";
                  } else if (var8.length() != 1 || !Character.isWhitespace(var8.charAt(0))) {
                     var6 = var8;
                  }
               } else if (var8.equals(var3)) {
                  var7.add(var5);
                  var5 = null;
               } else {
                  var5 = var5 + var8;
               }
            }

            if (var6 != null) {
               var7.add(var6);
            }

            if (var5 != null) {
               var7.add(var5);
            }

            String[] var10 = new String[var7.size()];

            for(int var9 = 0; var9 < var7.size(); ++var9) {
               var10[var9] = (String)var7.get(var9);
            }

            return var10;
         }
      }
   }

   protected VirtualMachine launch(String[] var1, String var2, TransportService.ListenKey var3, TransportService var4) throws IOException, VMStartException {
      Helper var5 = new Helper(var1, var2, var3, var4);
      var5.launchAndAccept();
      VirtualMachineManager var6 = Bootstrap.virtualMachineManager();
      return var6.createVirtualMachine(var5.connection(), var5.process());
   }

   private class Helper {
      private final String address;
      private TransportService.ListenKey listenKey;
      private TransportService ts;
      private final String[] commandArray;
      private Process process = null;
      private Connection connection = null;
      private IOException acceptException = null;
      private boolean exited = false;

      Helper(String[] var2, String var3, TransportService.ListenKey var4, TransportService var5) {
         this.commandArray = var2;
         this.address = var3;
         this.listenKey = var4;
         this.ts = var5;
      }

      String commandString() {
         String var1 = "";

         for(int var2 = 0; var2 < this.commandArray.length; ++var2) {
            if (var2 > 0) {
               var1 = var1 + " ";
            }

            var1 = var1 + this.commandArray[var2];
         }

         return var1;
      }

      synchronized void launchAndAccept() throws IOException, VMStartException {
         this.process = Runtime.getRuntime().exec(this.commandArray);
         Thread var1 = this.acceptConnection();
         Thread var2 = this.monitorTarget();

         try {
            while(this.connection == null && this.acceptException == null && !this.exited) {
               this.wait();
            }

            if (this.exited) {
               throw new VMStartException("VM initialization failed for: " + this.commandString(), this.process);
            }

            if (this.acceptException != null) {
               throw this.acceptException;
            }
         } catch (InterruptedException var7) {
            throw new InterruptedIOException("Interrupted during accept");
         } finally {
            var1.interrupt();
            var2.interrupt();
         }

      }

      Process process() {
         return this.process;
      }

      Connection connection() {
         return this.connection;
      }

      synchronized void notifyOfExit() {
         this.exited = true;
         this.notify();
      }

      synchronized void notifyOfConnection(Connection var1) {
         this.connection = var1;
         this.notify();
      }

      synchronized void notifyOfAcceptException(IOException var1) {
         this.acceptException = var1;
         this.notify();
      }

      Thread monitorTarget() {
         Thread var1 = new Thread(AbstractLauncher.this.grp, "launched target monitor") {
            public void run() {
               try {
                  Helper.this.process.waitFor();
                  Helper.this.notifyOfExit();
               } catch (InterruptedException var2) {
               }

            }
         };
         var1.setDaemon(true);
         var1.start();
         return var1;
      }

      Thread acceptConnection() {
         Thread var1 = new Thread(AbstractLauncher.this.grp, "connection acceptor") {
            public void run() {
               try {
                  Connection var1 = Helper.this.ts.accept(Helper.this.listenKey, 0L, 0L);
                  Helper.this.notifyOfConnection(var1);
               } catch (InterruptedIOException var2) {
               } catch (IOException var3) {
                  Helper.this.notifyOfAcceptException(var3);
               }

            }
         };
         var1.setDaemon(true);
         var1.start();
         return var1;
      }
   }
}
