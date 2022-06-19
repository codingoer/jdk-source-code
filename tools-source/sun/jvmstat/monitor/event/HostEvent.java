package sun.jvmstat.monitor.event;

import java.util.EventObject;
import sun.jvmstat.monitor.MonitoredHost;

public class HostEvent extends EventObject {
   public HostEvent(MonitoredHost var1) {
      super(var1);
   }

   public MonitoredHost getMonitoredHost() {
      return (MonitoredHost)this.source;
   }
}
