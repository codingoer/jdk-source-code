package sun.jvmstat.perfdata.monitor.protocol.rmi;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.remote.RemoteHost;

public class RemoteVmManager {
   private RemoteHost remoteHost;
   private String user;

   public RemoteVmManager(RemoteHost var1) {
      this(var1, (String)null);
   }

   public RemoteVmManager(RemoteHost var1, String var2) {
      this.user = var2;
      this.remoteHost = var1;
   }

   public Set activeVms() throws MonitorException {
      Object var1 = null;

      int[] var5;
      try {
         var5 = this.remoteHost.activeVms();
      } catch (RemoteException var4) {
         throw new MonitorException("Error communicating with remote host: " + var4.getMessage(), var4);
      }

      HashSet var2 = new HashSet(var5.length);

      for(int var3 = 0; var3 < var5.length; ++var3) {
         var2.add(new Integer(var5[var3]));
      }

      return var2;
   }
}
