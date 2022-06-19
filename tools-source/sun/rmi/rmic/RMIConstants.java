package sun.rmi.rmic;

import sun.tools.java.Identifier;

public interface RMIConstants extends Constants {
   Identifier idRemoteObject = Identifier.lookup("java.rmi.server.RemoteObject");
   Identifier idRemoteStub = Identifier.lookup("java.rmi.server.RemoteStub");
   Identifier idRemoteRef = Identifier.lookup("java.rmi.server.RemoteRef");
   Identifier idOperation = Identifier.lookup("java.rmi.server.Operation");
   Identifier idSkeleton = Identifier.lookup("java.rmi.server.Skeleton");
   Identifier idSkeletonMismatchException = Identifier.lookup("java.rmi.server.SkeletonMismatchException");
   Identifier idRemoteCall = Identifier.lookup("java.rmi.server.RemoteCall");
   Identifier idMarshalException = Identifier.lookup("java.rmi.MarshalException");
   Identifier idUnmarshalException = Identifier.lookup("java.rmi.UnmarshalException");
   Identifier idUnexpectedException = Identifier.lookup("java.rmi.UnexpectedException");
   int STUB_VERSION_1_1 = 1;
   int STUB_VERSION_FAT = 2;
   int STUB_VERSION_1_2 = 3;
   long STUB_SERIAL_VERSION_UID = 2L;
   int INTERFACE_HASH_STUB_VERSION = 1;
}
