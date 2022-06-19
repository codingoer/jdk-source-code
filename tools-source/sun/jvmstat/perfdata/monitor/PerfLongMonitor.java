package sun.jvmstat.perfdata.monitor;

import java.nio.LongBuffer;
import sun.jvmstat.monitor.AbstractMonitor;
import sun.jvmstat.monitor.LongMonitor;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;

public class PerfLongMonitor extends AbstractMonitor implements LongMonitor {
   LongBuffer lb;

   public PerfLongMonitor(String var1, Units var2, Variability var3, boolean var4, LongBuffer var5) {
      super(var1, var2, var3, var4);
      this.lb = var5;
   }

   public Object getValue() {
      return new Long(this.lb.get(0));
   }

   public long longValue() {
      return this.lb.get(0);
   }
}
