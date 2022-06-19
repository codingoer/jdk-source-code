package com.sun.jdi;

import jdk.Exported;

@Exported
public interface Type extends Mirror {
   String signature();

   String name();
}
