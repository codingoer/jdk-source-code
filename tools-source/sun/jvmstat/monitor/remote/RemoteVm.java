package sun.jvmstat.monitor.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteVm extends Remote {
   byte[] getBytes() throws RemoteException;

   int getCapacity() throws RemoteException;

   int getLocalVmId() throws RemoteException;

   void detach() throws RemoteException;
}
