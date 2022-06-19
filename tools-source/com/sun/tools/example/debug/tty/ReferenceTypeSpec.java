package com.sun.tools.example.debug.tty;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;

interface ReferenceTypeSpec {
   boolean matches(ReferenceType var1);

   ClassPrepareRequest createPrepareRequest();

   int hashCode();

   boolean equals(Object var1);
}
