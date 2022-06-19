package sun.jvmstat.perfdata.monitor;

import java.nio.IntBuffer;
import sun.jvmstat.monitor.AbstractMonitor;
import sun.jvmstat.monitor.IntegerMonitor;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;

public class PerfIntegerMonitor extends AbstractMonitor implements IntegerMonitor {
   IntBuffer ib;

   public PerfIntegerMonitor(String var1, Units var2, Variability var3, boolean var4, IntBuffer var5) {
      super(var1, var2, var3, var4);
      this.ib = var5;
   }

   public Object getValue() {
      return new Integer(this.ib.get(0));
   }

   public int intValue() {
      return this.ib.get(0);
   }
}
