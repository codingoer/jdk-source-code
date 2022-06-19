package com.sun.jdi.event;

import com.sun.jdi.Method;
import jdk.Exported;

@Exported
public interface MethodEntryEvent extends LocatableEvent {
   Method method();
}
