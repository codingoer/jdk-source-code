package com.sun.tools.jdi;

import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;

public interface VirtualMachineManagerService extends VirtualMachineManager {
   void setDefaultConnector(LaunchingConnector var1);

   void addConnector(Connector var1);

   void removeConnector(Connector var1);
}
