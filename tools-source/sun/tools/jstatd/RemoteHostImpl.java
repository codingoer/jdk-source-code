package sun.tools.jstatd;

import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.jvmstat.monitor.remote.BufferedMonitoredVm;
import sun.jvmstat.monitor.remote.RemoteHost;
import sun.jvmstat.monitor.remote.RemoteVm;

public class RemoteHostImpl implements RemoteHost, HostListener {
   private MonitoredHost monitoredHost;
   private Set activeVms;

   public RemoteHostImpl() throws MonitorException {
      try {
         this.monitoredHost = MonitoredHost.getMonitoredHost("localhost");
      } catch (URISyntaxException var2) {
      }

      this.activeVms = this.monitoredHost.activeVms();
      this.monitoredHost.addHostListener(this);
   }

   public RemoteVm attachVm(int var1, String var2) throws RemoteException, MonitorException {
      new Integer(var1);
      RemoteVm var4 = null;
      StringBuffer var5 = new StringBuffer();
      var5.append("local://").append(var1).append("@localhost");
      if (var2 != null) {
         var5.append("?mode=" + var2);
      }

      String var6 = var5.toString();

      try {
         VmIdentifier var7 = new VmIdentifier(var6);
         MonitoredVm var8 = this.monitoredHost.getMonitoredVm(var7);
         RemoteVmImpl var9 = new RemoteVmImpl((BufferedMonitoredVm)var8);
         var4 = (RemoteVm)UnicastRemoteObject.exportObject(var9, 0);
         return var4;
      } catch (URISyntaxException var10) {
         throw new RuntimeException("Malformed VmIdentifier URI: " + var6, var10);
      }
   }

   public void detachVm(RemoteVm var1) throws RemoteException {
      var1.detach();
   }

   public int[] activeVms() throws MonitorException {
      Object[] var1 = null;
      Object var2 = null;
      var1 = this.monitoredHost.activeVms().toArray();
      int[] var4 = new int[var1.length];

      for(int var3 = 0; var3 < var4.length; ++var3) {
         var4[var3] = (Integer)var1[var3];
      }

      return var4;
   }

   public void vmStatusChanged(VmStatusChangeEvent var1) {
      synchronized(this.activeVms) {
         this.activeVms.retainAll(var1.getActive());
      }
   }

   public void disconnected(HostEvent var1) {
   }
}
