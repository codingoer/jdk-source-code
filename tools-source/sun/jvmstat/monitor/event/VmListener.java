package sun.jvmstat.monitor.event;

import java.util.EventListener;

public interface VmListener extends EventListener {
   void monitorStatusChanged(MonitorStatusChangeEvent var1);

   void monitorsUpdated(VmEvent var1);

   void disconnected(VmEvent var1);
}
