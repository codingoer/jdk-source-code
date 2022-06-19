package sun.tools.jstat;

import java.util.Iterator;
import java.util.List;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.StringMonitor;

public class RawOutputFormatter implements OutputFormatter {
   private List logged;
   private String header;
   private boolean printStrings;

   public RawOutputFormatter(List var1, boolean var2) {
      this.logged = var1;
      this.printStrings = var2;
   }

   public String getHeader() throws MonitorException {
      if (this.header == null) {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.logged.iterator();

         while(var2.hasNext()) {
            Monitor var3 = (Monitor)var2.next();
            var1.append(var3.getName() + " ");
         }

         this.header = var1.toString();
      }

      return this.header;
   }

   public String getRow() throws MonitorException {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;
      Iterator var3 = this.logged.iterator();

      while(true) {
         while(var3.hasNext()) {
            Monitor var4 = (Monitor)var3.next();
            if (var2++ > 0) {
               var1.append(" ");
            }

            if (this.printStrings && var4 instanceof StringMonitor) {
               var1.append("\"").append(var4.getValue()).append("\"");
            } else {
               var1.append(var4.getValue());
            }
         }

         return var1.toString();
      }
   }
}
