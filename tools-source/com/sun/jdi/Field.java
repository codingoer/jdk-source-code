package com.sun.jdi;

import jdk.Exported;

@Exported
public interface Field extends TypeComponent, Comparable {
   String typeName();

   Type type() throws ClassNotLoadedException;

   boolean isTransient();

   boolean isVolatile();

   boolean isEnumConstant();

   boolean equals(Object var1);

   int hashCode();
}
