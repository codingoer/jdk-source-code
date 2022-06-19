package com.sun.jdi.connect;

import com.sun.jdi.VirtualMachine;
import java.io.IOException;
import java.util.Map;
import jdk.Exported;

@Exported
public interface ListeningConnector extends Connector {
   boolean supportsMultipleConnections();

   String startListening(Map var1) throws IOException, IllegalConnectorArgumentsException;

   void stopListening(Map var1) throws IOException, IllegalConnectorArgumentsException;

   VirtualMachine accept(Map var1) throws IOException, IllegalConnectorArgumentsException;
}
