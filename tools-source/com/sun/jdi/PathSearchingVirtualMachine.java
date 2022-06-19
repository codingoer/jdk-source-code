package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface PathSearchingVirtualMachine extends VirtualMachine {
   List classPath();

   List bootClassPath();

   String baseDirectory();
}
