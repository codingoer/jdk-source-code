package sun.tools.jstatd;

import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import sun.jvmstat.monitor.remote.RemoteHost;

public class Jstatd {
   private static Registry registry;
   private static int port = -1;
   private static boolean startRegistry = true;

   private static void printUsage() {
      System.err.println("usage: jstatd [-nr] [-p port] [-n rminame]");
   }

   static void bind(String var0, RemoteHostImpl var1) throws RemoteException, MalformedURLException, Exception {
      try {
         Naming.rebind(var0, var1);
      } catch (ConnectException var4) {
         if (startRegistry && registry == null) {
            int var3 = port < 0 ? 1099 : port;
            registry = LocateRegistry.createRegistry(var3);
            bind(var0, var1);
         } else {
            System.out.println("Could not contact registry\n" + var4.getMessage());
            var4.printStackTrace();
         }
      } catch (RemoteException var5) {
         System.err.println("Could not bind " + var0 + " to RMI Registry");
         var5.printStackTrace();
      }

   }

   public static void main(String[] var0) {
      String var1 = null;

      int var2;
      for(var2 = 0; var2 < var0.length && var0[var2].startsWith("-"); ++var2) {
         String var3 = var0[var2];
         if (var3.compareTo("-nr") == 0) {
            startRegistry = false;
         } else if (var3.startsWith("-p")) {
            if (var3.compareTo("-p") != 0) {
               port = Integer.parseInt(var3.substring(2));
            } else {
               ++var2;
               if (var2 >= var0.length) {
                  printUsage();
                  System.exit(1);
               }

               port = Integer.parseInt(var0[var2]);
            }
         } else if (var3.startsWith("-n")) {
            if (var3.compareTo("-n") != 0) {
               var1 = var3.substring(2);
            } else {
               ++var2;
               if (var2 >= var0.length) {
                  printUsage();
                  System.exit(1);
               }

               var1 = var0[var2];
            }
         } else {
            printUsage();
            System.exit(1);
         }
      }

      if (var2 < var0.length) {
         printUsage();
         System.exit(1);
      }

      if (System.getSecurityManager() == null) {
         System.setSecurityManager(new RMISecurityManager());
      }

      StringBuilder var9 = new StringBuilder();
      if (port >= 0) {
         var9.append("//:").append(port);
      }

      if (var1 == null) {
         var1 = "JStatRemoteHost";
      }

      var9.append("/").append(var1);

      try {
         System.setProperty("java.rmi.server.ignoreSubClasses", "true");
         RemoteHostImpl var4 = new RemoteHostImpl();
         RemoteHost var5 = (RemoteHost)UnicastRemoteObject.exportObject(var4, 0);
         bind(var9.toString(), var4);
      } catch (MalformedURLException var6) {
         if (var1 != null) {
            System.out.println("Bad RMI server name: " + var1);
         } else {
            System.out.println("Bad RMI URL: " + var9 + " : " + var6.getMessage());
         }

         System.exit(1);
      } catch (ConnectException var7) {
         System.out.println("Could not contact RMI registry\n" + var7.getMessage());
         System.exit(1);
      } catch (Exception var8) {
         System.out.println("Could not create remote object\n" + var8.getMessage());
         var8.printStackTrace();
         System.exit(1);
      }

   }
}
