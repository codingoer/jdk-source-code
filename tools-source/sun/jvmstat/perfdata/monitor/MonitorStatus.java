package sun.jvmstat.perfdata.monitor;

import java.util.List;

public class MonitorStatus {
   protected List inserted;
   protected List removed;

   public MonitorStatus(List var1, List var2) {
      this.inserted = var1;
      this.removed = var2;
   }

   public List getInserted() {
      return this.inserted;
   }

   public List getRemoved() {
      return this.removed;
   }
}
