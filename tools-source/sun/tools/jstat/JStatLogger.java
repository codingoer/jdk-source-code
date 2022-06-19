package sun.tools.jstat;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.PatternSyntaxException;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.StringMonitor;

public class JStatLogger {
   private MonitoredVm monitoredVm;
   private volatile boolean active = true;

   public JStatLogger(MonitoredVm var1) {
      this.monitoredVm = var1;
   }

   public void printNames(String var1, Comparator var2, boolean var3, PrintStream var4) throws MonitorException, PatternSyntaxException {
      List var5 = this.monitoredVm.findByPattern(var1);
      Collections.sort(var5, var2);
      Iterator var6 = var5.iterator();

      while(true) {
         Monitor var7;
         do {
            if (!var6.hasNext()) {
               return;
            }

            var7 = (Monitor)var6.next();
         } while(!var7.isSupported() && !var3);

         var4.println(var7.getName());
      }
   }

   public void printSnapShot(String var1, Comparator var2, boolean var3, boolean var4, PrintStream var5) throws MonitorException, PatternSyntaxException {
      List var6 = this.monitoredVm.findByPattern(var1);
      Collections.sort(var6, var2);
      this.printList(var6, var3, var4, var5);
   }

   public void printList(List var1, boolean var2, boolean var3, PrintStream var4) throws MonitorException {
      Iterator var5 = var1.iterator();

      while(true) {
         Monitor var6;
         do {
            if (!var5.hasNext()) {
               return;
            }

            var6 = (Monitor)var5.next();
         } while(!var6.isSupported() && !var3);

         StringBuilder var7 = new StringBuilder();
         var7.append(var6.getName()).append("=");
         if (var6 instanceof StringMonitor) {
            var7.append("\"").append(var6.getValue()).append("\"");
         } else {
            var7.append(var6.getValue());
         }

         if (var2) {
            var7.append(" ").append(var6.getUnits());
            var7.append(" ").append(var6.getVariability());
            var7.append(" ").append(var6.isSupported() ? "Supported" : "Unsupported");
         }

         var4.println(var7);
      }
   }

   public void stopLogging() {
      this.active = false;
   }

   public void logSamples(OutputFormatter var1, int var2, int var3, int var4, PrintStream var5) throws MonitorException {
      long var6 = 0L;
      int var8 = 0;
      int var9 = var2;
      if (var2 == 0) {
         var5.println(var1.getHeader());
         var9 = -1;
      }

      while(this.active) {
         if (var9 > 0) {
            --var8;
            if (var8 <= 0) {
               var8 = var9;
               var5.println(var1.getHeader());
            }
         }

         var5.println(var1.getRow());
         if (var4 > 0 && ++var6 >= (long)var4) {
            break;
         }

         try {
            Thread.sleep((long)var3);
         } catch (Exception var11) {
         }
      }

   }
}
