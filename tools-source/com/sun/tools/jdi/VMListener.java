package com.sun.tools.jdi;

import java.util.EventListener;

interface VMListener extends EventListener {
   boolean vmSuspended(VMAction var1);

   boolean vmNotSuspended(VMAction var1);
}
