package sun.jvmstat.perfdata.monitor;

import java.nio.ByteBuffer;
import sun.jvmstat.monitor.Variability;

public class PerfStringVariableMonitor extends PerfStringMonitor {
   public PerfStringVariableMonitor(String var1, boolean var2, ByteBuffer var3) {
      this(var1, var2, var3, var3.limit());
   }

   public PerfStringVariableMonitor(String var1, boolean var2, ByteBuffer var3, int var4) {
      super(var1, Variability.VARIABLE, var2, var3, var4 + 1);
   }
}
