package com.sun.jdi;

import jdk.Exported;

@Exported
public interface FloatValue extends PrimitiveValue, Comparable {
   float value();

   boolean equals(Object var1);

   int hashCode();
}
