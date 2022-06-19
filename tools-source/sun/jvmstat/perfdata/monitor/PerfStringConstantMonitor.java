package sun.jvmstat.perfdata.monitor;

import java.nio.ByteBuffer;
import sun.jvmstat.monitor.Variability;

public class PerfStringConstantMonitor extends PerfStringMonitor {
   String data = super.stringValue();

   public PerfStringConstantMonitor(String var1, boolean var2, ByteBuffer var3) {
      super(var1, Variability.CONSTANT, var2, var3);
   }

   public Object getValue() {
      return this.data;
   }

   public String stringValue() {
      return this.data;
   }
}
