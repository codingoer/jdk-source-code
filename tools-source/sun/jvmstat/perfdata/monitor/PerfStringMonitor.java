package sun.jvmstat.perfdata.monitor;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import sun.jvmstat.monitor.StringMonitor;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;

public class PerfStringMonitor extends PerfByteArrayMonitor implements StringMonitor {
   private static Charset defaultCharset = Charset.defaultCharset();

   public PerfStringMonitor(String var1, Variability var2, boolean var3, ByteBuffer var4) {
      this(var1, var2, var3, var4, var4.limit());
   }

   public PerfStringMonitor(String var1, Variability var2, boolean var3, ByteBuffer var4, int var5) {
      super(var1, Units.STRING, var2, var3, var4, var5);
   }

   public Object getValue() {
      return this.stringValue();
   }

   public String stringValue() {
      String var1 = "";
      byte[] var2 = this.byteArrayValue();
      if (var2 != null && var2.length > 1 && var2[0] != 0) {
         int var3;
         for(var3 = 0; var3 < var2.length && var2[var3] != 0; ++var3) {
         }

         return new String(var2, 0, var3, defaultCharset);
      } else {
         return var1;
      }
   }
}
