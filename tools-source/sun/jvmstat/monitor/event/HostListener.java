package sun.jvmstat.monitor.event;

import java.util.EventListener;

public interface HostListener extends EventListener {
   void vmStatusChanged(VmStatusChangeEvent var1);

   void disconnected(HostEvent var1);
}
