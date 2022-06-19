package com.sun.tools.jdi;

import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.Type;

interface ValueContainer {
   Type type() throws ClassNotLoadedException;

   Type findType(String var1) throws ClassNotLoadedException;

   String typeName();

   String signature();
}
