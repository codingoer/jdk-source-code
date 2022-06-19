package com.sun.jdi;

import java.util.List;
import jdk.Exported;

@Exported
public interface ClassLoaderReference extends ObjectReference {
   List definedClasses();

   List visibleClasses();
}
