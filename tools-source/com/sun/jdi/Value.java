package com.sun.jdi;

import jdk.Exported;

@Exported
public interface Value extends Mirror {
   Type type();
}
