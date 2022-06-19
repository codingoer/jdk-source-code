package com.sun.jdi;

import jdk.Exported;

@Exported
public interface ArrayType extends ReferenceType {
   ArrayReference newInstance(int var1);

   String componentSignature();

   String componentTypeName();

   Type componentType() throws ClassNotLoadedException;
}
