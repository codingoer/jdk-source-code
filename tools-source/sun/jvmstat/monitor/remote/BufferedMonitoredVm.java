package sun.jvmstat.monitor.remote;

import sun.jvmstat.monitor.MonitoredVm;

public interface BufferedMonitoredVm extends MonitoredVm {
   byte[] getBytes();

   int getCapacity();
}
