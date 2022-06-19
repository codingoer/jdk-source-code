package com.sun.jdi.event;

import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface ThreadStartEvent extends Event {
   ThreadReference thread();
}
