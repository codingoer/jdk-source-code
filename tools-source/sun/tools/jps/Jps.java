package sun.tools.jps;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

public class Jps {
   private static Arguments arguments;

   public static void main(String[] var0) {
      try {
         arguments = new Arguments(var0);
      } catch (IllegalArgumentException var20) {
         System.err.println(var20.getMessage());
         Arguments.printUsage(System.err);
         System.exit(1);
      }

      if (arguments.isHelp()) {
         Arguments.printUsage(System.err);
         System.exit(0);
      }

      try {
         HostIdentifier var1 = arguments.hostId();
         MonitoredHost var25 = MonitoredHost.getMonitoredHost(var1);
         Set var3 = var25.activeVms();
         Iterator var4 = var3.iterator();

         while(true) {
            while(var4.hasNext()) {
               Integer var5 = (Integer)var4.next();
               StringBuilder var6 = new StringBuilder();
               Object var7 = null;
               int var8 = var5;
               var6.append(String.valueOf(var8));
               if (arguments.isQuiet()) {
                  System.out.println(var6);
               } else {
                  MonitoredVm var9 = null;
                  String var10 = "//" + var8 + "?mode=r";
                  String var11 = null;

                  try {
                     var11 = " -- process information unavailable";
                     VmIdentifier var12 = new VmIdentifier(var10);
                     var9 = var25.getMonitoredVm(var12, 0);
                     var11 = " -- main class information unavailable";
                     var6.append(" " + MonitoredVmUtil.mainClass(var9, arguments.showLongPaths()));
                     String var13;
                     if (arguments.showMainArgs()) {
                        var11 = " -- main args information unavailable";
                        var13 = MonitoredVmUtil.mainArgs(var9);
                        if (var13 != null && var13.length() > 0) {
                           var6.append(" " + var13);
                        }
                     }

                     if (arguments.showVmArgs()) {
                        var11 = " -- jvm args information unavailable";
                        var13 = MonitoredVmUtil.jvmArgs(var9);
                        if (var13 != null && var13.length() > 0) {
                           var6.append(" " + var13);
                        }
                     }

                     if (arguments.showVmFlags()) {
                        var11 = " -- jvm flags information unavailable";
                        var13 = MonitoredVmUtil.jvmFlags(var9);
                        if (var13 != null && var13.length() > 0) {
                           var6.append(" " + var13);
                        }
                     }

                     var11 = " -- detach failed";
                     var25.detach(var9);
                     System.out.println(var6);
                     var11 = null;
                  } catch (URISyntaxException var21) {
                     var7 = var21;

                     assert false;
                  } catch (Exception var22) {
                     var7 = var22;
                  } finally {
                     if (var11 != null) {
                        var6.append(var11);
                        if (arguments.isDebug() && var7 != null && ((Throwable)var7).getMessage() != null) {
                           var6.append("\n\t");
                           var6.append(((Throwable)var7).getMessage());
                        }

                        System.out.println(var6);
                        if (arguments.printStackTrace()) {
                           ((Throwable)var7).printStackTrace();
                        }
                        continue;
                     }
                  }
               }
            }

            return;
         }
      } catch (MonitorException var24) {
         if (var24.getMessage() != null) {
            System.err.println(var24.getMessage());
         } else {
            Throwable var2 = var24.getCause();
            if (var2 != null && var2.getMessage() != null) {
               System.err.println(var2.getMessage());
            } else {
               var24.printStackTrace();
            }
         }

         System.exit(1);
      }
   }
}
