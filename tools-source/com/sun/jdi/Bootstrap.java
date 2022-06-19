package com.sun.jdi;

import com.sun.tools.jdi.VirtualMachineManagerImpl;
import jdk.Exported;

@Exported
public class Bootstrap {
   public static synchronized VirtualMachineManager virtualMachineManager() {
      return VirtualMachineManagerImpl.virtualMachineManager();
   }
}
