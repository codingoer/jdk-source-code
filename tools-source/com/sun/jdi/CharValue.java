package com.sun.jdi;

import jdk.Exported;

@Exported
public interface CharValue extends PrimitiveValue, Comparable {
   char value();

   boolean equals(Object var1);

   int hashCode();
}
