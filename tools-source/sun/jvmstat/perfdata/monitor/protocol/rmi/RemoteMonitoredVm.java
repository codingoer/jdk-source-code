package sun.jvmstat.perfdata.monitor.protocol.rmi;

import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.MonitorStatusChangeEvent;
import sun.jvmstat.monitor.event.VmEvent;
import sun.jvmstat.monitor.event.VmListener;
import sun.jvmstat.monitor.remote.RemoteVm;
import sun.jvmstat.perfdata.monitor.AbstractMonitoredVm;
import sun.jvmstat.perfdata.monitor.CountedTimerTask;
import sun.jvmstat.perfdata.monitor.CountedTimerTaskUtils;
import sun.jvmstat.perfdata.monitor.MonitorStatus;

public class RemoteMonitoredVm extends AbstractMonitoredVm {
   private ArrayList listeners;
   private NotifierTask notifierTask;
   private SamplerTask samplerTask;
   private Timer timer;
   private RemoteVm rvm;
   private ByteBuffer updateBuffer;

   public RemoteMonitoredVm(RemoteVm var1, VmIdentifier var2, Timer var3, int var4) throws MonitorException {
      super(var2, var4);
      this.rvm = var1;
      this.pdb = new PerfDataBuffer(var1, var2.getLocalVmId());
      this.listeners = new ArrayList();
      this.timer = var3;
   }

   public void attach() throws MonitorException {
      this.updateBuffer = this.pdb.getByteBuffer().duplicate();
      if (this.interval > 0) {
         this.samplerTask = new SamplerTask();
         this.timer.schedule(this.samplerTask, 0L, (long)this.interval);
      }

   }

   public void detach() {
      try {
         if (this.interval > 0) {
            if (this.samplerTask != null) {
               this.samplerTask.cancel();
               this.samplerTask = null;
            }

            if (this.notifierTask != null) {
               this.notifierTask.cancel();
               this.notifierTask = null;
            }

            this.sample();
         }
      } catch (RemoteException var5) {
         System.err.println("Could not read data for remote JVM " + this.vmid);
         var5.printStackTrace();
      } finally {
         super.detach();
      }

   }

   public void sample() throws RemoteException {
      assert this.updateBuffer != null;

      ((PerfDataBuffer)this.pdb).sample(this.updateBuffer);
   }

   public RemoteVm getRemoteVm() {
      return this.rvm;
   }

   public void addVmListener(VmListener var1) {
      synchronized(this.listeners) {
         this.listeners.add(var1);
         if (this.notifierTask == null) {
            this.notifierTask = new NotifierTask();
            this.timer.schedule(this.notifierTask, 0L, (long)this.interval);
         }

      }
   }

   public void removeVmListener(VmListener var1) {
      synchronized(this.listeners) {
         this.listeners.remove(var1);
         if (this.listeners.isEmpty() && this.notifierTask != null) {
            this.notifierTask.cancel();
            this.notifierTask = null;
         }

      }
   }

   public void setInterval(int var1) {
      synchronized(this.listeners) {
         if (var1 != this.interval) {
            int var3 = this.interval;
            super.setInterval(var1);
            if (this.samplerTask != null) {
               this.samplerTask.cancel();
               SamplerTask var4 = this.samplerTask;
               this.samplerTask = new SamplerTask();
               CountedTimerTaskUtils.reschedule(this.timer, var4, this.samplerTask, var3, var1);
            }

            if (this.notifierTask != null) {
               this.notifierTask.cancel();
               NotifierTask var7 = this.notifierTask;
               this.notifierTask = new NotifierTask();
               CountedTimerTaskUtils.reschedule(this.timer, var7, this.notifierTask, var3, var1);
            }

         }
      }
   }

   void fireMonitorStatusChangedEvents(List var1, List var2) {
      ArrayList var3 = null;
      MonitorStatusChangeEvent var4 = null;
      synchronized(this.listeners) {
         var3 = (ArrayList)this.listeners.clone();
      }

      VmListener var6;
      for(Iterator var5 = var3.iterator(); var5.hasNext(); var6.monitorStatusChanged(var4)) {
         var6 = (VmListener)var5.next();
         if (var4 == null) {
            var4 = new MonitorStatusChangeEvent(this, var1, var2);
         }
      }

   }

   void fireMonitorsUpdatedEvents() {
      ArrayList var1 = null;
      VmEvent var2 = null;
      synchronized(this.listeners) {
         var1 = (ArrayList)this.listeners.clone();
      }

      VmListener var4;
      for(Iterator var3 = var1.iterator(); var3.hasNext(); var4.monitorsUpdated(var2)) {
         var4 = (VmListener)var3.next();
         if (var2 == null) {
            var2 = new VmEvent(this);
         }
      }

   }

   private class SamplerTask extends CountedTimerTask {
      private SamplerTask() {
      }

      public void run() {
         super.run();

         try {
            RemoteMonitoredVm.this.sample();
            RemoteMonitoredVm.this.fireMonitorsUpdatedEvents();
         } catch (RemoteException var2) {
            System.err.println("Exception taking sample for " + RemoteMonitoredVm.this.getVmIdentifier());
            var2.printStackTrace();
            this.cancel();
         }

      }

      // $FF: synthetic method
      SamplerTask(Object var2) {
         this();
      }
   }

   private class NotifierTask extends CountedTimerTask {
      private NotifierTask() {
      }

      public void run() {
         super.run();

         try {
            MonitorStatus var1 = RemoteMonitoredVm.this.getMonitorStatus();
            List var2 = var1.getInserted();
            List var3 = var1.getRemoved();
            if (!var2.isEmpty() || !var3.isEmpty()) {
               RemoteMonitoredVm.this.fireMonitorStatusChangedEvents(var2, var3);
            }
         } catch (MonitorException var4) {
            System.err.println("Exception updating monitors for " + RemoteMonitoredVm.this.getVmIdentifier());
            var4.printStackTrace();
         }

      }

      // $FF: synthetic method
      NotifierTask(Object var2) {
         this();
      }
   }
}
