package com.sun.jdi.event;

import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface ThreadDeathEvent extends Event {
   ThreadReference thread();
}
