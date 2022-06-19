package com.sun.jdi.request;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface MethodEntryRequest extends EventRequest {
   void addThreadFilter(ThreadReference var1);

   void addClassFilter(ReferenceType var1);

   void addClassFilter(String var1);

   void addClassExclusionFilter(String var1);

   void addInstanceFilter(ObjectReference var1);
}
