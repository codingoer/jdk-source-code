package com.sun.jdi;

import jdk.Exported;

@Exported
public interface ClassObjectReference extends ObjectReference {
   ReferenceType reflectedType();
}
