package com.sun.jdi.event;

import jdk.Exported;

@Exported
public interface ClassUnloadEvent extends Event {
   String className();

   String classSignature();
}
