package com.sun.jdi.request;

import com.sun.jdi.ReferenceType;
import jdk.Exported;

@Exported
public interface ClassPrepareRequest extends EventRequest {
   void addClassFilter(ReferenceType var1);

   void addClassFilter(String var1);

   void addClassExclusionFilter(String var1);

   void addSourceNameFilter(String var1);
}
