package com.sun.jdi.request;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import jdk.Exported;

@Exported
public interface StepRequest extends EventRequest {
   int STEP_INTO = 1;
   int STEP_OVER = 2;
   int STEP_OUT = 3;
   int STEP_MIN = -1;
   int STEP_LINE = -2;

   ThreadReference thread();

   int size();

   int depth();

   void addClassFilter(ReferenceType var1);

   void addClassFilter(String var1);

   void addClassExclusionFilter(String var1);

   void addInstanceFilter(ObjectReference var1);
}
