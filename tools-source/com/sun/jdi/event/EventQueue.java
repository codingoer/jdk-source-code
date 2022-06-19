package com.sun.jdi.event;

import com.sun.jdi.Mirror;
import jdk.Exported;

@Exported
public interface EventQueue extends Mirror {
   EventSet remove() throws InterruptedException;

   EventSet remove(long var1) throws InterruptedException;
}
