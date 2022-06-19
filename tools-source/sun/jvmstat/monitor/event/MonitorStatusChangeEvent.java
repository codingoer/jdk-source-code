package sun.jvmstat.monitor.event;

import java.util.List;
import sun.jvmstat.monitor.MonitoredVm;

public class MonitorStatusChangeEvent extends VmEvent {
   protected List inserted;
   protected List removed;

   public MonitorStatusChangeEvent(MonitoredVm var1, List var2, List var3) {
      super(var1);
      this.inserted = var2;
      this.removed = var3;
   }

   public List getInserted() {
      return this.inserted;
   }

   public List getRemoved() {
      return this.removed;
   }
}
