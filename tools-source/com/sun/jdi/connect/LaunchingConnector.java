package com.sun.jdi.connect;

import com.sun.jdi.VirtualMachine;
import java.io.IOException;
import java.util.Map;
import jdk.Exported;

@Exported
public interface LaunchingConnector extends Connector {
   VirtualMachine launch(Map var1) throws IOException, IllegalConnectorArgumentsException, VMStartException;
}
