package sun.jvmstat.monitor.event;

import java.util.Set;
import sun.jvmstat.monitor.MonitoredHost;

public class VmStatusChangeEvent extends HostEvent {
   protected Set active;
   protected Set started;
   protected Set terminated;

   public VmStatusChangeEvent(MonitoredHost var1, Set var2, Set var3, Set var4) {
      super(var1);
      this.active = var2;
      this.started = var3;
      this.terminated = var4;
   }

   public Set getActive() {
      return this.active;
   }

   public Set getStarted() {
      return this.started;
   }

   public Set getTerminated() {
      return this.terminated;
   }
}
