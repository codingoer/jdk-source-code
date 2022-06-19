package com.sun.jdi;

import jdk.Exported;

@Exported
public interface LongValue extends PrimitiveValue, Comparable {
   long value();

   boolean equals(Object var1);

   int hashCode();
}
