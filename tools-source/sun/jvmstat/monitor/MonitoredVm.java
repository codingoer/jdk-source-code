package sun.jvmstat.monitor;

import java.util.List;
import sun.jvmstat.monitor.event.VmListener;

public interface MonitoredVm {
   VmIdentifier getVmIdentifier();

   Monitor findByName(String var1) throws MonitorException;

   List findByPattern(String var1) throws MonitorException;

   void detach();

   void setInterval(int var1);

   int getInterval();

   void setLastException(Exception var1);

   Exception getLastException();

   void clearLastException();

   boolean isErrored();

   void addVmListener(VmListener var1) throws MonitorException;

   void removeVmListener(VmListener var1) throws MonitorException;
}
