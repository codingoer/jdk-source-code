package sun.jvmstat.perfdata.monitor.protocol.local;

import java.util.Timer;

public class LocalEventTimer extends Timer {
   private static LocalEventTimer instance;

   private LocalEventTimer() {
      super(true);
   }

   public static synchronized LocalEventTimer getInstance() {
      if (instance == null) {
         instance = new LocalEventTimer();
      }

      return instance;
   }
}
