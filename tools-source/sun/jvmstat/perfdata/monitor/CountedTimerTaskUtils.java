package sun.jvmstat.perfdata.monitor;

import java.util.Timer;

public class CountedTimerTaskUtils {
   private static final boolean DEBUG = false;

   public static void reschedule(Timer var0, CountedTimerTask var1, CountedTimerTask var2, int var3, int var4) {
      long var5 = System.currentTimeMillis();
      long var7 = var1.scheduledExecutionTime();
      long var9 = var5 - var7;
      long var11 = 0L;
      if (var1.executionCount() > 0L) {
         long var13 = (long)var4 - var9;
         var11 = var13 >= 0L ? var13 : 0L;
      }

      var0.schedule(var2, var11, (long)var4);
   }
}
