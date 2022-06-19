package com.sun.jdi.request;

import jdk.Exported;

@Exported
public interface ClassUnloadRequest extends EventRequest {
   void addClassFilter(String var1);

   void addClassExclusionFilter(String var1);
}
