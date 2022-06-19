package com.sun.jdi.event;

import com.sun.jdi.Value;
import jdk.Exported;

@Exported
public interface ModificationWatchpointEvent extends WatchpointEvent {
   Value valueToBe();
}
