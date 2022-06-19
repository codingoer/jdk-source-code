package sun.tools.jstatd;

import sun.jvmstat.monitor.remote.BufferedMonitoredVm;
import sun.jvmstat.monitor.remote.RemoteVm;

public class RemoteVmImpl implements RemoteVm {
   private BufferedMonitoredVm mvm;

   RemoteVmImpl(BufferedMonitoredVm var1) {
      this.mvm = var1;
   }

   public byte[] getBytes() {
      return this.mvm.getBytes();
   }

   public int getCapacity() {
      return this.mvm.getCapacity();
   }

   public void detach() {
      this.mvm.detach();
   }

   public int getLocalVmId() {
      return this.mvm.getVmIdentifier().getLocalVmId();
   }
}
