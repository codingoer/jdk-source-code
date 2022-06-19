package com.sun.tools.jdi;

import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.VirtualMachine;

abstract class PrimitiveTypeImpl extends TypeImpl implements PrimitiveType {
   PrimitiveTypeImpl(VirtualMachine var1) {
      super(var1);
   }

   abstract PrimitiveValue convert(PrimitiveValue var1) throws InvalidTypeException;

   public String toString() {
      return this.name();
   }
}
