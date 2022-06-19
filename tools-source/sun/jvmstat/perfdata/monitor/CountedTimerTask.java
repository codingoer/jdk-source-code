package sun.jvmstat.perfdata.monitor;

import java.util.TimerTask;

public class CountedTimerTask extends TimerTask {
   volatile long executionCount;

   public long executionCount() {
      return this.executionCount;
   }

   public void run() {
      ++this.executionCount;
   }
}
