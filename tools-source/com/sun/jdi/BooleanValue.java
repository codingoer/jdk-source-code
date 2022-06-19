package com.sun.jdi;

import jdk.Exported;

@Exported
public interface BooleanValue extends PrimitiveValue {
   boolean value();

   boolean equals(Object var1);

   int hashCode();
}
