package sun.jvmstat.monitor.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import sun.jvmstat.monitor.MonitorException;

public interface RemoteHost extends Remote {
   RemoteVm attachVm(int var1, String var2) throws RemoteException, MonitorException;

   void detachVm(RemoteVm var1) throws RemoteException, MonitorException;

   int[] activeVms() throws RemoteException, MonitorException;
}
