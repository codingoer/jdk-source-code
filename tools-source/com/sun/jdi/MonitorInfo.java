package com.sun.jdi;

import jdk.Exported;

@Exported
public interface MonitorInfo extends Mirror {
   ObjectReference monitor();

   int stackDepth();

   ThreadReference thread();
}
