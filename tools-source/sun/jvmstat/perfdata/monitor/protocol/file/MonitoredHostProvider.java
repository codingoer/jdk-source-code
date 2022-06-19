package sun.jvmstat.perfdata.monitor.protocol.file;

import java.util.HashSet;
import java.util.Set;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostListener;

public class MonitoredHostProvider extends MonitoredHost {
   public static final int DEFAULT_POLLING_INTERVAL = 0;

   public MonitoredHostProvider(HostIdentifier var1) {
      this.hostId = var1;
   }

   public MonitoredVm getMonitoredVm(VmIdentifier var1) throws MonitorException {
      return this.getMonitoredVm(var1, 0);
   }

   public MonitoredVm getMonitoredVm(VmIdentifier var1, int var2) throws MonitorException {
      return new FileMonitoredVm(var1, var2);
   }

   public void detach(MonitoredVm var1) {
      var1.detach();
   }

   public void addHostListener(HostListener var1) {
   }

   public void removeHostListener(HostListener var1) {
   }

   public Set activeVms() {
      return new HashSet(0);
   }
}
