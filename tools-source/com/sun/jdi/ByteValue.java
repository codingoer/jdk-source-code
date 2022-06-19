package com.sun.jdi;

import jdk.Exported;

@Exported
public interface ByteValue extends PrimitiveValue, Comparable {
   byte value();

   boolean equals(Object var1);

   int hashCode();
}
