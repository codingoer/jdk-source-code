package sun.jvmstat.perfdata.monitor;

import java.util.List;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.VmListener;
import sun.jvmstat.monitor.remote.BufferedMonitoredVm;

public abstract class AbstractMonitoredVm implements BufferedMonitoredVm {
   protected VmIdentifier vmid;
   protected AbstractPerfDataBuffer pdb;
   protected int interval;

   public AbstractMonitoredVm(VmIdentifier var1, int var2) throws MonitorException {
      this.vmid = var1;
      this.interval = var2;
   }

   public VmIdentifier getVmIdentifier() {
      return this.vmid;
   }

   public Monitor findByName(String var1) throws MonitorException {
      return this.pdb.findByName(var1);
   }

   public List findByPattern(String var1) throws MonitorException {
      return this.pdb.findByPattern(var1);
   }

   public void detach() {
   }

   public void setInterval(int var1) {
      this.interval = var1;
   }

   public int getInterval() {
      return this.interval;
   }

   public void setLastException(Exception var1) {
   }

   public Exception getLastException() {
      return null;
   }

   public void clearLastException() {
   }

   public boolean isErrored() {
      return false;
   }

   public MonitorStatus getMonitorStatus() throws MonitorException {
      return this.pdb.getMonitorStatus();
   }

   public abstract void addVmListener(VmListener var1);

   public abstract void removeVmListener(VmListener var1);

   public byte[] getBytes() {
      return this.pdb.getBytes();
   }

   public int getCapacity() {
      return this.pdb.getCapacity();
   }
}
