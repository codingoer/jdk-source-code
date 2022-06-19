package com.sun.jdi.event;

import com.sun.jdi.Mirror;
import com.sun.jdi.request.EventRequest;
import jdk.Exported;

@Exported
public interface Event extends Mirror {
   EventRequest request();
}
