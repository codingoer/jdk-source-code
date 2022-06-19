package com.sun.jdi;

import jdk.Exported;

@Exported
public interface LocalVariable extends Mirror, Comparable {
   String name();

   String typeName();

   Type type() throws ClassNotLoadedException;

   String signature();

   String genericSignature();

   boolean isVisible(StackFrame var1);

   boolean isArgument();

   boolean equals(Object var1);

   int hashCode();
}
