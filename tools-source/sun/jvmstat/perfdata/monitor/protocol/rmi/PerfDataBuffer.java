package sun.jvmstat.perfdata.monitor.protocol.rmi;

import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.remote.RemoteVm;
import sun.jvmstat.perfdata.monitor.AbstractPerfDataBuffer;

public class PerfDataBuffer extends AbstractPerfDataBuffer {
   private RemoteVm rvm;

   public PerfDataBuffer(RemoteVm var1, int var2) throws MonitorException {
      this.rvm = var1;

      try {
         ByteBuffer var3 = ByteBuffer.allocate(var1.getCapacity());
         this.sample(var3);
         this.createPerfDataBuffer(var3, var2);
      } catch (RemoteException var4) {
         throw new MonitorException("Could not read data for remote JVM " + var2, var4);
      }
   }

   public void sample(ByteBuffer var1) throws RemoteException {
      assert var1 != null;

      assert this.rvm != null;

      synchronized(var1) {
         var1.clear();
         var1.put(this.rvm.getBytes());
      }
   }
}
