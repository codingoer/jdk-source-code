package sun.jvmstat.perfdata.monitor.protocol.local;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.MonitorStatusChangeEvent;
import sun.jvmstat.monitor.event.VmEvent;
import sun.jvmstat.monitor.event.VmListener;
import sun.jvmstat.perfdata.monitor.AbstractMonitoredVm;
import sun.jvmstat.perfdata.monitor.CountedTimerTask;
import sun.jvmstat.perfdata.monitor.CountedTimerTaskUtils;
import sun.jvmstat.perfdata.monitor.MonitorStatus;

public class LocalMonitoredVm extends AbstractMonitoredVm {
   private ArrayList listeners;
   private NotifierTask task;

   public LocalMonitoredVm(VmIdentifier var1, int var2) throws MonitorException {
      super(var1, var2);
      this.pdb = new PerfDataBuffer(var1);
      this.listeners = new ArrayList();
   }

   public void detach() {
      if (this.interval > 0 && this.task != null) {
         this.task.cancel();
         this.task = null;
      }

      super.detach();
   }

   public void addVmListener(VmListener var1) {
      synchronized(this.listeners) {
         this.listeners.add(var1);
         if (this.task == null) {
            this.task = new NotifierTask();
            LocalEventTimer var3 = LocalEventTimer.getInstance();
            var3.schedule(this.task, (long)this.interval, (long)this.interval);
         }

      }
   }

   public void removeVmListener(VmListener var1) {
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

   void fireMonitorStatusChangedEvents(List var1, List var2) {
      MonitorStatusChangeEvent var3 = null;
      ArrayList var4 = null;
      synchronized(this.listeners) {
         var4 = (ArrayList)this.listeners.clone();
      }

      VmListener var6;
      for(Iterator var5 = var4.iterator(); var5.hasNext(); var6.monitorStatusChanged(var3)) {
         var6 = (VmListener)var5.next();
         if (var3 == null) {
            var3 = new MonitorStatusChangeEvent(this, var1, var2);
         }
      }

   }

   void fireMonitorsUpdatedEvents() {
      VmEvent var1 = null;
      ArrayList var2 = null;
      synchronized(this.listeners) {
         var2 = (ArrayList)cast(this.listeners.clone());
      }

      VmListener var4;
      for(Iterator var3 = var2.iterator(); var3.hasNext(); var4.monitorsUpdated(var1)) {
         var4 = (VmListener)var3.next();
         if (var1 == null) {
            var1 = new VmEvent(this);
         }
      }

   }

   static Object cast(Object var0) {
      return var0;
   }

   private class NotifierTask extends CountedTimerTask {
      private NotifierTask() {
      }

      public void run() {
         super.run();

         try {
            MonitorStatus var1 = LocalMonitoredVm.this.getMonitorStatus();
            List var2 = var1.getInserted();
            List var3 = var1.getRemoved();
            if (!var2.isEmpty() || !var3.isEmpty()) {
               LocalMonitoredVm.this.fireMonitorStatusChangedEvents(var2, var3);
            }

            LocalMonitoredVm.this.fireMonitorsUpdatedEvents();
         } catch (MonitorException var4) {
            System.err.println("Exception updating monitors for " + LocalMonitoredVm.this.getVmIdentifier());
            var4.printStackTrace();
         }

      }

      // $FF: synthetic method
      NotifierTask(Object var2) {
         this();
      }
   }
}
