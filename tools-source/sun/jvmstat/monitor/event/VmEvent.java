package sun.jvmstat.monitor.event;

import java.util.EventObject;
import sun.jvmstat.monitor.MonitoredVm;

public class VmEvent extends EventObject {
   public VmEvent(MonitoredVm var1) {
      super(var1);
   }

   public MonitoredVm getMonitoredVm() {
      return (MonitoredVm)this.source;
   }
}
