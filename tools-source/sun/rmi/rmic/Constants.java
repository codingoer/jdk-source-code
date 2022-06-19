package sun.rmi.rmic;

import sun.tools.java.Identifier;

public interface Constants extends sun.tools.java.Constants {
   Identifier idRemote = Identifier.lookup("java.rmi.Remote");
   Identifier idRemoteException = Identifier.lookup("java.rmi.RemoteException");
}
