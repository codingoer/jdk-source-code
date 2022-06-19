package sun.jvmstat.perfdata.monitor.protocol.file;

import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.VmListener;
import sun.jvmstat.perfdata.monitor.AbstractMonitoredVm;

public class FileMonitoredVm extends AbstractMonitoredVm {
   public FileMonitoredVm(VmIdentifier var1, int var2) throws MonitorException {
      super(var1, var2);
      this.pdb = new PerfDataBuffer(var1);
   }

   public void addVmListener(VmListener var1) {
   }

   public void removeVmListener(VmListener var1) {
   }
}
