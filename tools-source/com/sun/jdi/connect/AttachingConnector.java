package com.sun.jdi.connect;

import com.sun.jdi.VirtualMachine;
import java.io.IOException;
import java.util.Map;
import jdk.Exported;

@Exported
public interface AttachingConnector extends Connector {
   VirtualMachine attach(Map var1) throws IOException, IllegalConnectorArgumentsException;
}
