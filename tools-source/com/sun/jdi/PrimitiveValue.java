package com.sun.jdi;

import jdk.Exported;

@Exported
public interface PrimitiveValue extends Value {
   boolean booleanValue();

   byte byteValue();

   char charValue();

   short shortValue();

   int intValue();

   long longValue();

   float floatValue();

   double doubleValue();
}
