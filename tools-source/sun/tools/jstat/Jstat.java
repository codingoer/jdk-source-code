package sun.tools.jstat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;

public class Jstat {
   private static Arguments arguments;

   public static void main(String[] var0) {
      try {
         arguments = new Arguments(var0);
      } catch (IllegalArgumentException var3) {
         System.err.println(var3.getMessage());
         Arguments.printUsage(System.err);
         System.exit(1);
      }

      if (arguments.isHelp()) {
         Arguments.printUsage(System.out);
         System.exit(0);
      }

      if (arguments.isOptions()) {
         OptionLister var1 = new OptionLister(arguments.optionsSources());
         var1.print(System.out);
         System.exit(0);
      }

      try {
         if (arguments.isList()) {
            logNames();
         } else if (arguments.isSnap()) {
            logSnapShot();
         } else {
            logSamples();
         }
      } catch (MonitorException var4) {
         if (var4.getMessage() != null) {
            System.err.println(var4.getMessage());
         } else {
            Throwable var2 = var4.getCause();
            if (var2 != null && var2.getMessage() != null) {
               System.err.println(var2.getMessage());
            } else {
               var4.printStackTrace();
            }
         }

         System.exit(1);
      }

      System.exit(0);
   }

   static void logNames() throws MonitorException {
      VmIdentifier var0 = arguments.vmId();
      int var1 = arguments.sampleInterval();
      MonitoredHost var2 = MonitoredHost.getMonitoredHost(var0);
      MonitoredVm var3 = var2.getMonitoredVm(var0, var1);
      JStatLogger var4 = new JStatLogger(var3);
      var4.printNames(arguments.counterNames(), arguments.comparator(), arguments.showUnsupported(), System.out);
      var2.detach(var3);
   }

   static void logSnapShot() throws MonitorException {
      VmIdentifier var0 = arguments.vmId();
      int var1 = arguments.sampleInterval();
      MonitoredHost var2 = MonitoredHost.getMonitoredHost(var0);
      MonitoredVm var3 = var2.getMonitoredVm(var0, var1);
      JStatLogger var4 = new JStatLogger(var3);
      var4.printSnapShot(arguments.counterNames(), arguments.comparator(), arguments.isVerbose(), arguments.showUnsupported(), System.out);
      var2.detach(var3);
   }

   static void logSamples() throws MonitorException {
      final VmIdentifier var0 = arguments.vmId();
      int var1 = arguments.sampleInterval();
      final MonitoredHost var2 = MonitoredHost.getMonitoredHost(var0);
      MonitoredVm var3 = var2.getMonitoredVm(var0, var1);
      final JStatLogger var4 = new JStatLogger(var3);
      Object var5 = null;
      if (arguments.isSpecialOption()) {
         OptionFormat var6 = arguments.optionFormat();
         var5 = new OptionOutputFormatter(var3, var6);
      } else {
         List var10 = var3.findByPattern(arguments.counterNames());
         Collections.sort(var10, arguments.comparator());
         ArrayList var7 = new ArrayList();
         Iterator var8 = var10.iterator();

         while(true) {
            while(var8.hasNext()) {
               Monitor var9 = (Monitor)var8.next();
               if (!var9.isSupported() && !arguments.showUnsupported()) {
                  var8.remove();
               } else if (var9.getVariability() == Variability.CONSTANT) {
                  var8.remove();
                  if (arguments.printConstants()) {
                     var7.add(var9);
                  }
               } else if (var9.getUnits() == Units.STRING && !arguments.printStrings()) {
                  var8.remove();
               }
            }

            if (!var7.isEmpty()) {
               var4.printList(var7, arguments.isVerbose(), arguments.showUnsupported(), System.out);
               if (!var10.isEmpty()) {
                  System.out.println();
               }
            }

            if (var10.isEmpty()) {
               var2.detach(var3);
               return;
            }

            var5 = new RawOutputFormatter(var10, arguments.printStrings());
            break;
         }
      }

      Runtime.getRuntime().addShutdownHook(new Thread() {
         public void run() {
            var4.stopLogging();
         }
      });
      HostListener var11 = new HostListener() {
         public void vmStatusChanged(VmStatusChangeEvent var1) {
            Integer var2x = new Integer(var0.getLocalVmId());
            if (var1.getTerminated().contains(var2x)) {
               var4.stopLogging();
            } else if (!var1.getActive().contains(var2x)) {
               var4.stopLogging();
            }

         }

         public void disconnected(HostEvent var1) {
            if (var2 == var1.getMonitoredHost()) {
               var4.stopLogging();
            }

         }
      };
      if (var0.getLocalVmId() != 0) {
         var2.addHostListener(var11);
      }

      var4.logSamples((OutputFormatter)var5, arguments.headerRate(), arguments.sampleInterval(), arguments.sampleCount(), System.out);
      if (var11 != null) {
         var2.removeHostListener(var11);
      }

      var2.detach(var3);
   }
}
