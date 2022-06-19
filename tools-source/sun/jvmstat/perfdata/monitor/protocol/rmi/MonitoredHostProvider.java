package sun.jvmstat.perfdata.monitor.protocol.rmi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.jvmstat.monitor.remote.RemoteHost;
import sun.jvmstat.monitor.remote.RemoteVm;
import sun.jvmstat.perfdata.monitor.CountedTimerTask;
import sun.jvmstat.perfdata.monitor.CountedTimerTaskUtils;

public class MonitoredHostProvider extends MonitoredHost {
   private static final String serverName = "/JStatRemoteHost";
   private static final int DEFAULT_POLLING_INTERVAL = 1000;
   private ArrayList listeners;
   private NotifierTask task;
   private HashSet activeVms;
   private RemoteVmManager vmManager;
   private RemoteHost remoteHost;
   private Timer timer;

   public MonitoredHostProvider(HostIdentifier var1) throws MonitorException {
      this.hostId = var1;
      this.listeners = new ArrayList();
      this.interval = 1000;
      this.activeVms = new HashSet();
      String var3 = "/JStatRemoteHost";
      String var4 = var1.getPath();
      if (var4 != null && var4.length() > 0) {
         var3 = var4;
      }

      String var2;
      if (var1.getPort() != -1) {
         var2 = "rmi://" + var1.getHost() + ":" + var1.getPort() + var3;
      } else {
         var2 = "rmi://" + var1.getHost() + var3;
      }

      String var6;
      try {
         this.remoteHost = (RemoteHost)Naming.lookup(var2);
      } catch (RemoteException var7) {
         var6 = "RMI Registry not available at " + var1.getHost();
         if (var1.getPort() == -1) {
            var6 = var6 + ":" + 1099;
         } else {
            var6 = var6 + ":" + var1.getPort();
         }

         if (var7.getMessage() != null) {
            throw new MonitorException(var6 + "\n" + var7.getMessage(), var7);
         }

         throw new MonitorException(var6, var7);
      } catch (NotBoundException var8) {
         var6 = var8.getMessage();
         if (var6 == null) {
            var6 = var2;
         }

         throw new MonitorException("RMI Server " + var6 + " not available", var8);
      } catch (MalformedURLException var9) {
         var9.printStackTrace();
         throw new IllegalArgumentException("Malformed URL: " + var2);
      }

      this.vmManager = new RemoteVmManager(this.remoteHost);
      this.timer = new Timer(true);
   }

   public MonitoredVm getMonitoredVm(VmIdentifier var1) throws MonitorException {
      return this.getMonitoredVm(var1, 1000);
   }

   public MonitoredVm getMonitoredVm(VmIdentifier var1, int var2) throws MonitorException {
      VmIdentifier var3 = null;

      try {
         var3 = this.hostId.resolve(var1);
         RemoteVm var4 = this.remoteHost.attachVm(var1.getLocalVmId(), var1.getMode());
         RemoteMonitoredVm var5 = new RemoteMonitoredVm(var4, var3, this.timer, var2);
         var5.attach();
         return var5;
      } catch (RemoteException var6) {
         throw new MonitorException("Remote Exception attaching to " + var3.toString(), var6);
      } catch (URISyntaxException var7) {
         throw new IllegalArgumentException("Malformed URI: " + var1.toString(), var7);
      }
   }

   public void detach(MonitoredVm var1) throws MonitorException {
      RemoteMonitoredVm var2 = (RemoteMonitoredVm)var1;
      var2.detach();

      try {
         this.remoteHost.detachVm(var2.getRemoteVm());
      } catch (RemoteException var4) {
         throw new MonitorException("Remote Exception detaching from " + var1.getVmIdentifier().toString(), var4);
      }
   }

   public void addHostListener(HostListener var1) {
      synchronized(this.listeners) {
         this.listeners.add(var1);
         if (this.task == null) {
            this.task = new NotifierTask();
            this.timer.schedule(this.task, 0L, (long)this.interval);
         }

      }
   }

   public void removeHostListener(HostListener var1) {
      synchronized(this.listeners) {
         this.listeners.remove(var1);
         if (this.listeners.isEmpty() && this.task != null) {
            this.task.cancel();
            this.task = null;
         }

      }
   }

   public void setInterval(int var1) {
      synchronized(this.listeners) {
         if (var1 != this.interval) {
            int var3 = this.interval;
            super.setInterval(var1);
            if (this.task != null) {
               this.task.cancel();
               NotifierTask var4 = this.task;
               this.task = new NotifierTask();
               CountedTimerTaskUtils.reschedule(this.timer, var4, this.task, var3, var1);
            }

         }
      }
   }

   public Set activeVms() throws MonitorException {
      return this.vmManager.activeVms();
   }

   private void fireVmStatusChangedEvents(Set var1, Set var2, Set var3) {
      ArrayList var4 = null;
      VmStatusChangeEvent var5 = null;
      synchronized(this.listeners) {
         var4 = (ArrayList)this.listeners.clone();
      }

      HostListener var7;
      for(Iterator var6 = var4.iterator(); var6.hasNext(); var7.vmStatusChanged(var5)) {
         var7 = (HostListener)var6.next();
         if (var5 == null) {
            var5 = new VmStatusChangeEvent(this, var1, var2, var3);
         }
      }

   }

   void fireDisconnectedEvents() {
      ArrayList var1 = null;
      HostEvent var2 = null;
      synchronized(this.listeners) {
         var1 = (ArrayList)this.listeners.clone();
      }

      HostListener var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var4.disconnected(var2)) {
         var4 = (HostListener)var3.next();
         if (var2 == null) {
            var2 = new HostEvent(this);
         }
      }

   }

   private class NotifierTask extends CountedTimerTask {
      private NotifierTask() {
      }

      public void run() {
         super.run();
         HashSet var1 = MonitoredHostProvider.this.activeVms;

         try {
            MonitoredHostProvider.this.activeVms = (HashSet)MonitoredHostProvider.this.vmManager.activeVms();
         } catch (MonitorException var6) {
            System.err.println("MonitoredHostProvider: polling task caught MonitorException:");
            var6.printStackTrace();
            MonitoredHostProvider.this.setLastException(var6);
            MonitoredHostProvider.this.fireDisconnectedEvents();
         }

         if (!MonitoredHostProvider.this.activeVms.isEmpty()) {
            HashSet var2 = new HashSet();
            HashSet var3 = new HashSet();
            Iterator var4 = MonitoredHostProvider.this.activeVms.iterator();

            while(var4.hasNext()) {
               Integer var5 = (Integer)var4.next();
               if (!var1.contains(var5)) {
                  var2.add(var5);
               }
            }

            var4 = var1.iterator();

            while(var4.hasNext()) {
               Object var7 = var4.next();
               if (!MonitoredHostProvider.this.activeVms.contains(var7)) {
                  var3.add(var7);
               }
            }

            if (!var2.isEmpty() || !var3.isEmpty()) {
               MonitoredHostProvider.this.fireVmStatusChangedEvents(MonitoredHostProvider.this.activeVms, var2, var3);
            }

         }
      }

      // $FF: synthetic method
      NotifierTask(Object var2) {
         this();
      }
   }
}
