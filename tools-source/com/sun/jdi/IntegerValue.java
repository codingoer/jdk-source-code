package com.sun.jdi;

import jdk.Exported;

@Exported
public interface IntegerValue extends PrimitiveValue, Comparable {
   int value();

   boolean equals(Object var1);

   int hashCode();
}
