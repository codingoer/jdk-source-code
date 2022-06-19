package com.sun.jdi.event;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface ClassPrepareEvent extends Event {
   ThreadReference thread();

   ReferenceType referenceType();
}
