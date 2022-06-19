package com.sun.jdi.event;

import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import jdk.Exported;

@Exported
public interface ExceptionEvent extends LocatableEvent {
   ObjectReference exception();

   Location catchLocation();
}
