package com.sun.jdi.request;

import com.sun.jdi.Locatable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface BreakpointRequest extends EventRequest, Locatable {
   Location location();

   void addThreadFilter(ThreadReference var1);

   void addInstanceFilter(ObjectReference var1);
}
