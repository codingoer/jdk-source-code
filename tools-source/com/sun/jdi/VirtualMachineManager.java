package com.sun.jdi;

import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.spi.Connection;
import java.io.IOException;
import java.util.List;
import jdk.Exported;

@Exported
public interface VirtualMachineManager {
   LaunchingConnector defaultConnector();

   List launchingConnectors();

   List attachingConnectors();

   List listeningConnectors();

   List allConnectors();

   List connectedVirtualMachines();

   int majorInterfaceVersion();

   int minorInterfaceVersion();

   VirtualMachine createVirtualMachine(Connection var1, Process var2) throws IOException;

   VirtualMachine createVirtualMachine(Connection var1) throws IOException;
}
