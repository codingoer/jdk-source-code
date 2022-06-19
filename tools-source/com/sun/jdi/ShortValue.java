package com.sun.jdi;

import jdk.Exported;

@Exported
public interface ShortValue extends PrimitiveValue, Comparable {
   short value();

   boolean equals(Object var1);

   int hashCode();
}
