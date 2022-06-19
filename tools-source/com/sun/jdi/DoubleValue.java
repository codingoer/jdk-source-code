package com.sun.jdi;

import jdk.Exported;

@Exported
public interface DoubleValue extends PrimitiveValue, Comparable {
   double value();

   boolean equals(Object var1);

   int hashCode();
}
