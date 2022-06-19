package sun.jvmstat.perfdata.monitor.protocol.local;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;
import sun.jvmstat.perfdata.monitor.CountedTimerTask;
import sun.jvmstat.perfdata.monitor.CountedTimerTaskUtils;

public class MonitoredHostProvider extends MonitoredHost {
   private static final int DEFAULT_POLLING_INTERVAL = 1000;
   private ArrayList listeners;
   private NotifierTask task;
   private HashSet activeVms;
   private LocalVmManager vmManager;

   public MonitoredHostProvider(HostIdentifier var1) {
      this.hostId = var1;
      this.listeners = new ArrayList();
      this.interval = 1000;
      this.activeVms = new HashSet();
      this.vmManager = new LocalVmManager();
   }

   public MonitoredVm getMonitoredVm(VmIdentifier var1) throws MonitorException {
      return this.getMonitoredVm(var1, 1000);
   }

   public MonitoredVm getMonitoredVm(VmIdentifier var1, int var2) throws MonitorException {
      try {
         VmIdentifier var3 = this.hostId.resolve(var1);
         return new LocalMonitoredVm(var3, var2);
      } catch (URISyntaxException var4) {
         throw new IllegalArgumentException("Malformed URI: " + var1.toString(), var4);
      }
   }

   public void detach(MonitoredVm var1) {
      var1.detach();
   }

   public void addHostListener(HostListener var1) {
      synchronized(this.listeners) {
         this.listeners.add(var1);
         if (this.task == null) {
            this.task = new NotifierTask();
            LocalEventTimer var3 = LocalEventTimer.getInstance();
            var3.schedule(this.task, (long)this.interval, (long)this.interval);
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
               LocalEventTimer var5 = LocalEventTimer.getInstance();
               CountedTimerTaskUtils.reschedule(var5, var4, this.task, var3, var1);
            }

         }
      }
   }

   public Set activeVms() {
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

   private class NotifierTask extends CountedTimerTask {
      private NotifierTask() {
      }

      public void run() {
         super.run();
         HashSet var1 = MonitoredHostProvider.this.activeVms;
         MonitoredHostProvider.this.activeVms = (HashSet)MonitoredHostProvider.this.vmManager.activeVms();
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
               Object var6 = var4.next();
               if (!MonitoredHostProvider.this.activeVms.contains(var6)) {
                  var3.add(var6);
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
