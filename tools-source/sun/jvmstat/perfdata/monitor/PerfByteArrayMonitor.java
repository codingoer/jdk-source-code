package sun.jvmstat.perfdata.monitor;

import java.nio.ByteBuffer;
import sun.jvmstat.monitor.AbstractMonitor;
import sun.jvmstat.monitor.ByteArrayMonitor;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;

public class PerfByteArrayMonitor extends AbstractMonitor implements ByteArrayMonitor {
   ByteBuffer bb;

   public PerfByteArrayMonitor(String var1, Units var2, Variability var3, boolean var4, ByteBuffer var5, int var6) {
      super(var1, var2, var3, var4, var6);
      this.bb = var5;
   }

   public Object getValue() {
      return this.byteArrayValue();
   }

   public byte[] byteArrayValue() {
      this.bb.position(0);
      byte[] var1 = new byte[this.bb.limit()];
      this.bb.get(var1);
      return var1;
   }

   public byte byteAt(int var1) {
      this.bb.position(var1);
      return this.bb.get();
   }

   public int getMaximumLength() {
      return this.bb.limit();
   }
}
