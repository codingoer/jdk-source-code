package com.sun.jdi;

import jdk.Exported;

@Exported
public interface StringReference extends ObjectReference {
   String value();
}
