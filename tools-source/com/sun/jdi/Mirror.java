package com.sun.jdi;

import jdk.Exported;

@Exported
public interface Mirror {
   VirtualMachine virtualMachine();

   String toString();
}
