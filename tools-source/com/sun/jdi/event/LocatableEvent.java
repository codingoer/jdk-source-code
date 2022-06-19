package com.sun.jdi.event;

import com.sun.jdi.Locatable;
import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface LocatableEvent extends Event, Locatable {
   ThreadReference thread();
}
